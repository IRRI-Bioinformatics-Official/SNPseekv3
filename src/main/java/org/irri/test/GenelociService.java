package org.irri.test;

import java.util.ArrayList;
import java.util.List;

public class GenelociService {
    
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