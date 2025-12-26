package org.irri.test;


import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.irri.test.ListMatrixModel;
import org.irri.test.GeneLociRenderer;

import java.util.ArrayList;
import java.util.List;

public class GenotypeResultViewModel {
    
    private ListMatrixModel<GenotypeData> genotypeResults;
    private boolean hasResults = false;
    private boolean loading = false;
    private String searchCriteria = "";
    private int totalResults = 0;
    private GenotypeRenderer renderer;
    
    @WireVariable
    private GenotypeService genotypeService;
    
    public GenotypeResultViewModel() {
        genotypeResults = new ListMatrixModel<>(new ArrayList<>());
        renderer = new GenotypeRenderer();
        
        if (genotypeService == null) {
            genotypeService = new GenotypeService();
        }
    }
    
    @Command
    @NotifyChange({"genotypeResults", "hasResults", "loading", "totalResults", "searchCriteria"})
    public void performSearch(@BindingParam("lociId") String lociId, 
                             @BindingParam("lociName") String lociName) {
        try {
            loading = true;
            searchCriteria = lociName != null ? lociName : lociId;
            
            List<GenotypeData> results = genotypeService.searchByLoci(lociId);
            
            genotypeResults.clear();
            genotypeResults.addAll(results);
            
            totalResults = results.size();
            hasResults = totalResults > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            genotypeResults.clear();
            hasResults = false;
            totalResults = 0;
        } finally {
            loading = false;
        }
    }
    
    @Command
    @NotifyChange({"genotypeResults", "hasResults", "totalResults", "searchCriteria"})
    public void clearResults() {
        genotypeResults.clear();
        hasResults = false;
        totalResults = 0;
        searchCriteria = "";
    }
    
    // Getters
    public ListMatrixModel<GenotypeData> getGenotypeResults() {
        return genotypeResults;
    }
    
    public boolean isHasResults() {
        return hasResults;
    }
    
    public boolean isLoading() {
        return loading;
    }
    
    public String getSearchCriteria() {
        return searchCriteria;
    }
    
    public int getTotalResults() {
        return totalResults;
    }
    
    public GenotypeRenderer getRenderer() {
        return renderer;
    }
    
    public List<GenotypeData> getGenotypeData() {
        if (genotypeResults != null) {
            return genotypeResults.getData();
        }
        return new ArrayList<>();
    }
}