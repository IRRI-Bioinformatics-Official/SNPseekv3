package org.irri.iric.portal.genotype.zkui;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpSession;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.irri.iric.ds.chado.domain.Organism;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.zkoss.chart.Charts;
import org.zkoss.chart.PlotLine;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.Theme;
import org.zkoss.chart.XAxis;
import org.zkoss.chart.YAxis;
import org.zkoss.chart.model.BoxPlotModel;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultBoxPlotModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@Controller("ExtTabController")
public class ExtTabController extends SelectorComposer<Window> {

	

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;
	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade varietyfacade;

	@Wire
	private Div divHaplotype;
	@Wire
	private Div divMDS;
	@Wire
	private Div divJbrowse;
	@Wire
	private Div divVistaRev;
	@Wire
	private Div divVista;
	@Wire
	private Div divSnpeff;
	@Wire
	private Listbox listboxSnpeff;
	
	@Wire
	private Div divPhylo;
	@Wire
	private Iframe iframeJbrowse;
	
	@Wire
	private Charts chartMDS;
	@Wire
	private Listbox listboxHighlightVariety;
	
	
	@Wire
	private Tab tabVista;
	@Wire
	private Tabpanel tabpanelVista;
	@Wire
	private Tab tabVista1;
	@Wire
	private Tab tabVista2;
	@Wire
	private Tab tabVista3;
	@Wire
	private Tab tabVista4;

	@Wire
	private Iframe iframeVista1;
	@Wire
	private Iframe iframeVista2;
	@Wire
	private Iframe iframeVista3;
	@Wire
	private Iframe iframeVista4;
	private String vistaurl1;
	private String vistaurl2;
	private String vistaurl3;
	private String vistaurl4;

	@Wire
	private Iframe iframeHaplotype;
	@Wire
	private Tab tabHaplotype;
	@Wire
	private Button buttonHaplotypelog;
	@Wire
	private Listbox listboxHaploResolution;
	@Wire
	private Label labelGlobal;
	@Wire
	private Label labelLocal;
	@Wire
	private Slider sliderClusterWieght;
	@Wire
	private Button buttonHaploUpdate;

	@Wire
	private Button buttonHaploOrder;
	@Wire
	private Button buttonHaploImage;
	private String haplofilename;

	@Wire
	private Tab tabHaploHaploview;
	@Wire
	private Tab tabHaploAutogroups;
	@Wire
	private Tab tabHaploTree;
	@Wire
	private Tab tabHaploGroupAlleles;
	@Wire
	private Button buttonHaploDownloadNewick;
	@Wire
	private Button buttonHaploDisplayTree;

	@Wire
	private Iframe iframeAutogroups;

	@Wire
	private Listbox listboxKgroupMethod;
	@Wire
	private Decimalbox intboxKgroupThreshold;
	@Wire
	private Listbox listboxAutogroup;
	@Wire
	private Slider sliderCuttreeThreshold;

	// @Wire
	// private Hbox hboxHaplotype;
	@Wire
	private Iframe iframeHaplotypeTree;
	@Wire
	private Listbox listboxGroupAlleleMatrix;
	@Wire
	private Listhead listheadGroupAlleleMatrix;
	// @Wire
	@Wire
	private Button buttonDownloadGroupMatrix;
	@Wire
	private Checkbox checkboxCreateHaplotype;
	@Wire
	private Listbox listboxImagesize;
	@Wire	
	private Charts chartGroupPhenotypeStacked;
	@Wire
	private Charts chartGroupPhenotypeErrorbars;

	@Wire
	private Div divGroupPhenoQuant;
	@Wire
	private Div divGroupPhenoCat;
	@Wire
	private Div divGroupPhenoQuantBox;

	@Wire
	private Radio radioGroupPhenotypeBox;
	@Wire
	private Radio radioGroupPhenotypeErrorbars;

	@Wire
	private Checkbox checkboxNormalizeStackedCategories;
	@Wire
	private Checkbox checkboxOutlierGroupPhenotypeBox;
	@Wire
	private Textbox textboxCuttreeThreshold;
	@Wire
	private Label labelGroupVarietyPhenotype;
	@Wire
	private Button buttonAddPhenValuesToMatrix;

	private double hctreemaxlog2height = -1;
	private double hctreeminlog2height = -1;

	private Map<String, Collection> mapKgroupCat2Varieties;
	@Wire
	private Listbox listboxGroupVarietyPhenotypeStacked;
	@Wire
	private Listheader listheaderGroupVarietyPhenotypeStackedPhenotype;
	@Wire
	private Hbox hboxAlleleFrequency;
	@Wire
	private Vbox vboxGroupAlleleFrequency;

//	
	public ExtTabController() {
		super();
	}


