package org.irri.test;

import java.util.Date;

/**
 * Model class representing genotype data
 */
public class GenotypeData {
    
    private Long id;
    private String sampleId;
    private String marker;
    private String allele1;
    private String allele2;
    private String genotype;
    private int qualityScore;
    private Date callDate;
    private String lociId;
    
    // Constructors
    public GenotypeData() {
    }
    
    public GenotypeData(String sampleId, String marker, String allele1, String allele2) {
        this.sampleId = sampleId;
        this.marker = marker;
        this.allele1 = allele1;
        this.allele2 = allele2;
        this.genotype = allele1 + "/" + allele2;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSampleId() {
        return sampleId;
    }
    
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }
    
    public String getMarker() {
        return marker;
    }
    
    public void setMarker(String marker) {
        this.marker = marker;
    }
    
    public String getAllele1() {
        return allele1;
    }
    
    public void setAllele1(String allele1) {
        this.allele1 = allele1;
    }
    
    public String getAllele2() {
        return allele2;
    }
    
    public void setAllele2(String allele2) {
        this.allele2 = allele2;
    }
    
    public String getGenotype() {
        return genotype;
    }
    
    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }
    
    public int getQualityScore() {
        return qualityScore;
    }
    
    public void setQualityScore(int qualityScore) {
        this.qualityScore = qualityScore;
    }
    
    public Date getCallDate() {
        return callDate;
    }
    
    public void setCallDate(Date callDate) {
        this.callDate = callDate;
    }
    
    public String getLociId() {
        return lociId;
    }
    
    public void setLociId(String lociId) {
        this.lociId = lociId;
    }
    
    @Override
    public String toString() {
        return "GenotypeData{" +
                "sampleId='" + sampleId + '\'' +
                ", marker='" + marker + '\'' +
                ", genotype='" + genotype + '\'' +
                '}';
    }
}