package org.irri.test;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

public class GenelociViewModel {

	private List<GenelociData> genelociResults = new ArrayList<>();
	private ListModelList<GenelociData> genelociData = new ListModelList<>();

	private GenelociData selectedLoci;
	private String searchTerm = "";
	private boolean hasGenelociResults = false;
	private GenotypeResultViewModel genotypeResultVM;

	@WireVariable
	private GenelociService genelociService;

	@Init
	public void init() {
		System.out.println("=== ViewModel Init ===");
		genelociResults = new ArrayList<>();
		genotypeResultVM = new GenotypeResultViewModel();

		if (genelociService == null) {
			genelociService = new GenelociService();
			System.out.println("Using mock GenelociService");
		}
	}

	@Command
	@NotifyChange({ "genelociData", "hasGenelociResults", "selectedLoci", "resultSize" })
	public void searchGeneloci() {
		System.out.println("=== SEARCH CALLED ===");

		List<GenelociData> results = genelociService.searchLoci(searchTerm);

		genelociData.clear();
		genelociData.addAll(results);
		
		System.out.println("Model now has: " + genelociData.size() + " items");

		hasGenelociResults = !genelociData.isEmpty();
		selectedLoci = null;
	}

	@Command
	@NotifyChange({"selectedLoci", "genotypeResultVM"})
	public void selectLociAndSearch(@BindingParam("lociId") String lociId) {
		System.out.println("=== SELECT LOCI CALLED ===");
		System.out.println("Loci ID: " + lociId);
		
		// Find the full data object from genelociData (not genelociResults)
		for (GenelociData data : genelociData) {
			if (data != null && data.getLociId().equals(lociId)) {
				selectedLoci = data;
				genotypeResultVM.performSearch(data.getLociId(), data.getLociName());
				break;
			}
		}
	}

	@Command
	@NotifyChange({ "hasGenelociResults", "searchTerm", "selectedLoci", "resultSize", "genelociData" })
	public void clearSearch() {
		genelociData.clear();
		searchTerm = "";
		hasGenelociResults = false;
		selectedLoci = null;
		genotypeResultVM.clearResults();
	}

	@Command
	@NotifyChange("genotypeResultVM")
	public void clearGenotypeResults() {
		genotypeResultVM.clearResults();
	}

	public GenelociData getSelectedLoci() {
		return selectedLoci;
	}

	public void setSelectedLoci(GenelociData selectedLoci) {
		this.selectedLoci = selectedLoci;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public boolean isHasGenelociResults() {
		return hasGenelociResults;
	}

	public GenotypeResultViewModel getGenotypeResultVM() {
		return genotypeResultVM;
	}

	public int getResultSize() {
		return genelociData != null ? genelociData.size() : 0;
	}

	public ListModelList<GenelociData> getGenelociData() {
		return genelociData;
	}
}