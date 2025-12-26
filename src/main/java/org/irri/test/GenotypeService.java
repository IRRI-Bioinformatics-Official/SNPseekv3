package org.irri.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GenotypeService {
    
    public List<GenotypeData> searchByLoci(String lociId) {
        // Simulate database query
        // In real app: return dao.findByLociId(lociId);
        
        List<GenotypeData> mockData = new ArrayList<>();
        String[] samples = {"SAMPLE001", "SAMPLE002", "SAMPLE003", "SAMPLE004", "SAMPLE005"};
        String[] markers = {"SNP_RS123", "SNP_RS456", "SNP_RS789"};
        String[] alleles = {"A", "T", "G", "C"};
        
        for (int i = 0; i < 15; i++) {
            GenotypeData data = new GenotypeData();
            data.setSampleId(samples[i % samples.length]);
            data.setMarker(markers[i % markers.length]);
            data.setAllele1(alleles[i % alleles.length]);
            data.setAllele2(alleles[(i + 1) % alleles.length]);
            data.setGenotype(data.getAllele1() + "/" + data.getAllele2());
            data.setQualityScore(75 + (i * 3) % 25);
            data.setCallDate(new Date());
            mockData.add(data);
        }
        
        return mockData;
    }
}