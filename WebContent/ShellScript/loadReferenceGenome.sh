#!/bin/bash

# Usage: ./refposgresLoader.sh -i input.txt -o outputName -n organism_name -p password

while getopts "i:o:n:p:" opt; do
  case $opt in
    i) input_file="$OPTARG" ;;
    o) outputname="$OPTARG" ;;
    n) organism_name="$OPTARG" ;;
    p) PGPASSWORD="$OPTARG" ;;
    *) echo "Usage: $0 -i input.txt -o output.csv -n organism_name -p password" >&2; exit 1 ;;
  esac
done


PGUSER="postgres"
PGDATABASE="snpseekdb"
PGHOST="192.168.15.34"
PGPORT="5432"

export PGPASSWORD

# Check if organism exists
echo "Adding '$organism_name'"
org_max_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT COALESCE(max(organism_id),0) FROM organism;" | tr -d '[:space:]')
org_next_id=$((org_max_id + 1))

# Ensure sequence is updated after insert
psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -c \
	  "SELECT setval('organism_organism_id_seq', $org_next_id, false);"

organism_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT organism_id FROM organism WHERE common_name='$organism_name' LIMIT 1;" | tr -d '[:space:]')
echo "retrieved from db: '$organism_id'"
if [[ -z "$organism_id" ]]; then
    echo "Organism '$organism_name' not found. Inserting..."
    psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -c \
        "INSERT INTO organism (abbreviation, genus, species, common_name) VALUES ('$organism_name','$organism_name', '$organism_name', '$organism_name');"
    organism_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT organism_id FROM organism WHERE common_name='$organism_name' LIMIT 1;" | tr -d '[:space:]')
fi

# Check if CVTERM: Chromosome and CV:sequence exists
# Check if CV 'sequence' exists
cv_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cv_id FROM cv WHERE name='sequence' LIMIT 1;" | tr -d '[:space:]')
if [[ -z "$cv_id" ]]; then
    echo "CV 'sequence' not found. Inserting..."
    psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -c "INSERT INTO cv (name) VALUES ('sequence');"
    cv_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cv_id FROM cv WHERE name='sequence' LIMIT 1;" | tr -d '[:space:]')
fi

# Check if CVTERM 'chromosome' exists for CV 'sequence'
cvterm_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cvterm_id FROM cvterm WHERE name='chromosome' AND cv_id=$cv_id LIMIT 1;" | tr -d '[:space:]')
if [[ -z "$cvterm_id" ]]; then
    echo "CVTERM 'chromosome' not found for CV 'sequence'. Inserting..."
    psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -c "INSERT INTO cvterm (cv_id, name) VALUES ($cv_id, 'chromosome');"
    cvterm_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cvterm_id FROM cvterm WHERE name='chromosome' AND cv_id=$cv_id LIMIT 1;" | tr -d '[:space:]')
fi


# Check if CVTERM 'gene' exists for CV 'sequence'
gene_cvterm_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cvterm_id FROM cvterm WHERE name='gene' AND cv_id=$cv_id LIMIT 1;" | tr -d '[:space:]')
if [[ -z "$cvterm_id" ]]; then
    echo "CVTERM 'gene' not found for CV 'sequence'. Inserting..."
    psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -c "INSERT INTO cvterm (cv_id, name) VALUES ($cv_id, 'gene');"
    gene_cvterm_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT cvterm_id FROM cvterm WHERE name='gene' AND cv_id=$cv_id LIMIT 1;" | tr -d '[:space:]')
fi




max_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT COALESCE(max(feature_id),0) FROM feature;" | tr -d '[:space:]')
next_id=$((max_id + 1))

