package org.irri.scripts;

import java.util.HashSet;
import java.util.Set;

import org.irri.iric.ds.chado.dao.VarietyDAO;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunGetSNPS {

	@Autowired
	@Qualifier("VarietyDAO")
	private static VarietyDAO germdao;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext-business.xml");
		
		String datasetValue = "gq92";
		String snpseValue = "3kfiltered";
		
		Set dataset = new HashSet<String>();
		Set snpset = new HashSet<String>();
		
//		dataset.add(args[0]);di 
		dataset.add(datasetValue);
		
		snpset.add(snpseValue);
		
//		String contig = args[1];
		String contig = "CHR12";
		
//		Set dataset = new Set
//		Set selDataset = getDataset();
//		Set runs = new HashSet(getGenotyperun());
//		if (checkboxIndel.isChecked()) {
//			// add indel runs with the same dataset
//			for (GenotypeRunPlatform p : genotype.getGenotyperuns("indel")) {
//				if (selDataset.contains(p.getDataset())) {
//					runs.add(p);
//				}
//			}
//		}
//		
//		GenotypeQueryParams params = new GenotypeQueryParams(null, contig, lStart, lStop,
//				checkboxSNP.isChecked(), checkboxIndel.isChecked(), getVariantset(), getDataset(), runs,
//				checkboxMismatchOnly.isChecked(), snpposlist, sSubpopulation, sLocus, checkboxAlignIndels.isChecked(),
//				checkboxShowAllRefAlleles.isChecked());

		
		
		
		
		
		
		DownloadListBox bx = new DownloadListBox(contig, dataset, snpset);
		
		try {
					
			
			bx.downloadBigListBox(datasetValue.trim()+"-"+ contig, contig);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
