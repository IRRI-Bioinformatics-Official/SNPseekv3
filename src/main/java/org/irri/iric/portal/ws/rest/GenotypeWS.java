package org.irri.iric.portal.ws.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.irri.iric.ds.chado.dao.VGenotypeRunDAO;
import org.irri.iric.ds.chado.dao.access.OrganismDAO;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.ds.chado.domain.model.VGenotypeRun;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jettison.json.JSONException;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.genotype.VariantTableArray;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
//import org.irri.iric.portal.genotype.zkui.SNPAllvarsRowRenderer;
//import org.irri.iric.portal.genotype.zkui.SNPListItemRenderer;
//import org.irri.iric.portal.genotype.zkui.SNPMatrixRenderer;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.ws.entity.VariantTableWS;
//import org.irri.iric.portal.ws.entity.VariantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller("GenotypeWebService")
@Path("/genotype")
public class GenotypeWS {

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;
	
	@Autowired
	private WorkspaceFacade workspace;

	@Autowired
	@Qualifier("GenotypeRunPlatformDAO")
	private VGenotypeRunDAO genotyperundao;

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO lisitemdao;

	@Autowired
	private VarietyFacade variety;

	private String dataset = VarietyFacade.DATASET_SNPINDELV2_IUPAC;

	private Map<String, String> mapVarReplace = new HashMap();
	private static final int GENO_VAR_CAP = 20000;

	private OrganismDAO organismDAO;

	public GenotypeWS() {
		super();
		// TODO Auto-generated constructor stub
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
		organismDAO = (OrganismDAO) AppContext.checkBean(organismDAO, "OrganismDAO");
		
		AppContext.debug("GenotypeWS started");

		// rename some variable names
		mapVarReplace.put("iricStockId", "varietyId");
		mapVarReplace.put("irisUniqueId", "irisId");
		mapVarReplace.put("oriCountry", "country");

	}

	/*
	 * private String getDataset() { return dataset; }
	 */

	private Set getDataset() {
		Set s = new HashSet();
		s.add(dataset);
		return s;
	}