	@Override
	public void doAfterCompose(Window comp) throws Exception {
		
		super.doAfterCompose(comp);

		try {
		
        divHaplotype.setVisible(false);
        divMDS.setVisible(false);
    	divJbrowse.setVisible(false);
    	divVistaRev.setVisible(false);
    	divVista.setVisible(false);
    	divSnpeff.setVisible(false);
    	divPhylo.setVisible(false);
    	
		Map parammap=Executions.getCurrent().getParameterMap();
        String paramstr=AppContext.convertParams2URL(parammap);
        AppContext.debug("loading " + paramstr);	
        Object tab= ((String[])parammap.get("tab"))[0]; 
        if(tab.equals("haplotype")) {
        	Set dataset = (Set)getSession().getAttribute("dataset");
        	GenotypeQueryParams p = (GenotypeQueryParams)getSession().getAttribute("queryparams");
        	String chr= (String)getSession().getAttribute("contig");
        	VariantTable table= (VariantTable)getSession().getAttribute("table");
        	String queryfilename= (String)getSession().getAttribute("queryfilename");
            	
        	onselectTabHaplotype((VarietyFacade)AppContext.checkBean(varietyfacade,"VarietyFacade"),dataset,p,chr,  table,
        			queryfilename);
        	divHaplotype.setVisible(true);
        }
        else if(tab.equals("mds")) {
        	
        	varietyfacade=(VarietyFacade)AppContext.checkBean(varietyfacade, "VarietyFacade");
        	
        	VariantStringData queryResult = (VariantStringData)getSession().getAttribute("queryresult");
        	Set dataset = (Set)getSession().getAttribute("dataset");
        	if (false) //(listboxPhenotype.getSelectedIndex() == 0)
    			;//show_mds_fromtable(chartMDS);
        	else {

    			List listvarnames = new ArrayList();
    			Set varnames = new TreeSet();
    			listvarnames.add("");
    			java.util.Iterator<BigDecimal> itvars = queryResult.getMapVariety2Order().keySet().iterator();
    			while (itvars.hasNext()) {
    				BigDecimal varid = itvars.next();
    				varnames.add(
    						((Variety) varietyfacade.getMapId2Variety(dataset).get(varid)).getName().toUpperCase());
    			}
    			listvarnames.addAll(varnames);
    			listboxHighlightVariety.setModel(new SimpleListModel(listvarnames));

    			//onselectPhenotype();
    		}
        	divMDS.setVisible(true);
        }
        else if(tab.equals("jbrowse")) {
			String jbrowseparam = (String)getSession().getAttribute("jbrowse");
			if(jbrowseparam==null)
				iframeJbrowse.setSrc("jbrowse.zul?tracks=DNA,msu7gff,msu7snpsv2,msu7indelsv2");
			else 
				iframeJbrowse.setSrc(jbrowseparam);
			divJbrowse.setVisible(true);
        }
        else if(tab.equals("vista")) {
			String reference = (String)getSession().getAttribute("reference");
			String contig = (String)getSession().getAttribute("contig");
			Integer start = (Integer)getSession().getAttribute("start");
			Integer end = (Integer)getSession().getAttribute("end");

    		updateVistaTab(reference,contig,
    				start,end);
    		
        	divVista.setVisible(true);
        }	
        else if(tab.equals("vistarev")) 
        	divVistaRev.setVisible(true);
        else if(tab.equals("phylo")) 
        	divPhylo.setVisible(true);
        else if(tab.equals("snpeff")) {
			VariantStringData queryResult = (VariantStringData)getSession().getAttribute("snpeff");
    		if (queryResult == null) {
    			AppContext.debug("queryResult==null");
    			return;
    		}
    		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
    		List listSnpeffs = genotype.getSnpEffects(queryResult.getListPos());
    		listboxSnpeff.setItemRenderer(new SNPEffListitemRenderer());
    		listboxSnpeff.setModel(new SimpleListModel(listSnpeffs));
        	divSnpeff.setVisible(true);
        } else
        	AppContext.debug("Invalid tab=" + tab);
        
		} catch(Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private void updateVistaSubtabs(String ref, String contig, long start, long end, String org1, String org2,
			String org3, String org4) {
		this.tabVista1.setLabel(ref + " vs " + org1);
		this.tabVista2.setLabel(ref + " vs " + org2);
		this.tabVista3.setLabel(ref + " vs " + org3);
		this.tabVista4.setLabel(ref + " vs " + org4);

		vistaurl1 = SNPQueryController.createVistaRef2Othersurl(ref, contig, start, end, org1);
		vistaurl2 =  SNPQueryController.createVistaRef2Othersurl(ref, contig, start, end, org2);
		vistaurl3 = SNPQueryController. createVistaRef2Othersurl(ref, contig, start, end, org3);
		vistaurl4 =  SNPQueryController.createVistaRef2Othersurl(ref, contig, start, end, org4);

		tabVista1.setSelected(true);
		this.iframeVista1.setSrc(vistaurl1);
	}


	private void updateVistaTab(String ref, String contig, Integer start, Integer end) {
		if (ref.equals(Organism.REFERENCE_NIPPONBARE))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_9311))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_NIPPONBARE, Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_IR64))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_NIPPONBARE,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_KASALATH))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					Organism.REFERENCE_NIPPONBARE, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_DJ123))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_NIPPONBARE);
	}

	@Listen("onClick =#tabVista1")
	public void onclickTabVista1() {
		AppContext.debug("displaying: " + vistaurl1);
		this.iframeVista1.setSrc(vistaurl1);
	}

	@Listen("onClick =#tabVista2")
	public void onclickTabVista2() {
		AppContext.debug("displaying: " + vistaurl2);
		this.iframeVista2.setSrc(vistaurl2);
	}

	@Listen("onClick =#tabVista3")
	public void onclickTabVista3() {
		AppContext.debug("displaying: " + vistaurl3);
		this.iframeVista3.setSrc(vistaurl3);
	}

	@Listen("onClick =#tabVista4")
	public void onclickTabVista4() {
		AppContext.debug("displaying: " + vistaurl4);
		this.iframeVista4.setSrc(vistaurl4);
	}

	
	public void onselectTabHaplotype(VarietyFacade varietyfacade, Set dataset, GenotypeQueryParams p,String chr, VariantTable table, String queryfilename) throws Exception{
		//if (haplofilename != null)
		//	return;
		this.tabHaploHaploview.setSelected(true);
		this.tabHaploAutogroups.setDisabled(true);
		this.tabHaploGroupAlleles.setDisabled(true);
		this.tabHaploTree.setDisabled(true);

		listboxGroupVarietyPhenotypeStacked.setVisible(false);
		labelGroupVarietyPhenotype.setVisible(false);

		hctreemaxlog2height = -1;
		hctreeminlog2height = -1;
		//GenotypeQueryParams p = this.fillGenotypeQueryParams();
		if (!p.isRegion()) {
			this.listboxHaploResolution.setSelectedIndex(0);
			this.listboxHaploResolution.setDisabled(true);
		} else
			this.listboxHaploResolution.setDisabled(false);
		// hboxHaplotype.setVisible(false);
		AppContext.debug("tabDisplayHaloptypeimage()");
		buttonHaplotypelog.setVisible(false);
		buttonHaploOrder.setDisabled(true);
		buttonHaploImage.setDisabled(true);

		//Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		//VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();

		Map mapVarid2Columns = new LinkedHashMap();
		int columns = table.getVariantStringData().getListPos().size();
		Iterator<BigDecimal> itVarsid = table.getVariantStringData().getMapVariety2Mismatch().keySet().iterator();
		while (itVarsid.hasNext()) {
			mapVarid2Columns.put(itVarsid.next(), columns);
		}

		haplofilename = "snp3kvars-" + queryfilename;
		p.setFilename(AppContext.getTempDir() + haplofilename);
		SNPQueryController.writeSummary(varietyfacade, dataset, true, p,
				/* AppContext.getTempDir() + qfilename, */table.getVariantStringData().getMapVariety2Order(),
				table.getVariantStringData().getMapVariety2Mismatch(), mapVarid2Columns, true);

		 //void generateBigListboxPlink(VarietyFacade varietyfacade,GenotypeQueryParams p,String chr,Set ds,VariantAlignmentTableArraysImpl table, String filename) {

		SNPQueryController.generateBigListboxPlink((VarietyFacade)AppContext.checkBean(varietyfacade,"VarietyFacade"),p,chr, dataset,table, AppContext.getTempDir() + haplofilename);

		//SNPQueryController.generateBigListboxPlink(table, AppContext.getTempDir() + haplofilename);
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		String res = this.listboxHaploResolution.getSelectedItem().getValue();
		double resFactor = 10;
		boolean genomecoord = p.isRegion();
		if (res.toLowerCase().equals("snp")) {
			genomecoord = false;
		} else {
			genomecoord = true;
			resFactor = Double.valueOf(res);
		}
		double localWeight = this.sliderClusterWieght.getCurposInDouble();

		int kgroups = 0;
		double kheight = 0;
		String autogroup = "nogroup";
		String kmethod = (String) listboxKgroupMethod.getSelectedItem().getValue();
		if (kmethod.equals("cuttreegroup")) {
			kgroups = Double.valueOf(sliderCuttreeThreshold.getCurposInDouble()).intValue();
			/*
			 * kgroups=intboxKgroupThreshold.getValue();
			 * if(intboxKgroupThreshold.getValue()<1) {
			 * Messagebox.show("Number of groups must be >0"); return; }
			 */
			autogroup = kmethod;
		} else if (kmethod.equals("cuttreeheight")) {
			kheight = sliderCuttreeThreshold.getCurposInDouble();

			autogroup = kmethod;
		} else if (kmethod.equals("cuttreeheight_norm")) {
			kheight = sliderCuttreeThreshold.getCurposInDouble();
			/*
			 * kheight=intboxKgroupThreshold.getValue();
			 * if(intboxKgroupThreshold.getValue()<1) {
			 * Messagebox.show("Height must be >0"); return; }
			 */
			autogroup = kmethod;
		} else if (kmethod.equals("autogroup")) {

			// if(this.listboxDataset.getSelectedItem().equals("gq92"))
			// listboxAutogroup.setSelectedIndex(0);
			autogroup = this.listboxAutogroup.getSelectedItem().getValue();
		}

		String imagesize = listboxImagesize.getSelectedItem().getValue();

		vboxGroupAlleleFrequency.setVisible(false);
		// s1.setOpen(false);
		tabHaploGroupAlleles.setDisabled(true);
		if (genotype.displayHapotypeImage(haplofilename, "jpeg", genomecoord, p, localWeight, resFactor, kgroups,
				(int) Math.round(kheight), autogroup, imagesize)) {
			AppContext.debug("displayHapotypeImage() success");
			buttonHaploOrder.setDisabled(false);
			buttonHaploImage.setDisabled(false);

			if(false) {
				try {
					Executions.forward(AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.html" );
				} catch(Exception  ex) {
					AppContext.debug(ex.getMessage());
				}
			} else {
				this.iframeHaplotype
						.setSrc(AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.html");
				this.iframeAutogroups.setSrc(AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename
						+ ".ped.autogroup.html");
				iframeAutogroups.invalidate();
				iframeHaplotype.invalidate();
				// hboxHaplotype.setVisible(true);
			}

			AppContext.debug("displayHapotypeImage() loaded");

			// if(kgroups>0 || kheight>0 || !autogroup.equals("nogroup")) {
			//if (!autogroup.equals("nogroup")) {
			if (false) {
				
				//mapVars2PropSnpstr = null;
				//displayGroupallelePhenotpes();

				try {
					//updateGroupAlleleFrequencyChart();
					
					vboxGroupAlleleFrequency.setVisible(true);
					// s1.setOpen(true);
					tabHaploGroupAlleles.setDisabled(false);

					// get max(log10(hctree height))
					try {
						double minmax[] = genotype.getMinMaxLog2treeheight(haplofilename + ".ped");
						hctreeminlog2height = minmax[0];
						hctreemaxlog2height = minmax[1];
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show(ex.getMessage(), "Catch Exception", Messagebox.OK, Messagebox.ERROR);
				}

			}
			this.tabHaploAutogroups.setDisabled(false);
			this.tabHaploTree.setDisabled(false);
			this.tabHaploGroupAlleles.setDisabled(false);

		} else {
			// iframeHaplotype.setVisible(false);
			// hboxHaplotype.setVisible(false);
			AppContext.debug("displayHapotypeImage() failed");
			buttonHaplotypelog.setHref(
					AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.stdout.log");
			buttonHaplotypelog.setVisible(true);

		}

	}
	
	@Listen("onClick = #buttonHaploOrder")
	public void onclickHaploorder() {
		try {
			Filedownload.save(new File(AppContext.getTempDir() + haplofilename + ".summary.txt.clustered.txt"),
					"text/plain");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick = #buttonHaploNewick")
	public void onclickHaploNewick() {
		try {
			Filedownload.save(new File(AppContext.getTempDir() + haplofilename + ".newick"), "text/plain");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick = #buttonHaploImage")
	public void onclickHaploimage() {
		try {

			List listFiles = new ArrayList();
			File f = new File(AppContext.getTempDir() + haplofilename + ".ped.jpeg");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.html");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.hctree.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.hctree.html");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.hvsk.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.log2hvsk.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.normlog2hvsk.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.autogroup.html");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.calinski.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.kmeansSSE.png");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.pamk.jpeg");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());
			f = new File(AppContext.getTempDir() + haplofilename + ".ped.pamk.pdf");
			if (f.exists())
				listFiles.add(f.getAbsolutePath());

			CreateZipMultipleFiles zip = new CreateZipMultipleFiles(
					AppContext.getTempDir() + haplofilename + ".haplo.zip", listFiles);
			zip.create(false);
			Filedownload.save(new File(AppContext.getTempDir() + haplofilename + ".haplo.zip"), "application/zip");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onSelect = #listboxKgroupMethod")
	public void onselectkmethod() {
		intboxKgroupThreshold.setVisible(false);
		this.listboxAutogroup.setVisible(false);
		sliderCuttreeThreshold.setVisible(false);
		textboxCuttreeThreshold.setVisible(false);

		String kmethod = (String) listboxKgroupMethod.getSelectedItem().getValue();
		if (kmethod.equals("nogroups")) {
		} else if (kmethod.equals("autogroup"))
			listboxAutogroup.setVisible(true);
		else {
			// intboxKgroupThreshold.setVisible(true);
			if (listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreegroup")) {
				
				//Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray
				//		.getModel();
				//VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
				
				VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl)getSession().getAttribute("table");

						    	
				double maxk = Math.min(15, table.getVariantStringData().getMapVariety2Mismatch().size());
				double curpos = 5;

				sliderCuttreeThreshold.setMinpos(1.0);
				sliderCuttreeThreshold.setMaxpos(maxk);
				sliderCuttreeThreshold.setStep(1.0);
				sliderCuttreeThreshold.setCurpos(curpos);
			} else if (listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreeheight")) {
				double maxh = 20;
				double curpos = 15;
				if (hctreemaxlog2height > -1) {
					maxh = Math.ceil(Math.pow(2, hctreemaxlog2height));
					curpos = maxh * 0.7;
				}
				sliderCuttreeThreshold.setMinpos(0.0);
				sliderCuttreeThreshold.setMaxpos(maxh);
				sliderCuttreeThreshold.setStep(0.1);
				sliderCuttreeThreshold.setCurpos(curpos);

				// intboxKgroupThreshold.setValue(BigDecimal.valueOf(5));
				// intboxKgroupThreshold.setVisible(true);
			} else if (listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreegroup_norm")) {
				sliderCuttreeThreshold.setMinpos(90.0);
				sliderCuttreeThreshold.setMaxpos(100.0);
				sliderCuttreeThreshold.setStep(0.01);
				sliderCuttreeThreshold.setCurpos(99.9);
			} else if (listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreeheight_norm")) {
				sliderCuttreeThreshold.setMinpos(0.0);
				sliderCuttreeThreshold.setMaxpos(100.0);
				sliderCuttreeThreshold.setStep(1.0);
				sliderCuttreeThreshold.setCurpos(75.0);

			}
			textboxCuttreeThreshold.setValue(String.format("%.2f", sliderCuttreeThreshold.getCurposInDouble()));
			sliderCuttreeThreshold.setVisible(true);
			textboxCuttreeThreshold.setVisible(true);
		}
	}

	@Listen("onClick = #buttonHaploDownloadNewick")
	public void onselectHaploDownloadNewick() throws Exception {
		if (!new File(AppContext.getTempDir() + haplofilename + ".ped.newick").exists()) {
			Messagebox.show("Newick file not generated");
			return;
		}
		try {
			String filetype = "text/plain";
			Filedownload.save(new File(AppContext.getTempDir() + haplofilename + ".ped.newick"), filetype);
			// AppContext.debug("File download complete! Saved to: "+filename);
			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			AppContext.debug("newick downlaod complete! Downloaded to:" + zksession.getRemoteHost() + "  "
					+ zksession.getRemoteAddr());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// @Listen("onSelect = #tabHaploTree")
	@Listen("onClick = #buttonHaploDisplayTree")
	public void onselectHaplotree() throws Exception {

		if (haplofilename == null)
			return;
		if (new File(AppContext.getTempDir() + haplofilename + ".ped.hctree.html").exists())
			return;
		iframeHaplotypeTree.setVisible(false);
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		// genotype.displayHapotypeTreeImage( haplofilename, "jpeg", genomecoord, p,
		// localWeight,resFactor, kgroups, kheight, autogroup, imagesize))
		double kheight = 0;
		if (this.listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreeheight_norm")) {
			// kheight=intboxKgroupThreshold.getValue();
			try {
				// kheight=sliderCuttreeThreshold.getCurpos();
				if (hctreemaxlog2height < 0) {
					double minmax[] = genotype.getMinMaxLog2treeheight(haplofilename + ".ped");
					hctreeminlog2height = minmax[0];
					hctreemaxlog2height = minmax[1];
				}
				kheight = Math.pow(2, hctreeminlog2height + (hctreemaxlog2height - hctreeminlog2height)
						* sliderCuttreeThreshold.getCurposInDouble() / 100.0);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (this.listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreeheight")) {
			// kheight=intboxKgroupThreshold.getValue();
			kheight = sliderCuttreeThreshold.getCurposInDouble();
		}
		AppContext.debug("effective kheight=" + kheight);

		genotype.displayHapotypeTreeImage(haplofilename, "png", kheight,
				Integer.valueOf((String) listboxImagesize.getSelectedItem().getValue()));
		this.iframeHaplotypeTree.setSrc(
				AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.hctree.html");
		iframeHaplotypeTree.invalidate();
		iframeHaplotypeTree.setVisible(true);
	}


//	@Listen("onClick =#checkboxOutlierGroupPhenotypeBox")
//	public void oncheckOutlier() {
//		try {
//			displayGroupallelePhenotpes();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	// @Listen("onClick = #chartGroupPhenotypeStacked")
	// public void onclickQualGroupPhenotype(ChartsClickEvent event) {
	//
	// AppContext.debug(event.getName() + " onClick " + event.getXAxis() + ", " +
	// event.getKeys() + ", " + event.getX() + ", " + event.getY());
	//
	// /*for (Point point: chartGroupPhenotypeStacked.getSelectedPoints()) {
	// //point.update(event.getXAxis(), event.getYAxis(), point.getHigh());
	// //point.setSelected(false);
	// AppContext.debug(event.getName() + " clicked " + point.getName() + ", " +
	// event.getXAxis() +", " + point.getX() );
	// }
	// */
	// }

//	@Listen("onPlotClick = #chartGroupPhenotypeStacked")
//	public void onclickQualGroupPhenotype(ChartsEvent event) {
//
//		Point point = event.getPoint();
//		// point.update(event.getXAxis(), event.getYAxis(), point.getHigh());
//		// point.setSelected(false);
//		// AppContext.debug("onPlotClick " + point.getX() + ", " + point.getY() + ", " +
//		// event.getSeries().getName() + ", " + event.getPointIndex() + ", " +
//		// event.getCategory().toString() + " , " +
//		// event.getSeries() + ", " + event.getCategory().getClass() + ", " +
//		// event.getPoint().getParent() );
//		AppContext.debug("onPlotClick " + event.getCategory() + "," + event.getSeries().getName());
//		// display varieties
//
//		if (mapKgroupCat2Varieties == null)
//			return;
//
//		AppContext.debug("mapKgroupCat2Varieties.key=" + mapKgroupCat2Varieties.keySet());
//		try {
//			Set dataset = (Set)getSession().getAttribute("dataset");
//			String sPhenotype = (String)getSession().getAttribute("phenotype");
//        	
//			//String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, dataset);
//			List listVars = new ArrayList();
//			Map varid2var = varietyfacade.getMapId2Variety(dataset);
//			Iterator<String> itVars = mapKgroupCat2Varieties
//					.get(event.getSeries().getName() + "-" + event.getCategory()).iterator();
//			while (itVars.hasNext()) {
//				String var = itVars.next();
//				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
//				Variety varobj = (Variety) varid2var.get(varid);
//				listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
//						mapVarid2Phenotype.get(varid) });
//			}
//			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
//			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
//			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
//			listboxGroupVarietyPhenotypeStacked.setVisible(true);
//			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + event.getCategory() + ", "
//					+ sPhenotype + " = " + event.getSeries().getName());
//			labelGroupVarietyPhenotype.setVisible(true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	@Listen("onSelection = #chartGroupPhenotypeBox")
//	public void onselectQuanGroupPhenotype(ChartsSelectionEvent event) {
//		// doing the zooming in function
//		double ymin = event.getYAxisMin().doubleValue();
//		double ymax = event.getYAxisMax().doubleValue();
//		int kgroup = event.getXAxisMax().intValue() + 1;
//
//		AppContext.debug("group " + kgroup + ", " + ymin + " - " + ymax);
//
//		if (mapKgroupCat2Varieties == null)
//			return;
//		try {
//			Set dataset = (Set)getSession().getAttribute("dataset");
//			String sPhenotype = (String)getSession().getAttribute("phenotype");
//
//			//String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, dataset);
//			List listVars = new ArrayList();
//			Map varid2var = varietyfacade.getMapId2Variety(dataset);
//			Iterator<String> itVars = mapKgroupCat2Varieties.get(String.format("%d", kgroup)).iterator();
//			while (itVars.hasNext()) {
//				String var = itVars.next();
//				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
//				Object phenval = mapVarid2Phenotype.get(varid);
//				if (phenval != null && phenval instanceof Number) {
//					double dval = ((Number) phenval).doubleValue();
//					if (dval <= ymax && dval >= ymin) {
//						Variety varobj = (Variety) varid2var.get(varid);
//						listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
//								phenval });
//					}
//				}
//			}
//
//			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
//			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
//			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
//			listboxGroupVarietyPhenotypeStacked.setVisible(true);
//			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + kgroup + ", " + sPhenotype
//					+ " between " + String.format("%.2f", ymin) + " and " + String.format("%.2f", ymax));
//			labelGroupVarietyPhenotype.setVisible(true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}
//
//	@Listen("onClick =#buttonAddPhenValuesToMatrix")
//	public void onclickaddphenvalues() {
//		StringBuffer buff = new StringBuffer();
//		Map mapKgroup2Values = new HashMap();
//		List lHeader = new ArrayList();
//    	Set dataset = (Set)getSession().getAttribute("dataset");
//
//		//String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//    	String sPhenotype=(String)getSession().getAttribute("phenotype");
//    	if (this.divGroupPhenoCat.isVisible()) {
//			buff.append("divGroupPhenoCat: ");
//			Iterator<String> itgroupcat = new TreeSet(mapKgroupCat2Varieties.keySet()).iterator();
//			Set<String> setCats = new TreeSet();
//			Map<String, Map> mapKgroup2Cats = new TreeMap();
//			while (itgroupcat.hasNext()) {
//				String gk = itgroupcat.next();
//				int vars = ((Collection) mapKgroupCat2Varieties.get(gk)).size();
//				buff.append(gk + "\t" + vars + "\n");
//				String k = gk.split("\\-")[1];
//				String cat = gk.split("\\-")[0];
//				setCats.add(cat);
//				Map mapCat2Vals = mapKgroup2Cats.get(k);
//				if (mapCat2Vals == null) {
//					mapCat2Vals = new HashMap();
//					mapKgroup2Cats.put(k, mapCat2Vals);
//				}
//				mapCat2Vals.put(cat, vars);
//			}
//			for (String cat : setCats) {
//				lHeader.add(sPhenotype + ":" + cat);
//			}
//			buff.append("\n");
//			Iterator<String> itk = mapKgroup2Cats.keySet().iterator();
//			while (itk.hasNext()) {
//				String k = itk.next();
//				List listVals = new ArrayList();
//				buff.append(k).append("\t");
//				for (String cat : setCats) {
//					Integer nvar = (Integer) mapKgroup2Cats.get(k).get(cat);
//					if (nvar == null)
//						listVals.add("");
//					else
//						listVals.add(nvar);
//					buff.append(nvar).append("\t");
//				}
//				mapKgroup2Values.put(k, listVals);
//				buff.append("\n");
//			}
//
//		} else if (this.divGroupPhenoQuantBox.isVisible()) {
//			try {
//				BoxPlotModel model = (BoxPlotModel) this.chartGroupPhenotypeBox.getModel();
//				Iterator itseries = model.getSeries().iterator();
//				buff.append("divGroupPhenoQuantBox: ");
//				lHeader.add(sPhenotype + " Q1");
//				lHeader.add(sPhenotype + " median");
//				lHeader.add(sPhenotype + " Q3");
//				while (itseries.hasNext()) {
//					Comparable ser = (Comparable) itseries.next();
//					int dcount = model.getDataCount(ser);
//					// buff.append( ((Series)ser ).getName() ).append(":\n");
//					buff.append(ser.toString()).append(":\n");
//					for (int i = 0; i < dcount; i++) {
//						double q1 = model.getQ1(ser, i).doubleValue();
//						double q2 = model.getMedian(ser, i).doubleValue();
//						double q3 = model.getQ3(ser, i).doubleValue();
//						buff.append(q1).append("\t").append(q2).append("\t").append(q3).append("\n");
//						List lVal = new ArrayList();
//						lVal.add(q1);
//						lVal.add(q2);
//						lVal.add(q3);
//						mapKgroup2Values.put(Integer.toString(i + 1), lVal);
//					}
//				}
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		} else if (this.chartGroupPhenotypeErrorbars.isVisible()) {
//
//			List<Point> lmean = chartGroupPhenotypeErrorbars.getSeries(0).getData();
//			Series series2 = chartGroupPhenotypeErrorbars.getSeries(1);
//			List<Point> lstd = series2.getData();
//			buff.append("chartGroupPhenotypeErrorbars: ");
//			lHeader.add(sPhenotype + " mean");
//			lHeader.add(sPhenotype + " variance");
//			for (int i = 0; i < lmean.size(); i++) {
//				buff.append(lmean.get(i).getX() + "\t" + lstd.get(i).getLow() + "\t" + lstd.get(i).getHigh() + "\t"
//						+ lmean.get(i).toJSONString() + "\t" + lstd.get(i).getX() + "\t" + lstd.get(i).getY() + "\t"
//						+ lstd.get(i).toJSONString() + "\n");
//				List lVal = new ArrayList();
//
//				double mean = Double.valueOf(lmean.get(i).toJSONString());
//				lVal.add(mean);
//				// lVal.add(lstd.get(i).getX());
//				lVal.add(Math.pow(lstd.get(i).getY().doubleValue() - mean, 2));
//				mapKgroup2Values.put(Integer.toString(i + 1), lVal);
//			}
//		}
//
//		AppContext.debug("buttonAddPhenValuesToMatrix " + buff.toString());
//
//		appendToGroupMatrix(lHeader, mapKgroup2Values);
//	}

	public void appendToGroupMatrix(List lHeader, Map mapKgroup2Values) {

		ListitemRenderer rend = listboxGroupAlleleMatrix.getItemRenderer();
		((VargroupListItemRenderer) rend).appendValues(lHeader, mapKgroup2Values);

		Listhead listHead = (Listhead) listboxGroupAlleleMatrix.getListhead(); // .getChildren().iterator().next();
		List<Listheader> listheader = listHead.getChildren();
		int nheads = listheader.size();
		Iterator<String> itH = lHeader.iterator();
		while (itH.hasNext()) {
			Listheader nheader = new Listheader(itH.next());
			nheader.setWidth("40px"); // nheader.setSort("auto");
			nheader.setSortAscending(new GroupMatrixListItemSorter(true, nheads));
			nheader.setSortDescending(new GroupMatrixListItemSorter(false, nheads));
			nheads++;
			listheader.add(nheader);
		}
		// listboxGroupAlleleMatrix.invalidate();
		listboxGroupAlleleMatrix.setItemRenderer(rend);
		listboxGroupAlleleMatrix.setModel(listboxGroupAlleleMatrix.getModel());
	}

//	@Listen("onSelection = #chartGroupPhenotypeErrorbars")
//	public void onselectQuanGroupPhenotypeError(ChartsSelectionEvent event) {
//		// doing the zooming in function
//		double ymin = event.getYAxisMin().doubleValue();
//		double ymax = event.getYAxisMax().doubleValue();
//		int kgroup = event.getXAxisMax().intValue() + 1;
//
//		AppContext.debug("group " + kgroup + ", " + ymin + " - " + ymax);
//
//		if (mapKgroupCat2Varieties == null)
//			return;
//		try {
//			
//			String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
//			List listVars = new ArrayList();
//			Map varid2var = varietyfacade.getMapId2Variety(getDataset());
//			Iterator<String> itVars = mapKgroupCat2Varieties.get(String.format("%d", kgroup)).iterator();
//			while (itVars.hasNext()) {
//				String var = itVars.next();
//				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
//				Object phenval = mapVarid2Phenotype.get(varid);
//				if (phenval != null && phenval instanceof Number) {
//					double dval = ((Number) phenval).doubleValue();
//					if (dval <= ymax && dval >= ymin) {
//						Variety varobj = (Variety) varid2var.get(varid);
//						listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
//								phenval });
//					}
//				}
//			}
//
//			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
//			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
//			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
//			listboxGroupVarietyPhenotypeStacked.setVisible(true);
//			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + kgroup + ", " + sPhenotype
//					+ " between " + String.format("%.2f", ymin) + " and " + String.format("%.2f", ymax));
//			labelGroupVarietyPhenotype.setVisible(true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}

//	private void displayGroupallelePhenotpes() throws Exception {
//
//		String autogroup = "nogroup";
//		String kmethod = (String) listboxKgroupMethod.getSelectedItem().getValue();
//		if (kmethod.equals("cuttreegroup") || kmethod.equals("cuttreeheight") || kmethod.equals("cuttreeheight_norm")) {
//			autogroup = kmethod;
//		} else if (kmethod.equals("autogroup")) {
//
//			// if(this.listboxDataset.getSelectedItem().equals("gq92"))
//			// listboxAutogroup.setSelectedIndex(0);
//			autogroup = this.listboxAutogroup.getSelectedItem().getValue();
//		}
//
//		if (autogroup.equals("nogroup"))
//			return;
//
//		if (mapVars2PropSnpstr == null)
//			mapVars2PropSnpstr = displayKgroupAlleleFrequencies(haplofilename);
//
//		// Map mapVars2PropSnpstr[]=displayKgroupAlleleFrequencies(haplofilename);
//
//		AppContext.debug("mapVars2PropSnpstr[]=" + mapVars2PropSnpstr);
//
//		labelGroupVarietyPhenotype.setVisible(false);
//		divGroupPhenoQuant.setVisible(false);
//		divGroupPhenoCat.setVisible(false);
//		buttonAddPhenValuesToMatrix.setVisible(false);
//		
//		Set dataset = (Set)getSession().getAttribute("dataset");
//		String sPhenotype = (String)getSession().getAttribute("phenotype");
//
//		if (sPhenotype!=null) {
//			buttonAddPhenValuesToMatrix.setVisible(true);
//			// draw group phenotype boxplots
//			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
//			Map<BigDecimal, Object> mapVarid2Phenotype = null;
//			//String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//			Set quanttraits = varietyfacade.getQuantTraits(dataset);
//
//			if (!quanttraits.contains(sPhenotype)) {
//				// Messagebox.show("No statistics for non-quantitative traits");
//
//				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype,dataset);
//
//				// AppContext.getMapVarunique2Id(varietyfacade.getMapId2Variety(getDataset()),
//				// true,true,true);
//				// name, pop,origorder, irisid, grp
//				Map mapVars2Prop = mapVars2PropSnpstr[0];
//				Iterator<String> itVar = mapVars2Prop.keySet().iterator();
//				Map<String, Map<Object, Set<String>>> mapGroup2Statistics = new HashMap();
//				int phencnt = 0;
//				while (itVar.hasNext()) {
//					String var = itVar.next();
//					String grp = ((String[]) mapVars2Prop.get(var))[4];
//					Map<Object, Set<String>> stat = mapGroup2Statistics.get(grp);
//					if (stat == null) {
//						stat = new HashMap();
//						mapGroup2Statistics.put(grp, stat);
//					}
//					BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true,
//							varietyfacade.getMapId2Variety(getDataset()));
//					if (varid == null) {
//						varid = AppContext.getVarunique2Id(var.replace("_", " "), true, true, true,
//								varietyfacade.getMapId2Variety(getDataset()));
//						if (varid == null)
//							throw new RuntimeException("cannot find varid for " + var);
//					}
//					Object phenvalue = mapVarid2Phenotype.get(varid);
//					if (phenvalue == null)
//						continue;
//					Set<String> lvars = stat.get(phenvalue);
//					if (lvars == null) {
//						lvars = new HashSet();
//						stat.put(phenvalue, lvars);
//					}
//					lvars.add(var);
//					phencnt++;
//				}
//
//				AppContext.debug("displayGroupallelePhenotpes phencnt=" + phencnt + " " + " groups="
//						+ mapGroup2Statistics.keySet() + " sPhenotype=" + sPhenotype);
//				// draw box plots
//				updatePhenotpeStackedplots(chartGroupPhenotypeStacked, mapGroup2Statistics, phencnt, sPhenotype);
//				// checkboxNormalizeStackedCategories.setVisible(true);
//				divGroupPhenoCat.setVisible(true);
//
//			} else {
//
//				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, dataset);
//
//				// AppContext.getMapVarunique2Id(varietyfacade.getMapId2Variety(getDataset()),
//				// true,true,true);
//				double totalphen = 0;
//				// name, pop,origorder, irisid, grp
//				mapKgroupCat2Varieties = new HashMap();
//				Map mapVars2Prop = mapVars2PropSnpstr[0];
//				Iterator<String> itVar = mapVars2Prop.keySet().iterator();
//				Map<String, DescriptiveStatistics> mapGroup2Statistics = new HashMap();
//				int phencnt = 0;
//				while (itVar.hasNext()) {
//					String var = itVar.next();
//					String grp = ((String[]) mapVars2Prop.get(var))[4];
//					DescriptiveStatistics stat = mapGroup2Statistics.get(grp);
//					if (stat == null) {
//						stat = new DescriptiveStatistics();
//						mapGroup2Statistics.put(grp, stat);
//					}
//					BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true,
//							varietyfacade.getMapId2Variety(dataset));
//					if (varid == null) {
//						varid = AppContext.getVarunique2Id(var.replace("_", " "), true, true, true,
//								varietyfacade.getMapId2Variety(dataset));
//						if (varid == null)
//							throw new RuntimeException("cannot find varid for " + var);
//					}
//					Number phenvalue = (Number) mapVarid2Phenotype.get(varid);
//					if (phenvalue == null)
//						continue;
//					double val = phenvalue.doubleValue();
//					stat.addValue(val);
//					phencnt++;
//					totalphen += val;
//
//					Set sVars = (Set) mapKgroupCat2Varieties.get(grp);
//					if (sVars == null) {
//						sVars = new HashSet();
//						mapKgroupCat2Varieties.put(grp, sVars);
//					}
//					sVars.add(var);
//				}
//				double avg = totalphen / phencnt;
//
//				AppContext.debug("displayGroupallelePhenotpes avg=" + avg + " " + " groups="
//						+ mapGroup2Statistics.keySet() + " sPhenotype=" + sPhenotype);
//				// draw box plots
//				if (radioGroupPhenotypeBox.isSelected()) {
//					// if(true) {
//					updatePhenotpeBoxplots(chartGroupPhenotypeBox, mapGroup2Statistics, avg, phencnt, sPhenotype);
//					divGroupPhenoQuant.setVisible(true);
//					divGroupPhenoQuantBox.setVisible(true);
//					// checkboxOutlierGroupPhenotypeBox.setVisible(true);
//				} else {
//					updatePhenotpeErrorbars(chartGroupPhenotypeErrorbars, mapGroup2Statistics, avg, phencnt,
//							sPhenotype);
//					divGroupPhenoQuant.setVisible(true);
//					divGroupPhenoQuantBox.setVisible(false);
//				}
//			}
//
//		}
//
//	}

//	@Listen("onClick =#radioGroupPhenotypeBox")
//	public void onClickradioGroupPhenotypeBox() {
//		divGroupPhenoQuantBox.setVisible(false);
//		chartGroupPhenotypeErrorbars.setVisible(false);
//		try {
//			displayGroupallelePhenotpes();
//			divGroupPhenoQuantBox.setVisible(true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//
//	@Listen("onClick =#radioGroupPhenotypeErrorbars")
//	public void radioGroupPhenotypeErrorbars() {
//		divGroupPhenoQuantBox.setVisible(false);
//		chartGroupPhenotypeErrorbars.setVisible(false);
//		try {
//			displayGroupallelePhenotpes();
//			chartGroupPhenotypeErrorbars.setVisible(true);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}

	@Listen("onClick =#checkboxNormalizeStackedCategories")
	public void onclickcheckboxNormalizeStackedCategories() {
		updatePhenotpeStackedplotsOption(chartGroupPhenotypeStacked, checkboxNormalizeStackedCategories.isChecked());
	}

	private void updatePhenotpeStackedplotsOption(Charts chart, boolean showPercent) {

		if (showPercent) {
			chart.getTooltip().setPointFormat("<span style=\"color:{series.color}\">{series.name}</span>"
					+ ": <b>{point.y}</b> ({point.percentage:.0f}%)<br/>");
			chart.getTooltip().setShared(true);
			chart.getPlotOptions().getColumn().setStacking("percent");

			YAxis yAxis = chart.getYAxis();
			yAxis.getTitle().setText(yAxis.getTitle().getText().replace("(Count)", "(Frequency)"));
		} else {
			chart.getTooltip().setFormatter(new JavaScriptValue(
					"function() {return '<b>' + this.x + '</b><br/>' + this.series.name + ': ' + this.y + '<br/>' + 'Total: ' + this.point.stackTotal;}"));
			YAxis yAxis = chart.getYAxis();
			yAxis.getTitle().setText(yAxis.getTitle().getText().replace("(Frequency)", "(Count)"));
			ColumnPlotOptions plotOptions = chart.getPlotOptions().getColumn();
			chart.getTooltip().setShared(false);
			plotOptions.setStacking("normal");
			plotOptions.getDataLabels().setEnabled(true);
			if (!isThemeStyleSet(chart, "dataLabelsColor")) {
				plotOptions.getDataLabels().setColor("white");
			}
			plotOptions.getDataLabels().setStyle("textShadow: '0 0 3px black'");
		}

	}

	private boolean isThemeStyleSet(Charts chart, String style) {
		Theme theme = chart.getTheme();
		return theme != null && theme.toString().contains(style);
	}

	private void updatePhenotpeStackedplots(Charts chart, Map mapGroup2Statistics, int phencnt, String sPhenotype) {

		AppContext.debug("mapGroup2Statistics=" + mapGroup2Statistics);
		try {

			CategoryModel model = new DefaultCategoryModel();

			Set setNgroup = new TreeSet();
			Iterator itgrp = mapGroup2Statistics.keySet().iterator();
			boolean isCatString = false;

			while (itgrp.hasNext()) {
				try {
					setNgroup.add(Integer.valueOf((String) itgrp.next()));
				} catch (Exception ex) {

				}
			}

			int groupcatcount = 0;

			itgrp = setNgroup.iterator();
			List categories = new ArrayList();
			mapKgroupCat2Varieties = new HashMap();

			while (itgrp.hasNext()) {
				String grp = itgrp.next().toString();
				Map<Object, Set<String>> ds = (Map) mapGroup2Statistics.get(grp);
				Set setCat = new TreeSet();
				Map mapNewCat2OrigCat = new HashMap();
				Map mapOrigCat2NewCat = new HashMap();
				Iterator itds = ds.keySet().iterator();
				while (itds.hasNext()) {
					Object cat = itds.next();
					if (cat instanceof String) {
						try {
							setCat.add(Integer.valueOf((String) cat));
							mapNewCat2OrigCat.put(Integer.valueOf((String) cat), cat);
							mapOrigCat2NewCat.put(cat, Integer.valueOf((String) cat));
							isCatString = false;
						} catch (Exception ex) {
							setCat.add(cat);
							mapNewCat2OrigCat.put(cat, cat);
							mapOrigCat2NewCat.put(cat, cat);
							isCatString = true;
						}
					} else if (cat instanceof Number) {
						try {
							setCat.add(((Number) cat).intValue());
							mapNewCat2OrigCat.put(((Number) cat).intValue(), cat);
							mapOrigCat2NewCat.put(cat, ((Number) cat).intValue());
						} catch (Exception ex) {
							setCat.add(cat.toString());
							mapNewCat2OrigCat.put(cat.toString(), cat);
							mapOrigCat2NewCat.put(cat, cat.toString());
							isCatString = true;
						}
					} else {
						setCat.add(cat.toString());
						isCatString = true;
						mapNewCat2OrigCat.put(cat.toString(), cat);
						mapOrigCat2NewCat.put(cat, cat.toString());
					}
				}

				// Set setstrCat=new TreeSet();
				Map mapOrigcat2Strcat = new HashMap();

				// convert to string
				if (!isCatString) {
					Set catLess999 = new TreeSet(setCat);
					catLess999.remove(Integer.valueOf(999));
					int maxcat = -1;
					Iterator<Integer> itcat = catLess999.iterator();
					while (itcat.hasNext()) {
						Integer cat = itcat.next();
						if (cat > maxcat)
							maxcat = cat;
					}
					itcat = setCat.iterator();
					while (itcat.hasNext()) {
						Integer cat = itcat.next();
						if (maxcat > 9) {
							// three digit
							String strcat = cat.toString();
							if (strcat.length() == 1)
								strcat = "00" + strcat;
							else if (strcat.length() == 2)
								strcat = "0" + strcat;
							mapOrigcat2Strcat.put(mapNewCat2OrigCat.get(cat), strcat);
						} else {
							// 1 digit
							mapOrigcat2Strcat.put(mapNewCat2OrigCat.get(cat), cat.toString());
						}
					}
				} else {
					Iterator<String> itcat = setCat.iterator();
					while (itcat.hasNext()) {
						String cat = itcat.next();
						mapOrigcat2Strcat.put(mapNewCat2OrigCat.get(cat), cat);
					}
				}

				itds = ds.keySet().iterator();
				while (itds.hasNext()) {
					Object cat = itds.next();
					int cnt = ds.get(cat).size();
					String catstr = (String) mapOrigcat2Strcat.get(cat);
					model.setValue(catstr, grp, cnt);
					groupcatcount++;
					mapKgroupCat2Varieties.put(catstr + "-" + grp, ds.get(cat));
				}

			}
			AppContext.debug("updatePhenotpeStackedplots models set, groupcatcount=" + groupcatcount);
			// chart.getLegend().setEnabled(false);

			// chart.setType("column");
			XAxis xAxis = chart.getXAxis();
			xAxis.setCategories(categories);
			xAxis.getTitle().setText("K-group");

			YAxis yAxis = chart.getYAxis();
			yAxis.getTitle().setText(sPhenotype + " (Count)");

			chart.setModel(model);

			updatePhenotpeStackedplotsOption(chart, checkboxNormalizeStackedCategories.isChecked());
			chart.getPlotOptions().getBubble().setAllowPointSelect(true);

			chart.setVisible(true);

			AppContext.debug("updatePhenotpeStackedplots chart displayed");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void updatePhenotpeBoxplots(Charts chart, Map mapGroup2Statistics, double avg, int phencnt,
			String sPhenotype) {

		try {

			BoxPlotModel model = new DefaultBoxPlotModel();
			Set setNgroup = new TreeSet();
			Iterator itgrp = mapGroup2Statistics.keySet().iterator();
			while (itgrp.hasNext()) {
				try {
					setNgroup.add(Integer.valueOf((String) itgrp.next()));
				} catch (Exception ex) {

				}
			}

			itgrp = setNgroup.iterator();
			List categories = new ArrayList();
			List listOutliers = new ArrayList();

			if (this.checkboxOutlierGroupPhenotypeBox.isChecked()) {

				while (itgrp.hasNext()) {
					String grp = itgrp.next().toString();
					DescriptiveStatistics ds = (DescriptiveStatistics) mapGroup2Statistics.get(grp);
					if (ds.getN() > 3) {
						double q1 = ds.getPercentile(25);
						double q3 = ds.getPercentile(75);
						double iqr = q3 - q1;
						double maxvalid = Double.MIN_VALUE;
						double minvalid = Double.MAX_VALUE;
						for (int i = 0; i < ds.getN(); i++) {
							double di = ds.getElement(i);
							if (di < q1 - 1.5 * iqr || di > q3 + 1.5 * iqr)
								listOutliers.add(new Point(Double.valueOf(grp) - 1, di));
							else {
								if (di < minvalid)
									minvalid = di;
								if (di > maxvalid)
									maxvalid = di;
							}
						}
						model.addValue("Observations", minvalid, q1, ds.getPercentile(50), q3, maxvalid);
						// model.addValue("Observations", ds.getMin(),
						// ds.getPercentile(25),ds.getPercentile(50), ds.getPercentile(75)
						// ,ds.getMax());
					} else
						model.addValue("Observations", ds.getMin(), ds.getPercentile(25), ds.getPercentile(50),
								ds.getPercentile(75), ds.getMax());

					categories.add(grp);
				}

			} else {
				while (itgrp.hasNext()) {
					String grp = itgrp.next().toString();
					DescriptiveStatistics ds = (DescriptiveStatistics) mapGroup2Statistics.get(grp);
					model.addValue("Observations", ds.getMin(), ds.getPercentile(25), ds.getPercentile(50),
							ds.getPercentile(75), ds.getMax());
					categories.add(grp);
				}
			}

			AppContext.debug("updatePhenotpeBoxplots models set");
			chart.getLegend().setEnabled(false);

			// chart.setModel((ChartsModel)null);
			// chart.setType("boxplot");

			/*
			 * XAxis xAxis = chart.getXAxis(); xAxis.setCategories(categories);
			 * xAxis.getTitle().setText("K-group");
			 */

			YAxis yAxis = chart.getYAxis();
			yAxis.getTitle().setText(sPhenotype);

			Series series0 = chart.getSeries(0);
			series0.getTooltip().setHeaderFormat("<em>K-group {point.key}</em><br/>");

			chart.getXAxis().setCategories(categories);
			chart.getXAxis().getTitle().setText("K-group");

			PlotLine plotLine = new PlotLine();
			plotLine.setValue(avg);
			plotLine.setColor("red");
			plotLine.setWidth(1);
			plotLine.getLabel().setText("All groups Mean: " + String.format("%.02f", avg));
			plotLine.getLabel().setAlign("center");
			plotLine.getLabel().setStyle("color: gray;");
			yAxis.setPlotLines(Arrays.asList(plotLine));

			chart.setModel(model);

			if (listOutliers.size() > 0) {
				Series series1 = chart.getSeries(1);
				series1.setName("Outlier");
				series1.setColor(chart.getColors().get(0));
				series1.setType("scatter");

				// series1.setData((Point[])listOutliers.toArray());
				AppContext.debug(listOutliers.size() + " outliers");
				// series1.setData((Point[])listOutliers.toArray(new
				// Point[listOutliers.size()]));
				Point outs[] = new Point[listOutliers.size()];

				Iterator<Point> itp = listOutliers.iterator();
				int pcnt = 0;
				while (itp.hasNext()) {
					Point p = itp.next();
					// series1.addPoint(p);
					outs[pcnt] = p;
					AppContext.debug("x=" + p.getX() + ", y=" + p.getY());
					pcnt++;
				}
				series1.setData(outs);
				series1.getMarker().setFillColor("white");
				series1.getMarker().setLineWidth(1);
				series1.getMarker().setLineColor(chart.getColors().get(0));
				series1.getTooltip().setPointFormat("Observation: {point.y}");
			}

			chart.setVisible(true);

			AppContext.debug("updatePhenotpeBoxplots chart displayed");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void updatePhenotpeErrorbars(Charts chart, Map mapGroup2Statistics, double avg, int phencnt,
			String sPhenotype) {

		try {

			// BoxPlotModel model = new DefaultBoxPlotModel();
			Set setNgroup = new TreeSet();
			Iterator itgrp = mapGroup2Statistics.keySet().iterator();
			while (itgrp.hasNext()) {
				try {
					setNgroup.add(Integer.valueOf((String) itgrp.next()));
				} catch (Exception ex) {

				}
			}

			itgrp = setNgroup.iterator();
			List categories = new ArrayList();
			List listOutliers = new ArrayList();

			List<Double> lMean = new ArrayList();
			List<Double> lVariance = new ArrayList();
			List<Double> lStdDev = new ArrayList();
			// if(this.checkboxOutlierGroupPhenotypeBox.isChecked()) {

			while (itgrp.hasNext()) {
				String grp = itgrp.next().toString();
				DescriptiveStatistics ds = (DescriptiveStatistics) mapGroup2Statistics.get(grp);
				lMean.add(ds.getMean());
				lVariance.add(ds.getVariance());
				lStdDev.add(ds.getStandardDeviation());

				// model.addValue("Observations", ds.getMin(),
				// ds.getPercentile(25),ds.getPercentile(50), ds.getPercentile(75)
				// ,ds.getMax());
				categories.add(grp);
			}

			AppContext.debug("updatePhenotpeErrorbars models set");

			// chart.getXAxis().setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun",
			// "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
			chart.getXAxis().setCategories(categories);
			chart.getXAxis().getTitle().setText("K-group");

			// Primary y Axis
			/*
			 * YAxis yAxis1 = chart.getYAxis(); yAxis1.getLabels().setFormat("{value}°C");
			 * yAxis1.setTitle("Temperature");
			 */
			// Secondary y Axis
			YAxis yAxis2 = chart.getYAxis(0);
			yAxis2.setTitle(sPhenotype);
			yAxis2.getLabels().setFormat("{value}");
			// yAxis2.setOpposite(true);
			setYAxisesColor(chart);

			chart.getTooltip().setShared(true);

			Series series1 = chart.getSeries(0);
			series1.setName(sPhenotype);
			series1.setType("column");
			series1.setYAxis(0);
			// series1.setData(49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4,
			// 194.1, 95.6, 54.4);
			series1.setData(lMean);
			series1.getPlotOptions().getTooltip().setPointFormat("<span style=\"font-weight: bold; color:"
					+ "{series.color}\">{series.name}</span>: <b>{point.y:.2f}</b>");

			Series series2 = chart.getSeries(1);
			series2.remove();
			series2 = chart.getSeries(1);
			series2.setName(sPhenotype + " std dev");
			series2.setType("errorbar");
			series2.setYAxis(0);
			// List<Point> ldat=new ArrayList();
			for (int iv = 0; iv < lMean.size(); iv++) {
				series2.addPoint(lMean.get(iv) - lStdDev.get(iv), lMean.get(iv) + lStdDev.get(iv));
				// ldat.add(new Point(lMean.get(iv)-lStdDev.get(iv),
				// lMean.get(iv)+lStdDev.get(iv)));
			}
			// series2.setda .setData(ldat);

			series2.getPlotOptions().getTooltip().setPointFormat("(std dev: {point.low:0.2f}-{point.high:0.2f})<br/>");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void setYAxisesColor(Charts chart) {
		YAxis yAxis1 = chart.getYAxis();
		YAxis yAxis2 = chart.getYAxis(1);
		String color1 = chart.getColors().get(0).stringValue();
		String color2 = chart.getColors().get(1).stringValue();
		yAxis1.getLabels().setStyle("color: '" + color2 + "'");
		yAxis1.getTitle().setStyle("color: '" + color2 + "'");
		yAxis2.getLabels().setStyle("color: '" + color1 + "'");
		yAxis2.getTitle().setStyle("color: '" + color1 + "'");
	}

	private void updatePhenotpeErrorbars2(Charts chart, Map mapGroup2Statistics, double avg, int phencnt,
			String sPhenotype) {

		try {

			// BoxPlotModel model = new DefaultBoxPlotModel();
			Set setNgroup = new TreeSet();
			Iterator itgrp = mapGroup2Statistics.keySet().iterator();
			while (itgrp.hasNext()) {
				try {
					setNgroup.add(Integer.valueOf((String) itgrp.next()));
				} catch (Exception ex) {

				}
			}

			itgrp = setNgroup.iterator();
			List categories = new ArrayList();
			List listOutliers = new ArrayList();

			List<Double> lMean = new ArrayList();
			List<Double> lVariance = new ArrayList();
			List<Double> lStdDev = new ArrayList();
			// if(this.checkboxOutlierGroupPhenotypeBox.isChecked()) {
			if (false) {

				while (itgrp.hasNext()) {
					String grp = itgrp.next().toString();
					DescriptiveStatistics ds = (DescriptiveStatistics) mapGroup2Statistics.get(grp);
					if (ds.getN() > 3) {
						double q1 = ds.getPercentile(25);
						double q3 = ds.getPercentile(75);
						double iqr = q3 - q1;
						double maxvalid = Double.MIN_VALUE;
						double minvalid = Double.MAX_VALUE;
						for (int i = 0; i < ds.getN(); i++) {
							double di = ds.getElement(i);
							if (di < q1 - 1.5 * iqr || di > q3 + 1.5 * iqr)
								listOutliers.add(new Point(Double.valueOf(grp) - 1, di));
							else {
								if (di < minvalid)
									minvalid = di;
								if (di > maxvalid)
									maxvalid = di;
							}
						}
						// model.addValue("Observations", minvalid,
						// q1,ds.getPercentile(50),q3,maxvalid);
						// model.addValue("Observations", ds.getMin(),
						// ds.getPercentile(25),ds.getPercentile(50), ds.getPercentile(75)
						// ,ds.getMax());
					} else
						// model.addValue("Observations", ds.getMin(),
						// ds.getPercentile(25),ds.getPercentile(50), ds.getPercentile(75)
						// ,ds.getMax());

						categories.add(grp);
				}

			} else {
				while (itgrp.hasNext()) {
					String grp = itgrp.next().toString();
					DescriptiveStatistics ds = (DescriptiveStatistics) mapGroup2Statistics.get(grp);
					lMean.add(ds.getMean());
					lVariance.add(ds.getVariance());
					lStdDev.add(ds.getStandardDeviation());

					// model.addValue("Observations", ds.getMin(),
					// ds.getPercentile(25),ds.getPercentile(50), ds.getPercentile(75)
					// ,ds.getMax());
					categories.add(grp);
				}
			}

			AppContext.debug("updatePhenotpeBoxplots models set");
			// chart.getLegend().setEnabled(false);

			AppContext.debug(" XAxis xAxis = chart.getXAxis();");

			// chart.setModel((ChartsModel)null);
			// chart.setType("boxplot");
			XAxis xAxis = chart.getXAxis();
			xAxis.setCategories(categories);
			xAxis.getTitle().setText("K-group");

			AppContext.debug("YAxis yAxis = chart.getYAxis();");
			YAxis yAxis = chart.getYAxis();
			yAxis.getTitle().setText(sPhenotype);
			// chart.setModel(model);

			Series series0 = chart.getSeries(0);
			series0.getTooltip().setHeaderFormat("<em>K-group {point.key}</em><br/>");

			AppContext.debug("Series series1 = chart.getSeries();");
			Series series1 = chart.getSeries();
			series1.getTooltip().setHeaderFormat("<em>K-group {point.key}</em><br/>");
			series1.setName(sPhenotype + " mean");
			series1.setType("column");
			series1.setYAxis(0);
			AppContext.debug("series1.setData(lMean);");
			series1.setData(lMean); // 49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6,
									// 54.4);
			series1.getPlotOptions().getTooltip().setPointFormat("<span style=\"font-weight: bold; color:"
					+ "{series.color}\">{series.name}</span>: <b>{point.y:.1f} mm</b>");

			chart.setVisible(true);

			AppContext.debug("updatePhenotpeErrorbars chart displayed");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	private class AlleleFreqLineData {
//		CategoryModel linecountmajormodel;
//		CategoryModel linecountminormodel;
//		CategoryModel line3rdallelemodel;
//		CategoryModel line4thallelemodel;
//		CategoryModel linepercentmajormodel;
//		CategoryModel linepercentminormodel;
//		CategoryModel linepercent3rdmodel;
//		CategoryModel linepercent4thmodel;
//		String tooltipjs;
//
//		Map<String, List> mapPos2Alleles;
//		Map<String, Map<String, String>> mapPos2Subpop2AllelesCountPercentStr;
//		public Map<String, List> mapPop2Majoralleles;
//	}
//
//	private Map[] displayKgroupAlleleFrequencies(String haplofilename) throws Exception {
//		// Map mapGroup2Var2Props=new LinkedHashMap();
//		// Map mapGroup2Var2Snpstr=new LinkedHashMap();
//		Map mapVars2Props = new LinkedHashMap();
//		Map mapVar2Snpstr = new LinkedHashMap();
//		List listPosRef = new ArrayList();
//		try {
//			// Map mapVar2Group=new HashMap();
//			BufferedReader br = new BufferedReader(
//					new FileReader(AppContext.getTempDir() + haplofilename + ".summary.txt.clustered.txt"));
//			String line = br.readLine();
//			while ((line = br.readLine()) != null) {
//				line = line.trim();
//				line = line.replace("\"", "");
//				if (line.isEmpty())
//					continue;
//				String cols[] = line.split("\t");
//				String grp = cols[7];
//
//				if (grp == null)
//					AppContext.debug("Null group for " + line);
//
//				String props[] = new String[] { cols[2], cols[4], cols[1], cols[3], grp };
//
//				mapVars2Props.put(cols[3], props);
//			}
//			br.close();
//			br = new BufferedReader(new FileReader(AppContext.getTempDir() + haplofilename + ".ped"));
//			int pos = 0;
//			while ((line = br.readLine()) != null) {
//				line = line.trim();
//				line = line.replace("\"", "");
//				if (line.isEmpty())
//					continue;
//				String cols[] = line.split("\t");
//				// String grp=(String)mapVar2Group.get(cols[0]);
//				if (pos != 0 && pos != (cols.length - 6) / 2)
//					throw new RuntimeException("inconsistent pos length in ped file");
//				else
//					pos = (cols.length - 6) / 2;
//
//				String snpstr[] = new String[pos];
//				for (int i = 0; i < 2 * pos; i += 2) {
//					String al1 = cols[6 + i];
//					String al2 = cols[6 + i + 1];
//					if (al1 == null || al2 == null) {
//						AppContext.debug("al1=" + al1 + ", al2=" + al2);
//						throw new RuntimeException("al1=" + al1 + ", al2=" + al2);
//					}
//					if (!al1.equals(al2)) {
//						snpstr[i / 2] = al1 + "/" + al2;
//					} else if (al1.equals("0")) {
//						snpstr[i / 2] = " ";
//					} else
//						snpstr[i / 2] = al1;
//
//				}
//				// mapvar2snp.put(cols[0],snpstr);
//				mapVar2Snpstr.put(cols[0], snpstr);
//			}
//			br.close();
//
//			br = new BufferedReader(new FileReader(AppContext.getTempDir() + haplofilename + ".map"));
//			BufferedReader br2 = new BufferedReader(
//					new FileReader(AppContext.getTempDir() + haplofilename + ".map.ref"));
//			String line2 = null;
//			while ((line = br.readLine()) != null) {
//				line = line.trim();
//				if (line.isEmpty())
//					continue;
//				while ((line2 = br2.readLine()) != null) {
//					line2 = line2.trim();
//					if (line2.isEmpty())
//						continue;
//					String cols1[] = line.split("\t");
//					String cols2[] = line2.split("\t");
//					if (!cols2[0].equals(cols1[1])) {
//						AppContext.debug("inconsistent pos in map file");
//						throw new RuntimeException("inconsistent pos in map file");
//					}
//					// snpid,ctg,pos,ref
//					listPosRef.add(new String[] { cols1[1], cols1[0], cols1[3], cols2[1] });
//					break;
//				}
//			}
//			br.close();
//			br2.close();
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//		/*
//		 * "clust_order" "orig_order" "variety" "iris_id" "pop" "mismatch" "snps" 1 2665
//		 * "MUXIQIU" "B071" "temp" 1 17 2 2676 "YJ30" "CX391" "temp" 1 17 3 2675
//		 * "JINYUAN 85" "CX389" "temp" 1 17 4 2674 "YUNGENG 23" "CX345" "temp" 1 17
//		 * 
//		 * IRIS_313-15909 IRIS_313-15909 0 0 0 -9 C C T T T T T T T T C C G G C C T T C
//		 * C C C G G G G G G A A T T
//		 * 
//		 */
//		/*
//		 * Iterator<Map> itMap=mapGroup2Var2Props.values().iterator();
//		 * while(itMap.hasNext()) { mapVars2Props.putAll(itMap.next()); }
//		 * 
//		 * Map mapVar2Snpstr=new LinkedHashMap();
//		 * itMap=mapGroup2Var2Snpstr.values().iterator(); while(itMap.hasNext()) {
//		 * mapVar2Snpstr.putAll(itMap.next()); }
//		 */
//
//		AppContext.debug("displayKgroupAlleleFrequencies mapVars2Props=" + mapVars2Props.size() + ",mapVar2Snpstr="
//				+ mapVar2Snpstr.size() + ",listPosRef=" + listPosRef.size());
//
//		Map<String, Map<String, Integer>> mapGroup2SubpopCount = new HashMap();
//
//		groupFreqlines = calculateKgroupAlleleFrequencies(mapVars2Props, mapVar2Snpstr, listPosRef,
//				mapGroup2SubpopCount);
//
//		AppContext.debug("mapGroup2SubpopCount=" + mapGroup2SubpopCount);
//
//		// generate blocksnpmatrix
//		// groupFreqlines[0].linepercentmajormodel.get
//
//		List listPopStr = new ArrayList();
//
//		Set setNgroup = new TreeSet();
//		Iterator itPop = new TreeSet(groupFreqlines[0].mapPop2Majoralleles.keySet()).iterator();
//		while (itPop.hasNext()) {
//			try {
//				setNgroup.add(Integer.valueOf((String) itPop.next()));
//			} catch (Exception ex) {
//			}
//		}
//		itPop = setNgroup.iterator();
//
//		// Iterator itPop=new TreeSet(
//		// groupFreqlines[0].mapPop2Majoralleles.keySet()).iterator();
//		int allvarcnt = mapVar2Snpstr.size();
//		while (itPop.hasNext()) {
//			String pop = itPop.next().toString();
//			if (pop.equals("all")) {
//				continue;
//			}
//
//			List listsnp = (List) groupFreqlines[0].mapPop2Majoralleles.get(pop);
//			List all = new ArrayList();
//			all.add(pop);
//			String allcount = "";
//			String subcount = "";
//			int varpoptotal = 0;
//			try {
//				allcount = mapGroup2SubpopCount.get(pop).get("all").toString();
//
//				StringBuffer buff = new StringBuffer();
//				Map<String, Integer> mapVarpop2Count = mapGroup2SubpopCount.get(pop);
//				Iterator<String> itVarpop = mapVarpop2Count.keySet().iterator();
//				while (itVarpop.hasNext()) {
//					String varpop = itVarpop.next();
//					if (varpop.equals("all")) {
//						allcount = mapVarpop2Count.get("all").toString();
//						continue;
//					}
//					try {
//						int varpopcnt = mapVarpop2Count.get(varpop);
//						varpoptotal += varpopcnt;
//						allcount = varpop + ":" + varpopcnt;
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					buff.append(allcount);
//					if (itVarpop.hasNext())
//						buff.append("/");
//				}
//				subcount = buff.toString();
//
//			} catch (Exception ex) {
//				AppContext.debug("pop=" + pop);
//				ex.printStackTrace();
//			}
//
//			// all.add(allcount);
//			all.add(subcount);
//			all.add(varpoptotal);
//			all.add(varpoptotal * 1.0 / allvarcnt);
//
//			all.addAll(listsnp);
//			listPopStr.add(all);
//		}
//
//		AppContext.debug("listPopStr=" + listPopStr.size());
//		try {
//			listboxGroupAlleleMatrix.setItemRenderer(new VargroupListItemRenderer());
//
//			Listhead listHead = (Listhead) listboxGroupAlleleMatrix.getListhead(); // .getChildren().iterator().next();
//			List<Listheader> listheader = listHead.getChildren();
//			listheader.clear();
//			Listheader nheader = new Listheader("KGROUP");
//			nheader.setWidth("40px");
//			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 0));
//			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 0));
//			listheader.add(nheader);
//			// nheader=new Listheader("SAMPLES"); nheader.setWidth("40px");
//			// listheader.add(nheader);
//			nheader = new Listheader("SUBPOPS:COUNT");
//			nheader.setWidth("300px");
//			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 1));
//			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 1));
//			listheader.add(nheader);
//			nheader = new Listheader("VARIETIES");
//			nheader.setWidth("40px");
//			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 2));
//			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 2));
//			listheader.add(nheader);
//			nheader = new Listheader("FREQUENCY");
//			nheader.setWidth("40px");
//			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 3));
//			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 3));
//			listheader.add(nheader);
//
//			Iterator<String[]> itposref = listPosRef.iterator();
//			int colidx = 4;
//			while (itposref.hasNext()) {
//				String headers[] = itposref.next();
//
//				// snpid,ctg,pos,ref
//				nheader = new Listheader(headers[1] + "-" + headers[2]); // nheader.setSort("auto");
//				nheader.setSortAscending(new GroupMatrixListItemSorter(true, colidx));
//				nheader.setSortDescending(new GroupMatrixListItemSorter(false, colidx));
//				colidx++;
//				nheader.setWidth("20px");
//				listheader.add(nheader);
//			}
//			listboxGroupAlleleMatrix.setModel(new SimpleListModel(listPopStr));
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			Messagebox.show("catched exception:" + ex.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
//		}
//
//		Map retmap[] = new Map[] { mapVars2Props, mapVar2Snpstr };
//		AppContext.debug("retmap[]=" + retmap);
//		return retmap;
//	}

//	@Listen("onClick = #buttonDownloadGroupMatrix")
//	public void onclickDownloadGroupMatrix() {
//		try {
//			SimpleListModel m = (SimpleListModel) listboxGroupAlleleMatrix.getModel();
//			Listhead listHead = (Listhead) listboxGroupAlleleMatrix.getListhead(); // .getChildren().iterator().next();
//			Iterator<Component> itlistheader = (Iterator<Component>) listHead.getChildren().iterator();
//			StringBuffer buff = new StringBuffer();
//			while (itlistheader.hasNext()) {
//				Listheader l = (Listheader) itlistheader.next();
//				buff.append(l.getLabel());
//				if (itlistheader.hasNext())
//					buff.append("\t");
//			}
//			buff.append("\n");
//
//			ListitemRenderer rend = listboxGroupAlleleMatrix.getItemRenderer();
//			Map mapKgroup2AppendValues = ((VargroupListItemRenderer) rend).getMapKgroup2AppendValues();
//
//			for (int i = 0; i < m.getSize(); i++) {
//				List<String> el = (List) m.getElementAt(i);
//				Iterator<String> itel = el.iterator();
//				String kgroup = itel.next();
//				buff.append(kgroup).append("\t");
//				while (itel.hasNext()) {
//					Object o = itel.next();
//					String al = "";
//					if (al == null)
//						al = "";
//					else if (o instanceof String) {
//						if (o.equals("0"))
//							al = "";
//						else
//							al = (String) o;
//					} else if (o instanceof Double) {
//						// if(o.equals(0)) al="";
//						// else al= String.format("0.2f", (Double)o);
//						al = String.format("%.02f", ((Double) o).doubleValue());
//					} else if (o instanceof Number) {
//						// if(o.equals(0)) al="";
//						// else al=o.toString();
//						al = o.toString();
//					}
//					buff.append(al);
//					if (itel.hasNext())
//						buff.append("\t");
//				}
//
//				if (mapKgroup2AppendValues != null) {
//					List lVals = (List) mapKgroup2AppendValues.get(kgroup);
//					Iterator itV = lVals.iterator();
//					while (itV.hasNext()) {
//						Object o = itV.next();
//						String al = "";
//						if (o == null) {
//						} else if (o instanceof String) {
//							if (o.equals("0"))
//								al = "";
//							else
//								al = (String) o;
//						} else if (o instanceof Double) {
//							// if(o.equals(0)) al="";
//							// else al= String.format("0.2f", (Double)o);
//							al = String.format("%.02f", (Double) o);
//						} else if (o instanceof Number) {
//							// if(o.equals(0)) al="";
//							// else al=o.toString();
//							al = o.toString();
//						}
//						buff.append("\t").append(al);
//					}
//				}
//
//				if (i < m.getSize() + 1)
//					buff.append("\n");
//			}
//
//			Filedownload.save(buff.toString(), "text/plain", "snp3kgroups-" + queryFilename());
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}

	@Listen("onClick = #buttonHaploUpdate")
	public void onclickUpdateHaplo() {
		try {
			Set dataset = (Set)getSession().getAttribute("dataset");
        	GenotypeQueryParams p = (GenotypeQueryParams)getSession().getAttribute("queryparams");
        	String chr= (String)getSession().getAttribute("contig");
        	VariantTable table= (VariantTable)getSession().getAttribute("table");
        	String queryfilename= (String)getSession().getAttribute("queryfilename");
        	haplofilename = null;
				
        	onselectTabHaplotype((VarietyFacade)AppContext.checkBean(varietyfacade,"VarietyFacade"),dataset,p,chr,  table,
        			queryfilename);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onScroll = #sliderClusterWieght")
	public void onScrollHaloWeight() {
		double curpos = sliderClusterWieght.getCurposInDouble();
		labelGlobal.setValue(String.format("Global %.2f", (1 - curpos)));
		labelLocal.setValue(String.format("Local %.2f", curpos));

	}

	@Listen("onScroll = #sliderCuttreeThreshold")
	public void onScrollCuttreeThresholdt() {
		double curpos = sliderCuttreeThreshold.getCurposInDouble();
		textboxCuttreeThreshold.setValue(String.format("%.2f", curpos));
	}


	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

}
