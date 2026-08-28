package org.irri.iric.portal.ws.rest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.irri.iric.ds.chado.domain.Phenotype;
//import org.codehaus.jackson.map.ObjectMapper;
//import org.codehaus.jettison.json.JSONException;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller("VarietyWebService")
@Path("/variety")
public class VarietyWS {

	@Autowired
	private VarietyFacade variety;
	private Map mapVarReplace;
	private String dataset = VarietyFacade.DATASET_SNPINDELV2_IUPAC;

	public VarietyWS() {
		super();
		
		variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
		mapVarReplace = new HashMap();

		AppContext.debug("VarietyWS started");

		// rename some variables
		mapVarReplace.put("iricStockId", "varietyId");
		mapVarReplace.put("irisUniqueId", "irisId");
		mapVarReplace.put("oriCountry", "country");
	}

	private static final int VARIETY_LIST_CAP = 25000;
	private static final int PHENOTYPE_LIST_CAP = 500;

	private List applyPage(java.util.Collection col, int limit, int offset, int cap) {
		List list = new ArrayList(col);
		int effectiveLimit = (limit <= 0) ? cap : Math.min(limit, cap);
		int from = Math.max(0, offset);
		if (from >= list.size()) return new ArrayList();
		int to = Math.min(from + effectiveLimit, list.size());
		return list.subList(from, to);
	}

