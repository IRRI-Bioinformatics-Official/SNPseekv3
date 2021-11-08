package user.ui.module.workspace;

public class CustomList {

	String listname;

	String dataset;

	Integer phenotype;

	String varietyList;
	
	String snpList;

	String LocusList;

	String chromosome;
	
	boolean snpAllele;
	
	boolean snpPvalue;
	
	boolean verifySnp;

	public String getListname() {
		return listname;
	}

	public void setListname(String listname) {
		this.listname = listname;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public Integer getPhenotype() {
		return phenotype;
	}

	public void setPhenotype(Integer phenotype) {
		this.phenotype = phenotype;
	}

	public String getVarietyList() {
		return varietyList;
	}

	public void setVarietyList(String varietyList) {
		this.varietyList = varietyList;
	}

	public String getLocusList() {
		return LocusList;
	}

	public void setLocusList(String locusList) {
		LocusList = locusList;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public boolean isSnpAllele() {
		return snpAllele;
	}

	public void setSnpAllele(boolean snpAllele) {
		this.snpAllele = snpAllele;
	}

	public boolean isSnpPvalue() {
		return snpPvalue;
	}

	public void setSnpPvalue(boolean snpPvalue) {
		this.snpPvalue = snpPvalue;
	}

	public String getSnpList() {
		return snpList;
	}

	public void setSnpList(String snpList) {
		this.snpList = snpList;
	}

	public boolean isVerifySnp() {
		return verifySnp;
	}

	public void setVerifySnp(boolean verifySnp) {
		this.verifySnp = verifySnp;
	}
	
	
	
	

}