	@GET
	@Path("/variety")
	@Produces("application/json")
	public Response getVarieties(
			@DefaultValue("0")  @QueryParam("offset") int offset,
			@DefaultValue("-1") @QueryParam("limit")  int limit)
			throws JSONException {

		try {
			List vars = variety.getVarietyNames(getDataset());
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200).entity(AppContext.replaceString(
					mapper.writeValueAsString(applyPage(vars, offset, limit)), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/variety/{id}")
	@GET
	@Produces("application/json")
	public Response getVarietiesById(@PathParam("id") long lId) throws JSONException {

		try {
			return Response.status(200).entity(AppContext.replaceString(new ObjectMapper()
					.writeValueAsString(variety.getMapId2Variety(dataset).get(BigDecimal.valueOf(lId))), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/variety/subpopulation/{subpop}")
	@GET
	@Produces("application/json")
	public Response getVarietiesBySubpopulation(@PathParam("subpop") String sSubpop) throws JSONException {

		try {
			return Response.status(200).entity(AppContext.replaceString(
					new ObjectMapper().writeValueAsString(variety.getGermplasmBySubpopulation(sSubpop, getDataset())),
					mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/variety/subpopulation")
	@GET
	@Produces("application/json")
	public Response getVarietiesSubpopulation() throws JSONException {

		try {
			return Response.status(200)
					.entity(new ObjectMapper().writeValueAsString(variety.getSubpopulations(getDataset()))).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/variety/country/{country}")
	@GET
	@Produces("application/json")
	public Response getVarietiesByCountry(@PathParam("country") String sCountry) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);
			return Response.status(200)
					.entity(AppContext.replaceString(
							new ObjectMapper().writeValueAsString(variety.getGermplasmByCountry(sCountry, s)),
							mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/variety/country")
	@GET
	@Produces("application/json")
	public Response getVarietiesCountry() throws JSONException {

		try {
			return Response.status(200).entity(new ObjectMapper().writeValueAsString(variety.getCountries(dataset)))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/variety/name")
	@GET
	@Produces("application/json")
	public Response getVarietiesNames() throws JSONException {

		try {
			return Response.status(200)
					.entity(new ObjectMapper().writeValueAsString(variety.getVarietyNames(getDataset()))).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/variety/namelike/{name}")
	@GET
	@Produces("application/json")
	public Response getVarietiesNameLike(@PathParam("name") String sName) throws JSONException {

		try {
			return Response.status(200).entity(
					new ObjectMapper().writeValueAsString(variety.getGermplasmByNameLike(sName + "%", getDataset())))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@GET
	@Path("/snp/list")
	@Produces("application/json")
	@ResponseBody
	public Response getGeneLists() throws JSONException {

		try {
			List listnames = new ArrayList();
			listnames.addAll(workspace.getSnpPositionListNames());
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(listnames), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@GET
	@Path("/gene/list/{name}")
	@Produces("application/json")
	@ResponseBody
	public Response getGeneInLists(@PathParam("name") String listname) throws JSONException {

		try {
			List listloc = new ArrayList();
			listloc.addAll(workspace.getSnpPositions("any", listname));
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(listloc), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/gettable")
	@GET
	@Produces({ "application/json", "text/plain", "text/csv" })
	public Response getVariantByVarietyId(@DefaultValue("9") @QueryParam("organismId") Integer organismId, @QueryParam("dataset") String dataset, @DefaultValue("all") @QueryParam("varid") String sVarids,
			@QueryParam("chr") String sChr, @QueryParam("start") Long lStart, @QueryParam("end") Long lEnd,
			@DefaultValue("true") @QueryParam("snp") boolean bSNP,
			@DefaultValue("false") @QueryParam("indel") boolean bIndel,
			@DefaultValue("false") @QueryParam("coreonly") boolean bCoreonly,
			@DefaultValue("false") @QueryParam("mismatchonly") boolean bMismatchonly,
			@QueryParam("poslist") String sSnppos, @QueryParam("subpopulation") String sSubpopulation,
			@QueryParam("locus") String sLocus, @DefaultValue("false") @QueryParam("alignindels") boolean bAlignIndels,
			@DefaultValue("-1") @QueryParam("varLimit") int varLimit,
			@DefaultValue("0") @QueryParam("varOffset") int varOffset,
			@DefaultValue("json") @QueryParam("format") String format)
			throws JSONException {

		// if(sChr==null) throw new JSONException("parameters chr is required");
		if (sChr == null && sLocus == null && sSnppos == null)
			throw new JSONException("parameters (chr,start,end) OR locus OR (chr AND poslist) is required");

		if ((sSnppos == null || sSnppos.isEmpty()) && (lStart == null || lEnd == null) && sLocus == null)
			throw new JSONException("parameters (start AND end) OR poslist OR locus is required");
		if (sSnppos != null && !sSnppos.isEmpty() && lStart != null && lEnd != null)
			throw new JSONException("only one of either (start AND end) OR poslist is required");

		// if(!sVarids.equals("all") && sSubpopulation==null) throw new
		// JSONException("parameter varid OR subpopulation is required");
		if (!sVarids.equals("all") && sSubpopulation != null)
			throw new JSONException("only one of either varid OR subpopulation is required");

		Collection<BigDecimal> colVarIds = null;
		if (!sVarids.equals("all")) {
			String vaidss[] = sVarids.split(",");
			colVarIds = new HashSet();
			for (int ivar = 0; ivar < vaidss.length; ivar++) {
				colVarIds.add(BigDecimal.valueOf(Long.valueOf(vaidss[ivar])));
			}
		}

		Collection<BigDecimal> colPos = null;
		if (sSnppos != null) {
			String pos[] = sSnppos.split(",");
			colPos = new HashSet();
			for (int ipos = 0; ipos < pos.length; ipos++) {
				colPos.add(BigDecimal.valueOf(Long.valueOf(pos[ipos])));
			}
		}

		AppContext.debug("params=" + colVarIds + " " + sChr + " " + lStart + " " + lEnd + " " + bSNP + " " + bIndel
				+ " " + bCoreonly + " " + bMismatchonly + " " + colPos + " " + sSubpopulation + " " + sLocus);

		try {
			VariantTable table = getVariantTable(colVarIds, sChr, lStart, lEnd, bSNP, bIndel, bCoreonly, bMismatchonly,
					colPos, sSubpopulation, sLocus, bAlignIndels, organismDAO.getOrganismByID(organismId), dataset);
			if (table == null)
				throw new JSONException("VariantTable is null");

			VariantTableArray tableArr = (VariantTableArray) table;
			int effectiveLimit = (varLimit <= 0) ? GENO_VAR_CAP : varLimit;
			int from = Math.max(0, varOffset);
			int numVar = (tableArr.getVarid() != null) ? tableArr.getVarid().length : 0;
			int to = Math.min(from + effectiveLimit, numVar);

			if ("tsv".equalsIgnoreCase(format)) {
				String tsv = buildTsv(tableArr, sVarids, from, to);
				return Response.ok(tsv, "text/plain").build();
			}
			if ("csv".equalsIgnoreCase(format)) {
				String csv = buildCsv(tableArr, dataset, from, to);
				return Response.ok(csv, "text/csv").build();
			}
			return Response.status(200).entity(new ObjectMapper().writeValueAsString(
					sliceVarieties(tableArr, varOffset, varLimit))).build();

		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/posttable")
	@POST
	@Produces("application/json")
	public Response postVariantByVarietyId(@FormParam("organismId") Integer organismId, @FormParam("dataset") String dataset, @FormParam("varid") List<Long> lVarids, @FormParam("chr") String sChr,
			@FormParam("start") Long lStart, @FormParam("end") Long lEnd,
			@DefaultValue("true") @FormParam("snp") boolean bSNP,
			@DefaultValue("false") @FormParam("indel") boolean bIndel,
			@DefaultValue("false") @FormParam("coreonly") boolean bCoreonly,
			@DefaultValue("false") @FormParam("mismatchonly") boolean bMismatchonly,
			@FormParam("poslist") List<Long> lSnppos, @FormParam("subpopulation") String sSubpopulation,
			@FormParam("locus") String sLocus, @DefaultValue("false") @FormParam("alignindel") boolean bAlignIndels,
			@FormParam("varLimit") Integer varLimit,
			@FormParam("varOffset") Integer varOffset)
			throws JSONException {

		if (sChr == null && sLocus == null && lSnppos.isEmpty())
			throw new JSONException("parameters (chr,start,end) OR locus OR (chr AND poslist) is required");

		if (lSnppos.isEmpty() && (lStart == null || lEnd == null) && sLocus == null)
			throw new JSONException("parameters (start AND end) OR poslist is required");
		if (!lSnppos.isEmpty() && lStart != null && lEnd != null)
			throw new JSONException("only one of either (start AND end) OR poslist is required");

		// if(lVarids.isEmpty() && sSubpopulation==null) throw new
		// JSONException("parameter varid OR subpopulation is required");
		if (!lVarids.isEmpty() && sSubpopulation != null)
			throw new JSONException("only one of either varid OR subpopulation is required");

		Collection<BigDecimal> colVarIds = null;
		if (lVarids != null && !lVarids.isEmpty()) {
			colVarIds = new HashSet<>();
			for (Long id : lVarids) colVarIds.add(BigDecimal.valueOf(id));
		}

		Organism organism = organismDAO.getOrganismByID(organismId);

		Collection<BigDecimal> colPos = null;
		if (lSnppos != null && !lSnppos.isEmpty()) {
			colPos = new HashSet<>();
			for (Long pos : lSnppos) colPos.add(BigDecimal.valueOf(pos));
		}

		AppContext.debug("params=" + colVarIds + " " + sChr + " " + lStart + " " + lEnd + " " + bSNP + " " + bIndel
				+ " " + bCoreonly + " " + bMismatchonly + " " + colPos + " " + sSubpopulation + " " + sLocus);

		try {
			VariantTable table = getVariantTable(colVarIds, sChr, lStart, lEnd, bSNP, bIndel, bCoreonly, bMismatchonly,
					colPos, sSubpopulation, sLocus, bAlignIndels, organism, dataset);

			if (table == null)
				throw new JSONException("VariantTable is null");

			int effectiveVarLimit = (varLimit == null) ? -1 : varLimit;
			int effectiveVarOffset = (varOffset == null) ? 0 : varOffset;
			return Response.status(200).entity(new ObjectMapper().writeValueAsString(
					sliceVarieties((VariantTableArray) table, effectiveVarOffset, effectiveVarLimit))).build();

		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	private Map<String, Object> sliceVarieties(VariantTableArray table, int varOffset, int varLimit) {
		Long[] allVarid = table.getVarid();
		int total = (allVarid == null) ? 0 : allVarid.length;
		int effectiveLimit = (varLimit <= 0) ? GENO_VAR_CAP : varLimit;
		int from = Math.max(0, varOffset);
		int to = Math.min(from + effectiveLimit, total);

		Map<String, Object> tile = new LinkedHashMap<>();
		tile.put("message", table.getMessage());
		tile.put("position", table.getPosition());
		tile.put("reference", table.getReference());
		tile.put("contigs", table.getContigs());
		tile.put("varOffset", from);
		tile.put("varTotal", total);

		if (from >= total || total == 0) {
			tile.put("varid", new Long[0]);
			tile.put("varname", new String[0]);
			tile.put("varmismatch", new Double[0]);
			tile.put("varalleles", new Object[0][]);
		} else {
			tile.put("varid", Arrays.copyOfRange(allVarid, from, to));
			tile.put("varname", Arrays.copyOfRange(table.getVarname(), from, to));
			tile.put("varmismatch", Arrays.copyOfRange(table.getVarmismatch(), from, to));
			tile.put("varalleles", Arrays.copyOfRange(table.getVaralleles(), from, to));
		}
		return tile;
	}

	private List applyPage(List list, int offset, int limit) {
		int total = list.size();
		int effectiveLimit = (limit <= 0) ? GENO_VAR_CAP : limit;
		int from = Math.max(0, offset);
		if (from >= total) return new ArrayList();
		int to = Math.min(from + effectiveLimit, total);
		return list.subList(from, to);
	}

	private String buildTsv(VariantTable table, String sVarids, int from, int to) {
		VariantTableArray arr = (VariantTableArray) table;
		Long[]     varids    = arr.getVarid();      // [variety_idx]
		String[]   varnames  = arr.getVarname();    // [variety_idx]
		Position[] positions = arr.getPosition();   // [pos_idx]
		Object[][] alleles   = arr.getVaralleles(); // [variety_idx][pos_idx]

		int numPositions = (positions != null) ? positions.length : 0;

		String chr = (positions != null && numPositions > 0 && positions[0] != null)
				? positions[0].getContig() : "";

		StringBuilder sb = new StringBuilder();
		sb.append("VarietyName\tChr");
		for (int p = 0; p < numPositions; p++)
			sb.append('\t').append(positions[p] != null ? positions[p].getPosition() : "");
		sb.append('\n');

		for (int v = from; v < to; v++) {
			String label = (varnames != null && v < varnames.length && varnames[v] != null)
					? varnames[v] : String.valueOf(varids[v]);
			sb.append(label).append('\t').append(chr);
			for (int p = 0; p < numPositions; p++) {
				Object a = (alleles != null && v < alleles.length && alleles[v] != null && p < alleles[v].length)
						? alleles[v][p] : null;
				sb.append('\t').append(a != null ? a : ".");
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private String buildCsv(VariantTable table, String dataset, int from, int to) {
		VariantTableArray arr = (VariantTableArray) table;
		Long[]     varids    = arr.getVarid();      // [variety_idx]
		String[]   varnames  = arr.getVarname();    // [variety_idx]
		Position[] positions = arr.getPosition();   // [pos_idx]
		Object[][] alleles   = arr.getVaralleles(); // [variety_idx][pos_idx]
		Double[]   mismatch  = arr.getVarmismatch(); // [variety_idx]
		String[]   reference = arr.getReference();  // [pos_idx]

		int numPositions = (positions != null) ? positions.length : 0;

		lisitemdao = (ListItemsDAO) AppContext.checkBean(lisitemdao, "ListItems");
		Map<BigDecimal, StockSample> sampleMap = (lisitemdao != null && dataset != null)
				? lisitemdao.getMapId2Sample(dataset) : null;

		StringBuilder sb = new StringBuilder();

		// Row 1: header with chromosomal positions
		sb.append("JAPONICA NIPPONBARE POSITIONS,ASSAY ID,ACCESSION,SUBPOPULATION,MISMATCH");
		for (int p = 0; p < numPositions; p++)
			sb.append(',').append(positions[p] != null ? positions[p].getPosition() : "");
		sb.append('\n');

		// Row 2: reference alleles
		sb.append("JAPONICA NIPPONBARE ALLELES,,,,");
		for (int p = 0; p < numPositions; p++)
			sb.append(',').append(reference != null && p < reference.length && reference[p] != null ? reference[p] : "");
		sb.append('\n');

		// Data rows: one per variety
		for (int v = from; v < to; v++) {
			String varname = (varnames != null && v < varnames.length && varnames[v] != null) ? varnames[v] : "";
			String assay = "";
			String accession = "";
			String subpop = "";
			if (sampleMap != null && varids != null && v < varids.length) {
				StockSample ss = sampleMap.get(BigDecimal.valueOf(varids[v]));
				if (ss != null) {
					if (ss.getAssay() != null) assay = ss.getAssay();
					if (ss.getAccession() != null) accession = ss.getAccession();
					if (ss.getSubpopulation() != null) subpop = ss.getSubpopulation();
				}
			}
			double miss = (mismatch != null && v < mismatch.length && mismatch[v] != null) ? mismatch[v] : 0.0;

			sb.append(csvQuote(varname));
			sb.append(',').append(csvQuote(assay));
			sb.append(',').append(csvQuote(accession));
			sb.append(',').append(csvQuote(subpop));
			sb.append(',').append(miss);
			for (int p = 0; p < numPositions; p++) {
				Object a = (alleles != null && v < alleles.length && alleles[v] != null && p < alleles[v].length)
						? alleles[v][p] : null;
				sb.append(',').append(a != null ? a : "");
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	private String csvQuote(String value) {
		if (value == null || value.isEmpty()) return "";
		if (value.contains(",") || value.contains("\"") || value.contains("\n"))
			return "\"" + value.replace("\"", "\"\"") + "\"";
		return value;
	}

	private VariantTable getVariantTable(Collection colVarIds, String sChr, Long lStart, Long lEnd, boolean bSNP,
			boolean bIndel, boolean bCoreonly, boolean bMismatchonly, Collection poslist, String sSubpopulation,
			String sLocus, boolean bAlignIndels, Organism organism, String dataset) throws Exception {

		if (organism == null)
			throw new Exception("No organism found for the given organismId");

		boolean showAllRefsAllele = false;
		// GenotypeQueryParams params = new GenotypeQueryParams(colVarIds,sChr, lStart,
		// lEnd, bSNP, bIndel,
		// (bCoreonly?GenotypeQueryParams.SNP_CORE:GenotypeQueryParams.SNP_FILTERED) ,
		// bMismatchonly, poslist, sSubpopulation, sLocus, bAlignIndels,
		// showAllRefsAllele );

		Set sVS = new HashSet();
		if (bCoreonly)
			sVS.add("3kcore");
		else
			sVS.add("3kfiltered");
		Set sVar = new HashSet();
		sVar.add(dataset);
		Set sRun = new HashSet();
//		sRun.add("3kfiltered");

		genotyperundao = (VGenotypeRunDAO) AppContext.checkBean(genotyperundao, "GenotypeRunPlatformDAO");
		Set<VGenotypeRun> vrun = genotyperundao.findVGenotypeRunByVariantset(dataset);
		VGenotypeRun first = vrun.stream().findFirst().orElse(null);
		if (first == null)
			throw new Exception("No genotype run found for dataset: " + dataset);
		sRun.add(first);

		
		
		GenotypeQueryParams params = new GenotypeQueryParams(colVarIds, sChr, lStart, lEnd, bSNP, bIndel, sVS, sVar,
				sRun, bMismatchonly, poslist, sSubpopulation, sLocus, bAlignIndels, showAllRefsAllele, organism);

		params.setDataset(dataset);
		VariantTableArray varianttable = new VariantAlignmentTableArraysImpl();

		AppContext.logQuery("WS " + params.toString());

		VariantStringData queryRawResult = genotype.queryGenotype(params);
		varianttable = new VariantTableWS(
				(VariantTableArray) genotype.fillGenotypeTable(varianttable, queryRawResult, params));

		return varianttable;

	}

	/*
	 * @POST
	 * 
	 * @Path("/sendemail")
	 * 
	 * @Produces(MediaType.TEXT_PLAIN) public Response sendEmail(@FormParam("email")
	 * String email) { AppContext.debug(email); return Response.ok("email=" +
	 * email).build(); }
	 */

	// The resource look like this
	/*
	 * @Path("/variants")
	 * 
	 * @POST
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON) public void setJsonl(String array){
	 * JSONArray o = new JSONArray(last_data); AppContext.debug(o.toString()); }
	 */

}