package your.package.viewmodel;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import your.package.model.GenelociData;
import your.package.service.GenelociService;

import java.util.ArrayList;
import java.util.List;

public class GenelociViewModel {
    
    private ListModelList<GenelociData> genelociResults;
    private GenelociData selectedLoci;
    private String searchTerm = "";
    private boolean hasGenelociResults = false;
    
    // This is the key: instance of the reusable genotype ViewModel
    private GenotypeResultViewModel genotypeResultVM;
    
    @WireVariable
    private GenelociService genelociService;
    
    @Init
    public void init() {
        genelociResults = new ListModelList<>();
        genotypeResultVM = new GenotypeResultViewModel();
        
        if (genelociService == null) {
            genelociService = new GenelociService();
        }
    }
    
    /**
     * Search for gene loci based on search term
     */
    @Command
    @NotifyChange({"genelociResults", "hasGenelociResults", "selectedLoci"})
    public void searchGeneloci() {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return;
        }
        
        List<GenelociData> results = genelociService.searchLoci(searchTerm);
        genelociResults.clear();
        genelociResults.addAll(results);
        hasGenelociResults = !genelociResults.isEmpty();
        selectedLoci = null;
    }
    
    /**
     * This is the key method: when user clicks a loci row,
     * it triggers the genotype search in the reusable component
     */
    @Command
    @NotifyChange({"selectedLoci", "genotypeResultVM"})
    public void selectLociAndSearch(@BindingParam("loci") GenelociData loci) {
        selectedLoci = loci;
        
        // Call the reusable component's search method
        genotypeResultVM.performSearch(loci.getLociId(), loci.getLociName());
    }
    
    @Command
    @NotifyChange({"genelociResults", "hasGenelociResults", "searchTerm", "selectedLoci"})
    public void clearSearch() {
        genelociResults.clear();
        searchTerm = "";
        hasGenelociResults = false;
        selectedLoci = null;
        genotypeResultVM.clearResults();
    }
    
    // Getters and Setters
    public ListModelList<GenelociData> getGenelociResults() {
        return genelociResults;
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
    
    /**
     * CRITICAL: This getter allows the ZUL to reference the genotype ViewModel
     */
    public GenotypeResultViewModel getGenotypeResultVM() {
        return genotypeResultVM;
    }
}

// Sample Service and Model classes
class GenelociService {
    public List<GenelociData> searchLoci(String searchTerm) {
        // Mock data - replace with actual database query
        List<GenelociData> mockData = new ArrayList<>();
        
        String[] chromosomes = {"Chr1", "Chr2", "Chr3", "Chr4", "Chr5"};
        String[] types = {"SNP", "Indel", "CNV", "STR"};
        
        for (int i = 1; i <= 10; i++) {
            GenelociData data = new GenelociData();
            data.setLociId("LOCI_" + String.format("%04d", i));
            data.setLociName("Gene" + i + "_Marker");
            data.setChromosome(chromosomes[i % chromosomes.length]);
            data.setPosition(String.valueOf(1000000 + (i * 150000)));
            data.setType(types[i % types.length]);
            data.setDescription("Description for gene loci marker " + i);
            mockData.add(data);
        }
        
        return mockData;
    }
}

class GenelociData {
    private String lociId;
    private String lociName;
    private String chromosome;
    private String position;
    private String type;
    private String description;
    
    // Getters and Setters
    public String getLociId() { return lociId; }
    public void setLociId(String lociId) { this.lociId = lociId; }
    
    public String getLociName() { return lociName; }
    public void setLociName(String lociName) { this.lociName = lociName; }
    
    public String getChromosome() { return chromosome; }
    public void setChromosome(String chromosome) { this.chromosome = chromosome; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}