floc_max_id=$(psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" -t -c "SELECT COALESCE(max(featureloc_id),0) FROM featureloc;" | tr -d '[:space:]')
floc_next_id=$((floc_max_id + 1))

echo "feature_id,dbxref_id,organism_id,name,uniquename,residues,seqlen,md5checksum,type_id,is_analysis,is_obsolete,timeaccessioned,timelastmodified" > "${outputname}_feature.csv"
echo "featureloc_id,feature_id,srcfeature_id,fmin,is_fmin_partial,fmax,is_fmax_partial,strand,phase,residue_info,locgroup,rank" > "${outputname}_featureloc.csv"
echo "logs" > logs.txt  

chroms=$(grep -oiE 'chr[0-9A-Za-z]+' "$input_file" | sort -u)

declare -A chr_feature_id

next_id=$((max_id + 1))
for chr in $chroms; do
    timenow=$(date '+%Y-%m-%d %H:%M:%S')
    norm_chr=$(echo "$chr" | sed -E 's/chr0*([0-9]+)/Chr\1/I; s/chr([A-Za-z]+)/Chr\1/I')
    seqlen=$(awk -v chr="$chr" '
        tolower($1)==tolower(chr) && $3=="gene" { val5=$5 }
        END { if(val5) print val5 }
    ' "$input_file")
    if [[ -n "$seqlen" ]]; then
        echo "$next_id,,$organism_id,$norm_chr,$chr,,$seqlen,,$cvterm_id,false,false,$timenow,$timenow" >> "${outputname}_feature.csv"
        chr_feature_id["$chr"]=$next_id
        next_id=$((next_id + 1))
    fi
done

gene_max_id=$next_id

gene_cvtermid=$gene_cvterm_id

echo "gene $gene_cvtermid" 

for chr in "${!chr_feature_id[@]}"; do
    echo "$chr,${chr_feature_id[$chr]}"
done > chr_feature_id_map.txt

awk -v OFS=',' -v gene_max_id="$gene_max_id" -v organism_id="$organism_id" -v cvterm_id="$cvterm_id" -v gene_cvtermid="$gene_cvtermid" -v outputname="$outputname" -v mapfile="chr_feature_id_map.txt" '
    BEGIN {
        timenow=strftime("%Y-%m-%d %H:%M:%S");
        floc_next_id='"$floc_next_id"';

        while ((getline < mapfile) > 0) {
            split($0, arr, ",");
            chr2fid[arr[1]] = arr[2];
        }
    }
    $3 == "gene" {
         gene_feature_id = gene_max_id++;
        # Extract ID from attributes column ($9)
        match($9, /ID=([^;]+)/, arr)
        uniquename = (arr[1] ? arr[1] : $9)
        # remove 'gene:' prefix if present else remove next line 
        sub(/^gene:/, "", uniquename)
        lengthSeq = $5 - $4
        # Add to feature.csv
        print gene_feature_id ",",
              organism_id ,
              uniquename "," uniquename ,  # uniquename, residues, seqlen (adjust as needed)
              ","lengthSeq ",," gene_cvtermid ",false,false", timenow "," timenow >> outputname "_feature.csv";
        fid = chr2fid[$1];
        # Add to featureloc.csv
        print floc_next_id "," gene_feature_id "," fid "," $4 ",false," $5 ",false," 1 ",,,0,0" >> outputname "_featureloc.csv";
        floc_next_id++;
    }
' "$input_file"

# --- Copy generated CSV files into PostgreSQL ---

echo "Loading data into PostgreSQL..."

psql "host=$PGHOST dbname=$PGDATABASE user=$PGUSER port=$PGPORT" <<EOF
\copy feature(feature_id,dbxref_id,organism_id,name,uniquename,residues,seqlen,md5checksum,type_id,is_analysis,is_obsolete,timeaccessioned,timelastmodified) FROM '${outputname}_feature.csv' CSV HEADER;
\copy featureloc(featureloc_id,feature_id,srcfeature_id,fmin,is_fmin_partial,fmax,is_fmax_partial,strand,phase,residue_info,locgroup,rank) FROM '${outputname}_featureloc.csv' CSV HEADER;
EOF

echo "✅ Data successfully copied to database."