	@GET
	@Path("/")
	@Produces("application/json")
	@ResponseBody
	public Response getVarieties(@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			Set vars = variety.getGermplasm(dataset);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200).entity(AppContext.replaceString(mapper.writeValueAsString(applyPage(vars, limit, offset, VARIETY_LIST_CAP)), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@GET
	@Path("/count")
	@Produces("application/json")
	public Response getVarietyCount() throws JSONException {
		try {
			int count = variety.getGermplasm(dataset).size();
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200).entity(mapper.writeValueAsString(
					java.util.Collections.singletonMap("count", count))).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/{id}")
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

	@Path("/subpopulation/{subpop}")
	@GET
	@Produces("application/json")
	public Response getVarietiesBySubpopulation(@PathParam("subpop") String sSubpop,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			return Response.status(200)
					.entity(AppContext.replaceString(new ObjectMapper()
							.writeValueAsString(applyPage(variety.getGermplasmBySubpopulation(sSubpop, dataset), limit, offset, VARIETY_LIST_CAP)), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@Path("/subpopulation")
	@GET
	@Produces("application/json")
	public Response getVarietiesSubpopulation() throws JSONException {

		try {
			return Response.status(200)
					.entity(new ObjectMapper().writeValueAsString(variety.getSubpopulations(dataset))).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/country/{country}")
	@GET
	@Produces("application/json")
	public Response getVarietiesByCountry(@PathParam("country") String sCountry,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			return Response.status(200)
					.entity(AppContext.replaceString(
							new ObjectMapper().writeValueAsString(applyPage(variety.getGermplasmByCountry(sCountry, s), limit, offset, VARIETY_LIST_CAP)),
							mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/country")
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

	@Path("/name")
	@GET
	@Produces("application/json")
	public Response getVarietiesNames(@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);
			return Response.status(200).entity(new ObjectMapper().writeValueAsString(applyPage(variety.getVarietyNames(s), limit, offset, VARIETY_LIST_CAP)))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@Path("/namelike/{name}")
	@GET
	@Produces("application/json")
	public Response getVarietiesNameLike(@PathParam("name") String sName,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			return Response.status(200)
					.entity(new ObjectMapper().writeValueAsString(applyPage(variety.getGermplasmByNameLike(sName + "%", s), limit, offset, VARIETY_LIST_CAP)))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}

	}

	@GET
	@Path("/phenotypes")
	@Produces("application/json")
	public Response getPhenotypes() throws JSONException {

		try {
			Map pnenotypeId = variety.getPhenotypeDefinitions(dataset);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(pnenotypeId), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/COterms/trait")
	@Produces("application/json")
	public Response getCoTerms() throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			Map<String, BigDecimal> coMap = variety.getTraits(s, false);

			Map<String, String> finalList = new HashMap<>();
			for (Map.Entry<String, BigDecimal> entry : coMap.entrySet()) {
				String[] phenotype = entry.getKey().split("::");
				finalList.put(phenotype[0], phenotype[1]);
			}

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(finalList), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/phenotypes/{phenid}")
	@Produces("application/json")
	public Response getPhenotypes4AllVarieties(@PathParam("phenid") String sPhenId,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			List vars = variety.getVarietyByPhenotype(sPhenId, dataset);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200).entity(AppContext.replaceString(mapper.writeValueAsString(applyPage(vars, limit, offset, PHENOTYPE_LIST_CAP)), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/COterms/trait/{coTerm}")
	@Produces("application/json")
	public Response getCOterms4AllVarieties(@PathParam("coTerm") String coTerm,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			BigDecimal sPhenId = variety.getPhenotypeId(coTerm, dataset);
			List vars = variety.getVarietyByPhenotype(sPhenId.toString(), dataset);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200).entity(AppContext.replaceString(mapper.writeValueAsString(applyPage(vars, limit, offset, PHENOTYPE_LIST_CAP)), mapVarReplace))
					.build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/all/phenotypes/{phenid}")
	@Produces("application/json")
	public Response getVarietyPhenotype(@PathParam("phenid") String sPhenId,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		try {
			List pnenotypes = variety.getPhenotypesByGermplasm(sPhenId, dataset);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(applyPage(pnenotypes, limit, offset, PHENOTYPE_LIST_CAP)), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/all/COterms/trait/{coTerm}")
	@Produces("application/json")
	public Response getVarietyCOterm(@PathParam("coTerm") String coTerm,
			@DefaultValue("-1") @QueryParam("limit") int limit,
			@DefaultValue("0") @QueryParam("offset") int offset) throws JSONException {

		List pnenotypes;

		try {
			BigDecimal sPhenId = variety.getPhenotypeId(coTerm, dataset);

			if (sPhenId != null && !sPhenId.toString().equals(""))
				pnenotypes = variety.getPhenotypesByGermplasm(sPhenId.toString(), dataset);
			else
				pnenotypes = new ArrayList<>();

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(applyPage(pnenotypes, limit, offset, PHENOTYPE_LIST_CAP)), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/{varid}/phenotypes/{phenid}")
	@Produces("application/json")
	public Response getVarietyPhenotypes(@PathParam("varid") String sVarId, @PathParam("phenid") String sPhenId)
			throws JSONException {

		try {
			Phenotype pnenotypes = variety.getPhenotypesByGermplasm(
					variety.getMapId2Variety(dataset).get(BigDecimal.valueOf(Long.valueOf(sVarId))), dataset, sPhenId);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(pnenotypes), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/{varid}/COterms/trait/{coTerm}")
	@Produces("application/json")
	public Response getVarietyCOterms(@PathParam("varid") String sVarId, @PathParam("coTerm") String coTerm)
			throws JSONException {

		try {

			Phenotype pnenotypes;

			BigDecimal phenId = variety.getPhenotypeId(coTerm, dataset);

			if (phenId != null && !phenId.toString().equals(""))
				pnenotypes = variety.getPhenotypesByGermplasm(
						variety.getMapId2Variety(dataset).get(BigDecimal.valueOf(Long.valueOf(sVarId))), dataset,
						phenId.toString());
			else
				pnenotypes = null;

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(pnenotypes), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/{varid}/phenotypes")
	@Produces("application/json")
	public Response getVarietyPhenotypes(@PathParam("varid") String sVarId) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			List pnenotypes = variety.getPhenotypesByGermplasm(
					variety.getMapId2Variety(dataset).get(BigDecimal.valueOf(Long.valueOf(sVarId))), s);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(pnenotypes), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/{varid}/COterms")
	@Produces("application/json")
	public Response getVarietyCOterms(@PathParam("varid") String sVarId) throws JSONException {

		try {
			Set s = new HashSet();
			s.add(dataset);

			List pnenotypes = variety.getPhenotypesByGermplasm(
					variety.getMapId2Variety(dataset).get(BigDecimal.valueOf(Long.valueOf(sVarId))), s);

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(pnenotypes), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/passports")
	@Produces("application/json")
	public Response getPassports() throws JSONException {

		try {
			Map passportId = variety.getPassportDefinitions(dataset);
			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(passportId), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	@GET
	@Path("/{varid}/passports")
	@Produces("application/json")
	public Response getVarietyPassports(@PathParam("varid") String sVarId) throws JSONException {

		try {
			// List passports = variety.getPhenotypesByGermplasm(
			// variety.getMapId2Variety().get( BigDecimal.valueOf(Long.valueOf(sVarId)) ) );
			List passports = new ArrayList();
			Set s = new HashSet();
			s.add(dataset);

			passports.addAll(variety.getPassportByVarietyid(BigDecimal.valueOf(Long.valueOf(sVarId))));

			ObjectMapper mapper = new ObjectMapper();
			return Response.status(200)
					.entity(AppContext.replaceString(mapper.writeValueAsString(passports), mapVarReplace)).build();
		} catch (Exception ex) {
			throw new JSONException(ex);
		}
	}

	// @GET
	// @Path("/passports/{passid}")
	// @Produces("application/json")
	// public Response getPassport4AllVarieties(@PathParam("passid") String sPassId)
	// throws JSONException {
	//
	// try {
	// List vars = variety.getVarietyByPassport( sPassId);
	//
	// ObjectMapper mapper = new ObjectMapper();
	// return Response.status(200).entity(
	// AppContext.replaceString(mapper.writeValueAsString(vars), mapVarReplace
	// )).build();
	// } catch(Exception ex)
	// {
	// throw new JSONException(ex);
	// }
	// }

}
