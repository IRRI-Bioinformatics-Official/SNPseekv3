package org.irri.test;

public class GenelociData {

	private String lociId;
	private String lociName;
	private String chromosome;
	private String position;
	private String type;
	private String description;

	public String getLociId() {
		return lociId;
	}

	public String getLociName() {
		return lociName;
	}

	public String getChromosome() {
		return chromosome;
	}

	public String getPosition() {
		return position;
	}

	public String getType() {
		return type;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "GenelociData{lociId='" + lociId + "', lociName='" + lociName + "'}";
	}

	public void setLociId(String lociId) {
		this.lociId = lociId;
		
	}

	public void setLociName(String lociName) {
		this.lociName = lociName;
		
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
		
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setType(String type) {
		this.type = type;
		
	}

	public void setDescription(String description) {
		this.description = description;
	}

}