package org.irri.iric.portal.domain;

import java.util.Set;

import org.irri.iric.ds.chado.domain.Locus;

/**
 * Merged (Overlapping MSU7, RAP, FGenesh) locus interface
 * 
 * @author LMansueto
 *
 */
public interface MergedLoci extends Locus {

	/**
	 * IRIC defined name for merged MSU7,RAP,Fgenesh loci
	 * 
	 * @return
	 */
	public String getIRICName();

	/**
	 * MSU7 locus name
	 * 
	 * @return
	 */
	public String getMSU7Name();

	/**
	 * RAP predicted locus name
	 * 
	 * @return
	 */
	public String getRAPPredName();

	/**
	 * RAP representative locus name
	 * 
	 * @return
	 */
	public String getRAPRepName();

	/**
	 * FGenesh++ locus name
	 * 
	 * @return
	 */
	public String getFGeneshName();

	/**
	 * 
	 * @return
	 */
	public Set<String> getOverlappingGenes();

}
