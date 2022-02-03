package user.ui.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.ds.chado.domain.Gene;
import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.irri.iric.portal.SimpleListModelExt;
import org.irri.iric.portal.admin.AsyncJob;
import org.irri.iric.portal.admin.AsyncJobImpl;
import org.irri.iric.portal.admin.AsyncJobReport;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.PhylotreeQueryParams;
import org.irri.iric.portal.genotype.PhylotreeService;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import org.irri.iric.portal.genotype.zkui.GroupMatrixListItemSorter;
import org.irri.iric.portal.genotype.zkui.Object2StringMultirefsMatrixModel;
import org.irri.iric.portal.genotype.zkui.Object2StringMultirefsMatrixRenderer;
import org.irri.iric.portal.genotype.zkui.SNPEffListitemRenderer;
import org.irri.iric.portal.genotype.zkui.VargroupListItemRenderer;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.variety.service.Data;
import org.irri.iric.portal.variety.zkui.VarietyQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.chart.AxisLabels;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsSelectionEvent;
import org.zkoss.chart.Legend;
import org.zkoss.chart.PlotLine;
import org.zkoss.chart.Point;
import org.zkoss.chart.ResetZoomButton;
import org.zkoss.chart.Series;
import org.zkoss.chart.Theme;
import org.zkoss.chart.Tooltip;
import org.zkoss.chart.XAxis;
import org.zkoss.chart.YAxis;
import org.zkoss.chart.model.BoxPlotModel;
import org.zkoss.chart.model.CategoryModel;
import org.zkoss.chart.model.DefaultBoxPlotModel;
import org.zkoss.chart.model.DefaultCategoryModel;
import org.zkoss.chart.plotOptions.ColumnPlotOptions;
import org.zkoss.json.JavaScriptValue;
import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Biglistbox;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zkmax.zul.MatrixComparatorProvider;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zul.A;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelExt;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import user.ui.module.util.FakerMatrixModel;
import user.ui.module.util.Object2StringMatrixComparatorProvider;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * 
 * @author jumperchen
 */
public class GenotypeQueryController extends SelectorComposer<Window> {

	// INPUT FORM ELEMENTS
	// Dataset Group
	@Wire
	private Menuitem buttonDownloadCsv;
	@Wire
	private Menuitem buttonDownloadTab;
	@Wire
	private Menuitem buttonDownloadPlink;
	@Wire
	private Menuitem buttonDownloadFlapjack;
	@Wire
	private Groupbox datasetGroup;
	@Wire
	private Div datasetIcon;
	@Wire
	private Div currentSite;
	@Wire
	private Bandbox bandboxVarietyset;
	@Wire
	private Listbox listboxVarietyset;
	@Wire
	private Bandbox bandboxVariantset;
	@Wire
	private Listbox listboxVariantset;
	@Wire
	private Div divRunid;
	@Wire
	private Bandbox bandboxRunid;
	@Wire
	private Listbox listboxRunid;
	@Wire
	private Combobox comboVar1;
	@Wire
	private Label labelExampleVariety;
	@Wire
	private Combobox comboVar2;
	@Wire
	private Radio radioUseAccession;
	@Wire
	private Radio radioUseVarname;
	@Wire
	private Listbox listboxSubpopulation;
	@Wire
	private Checkbox checkboxAllvarieties;
	@Wire
	private Listbox listboxMyVarieties;
	// End Dataset Group

	// Region Group
	@Wire
	private Listbox listboxReference;
	@Wire
	private Checkbox checkboxShowNPBPosition;
	@Wire
	private Checkbox checkboxShowAllRefAlleles;
	@Wire
	private Combobox selectChr;
	@Wire
	private Intbox intStart;
	@Wire
	private Intbox intStop;
	@Wire
	private Combobox comboGene;
	@Wire
	private Listbox listboxMySNPList;
	@Wire
	private Listbox listboxMyLocusList;
	@Wire
	private Listbox listboxAlleleFilter;
	@Wire
	private Listbox listboxVarietyAlleleFilter;
	@Wire
	private Listbox listboxDatasetSnpOps;
	// END REGION GROUP

	// START OPTION GROUP
	@Wire
	private Checkbox checkboxIndel;
	@Wire
	private Listbox listboxPhenotype;
	@Wire
	private Radio radioLegacyTrait;
	@Wire
	private Radio radioCoTrait;
	@Wire
	private Checkbox checkboxMismatchOnly;
	@Wire
	private Radio radioAllSnps;
	@Wire
	private Radio radioNonsynHighlights;
	@Wire
	private Radio radioNonsynSnps;
	@Wire
	private Radio radioNonsynSnpsPlusSplice;
	@Wire
	private Listbox listboxMissingAllele;

	// END OPTION GROUP

	// START ADVANCED OPTION GROUP
	@Wire
	private Radio radioColorMismatch;
	@Wire
	private Radio radioColorAllele;

	// START RESULT PANELS
	@Wire
	private Div result_sideBarDiv;
	// unused
	// @Wire
	// private Listbox listboxSNPListAlleles;
	// @Wire
	// private Listbox listboxHighlightVarietyList;
	// END REFACTOR

	@Wire
	private Charts chartAlleleFrequency;
	@Wire
	private Radio radioShowAlleleFrequency;
	@Wire
	private Radio radioMajorAlleles;
	@Wire
	private Radio radioMinorAlleles;
	@Wire
	private Radio radio3rdAlleles;
	@Wire
	private Radio radio4thAlleles;
	@Wire
	private Radio radioShowAlleleCount;
	@Wire
	private Radio radioShowGenotypeFrequency;
	@Wire
	private Radio radioShowGenotypeCount;
	// number of frozen columns in variant table display
	int frozenCols = AppContext.getSnpMatrixFrozenCols(); // 4; // with dataset

	// SNP EFF Table
	@Wire
	private Listbox listboxSnpeff;

	// HAPLOTYPE VIEW
	@Wire
	private Tab tabHaploHaploview;
	@Wire
	private Tab tabHaploAutogroups;
	@Wire
	private Tab tabHaploTree;
	@Wire
	private Tab tabHaploGroupAlleles;
	@Wire
	private Listbox listboxGroupVarietyPhenotypeStacked;
	@Wire
	private Label labelGroupVarietyPhenotype;
	@Wire
	private Listbox listboxHaploResolution;
	@Wire
	private Button buttonHaploOrder;
	@Wire
	private Button buttonHaploImage;
	@Wire
	private Slider sliderClusterWieght;
	@Wire
	private Listbox listboxKgroupMethod;
	@Wire
	private Slider sliderCuttreeThreshold;
	@Wire
	private Listbox listboxAutogroup;
	@Wire
	private Listbox listboxImagesize;
	@Wire
	private Vbox vboxGroupAlleleFrequency;
	@Wire
	private Iframe iframeHaplotype;
	@Wire
	private Iframe iframeAutogroups;

	// MDS PLOT VIEW
	@Wire
	private Charts chartMDS;
	@Wire
	private Listbox listboxHighlightVariety;
	@Wire
	private Listbox listboxHighlightVarietyList;
	// distance matrix from phylo and mds
	private List varpairDistance;
	@Wire
	private Label labelMDSPhenotype;
	@Wire
	private Listbox listboxGroupAlleleMatrix;
	@Wire
	private Div divGroupPhenoQuant;
	@Wire
	private Div divGroupPhenoCat;
	@Wire
	private Button buttonAddPhenValuesToMatrix;
	@Wire
	private Charts chartGroupPhenotypeStacked;
	private Map<String, Collection> mapKgroupCat2Varieties;
	@Wire
	private Radio radioGroupPhenotypeBox;
	@Wire
	private Radio radioGroupPhenotypeErrorbars;
	@Wire
	private Checkbox checkboxNormalizeStackedCategories;

	@Wire
	private Charts chartGroupPhenotypeBox;
	@Wire
	private Checkbox checkboxOutlierGroupPhenotypeBox;
	@Wire
	private Div divGroupPhenoQuantBox;
	@Wire
	private Charts chartGroupPhenotypeErrorbars;

	// GALAXY
	private boolean refreshgalaxy;
	@Wire
	private Include includeGalaxy;

	@Wire
	private GoldenPanel goldenPA_snpeffect;

	@Wire
	private GoldenPanel goldenPA_haplotype;

	@Wire
	private GoldenPanel goldenPanel_mdsPlot;

	@Wire
	private Label nmVarietyResult;

	@Wire
	private Label nmPosResult;

	// VARIABLE
	private int nAllelefreqLabels = 25;
	// data for allele and genotype frequence chart
	private AlleleFreqLineData[] varFreqlines = null;
	private AlleleFreqLineData[] groupFreqlines = null;
	private String urljbrowse, gfffile, urlphylo, urljbrowsephylo;

	private String haplofilename;

	private double hctreemaxlog2height = -1;
	private double hctreeminlog2height = -1;

	private Map mapVars2PropSnpstr[];

	@Wire
	private Div tip;

	@Wire
	private Div backQueryDiv;

	@Wire
	private Div regionIcon;

	@Wire
	private Div optionIcon;

	@Wire
	private Div initDiv;

	@Wire
	private Div resultContentDiv;

	@Wire
	private Div resultDiv;

	@Wire
	private A resultLink;

	@Wire
	private Checkbox resultPanelBox;

	@Wire
	private Div resultDiv2;

	@Wire
	private Checkbox resultPanelBox2;

	@Wire
	private A alleleLink;

	@Wire
	private Div resultDiv3;

	@Wire
	private Checkbox resultPanelBox3;

	@Wire
	private A seLink;

	@Wire
	private Div resultDiv4;

	@Wire
	private A jbrowseLink;

	@Wire
	private Checkbox resultPanelBox4;

	@Wire
	private Div resultDiv5;

	@Wire
	private A haplotypeLink;

	@Wire
	private Checkbox resultPanelBox5;

	@Wire
	private Div resultDiv6;

	@Wire
	private A compareGenomesLink;

	@Wire
	private Checkbox resultPanelBox6;

	@Wire
	private Div resultDiv7;

	@Wire
	private A mdsplotLink;

	@Wire
	private Checkbox resultPanelBox7;

	@Wire
	private Div resultDiv8;

	@Wire
	private A galaxyLink;

	@Wire
	private Div collapseDiv;

	@Wire
	private A collapseLink;

	@Wire
	private Textbox content;

	@Wire
	private Iframe iframeJbrowse;

	@Wire
	private Button searchButton;

	@Wire
	private Span resultSpan;

	@Wire
	private Span resultSpan2;

	@Wire
	private Span resultSpan3;

	@Wire
	private Span resultSpan4;

	@Wire
	private Span resultSpan5;

	@Wire
	private Span resultSpan6;

	@Wire
	private Span resultSpan7;

	@Wire
	private Span resultSpan8;

	@Wire
	private Span collapseSpan;

	@Wire
	private Button minResult;

	@Wire
	private Groupbox regionGroup;

	@Wire
	private Groupbox optionsGroup;
	@Wire
	private Biglistbox biglistboxArray;

	@Wire
	private Div divDownloadOnlyMsg;

	@Wire
	private Div resultHeader;

	@Wire
	private Label labelDownloadOnlyMsg;
	@Wire
	private Auxheader auxheader3Phenotype;
	@Wire
	private Radio radioWait;
	@Wire
	private Radio radioAsync;
	@Wire
	private Label labelDownloadProgressMsg;
	@Wire
	private A aDownloadProgressURL;
	@Wire
	private Listbox listboxSnpresult;
	@Wire
	private Div divPairwise;

	private Object2StringMultirefsMatrixModel biglistboxModel = null;

	private String sPhenotype;
	private boolean sePanelLoaded;
	private boolean haplotypePanelLoaded;
	private boolean mdsPLotPanelLoaded;
	private boolean galaxyPanelLoaded;

	// holds SNP result for display
	private VariantStringData queryResult;

	// holds SNP result for variant table display
	private VariantTable varianttable;

	// number of visible rows in variant table display
	private int biglistboxRows = 16;

	// bottom visible row in variant table display
	private int lastY;

	private MatrixComparatorProvider matriccmpproviderAsc;
	private MatrixComparatorProvider matriccmpproviderDesc;

	@Wire
	private Biglistbox biglistboxArrayLarge;

	@Wire
	private Button buttonSearch;

	final private String classGenotypefacade = "GenotypeFacade";

	GenotypeQueryParams params;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	@Autowired
	@Qualifier("GenomicsFacade")
	private GenomicsFacade genomicsfacade;

	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade varietyfacade;
	
	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade_orig;

	Map<BigDecimal, Object> mapVarid2Phenotype;

	// holds SNP query result
	private VariantStringData queryRawResult;

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		List<String> listReference = genotype.getReferenceGenomes();
		listboxReference.setModel(new SimpleListModel<String>(listReference));
		if (listReference.size() > 0)
			listboxReference.setSelectedIndex(0);

		initAllList(true);

		setDatasetSubsection();
		setRegionSubsection();

		biglistboxArray.setModel(new FakerMatrixModel(0, 0));
		System.out.println("BIG LIST BOX" + biglistboxArray.isAutoCols());
		biglistboxArray.setColWidth("100px");

		initFrom();

		iframeJbrowse
				.setSrc("https://snp-seek.irri.org/jbrowse/?loc=chr01%3A2901..10815&tracks=DNA%2Cmsu7gff&highlight=");
		System.out.println("setting div");

	}

	/**
	 * Initializes content for list
	 */
	void initAllList() {
		initAllList(false);
	}

	/**
	 * Initializes content for list
	 */
	void initAllList(boolean create) {

		List listVarlistNames = new ArrayList();
		listVarlistNames.add("");
		listVarlistNames.addAll(workspace.getVarietylistNames());
		listVarlistNames.add("create new list...");

		java.util.List listSNPlistNames = new java.util.ArrayList();
		listSNPlistNames.add("");
		listSNPlistNames.addAll(workspace.getSnpPositionListNames());
		listSNPlistNames.add("create new list...");

		java.util.List listSNPlistAlleleNames = new java.util.ArrayList();
		listSNPlistAlleleNames.add("");
		listSNPlistAlleleNames.addAll(workspace.getSnpPositionAlleleListNames());
		listSNPlistAlleleNames.add("create new list...");

		java.util.List listLocuslistNames = new java.util.ArrayList();

		listLocuslistNames.add("");
		listLocuslistNames.addAll(workspace.getLocuslistNames());
		listLocuslistNames.add("create new list...");

		if (create) {
			listboxMySNPList.setModel(new ListModelList(listSNPlistNames));
			listboxMyLocusList.setModel(new ListModelList(listLocuslistNames));
			listboxAlleleFilter.setModel(new ListModelList(listSNPlistAlleleNames));
			// Unuse so far
			// listboxSNPListAlleles.setModel(new ListModelList(listSNPlistAlleleNames));
			// listboxHighlightVarietyList.setModel(new ListModelList(listVarlistNames));
			listboxMyVarieties.setModel(new ListModelList(listVarlistNames));
		} else {
			if (listSNPlistNames.size() != listboxMySNPList.getRows()) {
				((ListModelList) listboxMySNPList.getModel()).clear();
				((ListModelList) listboxMySNPList.getModel()).addAll(listSNPlistNames);
			}
			if (listLocuslistNames.size() != listboxMyLocusList.getRows()) {
				((ListModelList) listboxMyLocusList.getModel()).clear();
				((ListModelList) listboxMyLocusList.getModel()).addAll(listLocuslistNames);
			}
			if (listSNPlistAlleleNames.size() != listboxAlleleFilter.getRows()) {
				((ListModelList) listboxAlleleFilter.getModel()).clear();
				((ListModelList) listboxAlleleFilter.getModel()).addAll(listSNPlistAlleleNames);
			}
			// unused so far
			// if (listSNPlistAlleleNames.size() != listboxSNPListAlleles.getRows()) {
			// ((ListModelList) listboxSNPListAlleles.getModel()).clear();
			// ((ListModelList)
			// listboxSNPListAlleles.getModel()).addAll(listSNPlistAlleleNames);
			// }
			// if (listVarlistNames.size() != listboxHighlightVarietyList.getRows()) {
			// ((ListModelList) listboxHighlightVarietyList.getModel()).clear();
			// ((ListModelList)
			// listboxHighlightVarietyList.getModel()).addAll(listVarlistNames);
			// }
			if (listVarlistNames.size() != listboxMyVarieties.getRows()) {
				((ListModelList) listboxMyVarieties.getModel()).clear();
				((ListModelList) listboxMyVarieties.getModel()).addAll(listVarlistNames);
			}
		}

		listboxMyLocusList.setSelectedIndex(0);
		listboxMySNPList.setSelectedIndex(0);
		listboxAlleleFilter.setSelectedIndex(0);
		// Unuse so far
		// listboxSNPListAlleles.setSelectedIndex(0);
		// listboxHighlightVarietyList.setSelectedIndex(0);
		listboxMyVarieties.setSelectedIndex(0);

	}

	/**
	 * Set Region Subsection
	 */
	private void setRegionSubsection() {
		comboGene.setModel(getGeneList());
		setChrCombo();

		List<String> listReference;

		try {
			listReference = genotype.getReferenceGenomes();

			listboxReference.setModel(new SimpleListModel<String>(listReference));
			if (listReference.size() > 0)
				listboxReference.setSelectedIndex(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return A list of Gene Names
	 */
	private ListModel<?> getGeneList() {
		List<String> genenames = genotype.getGenenames();
		ListModel<String> geneComboModel = new SimpleListModelExt(genenames);

		return geneComboModel;
	}

	@Listen("onChange = #comboGene")
	public void onChange$Gene() {
		onSelect$Gene();
	}

	@Listen("onSelect = #comboGene")
	public void onSelect$Gene() {
		if (!comboGene.getValue().isEmpty()) {
			try {
				genotype = (GenotypeFacade) AppContext.checkBean(genotype, classGenotypefacade);
				Locus gene = guessGene(comboGene.getValue().trim());

				if (gene == null) {
					return;
				}

				selectChr.setValue(gene.getContig().replace("0", "").toUpperCase());

				intStart.setValue(gene.getFmin());
				intStop.setValue(gene.getFmax());
				listboxMySNPList.setSelectedIndex(0);
				this.listboxMyLocusList.setSelectedIndex(0);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Populate CHRs in Combo Box
	 */
	private void setChrCombo() {
		List<String> listContigs = genotype.getContigsForReference(AppContext.getDefaultOrganism());
		// author: bohemian
		// https://stackoverflow.com/questions/13973503/sorting-strings-that-contains-number-in-java
		Collections.sort(listContigs, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return extractInt(o1) - extractInt(o2);
			}

			int extractInt(String s) {
				String num = s.replaceAll("\\D", "");
				// return 0 if no digits found
				return num.isEmpty() ? 0 : Integer.parseInt(num);
			}
		});
		// End sort

		if (listContigs != null && !listContigs.isEmpty()) {
			selectChr.setModel(new SimpleListModelExt(listContigs));
		}

	}

	private Locus guessGene(String genename) {
		// fill the contig,start,stop boxes
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, classGenotypefacade);

		Gene gene = genotype.getGeneFromName(genename, this.listboxReference.getSelectedItem().getLabel());

		if (gene == null) {
			genomicsfacade = (GenomicsFacade) AppContext.checkBean(genomicsfacade, "GenomicsFacade");
			TextSearchOptions options = new TextSearchOptions(comboGene.getValue(), false, true, false);

			List<Locus> loclist = genomicsfacade.getLociBySynonym(options,
					this.listboxReference.getSelectedItem().getLabel(), "msu7");
			if (loclist.size() == 0) {
				loclist = genomicsfacade.getLociBySynonym(options, this.listboxReference.getSelectedItem().getLabel(),
						"rap");
			}

			if (loclist.size() == 0) {
				Messagebox.show("Cannot find coordinates for gene locus " + comboGene.getValue());
				return null;
			} else if (loclist.size() > 1) {
				StringBuffer buff = new StringBuffer();
				buff.append("Multiple loci (" + loclist.size() + ") matched name " + comboGene.getValue()
						+ ", select only one from this list. ");
				Iterator<Locus> itLoc = loclist.iterator();
				while (itLoc.hasNext()) {
					Locus thisloc = itLoc.next();
					buff.append(thisloc.getUniquename() + " [" + thisloc.getContig() + ":" + thisloc.getFmin() + ".."
							+ thisloc.getFmax() + "]");
					if (itLoc.hasNext())
						buff.append(",");
				}
				Messagebox.show(buff.toString());
				return null;
			}

			return loclist.get(0);

		}

		return gene;
	}

	/**
	 * Set Dataset Subsection
	 * 
	 */
	private void setDatasetSubsection() {
		setVarietyset();
		setVariantset();

		listboxSubpopulation.setModel(getSubPopulationModel());
		listboxSubpopulation.setSelectedIndex(1);

		fillVariantsetListbox();

	}

	private ListModel<String> getSubPopulationModel() {
		List<String> subpopulations = genotype.getSubpopulations(VarietyFacade.DATASET_SNPINDELV2_IUPAC);

		List<String> subpopulationstmp = new ArrayList<String>();
		subpopulationstmp.add("all varieties");
		subpopulationstmp.addAll(subpopulations);
		subpopulations = AppContext.createUniqueUpperLowerStrings(subpopulationstmp, false, true);
		subpopulationstmp = null;

		return new SimpleListModel<String>(subpopulations);
	}

	private List getGenotyperun() {
		List l = new ArrayList();
		Set intrunids = getRunids();
		Iterator it = genotype.getGenotyperuns(getDataset(), getVariantset(), "SNP").iterator();
		while (it.hasNext()) {
			GenotypeRunPlatform r = (GenotypeRunPlatform) it.next();
			if (intrunids.contains(r.getGenotypeRunId()) || intrunids.size() == 0)
				l.add(r);
		}
		return l;
	}

	/**
	 * read the query components to GenotypeQueryParams
	 * 
	 * @return
	 */
	private GenotypeQueryParams fillGenotypeQueryParams() {

		Set setVarieties = null;
		String sSubpopulation = null;

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		String contig = selectChr.getValue();

		if (listboxSubpopulation.getSelectedIndex() > 1) {
			sSubpopulation = listboxSubpopulation.getSelectedItem().getLabel();
			setVarieties = genotype.getVarietiesForSubpopulation(sSubpopulation, getDataset());
		} else if (listboxMyVarieties.getSelectedIndex() > 0) {
			setVarieties = workspace.getVarieties(listboxMyVarieties.getSelectedItem().getLabel());
		}

		Set snpposlist = null;

		if (listboxMySNPList.getSelectedIndex() > 0) {
			String chrlistname[] = listboxMySNPList.getSelectedItem().getLabel().split(":");
			// snpposlist = workspace.getSnpPositions( Integer.valueOf(
			// chrlistname[0].replace("CHR","").trim() ) , chrlistname[1].trim() );
			snpposlist = workspace.getSnpPositions(chrlistname[0].trim(), chrlistname[1].trim());
			contig = chrlistname[0].trim();
		}
		/*
		 * listboxAlleleFilter: Match genotype: snp/alleles
		 * 
		 */
		else if (listboxAlleleFilter.getSelectedIndex() > 0) {
			String chrlistname[] = listboxAlleleFilter.getSelectedItem().getLabel().split(":");
			// snpposlist = workspace.getSnpPositions( Integer.valueOf(
			// chrlistname[0].replace("CHR","").trim() ) , chrlistname[1].trim() );
			snpposlist = workspace.getSnpPositions(chrlistname[0].trim(), chrlistname[1].trim());
			contig = chrlistname[0].trim();
		}

		Set locuslist = null;
		if ((listboxMyLocusList.getSelectedIndex() > 0) && (listboxMyLocusList.getSelectedItem() != null)) {
			locuslist = workspace.getLoci(listboxMyLocusList.getSelectedItem().getLabel());
			contig = "loci";
		}

		String genename = comboGene.getValue().trim().toUpperCase();
		String sLocus = null;
		if (!genename.isEmpty())
			sLocus = genename;

		Long lStart = null;
		Long lStop = null;
		if (intStart.getValue() != null)
			lStart = new Long(intStart.getValue());
		if (intStop.getValue() != null)
			lStop = new Long(intStop.getValue());

		Set selDataset = getDataset();
		Set runs = new HashSet(getGenotyperun());
		if (checkboxIndel.isChecked()) {
			// add indel runs with the same dataset
			for (GenotypeRunPlatform p : genotype.getGenotyperuns("indel")) {
				if (selDataset.contains(p.getDataset())) {
					runs.add(p);
				}
			}
		}

		GenotypeQueryParams params = new GenotypeQueryParams(setVarieties, contig, lStart, lStop, true,
				checkboxIndel.isChecked(), getVariantset(), getDataset(), runs, checkboxMismatchOnly.isChecked(),
				snpposlist, sSubpopulation, sLocus, false, checkboxShowAllRefAlleles.isChecked());
		params.setColors(this.radioColorMismatch.isSelected(), this.radioColorAllele.isSelected());
		params.setIncludedSnps(this.radioNonsynHighlights.isSelected(), this.radioNonsynSnps.isSelected(),
				this.radioNonsynSnpsPlusSplice.isSelected());
		if (this.comboVar1.getValue() != null && !this.comboVar1.getValue().isEmpty()
				&& this.comboVar2.getValue() != null && !this.comboVar2.getValue().isEmpty()) {
			params.setPairwiseComparison(comboVar1.getValue(), comboVar2.getValue(),
					this.radioUseAccession.isSelected());

			if (checkboxIndel.isChecked()) {
				Messagebox.show("Indels not yet included in pairwise comparison.");
				params.setbIndel(false);
				checkboxIndel.setChecked(false);
			}
		}

		if (lStop != null && lStart != null) {
			if (lStop - lStart > AppContext.getMaxlengthUni(getVariantset()))
				params.setbDownloadOnly(true);
		} else if (snpposlist != null) {
			if (snpposlist.size() > AppContext.getMaxSNPList(getVariantset()))
				params.setbDownloadOnly(true);
		} else if (locuslist != null) {
			if (locuslist.size() > AppContext.getMaxLocusList(getVariantset()))
				params.setbDownloadOnly(true);
		}

		// NOTE: unused elements
		// params.setbHeteroIndel(checkboxHeteroindels.isChecked());
		params.setColLoci(locuslist);

		// force false
		checkboxShowNPBPosition.setChecked(false);
		params.setOrganism(listboxReference.getSelectedItem().getLabel(), this.checkboxShowNPBPosition.isChecked(),
				true);

		params.setbAlleleFilter(this.listboxAlleleFilter.getSelectedIndex() > 0);

		if (this.listboxVarietyAlleleFilter.getSelectedIndex() > 0) {
			if (this.radioUseAccession.isSelected())
				params.setVarAlleleFilter(varietyfacade
						.getGermplasmByAccession(
								((String) listboxVarietyAlleleFilter.getSelectedItem().getValue()).trim(), getDataset())
						.getVarietyId());
			else
				params.setVarAlleleFilter(varietyfacade
						.getGermplasmByName(((String) listboxVarietyAlleleFilter.getSelectedItem().getValue()).trim(),
								getDataset())
						.iterator().next().getVarietyId());
		}

		params.setbCountMissingAs05(!listboxMissingAllele.getSelectedItem().getValue().equals("ignore"));

		// NOTE: unused elements
		// params.setbWaitResult(this.radioWait.isSelected());

		params.setDatasetPosOps(listboxDatasetSnpOps.getSelectedItem().getLabel());

		if (listboxPhenotype.getSelectedItem() != null) {
			// get Legacy trait, not CO TERMs
			String paramTrait;

			String input = listboxPhenotype.getSelectedItem().getLabel();
			if (!input.contains("::"))
				paramTrait = input;
			else {
				String[] coTerm = input.split("::");
				paramTrait = coTerm[1];
			}

			params.setPhenotype(paramTrait);
		}

		if (params.isbDownloadOnly()) {
			// NOTE: unused elements
			// params.setbGenerateHapmap(checkboxCreateHaplotype.isChecked());
			System.out.println("WARNING: REMOVED ELEMENT: create haplotype element");
		} else {
			params.setbGenerateHapmap(false);
		}

		return params;

	}

	@Listen("onClick=#backQueryDiv")
	public void onClick$backQueryDiv() {
		Clients.evalJavaScript("myFunction();");
	}

	@Listen("onActive=#goldenPA_snpeffect")
	public void onClick$goldenPA_sePanel() {
		if (!sePanelLoaded) {
			if (queryResult == null)
				return;

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
			List listSnpeffs = genotype.getSnpEffects(queryResult.getListPos());
			listboxSnpeff.setItemRenderer(new SNPEffListitemRenderer());
			this.listboxSnpeff.setModel(new SimpleListModel(listSnpeffs));
			sePanelLoaded = true;
		}
	}

	@Listen("onActive=#goldenPA_galaxy")
	public void onClick$goldenPA_galaxy() {
		System.out.println("galaxy");
		if (false) {
			GenotypeQueryParams p = fillGenotypeQueryParams();
			String filename = "snp3kvars-" + queryFilename();
			Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray
					.getModel();
			VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
			haplofilename = "snp3kvars-" + queryFilename();

			Map mapParamvals = new HashMap();
			mapParamvals.put("filename", filename);
			mapParamvals.put("query", p);
			mapParamvals.put("table", table);
			mapParamvals.put("reference", this.listboxReference.getSelectedItem().getLabel());

			if (listboxPhenotype.getSelectedIndex() > 0) {
				String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
				mapParamvals.put("sample2pheno", varietyfacade.getPhenotypeValues(sPhenotype, getDataset()));
				mapParamvals.put("phenoname", sPhenotype);
			}

			AppContext.debug("update galaxy page");

			getSession().removeAttribute("param_vals");
			getSession().putValue("param_vals", mapParamvals);

			if (refreshgalaxy) {
				// new query parameters
				includeGalaxy.setSrc("galaxy.zul?embed=genotype&refresh=1");
				includeGalaxy.setSrc("galaxy.zul?embed=genotype");
			} else
				includeGalaxy.setSrc("galaxy.zul?embed=genotype");

			refreshgalaxy = false;

			galaxyPanelLoaded = true;

		}
	}

	@Listen("onActive=#goldenPanel_mdsPlot")
	public void onClick$goldenPanel_mdsPlot() {

		if (!mdsPLotPanelLoaded) {
			AppContext.debug("selected index: " + listboxPhenotype.getSelectedIndex());
			if (listboxPhenotype.getSelectedIndex() == 0)
				show_mds_fromtable(chartMDS);
			else {

				List listvarnames = new ArrayList();
				Set varnames = new TreeSet();
				// listvarnames.add("");
				java.util.Iterator<BigDecimal> itvars = queryResult.getMapVariety2Order().keySet().iterator();
				while (itvars.hasNext()) {
					BigDecimal varid = itvars.next();
					varnames.add(((Variety) varietyfacade.getMapId2Variety(getDataset()).get(varid)).getName()
							.toUpperCase());
				}
				listvarnames.addAll(varnames);
				listboxHighlightVariety.setModel(new SimpleListModel(listvarnames));

				onSelect$Phenotype();

			}

			mdsPLotPanelLoaded = true;

		}
	}

	@Listen("onClick=#searchButton")
	public void queryVariants() {

		Clients.evalJavaScript("myFunction();");

		try {
			refreshgalaxy = true;
			sePanelLoaded = false;
			haplotypePanelLoaded = false;
			mdsPLotPanelLoaded = false;
			galaxyPanelLoaded = false;

			params = fillGenotypeQueryParams();

			GenotypeQueryParams params2 = params;
			params2.setIncludedSnps(false, false, false);

			queryRawResult = genotype.queryGenotype(params2);

			varianttable = new VariantAlignmentTableArraysImpl();
			varianttable = genotype.fillGenotypeTable(varianttable, queryRawResult, params);

			queryResult = varianttable.getVariantStringData();

			lastY = 0;

			List listSNPs = new java.util.ArrayList();

			mapVarid2Phenotype = null;
			sPhenotype = null;
			if (params.getPhenotype() != null && !params.getPhenotype().isEmpty()) {
				sPhenotype = params.getPhenotype();
				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
			}

			biglistboxArray.setRowHeight("29px");
			biglistboxArray.setColWidth("35px");

			// BIGLISTBOX

			Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(queryResult,
					params);
			biglistboxArray.setMatrixRenderer(renderer);
			biglistboxModel = new Object2StringMultirefsMatrixModel(varianttable,
					params /* fillGenotypeQueryParams() */, varietyfacade.getMapId2Sample(params.getDataset()),
					mapVarid2Phenotype, sPhenotype);
			biglistboxModel.setHeaderRows(biglistboxRows, lastY);
			biglistboxArray.setModel(biglistboxModel); // strRef));

			matriccmpproviderAsc = new Object2StringMatrixComparatorProvider<Object[]>(true);
			matriccmpproviderDesc = new Object2StringMatrixComparatorProvider<Object[]>(false);
			biglistboxArray.setSortAscending(matriccmpproviderAsc);
			biglistboxArray.setSortDescending(matriccmpproviderDesc);

			biglistboxModel.setHeaderRows(biglistboxRows, lastY);
			Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();

			biglistboxArray.setVisible(true);

			if (queryResult.getListPos() != null) {
				if (listSNPs.size() > 0) {
					// setup allele filters
					java.util.List listPosition = new java.util.ArrayList();
					listPosition.add("");

					model = (Object2StringMultirefsMatrixModel) this.biglistboxArray.getModel();
					int poscol = -1;
					List listPos = (List) model.getHeadAt(0);
					for (int i = 0; i < model.getColumnSize(); i++) {
						listPosition.add(listPos.get(i).toString());
					}

//				listboxPosition.setModel(new SimpleListModel(listPosition));
//				labelFilterResult.setValue("None .. " + listSNPs.size() + " varieties");
//				hboxFilterAllele.setVisible(true);
//				hboxDownload.setVisible(true);
				}
			}

			// SET RESULT PANELS
			nmVarietyResult.setValue("" + queryResult.getListVariantsString().size());
			nmPosResult.setValue("" + queryResult.getListPos().size());
			resultHeader.setVisible(true);
			initDiv.setVisible(false);
			// result_sideBarDiv.setVisible(true);
			// resultPanel.setVisible(true);
//			aFrequencyPanel.setVisible(true);
			// resultPanelBox.setChecked(true);
			resultContentDiv.setVisible(true);

			updateAlleleFrequencyChart();
			calculateAlleleFrequencies();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * for bigmatrix viewer
	 */

	class BiglistRowheaderModel extends SimpleListModel implements ListModelExt {

		private Comparator<Cell> _sorting;
		private boolean _sortDir = true;

		public BiglistRowheaderModel(List data) {
			super(data);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void sort(Comparator cmpr, boolean ascending) {

			try {
				// sort biglistboxarray
				_sorting = cmpr;
				_sortDir = ascending;

				AppContext.debug("sorting biglist from header");

				Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray
						.getModel();
				model.sort(cmpr, ascending);

			} catch (Exception ex) {
				ex.printStackTrace();

			}

		}

		@Override
		public String getSortDirection(Comparator cmpr) {

			if (Objects.equals(_sorting, cmpr))
				return _sortDir ? "ascending" : "descending";
			return "natural";
		}

	}

	/**
	 * Result Eventhandler
	 * 
	 ***/

	public static Map orderMap(Map orig) {
		Map map = new TreeMap();
		Iterator it = orig.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			map.put(orig.get(key), key);
		}
		return map;
	}

	private String queryFilename() {
		String filename = comboGene.getValue();
		if (filename.isEmpty()) {
			if (intStart != null && intStart.getValue() != null && intStop != null && intStop.getValue() != null)
				filename = selectChr.getValue() + "-" + intStart.getValue() + "-" + intStop.getValue();
			else if (this.listboxMySNPList.getSelectedIndex() > 0) {
				filename = this.listboxMySNPList.getSelectedItem().getLabel();
			} else if (this.listboxAlleleFilter.getSelectedIndex() > 0) {
				filename = "match-" + this.listboxAlleleFilter.getSelectedItem().getLabel();
			} else if (this.listboxMyLocusList.getSelectedIndex() > 0) {
				filename = this.listboxMyLocusList.getSelectedItem().getLabel();
			}

		}
		return filename.replace(":", "-").replace(" ", "") + "-" + AppContext.createTempFilename();
	}

	private void writeSummary(VarietyFacade varietyfacade, Set dataset, boolean first, GenotypeQueryParams params,
			Map mapVarid2Order, Map mapVarid2Score, Map mapVarid2Columns) {
		writeSummary(varietyfacade, dataset, first, params, mapVarid2Order, mapVarid2Score, mapVarid2Columns, false);
	}

	public static void writeSummary(VarietyFacade varietyfacade, Set dataset, boolean first, GenotypeQueryParams params,
			Map mapVarid2Order, Map mapVarid2Score, Map mapVarid2Columns, boolean plink) {
		try {
			String finalfilename = params.getFilename();
			File fsummary = new File(finalfilename + ".summary.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(fsummary, !first));

			if (first) {
				bw.append("VARIETY").append("\t");
				bw.append("ASSAY");
				bw.append("\t").append("SUBPOPULATION").append("\t");
				if (params.isbAlleleFilter())
					bw.append("MATCH\t");
				else
					bw.append("MISMATCH\t");
				bw.append("COLUMNS\n");
			}
			NumberFormat formatter = new DecimalFormat("#0.00");

			Map<BigDecimal, Variety> mapId2Var = varietyfacade.getMapId2Variety(params.getDataset());

			Map<Integer, BigDecimal> mapord2var = orderMap(mapVarid2Order);
			Iterator<Integer> itOrder = mapord2var.keySet().iterator();
			while (itOrder.hasNext()) {
				Integer ord = itOrder.next();
				BigDecimal varid = mapord2var.get(ord);
				Variety var = mapId2Var.get(varid);
				bw.append("\"").append(var.getName()).append("\"").append("\t");

				String irisid = var.getIrisId();
				if (plink) {
					if (irisid == null || irisid.isEmpty() || irisid.equals("NA"))
						irisid = var.getAccession();
					if (irisid == null || irisid.isEmpty() || irisid.equals("NA"))
						irisid = var.getName();
					String indvid = irisid.replaceAll(" ", "_");
					if (dataset.contains("hdra"))
						indvid += "_" + var.getVarietyId();
					irisid = indvid;
				} else {
					if (irisid == null || irisid.isEmpty())
						irisid = var.getAccession();
					if (irisid == null || irisid.isEmpty())
						irisid = var.getName();
					irisid = irisid.replaceAll(" ", "_");
				}

				bw.append(irisid);

				bw.append("\t").append(var.getSubpopulation()).append("\t")
						.append(formatter.format(mapVarid2Score.get(varid))).append("\t")
						.append(mapVarid2Columns.get(varid).toString()).append("\n");
			}

			bw.flush();
			bw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void generateBigListboxPlink(VarietyFacade varietyfacade, GenotypeQueryParams p, String chr, Set ds,
			VariantTable table1, String filename) {
		generateBigListboxPlink(varietyfacade, p, chr, ds, table1, filename, null, null);
	}

	public static void generateBigListboxPlink(VarietyFacade varietyfacade, GenotypeQueryParams p, String chr, Set ds,
			VariantTable table1, String filename, Map mapSample2Pheno, String phenonames) {
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) table1;

		if (mapSample2Pheno != null) {
			AppContext.debug(phenonames + "  mapSample2Pheno.keySet().size=" + mapSample2Pheno.size());
			// AppContext.debug(phenonames + " mapSample2Pheno.keySet()="+ new
			// TreeSet(mapSample2Pheno.keySet()));
			Set ts = new TreeSet();
			Long varids[] = table.getVarid();
			for (int il = 0; il < varids.length; il++)
				ts.add(varids[il]);
			AppContext.debug("table.getVarid().size=" + varids.length);
			// AppContext.debug("table.getVarid()=" + ts);

		}
		Map mapHasPheno = new LinkedHashMap();
		try {
			// String chr = this.selectChr.getValue();

			StringBuffer buff = new StringBuffer();
			StringBuffer buffref = new StringBuffer();
			StringBuffer buffannot = new StringBuffer();
			// buff.append("POSITION").append(delimiter).append("MISMATCH").append(delimiter);
			Position[] positions = table.getPosition();
			String refs[] = table.getReference();
			String contigs[] = table.getContigs();

			// String snpannots[] =
			// table.getSNPGenomicAnnotation(this.fillGenotypeQueryParams());
			String snpannots[] = table.getSNPGenomicAnnotation(p);

			System.out.println(snpannots.length);
			for (int i = 0; i < positions.length; i++) {
				if (!refs[i].equals("-")) {
					int pos = positions[i].getPosition().intValue();
					String snpid = null;
					String contig = positions[i].getContig();
					if (contigs != null)
						contig = contigs[i];
					contig = contig.replaceAll("\\.0*$", "");
					try {
						Integer.valueOf(AppContext.guessChrFromString(contig));
						snpid = "1" + String.format("%02d", Integer.valueOf(AppContext.guessChrFromString(contig)))
								+ String.format("%08d", pos);

						buff.append(contig).append("\t").append(snpid).append("\t0\t").append(pos).append("\n");
					} catch (Exception ex) {
						snpid = contig + "-" + String.format("%08d", pos);
						buff.append(contig).append("\t").append(snpid).append("\t0\t").append(pos).append("\n");
					}

					buffref.append(snpid).append("\t").append(refs[i].substring(0, 1)).append("\n");
					if (snpannots != null)
						buffannot.append(snpid).append("\t").append(snpannots[i]).append("\n");
					else
						buffannot.append(snpid).append("\t").append("U").append("\n");

				} else {
					// for indels
					BigDecimal pos = positions[i].getPosition();
					String snpid = null;
					String contig = positions[i].getContig();
					if (contigs != null)
						contig = contigs[i];
					try {
						Integer.valueOf(AppContext.guessChrFromString(contig));
						snpid = "1" + String.format("%02d", Integer.valueOf(AppContext.guessChrFromString(contig)))
								+ String.format("%08d", pos.intValue()) + "."
								+ pos.subtract(BigDecimal.valueOf(pos.longValue())).multiply(BigDecimal.valueOf(100))
										.intValue();

						buff.append(contig).append("\t").append(snpid).append("\t0\t").append(pos).append("\n");
					} catch (Exception ex) {
						// snpid= contig + "-" + String.format("%08d", pos);
						snpid = contig + "-" + String.format("%08d", pos.intValue()) + "."
								+ pos.subtract(BigDecimal.valueOf(pos.longValue())).multiply(BigDecimal.valueOf(100))
										.intValue();
						buff.append(contig).append("\t").append(snpid).append("\t0\t").append(pos).append("\n");
					}

					buffref.append(snpid).append("\t").append(refs[i]).append("\n");
					if (snpannots != null)
						buffannot.append(snpid).append("\t").append(snpannots[i]).append("\n");
					else
						buffannot.append(snpid).append("\t").append("U").append("\n");
				}
			}

			// String filetype = "text/plain";
			// Filedownload.save( buff.toString(), filetype , filename + ".map");

			FileWriter writer = new FileWriter(filename + ".map");
			writer.append(buff.toString());
			writer.flush();
			writer.close();

			writer = new FileWriter(filename + ".map.ref");
			writer.append(buffref.toString());
			writer.flush();
			writer.close();

			writer = new FileWriter(filename + ".map.annot");
			writer.append(buffannot.toString());
			writer.flush();
			writer.close();

			buff = new StringBuffer();

			Object[][] varalleles = table.getVaralleles();

			Map<String, Map<BigDecimal, StockSample>> mapDs = varietyfacade.getMapId2Sample(ds);

			AppContext.debug("writing pedfile " + table.getVarid().length + " x " + positions.length);
			AppContext.debug("table.getVaralleles()=" + table.getVaralleles().length + " x "
					+ (table.getVaralleles().length > 0 ? table.getVaralleles()[0].length : 0));

			StringBuffer buffSample = new StringBuffer();
			for (int i = 0; i < table.getVarid().length; i++) {
				Map<BigDecimal, StockSample> mapVarId2Sample = mapDs.get(table.getDataset()[i]);
				String indvid = AppContext
						.createSampleUniqueName(mapVarId2Sample.get(BigDecimal.valueOf(table.getVarid()[i])), ds);

				// is phenotype is given, dont include samples without phenotype value
				String sampleid = indvid;
				if (indvid.contains("__"))
					sampleid = indvid.split("__")[1];

				if (i == 0 && mapSample2Pheno != null && mapSample2Pheno.size() > 0) {
					AppContext.debug("mapSample2Pheno.key=" + mapSample2Pheno.keySet().iterator().next().getClass()
							+ "\n" + "able.getVarid()[i]" + table.getVarid()[i].getClass());
				}
				if (mapSample2Pheno != null) {
					BigDecimal bdvarid = BigDecimal.valueOf(table.getVarid()[i]);
					if (mapSample2Pheno.containsKey(bdvarid)) {
						mapHasPheno.put(indvid, mapSample2Pheno.get(bdvarid));
					} else {
						continue; // includesample=false;
					}
				}

				buff.append(indvid).append("\t").append(indvid).append("\t0\t0\t0\t-9\t");
				buffSample.append(indvid).append("\n");

				for (int j = 0; j < refs.length; j++) {
					String allele1 = (String) varalleles[i][j];
					if (allele1.isEmpty())
						buff.append("0\t0");
					else {
						String alleles[] = allele1.split("/");
						if (alleles.length == 1)
							buff.append(allele1).append("\t").append(allele1);
						else
							buff.append(alleles[0]).append("\t").append(alleles[1]);
					}
					if (j < refs.length - 1)
						buff.append("\t");
				}
				buff.append("\n");
			}

			writer = new FileWriter(filename + ".txt");
			writer.append(buffSample.toString());
			writer.flush();
			writer.close();

			writer = new FileWriter(filename + ".ped");
			writer.append(buff.toString());
			writer.flush();
			writer.close();

			if (mapHasPheno.size() > 0) {
				writer = new FileWriter(filename + ".pheno");
				writer.write("<Phenotype>\ntaxa\tdata\ntaxa\t" + phenonames.replace(" ", "_") + "\n");
				// writer.write("Trait\t" + phenonames + "\n" );
				for (Object sample : mapHasPheno.keySet()) {
					writer.write(sample + "\t" + mapHasPheno.get(sample) + "\n");
				}
				writer.close();
				AppContext.debug("created datafile " + filename + ".pheno");

			}

			AppContext.debug("created datafile " + filename + ".ped");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onActive=#goldenPA_haplotype")
	public void tabDisplayHaloptypeimage() throws Exception {
		onselectTabHaplotype(false);

		System.out.println("testHaplotype");

	}

	public void onselectTabHaplotype(boolean newTab) throws Exception {
		if (!biglistboxArray.isVisible())
			return;

		if (haplofilename != null)
			return;

		this.tabHaploHaploview.setSelected(true);
		this.tabHaploAutogroups.setDisabled(true);
		this.tabHaploGroupAlleles.setDisabled(true);
		this.tabHaploTree.setDisabled(true);

		listboxGroupVarietyPhenotypeStacked.setVisible(false);
		labelGroupVarietyPhenotype.setVisible(false);

		hctreemaxlog2height = -1;
		hctreeminlog2height = -1;
		GenotypeQueryParams p = this.fillGenotypeQueryParams();
		if (!p.isRegion()) {
			this.listboxHaploResolution.setSelectedIndex(0);
			this.listboxHaploResolution.setDisabled(true);
		} else
			this.listboxHaploResolution.setDisabled(false);
		AppContext.debug("tabDisplayHaloptypeimage()");

		buttonHaploOrder.setDisabled(true);
		buttonHaploImage.setDisabled(true);

		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();

		Map mapVarid2Columns = new LinkedHashMap();
		int columns = table.getVariantStringData().getListPos().size();
		Iterator<BigDecimal> itVarsid = table.getVariantStringData().getMapVariety2Mismatch().keySet().iterator();
		while (itVarsid.hasNext()) {
			mapVarid2Columns.put(itVarsid.next(), columns);
		}

		haplofilename = "snp3kvars-" + queryFilename();
		p.setFilename(AppContext.getTempDir() + haplofilename);
		writeSummary((VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade"), getDataset(), true, p,
				/* AppContext.getTempDir() + qfilename, */table.getVariantStringData().getMapVariety2Order(),
				table.getVariantStringData().getMapVariety2Mismatch(), mapVarid2Columns, true);

		generateBigListboxPlink((VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade"),
				fillGenotypeQueryParams(), selectChr.getValue(), getDataset(), table,
				AppContext.getTempDir() + haplofilename);
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

			if (newTab) {
				try {
					Executions.forward(
							AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.html");
				} catch (Exception ex) {
					AppContext.debug(ex.getMessage());
				}
			} else {
				this.iframeHaplotype.setSrc(
						AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.html");
				this.iframeAutogroups.setSrc(AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename
						+ ".ped.autogroup.html");
				iframeAutogroups.invalidate();
				iframeHaplotype.invalidate();
				// hboxHaplotype.setVisible(true);
			}

			AppContext.debug("displayHapotypeImage() loaded");

			// if(kgroups>0 || kheight>0 || !autogroup.equals("nogroup")) {
			if (!autogroup.equals("nogroup")) {
				mapVars2PropSnpstr = null;
				// COMMENTED FOR TESTING
				// displayGroupallelePhenotpes();

				try {
					// COMMENTED FOR TESTING
					// updateGroupAlleleFrequencyChart();

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
//			buttonHaplotypelog.setHref(
//					AppContext.getHostname() + "/" + AppContext.getTempFolder() + haplofilename + ".ped.stdout.log");
//			buttonHaplotypelog.setVisible(true);

		}

	}

	private void show_mds_fromtable(Charts mdsChart) {
		show_mds_fromtable(mdsChart, null, null, null, null);
	}

	private void show_mds_fromtable(Charts mdsChart, Collection highlight) {
		show_mds_fromtable(mdsChart, highlight, null, null, null);
	}

	private void show_mds_fromtable(Charts mdsChart, Collection highlight, Double min, Double max,
			Map<BigDecimal, Object> mapVar2Val) {

		mdsChart.setVisible(false);

		List listIds = new ArrayList();

		StringBuffer varids = new StringBuffer();
		Iterator itid = queryResult.getMapVariety2Order().keySet().iterator();
		while (itid.hasNext()) {
			varids.append(itid.next());
			if (itid.hasNext())
				varids.append(",");

		}

		Object[] mdsresult = null;
		if (this.varpairDistance == null) {
			mdsresult = genotype.constructMDS(queryResult.getMapVariety2Order(), queryResult, new PhylotreeQueryParams(
					fillGenotypeQueryParams(), PhylotreeService.PHYLOTREE_METHOD_TOPN, -1, 1.0, 1.0));
			varpairDistance = (List) mdsresult[1];
			if (mapVar2Val == null)
				VarietyQueryController.plotXY(mdsChart, varietyfacade.getMapId2Variety(getDataset()),
						(double[][]) mdsresult[0], "Varieties MDS Plot", varids.toString(), null, false, highlight);
			else
				VarietyQueryController.plotXY(mdsChart, varietyfacade.getMapId2Variety(getDataset()),
						(double[][]) mdsresult[0], "Varieties MDS Plot", varids.toString(), null, false, min, max,
						mapVar2Val);

		} else {
			double[][] mds = (double[][]) genotype.constructMDS(queryResult.getMapVariety2Order(), varpairDistance,
					"1")[0];
			if (mapVar2Val == null)
				VarietyQueryController.plotXY(mdsChart, varietyfacade.getMapId2Variety(getDataset()), mds,
						"Varieties MDS Plot", varids.toString(), null, false, highlight);
			else
				VarietyQueryController.plotXY(mdsChart, varietyfacade.getMapId2Variety(getDataset()), mds,
						"Varieties MDS Plot", varids.toString(), null, false, min, max, mapVar2Val);
		}

		mdsChart.setVisible(true);
	}

	private void initPhenotype(VariantTable newtable) {

		Map<BigDecimal, Object> mapVarid2Phenotype = null;
		String sPhenotype = "";

		if (listboxPhenotype.getSelectedItem() != null) {
			sPhenotype = listboxPhenotype.getSelectedItem().getLabel().trim();
		}
		if (newtable == null || !biglistboxArray.isVisible() || biglistboxModel == null)
			return;
		if (sPhenotype.isEmpty()) {
			sPhenotype = null;
		} else {
			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
			mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
		}

		/*
		 * COMMENTED FOR PRINTING biglistboxModel = new
		 * Object2StringMultirefsMatrixModel(newtable, fillGenotypeQueryParams(),
		 * varietyfacade.getMapId2Sample(getDataset()), gridBiglistheader,
		 * mapVarid2Phenotype, sPhenotype);
		 * 
		 * Object2StringMultirefsMatrixRenderer renderer = new
		 * Object2StringMultirefsMatrixRenderer(
		 * biglistboxModel.getData().getVariantStringData(), fillGenotypeQueryParams());
		 * biglistboxArray.setMatrixRenderer(renderer);
		 * biglistboxModel.setHeaderRows(biglistboxRows, lastY);
		 * this.biglistboxArray.setModel(biglistboxModel);
		 * gridBiglistheader.setModel(new
		 * BiglistRowheaderModel(biglistboxModel.getRowHeaderList(biglistboxRows)));
		 * 
		 * onScrollYTable(0);
		 */

	}

	@Listen("onClick = #buttonClearFilterAllele")
	public void onclick$clearFilterAllele() {

		try {

			initPhenotype(varianttable);

			// COmmented removing button label
//			if (queryRawResult != null)
//				if (queryRawResult.getListVariantsString() != null)
//					labelFilterResult
//							.setValue("None .. " + queryRawResult.getListVariantsString().size() + " varieties");
//				else
//					labelFilterResult.setValue("None ..");
			varFreqlines = calculateAlleleFrequencies();

			gfffile = null;
			haplofilename = null;
			this.tabHaploGroupAlleles.setDisabled(true);
			this.tabHaploTree.setDisabled(true);
			this.tabHaploAutogroups.setDisabled(true);
			// iframePhylotree.setSrc(null);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Collection getPhenUniqueValues(Object retobj[]) {
		List listValues = new java.util.ArrayList();
		Iterator<CvTermUniqueValues> itValues = ((Set) retobj[0]).iterator();
		while (itValues.hasNext()) {
			CvTermUniqueValues value = itValues.next();
			if (value == null) {
				AppContext.debug("null value");
				continue;
			}
			listValues.add(value.getValue());
		}
		return listValues;
	}

	public Object[] getPhenMinMaxValues() {
		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		listboxHighlightVarietyList.setSelectedIndex(0);
		listboxHighlightVariety.setSelectedIndex(0);
		if (listboxPhenotype.getSelectedIndex() > 0) {
			String phenname = listboxPhenotype.getSelectedItem().getLabel();
			List val = (List) getPhenUniqueValues(varietyfacade.getPhenotypeUniqueValues(phenname, getDataset()));
			if (varietyfacade.getQuantTraits(getDataset()).contains(phenname)) {
				// qualitative
				Set sortedvals = new TreeSet();
				for (int i = 0; i < val.size(); i++) {
					try {
						double dval = Double.valueOf(val.get(i).toString());
						sortedvals.add(dval);
					} catch (Exception ex) {
						AppContext.debug("val=" + val.get(i) + "; " + val.get(i).getClass().getCanonicalName());
					}
				}

				List lsortedvals = new ArrayList();
				lsortedvals.addAll(sortedvals);

				// AppContext.debug( "vals=" + lsortedvals);
				double maxval = ((Number) lsortedvals.get(lsortedvals.size() - 1)).doubleValue();
				double minval = ((Number) lsortedvals.get(0)).doubleValue();

				Map<BigDecimal, Object> mapVar2Phenval = varietyfacade.getPhenotypeValues(phenname, getDataset());

				// showmds_allvars(chartMDS, null, minval, maxval, mapVar2Phenval);
				labelMDSPhenotype.setValue("QUANITATIVE");
				return new Object[] { minval, maxval, mapVar2Phenval };
			} else {
				// categorical
				Set hashvals = new HashSet();

				try {
					for (int i = 0; i < val.size(); i++) {
						int dval = Double.valueOf(val.get(i).toString()).intValue();
						hashvals.add(dval);
					}
					Set sortedvals = new TreeSet(hashvals);
					List lsortedvals = new ArrayList();
					lsortedvals.addAll(sortedvals);
					int maxval = ((Number) lsortedvals.get(lsortedvals.size() - 1)).intValue();
					if (maxval > 1000) {
						int max1 = maxval;
						maxval = ((Number) lsortedvals.get(lsortedvals.size() - 2)).intValue();
						AppContext.debug("change catvalue max " + max1 + " to " + maxval);
					}
					int minval = ((Number) lsortedvals.get(0)).intValue();
					Map<BigDecimal, Object> mapVar2Phenval = varietyfacade.getPhenotypeValues(phenname, getDataset());
					labelMDSPhenotype.setValue("CATEGORICAL (NUMBER-CODED)");
					return new Object[] { Double.valueOf(minval), Double.valueOf(maxval), mapVar2Phenval };

				} catch (Exception ex) {
					ex.printStackTrace();
					AppContext.debug("categorical value for " + phenname + " not numeric");
					labelMDSPhenotype.setValue("DESCRIPTIVE (TEXT-CODED)/Non-comparable category values");

					return null;
				}
			}
		} else {
			labelMDSPhenotype.setValue("");
			return null;
		}

	}

	/**
	 * calculate allele and genotype counts from table data
	 */
	private AlleleFreqLineData[] calculateKgroupAlleleFrequencies(Map<String, String[]> mapVar2Props,
			Map<String, String[]> mapVar2Snpstr, List<String[]> listPos,
			Map<String, Map<String, Integer>> mapGroup2SubpopCount) throws Exception {

		try {

			Map<String, Map<String, Map<String, Integer>>> mapPos2Subpop2Allele2Count = new TreeMap();
			Map<String, Map<String, Map<String, Integer>>> mapPos2Subpop2Genotype2Count = new TreeMap();

			// Object2StringMultirefsMatrixModel model =
			// (Object2StringMultirefsMatrixModel)this.biglistboxArray.getModel();
			// int poscol=-1;

			Set subpops = new LinkedHashSet();
			subpops.add("all");
			// for(int i=0; i<biglistboxArray.getCols(); i++) {

			/*
			 * int startCol=frozenCols;
			 * 
			 * if(listboxPhenotype.getSelectedItem()!=null &&
			 * !listboxPhenotype.getSelectedItem().getLabel().isEmpty()) { startCol++; };
			 */

			Iterator<String[]> itPos = listPos.iterator();

			int i = 0;
			while (itPos.hasNext()) {

				// AppContext.debug(listPos.get(i).getClass() + ", " +
				// listPos.get(i).toString());
				String[] strs = itPos.next();
				String contigpos = strs[1] + "-" + strs[2];
				Map mapSubpop2Allele2Count = new TreeMap();
				Map mapSubpop2Genotype2Count = new TreeMap();

				Iterator<String> itVars = mapVar2Snpstr.keySet().iterator();
				int j = 0;
				while (itVars.hasNext()) {
					String var = itVars.next();

					if (var == null)
						AppContext.debug("var==null");

					String[] snpstr = (String[]) mapVar2Snpstr.get(var);
					// String subpop = model.getCellAt( model.getElementAt(j), 2).toString();

					if (snpstr == null)
						AppContext.debug("snpstr==null for " + var);

					// name, pop,origorder, irisid, grp
					String[] props = (String[]) mapVar2Props.get(var);

					if (props == null)
						AppContext.debug("props==null for " + var);

					String subpop = props[4]; // +"-"+props[1]; // kgroup id

					if (i == 0) {
						String varpop = props[1];
						if (varpop.isEmpty())
							varpop = "no subpop";
						Map<String, Integer> mapSubpop2Count = mapGroup2SubpopCount.get(subpop);
						if (mapSubpop2Count == null) {
							mapSubpop2Count = new LinkedHashMap();
							mapGroup2SubpopCount.put(subpop, mapSubpop2Count);
						}
						Integer varpopcnt = mapSubpop2Count.get(varpop);
						if (varpopcnt == null)
							varpopcnt = 0;
						varpopcnt++;
						mapSubpop2Count.put(varpop, varpopcnt);

						varpopcnt = mapSubpop2Count.get("all");
						if (varpopcnt == null)
							varpopcnt = 0;
						varpopcnt++;
						mapSubpop2Count.put("all", varpopcnt);
					}

					subpops.add(subpop);
					Map mapAllele2Count = (Map) mapSubpop2Allele2Count.get(subpop);
					if (mapAllele2Count == null) {
						mapAllele2Count = new HashMap();
						mapSubpop2Allele2Count.put(subpop, mapAllele2Count);
					}

					String allele = snpstr[i];

					allele = allele.trim();

					// if(allele.isEmpty()) allele=" ";
					if (allele.isEmpty())
						continue;

					if (allele.contains("/")) {

						// heterogygous

						String alleles12[] = null;
						try {
							alleles12 = new String[] { String.valueOf(allele.charAt(0)),
									String.valueOf(allele.charAt(2)) };

						} catch (Exception ex) {
							AppContext.debug("allele=" + allele);
							// ex.printStackTrace();
							// throw new RuntimeException(ex);
							if (allele.startsWith("/"))
								alleles12 = new String[] { String.valueOf(" "), String.valueOf(allele.charAt(1)) };
							else if (allele.endsWith("/"))
								alleles12 = new String[] { String.valueOf(allele.charAt(1)), String.valueOf(" ") };
							else
								alleles12 = new String[] { String.valueOf(" "), String.valueOf(" ") };
						}

						Integer allele1count = (Integer) mapAllele2Count.get(alleles12[0]);
						if (allele1count == null)
							allele1count = 0;
						allele1count = allele1count + 1;
						mapAllele2Count.put(alleles12[0], allele1count);

						Integer allele2count = (Integer) mapAllele2Count.get(alleles12[1]);
						if (allele2count == null)
							allele2count = 0;
						allele2count = allele2count + 1;
						mapAllele2Count.put(alleles12[1], allele2count);

						// AppContext.debug(subpop + " " + contigpos + " " + allele + "
						// mapAllele2Count=" + mapAllele2Count + " added alleles " + alleles12[0] + ", "
						// + alleles12[1]);

						mapSubpop2Allele2Count.put(subpop, mapAllele2Count);

						Map mapAllele2CountAll = (Map) mapSubpop2Allele2Count.get("all");
						if (mapAllele2CountAll == null) {
							mapAllele2CountAll = new HashMap();
							mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
						}

						Integer allele1countall = (Integer) mapAllele2CountAll.get(alleles12[0]);
						if (allele1countall == null)
							allele1countall = 0;
						allele1countall = allele1countall + 1;
						mapAllele2CountAll.put(alleles12[0], allele1countall);

						Integer allele2countall = (Integer) mapAllele2CountAll.get(alleles12[1]);
						if (allele2countall == null)
							allele2countall = 0;
						allele2countall = allele2countall + 1;
						mapAllele2CountAll.put(alleles12[1], allele2countall);

						mapSubpop2Allele2Count.put("all", mapAllele2CountAll);

					} else {

						// homozygous

						Integer allelecount = (Integer) mapAllele2Count.get(allele);
						if (allelecount == null)
							allelecount = 0;
						allelecount = allelecount + 2;
						mapAllele2Count.put(allele, allelecount);

						mapSubpop2Allele2Count.put(subpop, mapAllele2Count);

						Map mapAllele2CountAll = (Map) mapSubpop2Allele2Count.get("all");
						if (mapAllele2CountAll == null) {
							mapAllele2CountAll = new HashMap();
							mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
						}
						Integer allelecountall = (Integer) mapAllele2CountAll.get(allele);
						if (allelecountall == null)
							allelecountall = 0;
						allelecountall = allelecountall + 2;
						mapAllele2CountAll.put(allele, allelecountall);

						// AppContext.debug("i=" + i + " j=" + j + " subpop=" + subpop + " allele=" +
						// allele + " allelecount=" + allelecount + " allelecountall=" +
						// allelecountall);

						mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
					}

					// genotype count

					Map mapGenotype2Count = (Map) mapSubpop2Genotype2Count.get(subpop);
					if (mapGenotype2Count == null) {
						mapGenotype2Count = new HashMap();
						mapSubpop2Genotype2Count.put(subpop, mapGenotype2Count);
					}

					Integer genotypecount = (Integer) mapGenotype2Count.get(allele);
					if (genotypecount == null)
						genotypecount = 0;
					genotypecount = genotypecount + 1;
					mapGenotype2Count.put(allele, genotypecount);

					mapSubpop2Genotype2Count.put(subpop, mapGenotype2Count);

					Map mapGenotype2CountAll = (Map) mapSubpop2Genotype2Count.get("all");
					if (mapGenotype2CountAll == null) {
						mapGenotype2CountAll = new HashMap();
						mapSubpop2Genotype2Count.put("all", mapGenotype2CountAll);
					}
					Integer genotypecountall = (Integer) mapGenotype2CountAll.get(allele);
					if (genotypecountall == null)
						genotypecountall = 0;
					genotypecountall = genotypecountall + 1;
					mapGenotype2CountAll.put(allele, genotypecountall);

					// AppContext.debug("i=" + i + " j=" + j + " subpop=" + subpop + " allele=" +
					// allele + " allelecount=" + allelecount + " allelecountall=" +
					// allelecountall);

					mapSubpop2Genotype2Count.put("all", mapGenotype2CountAll);

					j++;
				}

				mapPos2Subpop2Allele2Count.put(contigpos, mapSubpop2Allele2Count);
				mapPos2Subpop2Genotype2Count.put(contigpos, mapSubpop2Genotype2Count);

				i++;
			}

			AppContext.debug("KGroup  varieties " + mapVar2Props.size());
			AppContext.debug("KGroup mapPos2Subpop2Allele2Count=" + mapPos2Subpop2Allele2Count.size());
			AppContext.debug("KGroup mapPos2Subpop2Genotype2Count=" + mapPos2Subpop2Genotype2Count.size());

			return new AlleleFreqLineData[] { calcFreq(subpops, mapPos2Subpop2Allele2Count),
					calcFreq(subpops, mapPos2Subpop2Genotype2Count) };

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// updateAlleleFrequencyChart();

		return null;

	}

	private Map[] displayKgroupAlleleFrequencies(String haplofilename) throws Exception {
		// Map mapGroup2Var2Props=new LinkedHashMap();
		// Map mapGroup2Var2Snpstr=new LinkedHashMap();
		Map mapVars2Props = new LinkedHashMap();
		Map mapVar2Snpstr = new LinkedHashMap();
		List listPosRef = new ArrayList();
		try {
			// Map mapVar2Group=new HashMap();
			BufferedReader br = new BufferedReader(
					new FileReader(AppContext.getTempDir() + haplofilename + ".summary.txt.clustered.txt"));
			String line = br.readLine();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				line = line.replace("\"", "");
				if (line.isEmpty())
					continue;
				String cols[] = line.split("\t");
				String grp = cols[7];

				if (grp == null)
					AppContext.debug("Null group for " + line);

				String props[] = new String[] { cols[2], cols[4], cols[1], cols[3], grp };

				mapVars2Props.put(cols[3], props);
			}
			br.close();
			br = new BufferedReader(new FileReader(AppContext.getTempDir() + haplofilename + ".ped"));
			int pos = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				line = line.replace("\"", "");
				if (line.isEmpty())
					continue;
				String cols[] = line.split("\t");
				// String grp=(String)mapVar2Group.get(cols[0]);
				if (pos != 0 && pos != (cols.length - 6) / 2)
					throw new RuntimeException("inconsistent pos length in ped file");
				else
					pos = (cols.length - 6) / 2;

				String snpstr[] = new String[pos];
				for (int i = 0; i < 2 * pos; i += 2) {
					String al1 = cols[6 + i];
					String al2 = cols[6 + i + 1];
					if (al1 == null || al2 == null) {
						AppContext.debug("al1=" + al1 + ", al2=" + al2);
						throw new RuntimeException("al1=" + al1 + ", al2=" + al2);
					}
					if (!al1.equals(al2)) {
						snpstr[i / 2] = al1 + "/" + al2;
					} else if (al1.equals("0")) {
						snpstr[i / 2] = " ";
					} else
						snpstr[i / 2] = al1;

				}
				// mapvar2snp.put(cols[0],snpstr);
				mapVar2Snpstr.put(cols[0], snpstr);
			}
			br.close();

			br = new BufferedReader(new FileReader(AppContext.getTempDir() + haplofilename + ".map"));
			BufferedReader br2 = new BufferedReader(
					new FileReader(AppContext.getTempDir() + haplofilename + ".map.ref"));
			String line2 = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;
				while ((line2 = br2.readLine()) != null) {
					line2 = line2.trim();
					if (line2.isEmpty())
						continue;
					String cols1[] = line.split("\t");
					String cols2[] = line2.split("\t");
					if (!cols2[0].equals(cols1[1])) {
						AppContext.debug("inconsistent pos in map file");
						throw new RuntimeException("inconsistent pos in map file");
					}
					// snpid,ctg,pos,ref
					listPosRef.add(new String[] { cols1[1], cols1[0], cols1[3], cols2[1] });
					break;
				}
			}
			br.close();
			br2.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/*
		 * "clust_order" "orig_order" "variety" "iris_id" "pop" "mismatch" "snps" 1 2665
		 * "MUXIQIU" "B071" "temp" 1 17 2 2676 "YJ30" "CX391" "temp" 1 17 3 2675
		 * "JINYUAN 85" "CX389" "temp" 1 17 4 2674 "YUNGENG 23" "CX345" "temp" 1 17
		 * 
		 * IRIS_313-15909 IRIS_313-15909 0 0 0 -9 C C T T T T T T T T C C G G C C T T C
		 * C C C G G G G G G A A T T
		 * 
		 */
		/*
		 * Iterator<Map> itMap=mapGroup2Var2Props.values().iterator();
		 * while(itMap.hasNext()) { mapVars2Props.putAll(itMap.next()); }
		 * 
		 * Map mapVar2Snpstr=new LinkedHashMap();
		 * itMap=mapGroup2Var2Snpstr.values().iterator(); while(itMap.hasNext()) {
		 * mapVar2Snpstr.putAll(itMap.next()); }
		 */

		AppContext.debug("displayKgroupAlleleFrequencies mapVars2Props=" + mapVars2Props.size() + ",mapVar2Snpstr="
				+ mapVar2Snpstr.size() + ",listPosRef=" + listPosRef.size());

		Map<String, Map<String, Integer>> mapGroup2SubpopCount = new HashMap();

		groupFreqlines = calculateKgroupAlleleFrequencies(mapVars2Props, mapVar2Snpstr, listPosRef,
				mapGroup2SubpopCount);

		AppContext.debug("mapGroup2SubpopCount=" + mapGroup2SubpopCount);

		// generate blocksnpmatrix
		// groupFreqlines[0].linepercentmajormodel.get

		List listPopStr = new ArrayList();

		Set setNgroup = new TreeSet();
		Iterator itPop = new TreeSet(groupFreqlines[0].mapPop2Majoralleles.keySet()).iterator();
		while (itPop.hasNext()) {
			try {
				setNgroup.add(Integer.valueOf((String) itPop.next()));
			} catch (Exception ex) {
			}
		}
		itPop = setNgroup.iterator();

		// Iterator itPop=new TreeSet(
		// groupFreqlines[0].mapPop2Majoralleles.keySet()).iterator();
		int allvarcnt = mapVar2Snpstr.size();
		while (itPop.hasNext()) {
			String pop = itPop.next().toString();
			if (pop.equals("all")) {
				continue;
			}

			List listsnp = (List) groupFreqlines[0].mapPop2Majoralleles.get(pop);
			List all = new ArrayList();
			all.add(pop);
			String allcount = "";
			String subcount = "";
			int varpoptotal = 0;
			try {
				allcount = mapGroup2SubpopCount.get(pop).get("all").toString();

				StringBuffer buff = new StringBuffer();
				Map<String, Integer> mapVarpop2Count = mapGroup2SubpopCount.get(pop);
				Iterator<String> itVarpop = mapVarpop2Count.keySet().iterator();
				while (itVarpop.hasNext()) {
					String varpop = itVarpop.next();
					if (varpop.equals("all")) {
						allcount = mapVarpop2Count.get("all").toString();
						continue;
					}
					try {
						int varpopcnt = mapVarpop2Count.get(varpop);
						varpoptotal += varpopcnt;
						allcount = varpop + ":" + varpopcnt;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					buff.append(allcount);
					if (itVarpop.hasNext())
						buff.append("/");
				}
				subcount = buff.toString();

			} catch (Exception ex) {
				AppContext.debug("pop=" + pop);
				ex.printStackTrace();
			}

			// all.add(allcount);
			all.add(subcount);
			all.add(varpoptotal);
			all.add(varpoptotal * 1.0 / allvarcnt);

			all.addAll(listsnp);
			listPopStr.add(all);
		}

		AppContext.debug("listPopStr=" + listPopStr.size());
		try {
			listboxGroupAlleleMatrix.setItemRenderer(new VargroupListItemRenderer());

			Listhead listHead = (Listhead) listboxGroupAlleleMatrix.getListhead(); // .getChildren().iterator().next();
			List<Listheader> listheader = listHead.getChildren();
			listheader.clear();
			Listheader nheader = new Listheader("KGROUP");
			nheader.setWidth("40px");
			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 0));
			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 0));
			listheader.add(nheader);
			// nheader=new Listheader("SAMPLES"); nheader.setWidth("40px");
			// listheader.add(nheader);
			nheader = new Listheader("SUBPOPS:COUNT");
			nheader.setWidth("300px");
			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 1));
			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 1));
			listheader.add(nheader);
			nheader = new Listheader("VARIETIES");
			nheader.setWidth("40px");
			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 2));
			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 2));
			listheader.add(nheader);
			nheader = new Listheader("FREQUENCY");
			nheader.setWidth("40px");
			nheader.setSortAscending(new GroupMatrixListItemSorter(true, 3));
			nheader.setSortDescending(new GroupMatrixListItemSorter(false, 3));
			listheader.add(nheader);

			Iterator<String[]> itposref = listPosRef.iterator();
			int colidx = 4;
			while (itposref.hasNext()) {
				String headers[] = itposref.next();

				// snpid,ctg,pos,ref
				nheader = new Listheader(headers[1] + "-" + headers[2]); // nheader.setSort("auto");
				nheader.setSortAscending(new GroupMatrixListItemSorter(true, colidx));
				nheader.setSortDescending(new GroupMatrixListItemSorter(false, colidx));
				colidx++;
				nheader.setWidth("20px");
				listheader.add(nheader);
			}
			listboxGroupAlleleMatrix.setModel(new SimpleListModel(listPopStr));

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show("catched exception:" + ex.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
		}

		Map retmap[] = new Map[] { mapVars2Props, mapVar2Snpstr };
		AppContext.debug("retmap[]=" + retmap);
		return retmap;
	}

	private boolean isThemeStyleSet(Charts chart, String style) {
		Theme theme = chart.getTheme();
		return theme != null && theme.toString().contains(style);
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

	private void displayGroupallelePhenotpes() throws Exception {

		String autogroup = "nogroup";
		String kmethod = (String) listboxKgroupMethod.getSelectedItem().getValue();
		if (kmethod.equals("cuttreegroup") || kmethod.equals("cuttreeheight") || kmethod.equals("cuttreeheight_norm")) {
			autogroup = kmethod;
		} else if (kmethod.equals("autogroup")) {

			// if(this.listboxDataset.getSelectedItem().equals("gq92"))
			// listboxAutogroup.setSelectedIndex(0);
			autogroup = this.listboxAutogroup.getSelectedItem().getValue();
		}

		if (autogroup.equals("nogroup"))
			return;

		if (mapVars2PropSnpstr == null)
			mapVars2PropSnpstr = displayKgroupAlleleFrequencies(haplofilename);

		// Map mapVars2PropSnpstr[]=displayKgroupAlleleFrequencies(haplofilename);

		AppContext.debug("mapVars2PropSnpstr[]=" + mapVars2PropSnpstr);

		labelGroupVarietyPhenotype.setVisible(false);
		divGroupPhenoQuant.setVisible(false);
		divGroupPhenoCat.setVisible(false);
		buttonAddPhenValuesToMatrix.setVisible(false);

		if (this.listboxPhenotype.getSelectedIndex() > 0) {
			buttonAddPhenValuesToMatrix.setVisible(true);
			// draw group phenotype boxplots
			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
			Map<BigDecimal, Object> mapVarid2Phenotype = null;
			String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
			Set quanttraits = varietyfacade.getQuantTraits(getDataset());

			if (!quanttraits.contains(sPhenotype)) {
				// Messagebox.show("No statistics for non-quantitative traits");

				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());

				// AppContext.getMapVarunique2Id(varietyfacade.getMapId2Variety(getDataset()),
				// true,true,true);
				// name, pop,origorder, irisid, grp
				Map mapVars2Prop = mapVars2PropSnpstr[0];
				Iterator<String> itVar = mapVars2Prop.keySet().iterator();
				Map<String, Map<Object, Set<String>>> mapGroup2Statistics = new HashMap();
				int phencnt = 0;
				while (itVar.hasNext()) {
					String var = itVar.next();
					String grp = ((String[]) mapVars2Prop.get(var))[4];
					Map<Object, Set<String>> stat = mapGroup2Statistics.get(grp);
					if (stat == null) {
						stat = new HashMap();
						mapGroup2Statistics.put(grp, stat);
					}
					BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true,
							varietyfacade.getMapId2Variety(getDataset()));
					if (varid == null) {
						varid = AppContext.getVarunique2Id(var.replace("_", " "), true, true, true,
								varietyfacade.getMapId2Variety(getDataset()));
						if (varid == null)
							throw new RuntimeException("cannot find varid for " + var);
					}
					Object phenvalue = mapVarid2Phenotype.get(varid);
					if (phenvalue == null)
						continue;
					Set<String> lvars = stat.get(phenvalue);
					if (lvars == null) {
						lvars = new HashSet();
						stat.put(phenvalue, lvars);
					}
					lvars.add(var);
					phencnt++;
				}

				AppContext.debug("displayGroupallelePhenotpes phencnt=" + phencnt + " " + " groups="
						+ mapGroup2Statistics.keySet() + " sPhenotype=" + sPhenotype);
				// draw box plots
				updatePhenotpeStackedplots(chartGroupPhenotypeStacked, mapGroup2Statistics, phencnt, sPhenotype);
				// checkboxNormalizeStackedCategories.setVisible(true);
				divGroupPhenoCat.setVisible(true);

			} else {

				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());

				// AppContext.getMapVarunique2Id(varietyfacade.getMapId2Variety(getDataset()),
				// true,true,true);
				double totalphen = 0;
				// name, pop,origorder, irisid, grp
				mapKgroupCat2Varieties = new HashMap();
				Map mapVars2Prop = mapVars2PropSnpstr[0];
				Iterator<String> itVar = mapVars2Prop.keySet().iterator();
				Map<String, DescriptiveStatistics> mapGroup2Statistics = new HashMap();
				int phencnt = 0;
				while (itVar.hasNext()) {
					String var = itVar.next();
					String grp = ((String[]) mapVars2Prop.get(var))[4];
					DescriptiveStatistics stat = mapGroup2Statistics.get(grp);
					if (stat == null) {
						stat = new DescriptiveStatistics();
						mapGroup2Statistics.put(grp, stat);
					}
					BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true,
							varietyfacade.getMapId2Variety(getDataset()));
					if (varid == null) {
						varid = AppContext.getVarunique2Id(var.replace("_", " "), true, true, true,
								varietyfacade.getMapId2Variety(getDataset()));
						if (varid == null)
							throw new RuntimeException("cannot find varid for " + var);
					}
					Number phenvalue = (Number) mapVarid2Phenotype.get(varid);
					if (phenvalue == null)
						continue;
					double val = phenvalue.doubleValue();
					stat.addValue(val);
					phencnt++;
					totalphen += val;

					Set sVars = (Set) mapKgroupCat2Varieties.get(grp);
					if (sVars == null) {
						sVars = new HashSet();
						mapKgroupCat2Varieties.put(grp, sVars);
					}
					sVars.add(var);
				}
				double avg = totalphen / phencnt;

				AppContext.debug("displayGroupallelePhenotpes avg=" + avg + " " + " groups="
						+ mapGroup2Statistics.keySet() + " sPhenotype=" + sPhenotype);
				// draw box plots
				if (radioGroupPhenotypeBox.isSelected()) {
					// if(true) {
					updatePhenotpeBoxplots(chartGroupPhenotypeBox, mapGroup2Statistics, avg, phencnt, sPhenotype);
					divGroupPhenoQuant.setVisible(true);
					divGroupPhenoQuantBox.setVisible(true);
					// checkboxOutlierGroupPhenotypeBox.setVisible(true);
				} else {
					updatePhenotpeErrorbars(chartGroupPhenotypeErrorbars, mapGroup2Statistics, avg, phencnt,
							sPhenotype);
					divGroupPhenoQuant.setVisible(true);
					divGroupPhenoQuantBox.setVisible(false);
				}
			}

		}

	}

	@Listen("onSelect =#listboxPhenotype")
	public void onSelect$Phenotype() {
		if (listboxPhenotype.getSelectedItem() != null)
			if (listboxPhenotype.getSelectedItem().getLabel().equals("Create phenotype list...")) {
				Executions.sendRedirect("_workspace.zul?from=variety&src=snp&phenotype=true");
				return;
			}

		// commented
//		if (this.resultPanelBox.isChecked())
//			onclickClearFilterAllele();
		if (this.resultPanelBox7.isChecked()) {
			if (listboxPhenotype.getSelectedIndex() > 0) {
				listboxHighlightVariety.setSelectedIndex(0);
				listboxHighlightVarietyList.setSelectedIndex(0);
				Object[] phenvalues = getPhenMinMaxValues();
				if (phenvalues != null)
					show_mds_fromtable(chartMDS, null, (Double) phenvalues[0], (Double) phenvalues[1],
							(Map) phenvalues[2]);
			} else {
				show_mds_fromtable(chartMDS);
			}
		} else if (this.resultPanelBox5.isChecked()) {
			if (this.tabHaploGroupAlleles.isSelected()) {
				try {
					displayGroupallelePhenotpes();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

	@Listen("onOpen=#datasetGroup")
	public void onOpen$DatasetGroup() {
		if (datasetGroup.isOpen())
			datasetIcon.setClass("fas fa-chevron-down");
		else
			datasetIcon.setClass("fas fa-chevron-right");
	}

	@Listen("onOpen=#regionGroup")
	public void onOpen$RegionGroup() {
		if (regionGroup.isOpen())
			regionIcon.setClass("fas fa-chevron-down");
		else
			regionIcon.setClass("fas fa-chevron-right");

	}

	@Listen("onOpen=#optionsGroup")
	public void onOpen$OptionGroup() {
		if (optionsGroup.isOpen())
			optionIcon.setClass("fas fa-chevron-down");
		else
			optionIcon.setClass("fas fa-chevron-right");

	}

	@Wire
	private Portallayout portalLayout;

	@Listen("onPortalMove = #portalLayout")
	public void saveStatus() {
		int i = 0;
		for (Component portalChild : portalLayout.getChildren()) {
			List<String> portletIds = new ArrayList<String>();
			for (Component portlet : portalChild.getChildren())
				portletIds.add(portlet.getId());
			Executions.getCurrent().getSession().setAttribute("PortalChildren" + i++, portletIds);
		}
	}

	@Listen("onCreate = #portalLayout")
	public void initStatus() {

		List<? extends Component> panelchildren = portalLayout.getChildren();
		for (int i = 0; i < panelchildren.size(); i++) {
			List<String> panelIds = (List<String>) Executions.getCurrent().getSession()
					.getAttribute("PortalChildren" + i);
			if (panelIds != null) {
				for (String panelId : panelIds) {
					Panel newPanel = (Panel) portalLayout.getFellow(panelId);
					if (panelchildren.size() > 0)
						panelchildren.get(i).insertBefore(newPanel, panelchildren.get(0));
					else
						newPanel.setParent(panelchildren.get(i));

				}
			}
		}
	}

	@Listen("onSelect = #listboxVarietyset")
	public void onSelect$checkboxDroplistBoxVarietySet(Event e) throws InterruptedException {

		String str = "";

		for (Listitem li : listboxVarietyset.getItems()) {
			if (!li.isSelected()) {
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.getLabel();
		}
		bandboxVarietyset.setValue(str);
	}

	private void initFrom() {
		String from = Executions.getCurrent().getParameter("from");
		if (from != null) {
			try {
				if (from.equals("varietylist")) {
					listboxMyVarieties.setSelectedIndex(listboxMyVarieties.getItemCount() - 2);
					listboxSubpopulation.setSelectedIndex(0);
					checkboxAllvarieties.setChecked(false);
				} else if (from.equals("snplist")) {
					listboxMySNPList.setSelectedIndex(listboxMySNPList.getItemCount() - 2);
					listboxSubpopulation.setSelectedIndex(1);
					checkboxAllvarieties.setChecked(false);
				} else if (from.equals("snplistallele")) {
					listboxSubpopulation.setSelectedIndex(1);
					checkboxAllvarieties.setChecked(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	@Listen("onSelect = #listboxVariantset")
	public void onSelect$checkboxDroplistboxVariantset(Event e) throws InterruptedException {

		String str = "";

		for (Listitem li : listboxVariantset.getItems()) {
			if (!li.isSelected()) {
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.getLabel();
		}
		bandboxVariantset.setValue(str);
	}

	/**
	 * Fill VariantSet ListBox
	 */
	private void fillVariantsetListbox() {

		String[] ds = bandboxVarietyset.getText().split(",");
		Set setds = new HashSet();
		for (int i = 0; i < ds.length; i++)
			setds.add(ds[i].trim());
		List l = new ArrayList();
		Set singlevariants = new HashSet();

		Set prevvs = getVariantset();
		// bandboxVariantset.getChildren().get(0).getChildren().clear();
		Component bandpopup = bandboxVariantset.getChildren().get(0).getChildren().get(0);
		bandpopup.getChildren().clear();
		if (setds.size() > 0) {
			for (Object dsi : setds) {
				Collection vsdsi = genotype.getVariantsets((String) dsi, "SNP");
				l.addAll(vsdsi);
				if (vsdsi.size() == 1)
					singlevariants.add(dsi);

				Label labl = new Label("    " + (String) dsi);
				labl.setStyle("font-weight:bold");
				labl.setPre(true);
				bandpopup.getChildren().add(labl);

				Radiogroup rg = new Radiogroup();
				bandpopup.getChildren().add(rg);
				Radio firstradio = null;
				Radio selRadio = null;

				int radiocnt = 0;
				for (Object variantset : vsdsi) {
					Radio r = new Radio();
					r.setLabel((String) variantset);
					if (prevvs.contains(variantset))
						selRadio = r;
					r.setRadiogroup(rg);
					bandpopup.getChildren().add(r);
					if (firstradio == null)
						firstradio = r;
					radiocnt++;
				}
				if (selRadio != null)
					rg.setSelectedItem(selRadio);
				else
					rg.setSelectedItem(firstradio);
				// firstradio.setSelected(true);
			}
		}
		AppContext.debug(l.size() + " variantsets");

		SimpleListModel m = new SimpleListModel(l);
		m.setMultiple(true);
		listboxVariantset.setModel(m);

		Set vs = getVariantset();
		// remove unchecked varietyset
		Set tmpSelvs = new LinkedHashSet(vs);
		tmpSelvs.removeAll(l);
		vs.removeAll(tmpSelvs);

		vs.addAll(singlevariants);
		setVariantset(vs);

		update_runslist();
	}

	private void setVariantset() {
		Set<String> selVS = new HashSet<String>();
		selVS.add(AppContext.getDefaultVariantset()); // "3kfiltered");
		setVariantset(selVS);
	}

	private void setVariantset(Set s) {
		String str = "";
		for (Object li : s) {
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += (String) li;
		}
		bandboxVariantset.setValue(str);

		String curvariantset = null;
		for (Object comp : bandboxVariantset.getChildren().get(0).getChildren().get(0).getChildren()) {
			if (comp instanceof Label) {
				curvariantset = ((Label) comp).getValue();
			} else if (comp instanceof Radio) {
				Radio r = (Radio) comp;
				r.setSelected(s.contains(r.getLabel()));
			}
		}

	}

	private Set getDataset() {
		Set s = new LinkedHashSet();
		String[] ds = bandboxVarietyset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;
	}

	private void setVarietyset() {
		Set<String> selDS = new HashSet<String>();
		selDS.add(AppContext.getDefaultDataset());
		setVarietyset(selDS);
	}

	private Set getVariantset() {
		Set s = new LinkedHashSet();
		String[] ds = bandboxVariantset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;
	}

	private void update_runslist() {
		Set setvs = getVariantset();
		Set setds = getDataset();
		List l = new ArrayList();
		if (setvs.size() > 0 && setds.size() > 0) {
			l.addAll(genotype.getGenotyperuns(setds, setvs, "SNP"));
		}

		// check duplicated varietyset, variantset

		Map<String, List> mapDuplicates = new HashMap();
		Set intRunids = new HashSet();
		Set setSingleruns = new HashSet();
		for (Object gr : l) {
			GenotypeRunPlatform p = (GenotypeRunPlatform) gr;
			String key = p.getDataset() + "-" + p.getVariantset();
			if (mapDuplicates.containsKey(key)) {
				mapDuplicates.get(key).add(p);
			} else {
				List lp = new ArrayList();
				lp.add(p);
				mapDuplicates.put(key, lp);
			}
			intRunids.add(p.getGenotypeRunId());
		}

		List ldups = new ArrayList();
		for (String key : mapDuplicates.keySet()) {
			List lp = mapDuplicates.get(key);
			if (lp.size() > 1) {
				for (Object allr : lp) {
					GenotypeRunPlatform r = (GenotypeRunPlatform) allr;
					ldups.add(r.getGenotypeRunId() + "  " + key + "  "
							+ AppContext.datef.format(r.getDatePerformed().getTime()).split(" ", 2)[0].trim());
				}
			} else {
				setSingleruns.add(((GenotypeRunPlatform) lp.get(0)).getGenotypeRunId());
			}
		}
		AppContext.debug(l.size() + " runs, " + ldups.size() + " duplicated");
		SimpleListModel m = new SimpleListModel(ldups);
		m.setMultiple(true);
		listboxRunid.setModel(m);

		// Set runids= new HashSet();

		Set runids = getRunids();
		// remove unchecked variantset
		Set tmpSelvs = new LinkedHashSet(runids);
		tmpSelvs.removeAll(intRunids);
		runids.removeAll(tmpSelvs);

		runids.add(setSingleruns);
		bandboxRunid.setValue(setSingleruns.toString().replace("[", "").replace("]", ""));

		divRunid.setVisible(ldups.size() > 0);
	}

	private void setVarietyset(Set s) {
		SimpleListModel<String> listmodel2 = new SimpleListModel<String>(genotype.getVarietysets());
		listmodel2.setMultiple(true);

		listboxVarietyset.setModel(listmodel2);
		listboxVarietyset.setSelectedIndex(0);

		String str = "";
		for (Object li : s) {
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += (String) li;
		}
		bandboxVarietyset.setValue(str);
		Set setsel = new HashSet();
		for (Listitem li : listboxVarietyset.getItems()) {
			if (s.contains(li.getLabel())) {
				setsel.add(li);
			}
		}
		listboxVarietyset.setSelectedItems(setsel);
	}

	private Set getRunids() {
		String runids = bandboxRunid.getText().trim();
		Set intrunids = new LinkedHashSet();
		if (runids.length() > 0) {
			String runid[] = runids.split(",");
			for (int i = 0; i < runid.length; i++) {
				intrunids.add(Integer.valueOf(runid[i].trim()));
			}
		}
		return intrunids;
	}

	@Listen("onOpen = #bandboxVarietyset")
	public void onOpen$bandBoxVarietySet(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen() + " text=" + bandboxVarietyset.getText() + "  value="
				+ bandboxVarietyset.getValue());
		if (e.isOpen()) {
			setVarietyset(getDataset());
			return;
		}
		fillVariantsetListbox();

	}

	// Matrix comparator provider
	private class MyMatrixComparatorProvider<T> implements MatrixComparatorProvider<List<String>> {
		private int _x = -1;

		private boolean _acs;

		private MyComparator _cmpr;

		public MyMatrixComparatorProvider(boolean asc) {
			_acs = asc;
			_cmpr = new MyComparator(this);
		}

		@Override
		public Comparator<List<String>> getColumnComparator(int columnIndex) {
			this._x = columnIndex;
			return _cmpr;

		}

		// a real String comparator
		private class MyComparator implements Comparator<List<String>> {
			private MyMatrixComparatorProvider _mmc;

			public MyComparator(MyMatrixComparatorProvider mmc) {
				_mmc = mmc;
			}

			@Override
			public int compare(List<String> o1, List<String> o2) {
				return o1.get(_mmc._x).compareTo(o2.get(_mmc._x)) * (_acs ? 1 : -1);
			}
		}

	}

	/**
	 * CHART RESULT
	 */

	/**
	 * On selection in chart
	 * 
	 * @param event
	 */
	@Listen("onSelection = #chartAlleleFrequency")
	public void doSelection(ChartsSelectionEvent event) {
		// doing the zooming in function

		double min = event.getXAxisMin().doubleValue();
		double max = event.getXAxisMax().doubleValue();
		// updateSelection(min, max);
		long interval = Math.max(1, Double.valueOf((max - min) / nAllelefreqLabels).longValue());

		// enable the zooming out button
		// btn.setVisible(true);
		// Events.sendEvent(event.getName(), chartManhattan, null);

		AppContext.debug(
				"1event.getName()=" + event.getName() + "  min=" + min + " max=" + max + " interval=" + interval);

		chartAlleleFrequency.getXAxis().getLabels().setStep(interval);
		/*
		 * chartAlleleFrequency.getXAxis().setMax(max);
		 * chartAlleleFrequency.getXAxis().setMin(min);
		 */

		ResetZoomButton reset = chartAlleleFrequency.getChart().getResetZoomButton();
		Map mapChartAttribute = new HashMap();
		if (reset != null)
			mapChartAttribute = reset.getTheme();
		mapChartAttribute.put("visibility", "hidden");
		if (reset != null)
			reset.setTheme(mapChartAttribute);
	}

	/**
	 * Update allele frequency chart
	 */
	private void updateAlleleFrequencyChart() {

		try {

			// if(allelefreqlines==null) calculateAlleleFrequencies();
			varFreqlines = calculateAlleleFrequencies();

			CategoryModel selmodel = null;
			String tooltipjs = null;
			boolean show4th = false;
			AlleleFreqLineData allelefreqlines = varFreqlines[0];
			AlleleFreqLineData genotypefreqlines = varFreqlines[1];
			if (radioShowAlleleFrequency.isSelected()) {

				if (radioMajorAlleles.isSelected())
					selmodel = allelefreqlines.linepercentmajormodel;
				else if (radioMinorAlleles.isSelected())
					selmodel = allelefreqlines.linepercentminormodel;
				else if (radio3rdAlleles.isSelected())
					selmodel = allelefreqlines.linepercent3rdmodel;
				else if (radio4thAlleles.isSelected())
					selmodel = allelefreqlines.linepercent4thmodel;

				chartAlleleFrequency.getYAxis().setTitle("Allele frequency (%)");
				chartAlleleFrequency.setTitle("Allele frequency (%)");

				tooltipjs = allelefreqlines.tooltipjs;
				show4th = allelefreqlines.line4thallelemodel != null;

			} else if (radioShowAlleleCount.isSelected()) {
				if (radioMajorAlleles.isSelected())
					selmodel = allelefreqlines.linecountmajormodel;
				else if (radioMinorAlleles.isSelected())
					selmodel = allelefreqlines.linecountminormodel;
				else if (radio3rdAlleles.isSelected())
					selmodel = allelefreqlines.line3rdallelemodel;
				else if (radio4thAlleles.isSelected())
					selmodel = allelefreqlines.line4thallelemodel;

				chartAlleleFrequency.getYAxis().setTitle("Allele count");
				chartAlleleFrequency.setTitle("Allele count");
				tooltipjs = allelefreqlines.tooltipjs;
				show4th = allelefreqlines.line4thallelemodel != null;
			} else if (radioShowGenotypeFrequency.isSelected()) {

				if (radioMajorAlleles.isSelected())
					selmodel = genotypefreqlines.linepercentmajormodel;
				else if (radioMinorAlleles.isSelected())
					selmodel = genotypefreqlines.linepercentminormodel;
				else if (radio3rdAlleles.isSelected())
					selmodel = genotypefreqlines.linepercent3rdmodel;
				else if (radio4thAlleles.isSelected())
					selmodel = genotypefreqlines.linepercent4thmodel;

				chartAlleleFrequency.getYAxis().setTitle("Genotype frequency (%)");
				chartAlleleFrequency.setTitle("Genotype frequency");
				tooltipjs = genotypefreqlines.tooltipjs;
				show4th = genotypefreqlines.line4thallelemodel != null;

			} else if (radioShowGenotypeCount.isSelected()) {
				if (radioMajorAlleles.isSelected())
					selmodel = genotypefreqlines.linecountmajormodel;
				else if (radioMinorAlleles.isSelected())
					selmodel = genotypefreqlines.linecountminormodel;
				else if (radio3rdAlleles.isSelected())
					selmodel = genotypefreqlines.line3rdallelemodel;
				else if (radio4thAlleles.isSelected())
					selmodel = genotypefreqlines.line4thallelemodel;

				chartAlleleFrequency.getYAxis().setTitle("Genotype count");
				chartAlleleFrequency.setTitle("Genotype count");
				tooltipjs = genotypefreqlines.tooltipjs;
				show4th = genotypefreqlines.line4thallelemodel != null;

			}

			Tooltip ttc = new Tooltip();
			ttc.setFormatter(new JavaScriptValue(tooltipjs));
			this.chartAlleleFrequency.setTooltip(ttc);

			List listcols = new ArrayList();
			Iterator<String> itpop = allelefreqlines.mapPop2Majoralleles.keySet().iterator();
			while (itpop.hasNext()) {
				String subpop = itpop.next();
				String col = Data.getSubpopulationColor(subpop);
				listcols.add(col);
			}
			chartAlleleFrequency.setColors(listcols);
			radio4thAlleles.setVisible(show4th);

			chartAlleleFrequency.setModel(selmodel);
			AxisLabels xlabels = chartAlleleFrequency.getXAxis().getLabels();
			xlabels.setRotation(-75);
			xlabels.setAlign("right");
			chartAlleleFrequency.getXAxis().setTickInterval(null);

			PlotLine plotLine = new PlotLine();
			plotLine.setValue(0);
			plotLine.setWidth(1);
			plotLine.setColor("#808080");
			chartAlleleFrequency.getYAxis().addPlotLine(plotLine);

			Legend legend = chartAlleleFrequency.getLegend();
			legend.setLayout("vertical");
			legend.setAlign("right");
			legend.setVerticalAlign("middle");
			legend.setBorderWidth(0);

			double min = 0;
			double max = queryRawResult.getListPos().size(); // genotypefreqlines.linecountmajormodel.getSeries(0).
																// //chartAlleleFrequency.getXAxisSize();
			long interval = Math.max(1,
					Double.valueOf(Math.ceil(Double.valueOf((max - min) / nAllelefreqLabels))).longValue());
			AppContext.debug("max=" + max + "  min=" + min + "  (max-min)/" + nAllelefreqLabels + "="
					+ Double.valueOf(Math.ceil(Double.valueOf((max - min) / nAllelefreqLabels))).longValue()
					+ " allelefrqchart xlabelsteps=" + interval);
			xlabels.setStep(interval);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * calculate allele and genotype counts from table data
	 */
	private AlleleFreqLineData[] calculateAlleleFrequencies() throws Exception {

		Map<String, Map<String, Map<String, Integer>>> mapPos2Subpop2Allele2Count = new TreeMap();
		Map<String, Map<String, Map<String, Integer>>> mapPos2Subpop2Genotype2Count = new TreeMap();

		Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) this.biglistboxArray.getModel();
		int poscol = -1;
		List listPos = (List) model.getHeadAt(0);
		Set subpops = new LinkedHashSet();
		subpops.add("all");
		// for(int i=0; i<biglistboxArray.getCols(); i++) {

		int startCol = frozenCols;

		if (listboxPhenotype.getSelectedItem() != null && !listboxPhenotype.getSelectedItem().getLabel().isEmpty()) {
			startCol++;
		}
		;

		for (int i = startCol; i < model.getColumnSize(); i++) {

			// AppContext.debug(listPos.get(i).getClass() + ", " +
			// listPos.get(i).toString());

			String contigpos = listPos.get(i).toString();
			Map mapSubpop2Allele2Count = new TreeMap();
			Map mapSubpop2Genotype2Count = new TreeMap();

			// AppContext.debug("size: " + model.getSize());
			for (int j = 0; j < model.getSize(); j++) {

				// String subpop = model.getCellAt( model.getElementAt(j), 2).toString();

				Object subpopobj = ((Object[]) model.getElementAt(j).get(3))[3];

				String subpop = (subpopobj == null || subpopobj.toString().isEmpty() ? "no subpop"
						: subpopobj.toString()); // ((Object[])model.getElementAt(j).get(3))[3].toString();

				subpops.add(subpop);
				Map mapAllele2Count = (Map) mapSubpop2Allele2Count.get(subpop);
				if (mapAllele2Count == null) {
					mapAllele2Count = new HashMap();
					mapSubpop2Allele2Count.put(subpop, mapAllele2Count);
				}

				String allele = ((Object[]) model.getElementAt(j).get(i))[i].toString();

				allele = allele.trim();

				// if(allele.isEmpty()) allele=" ";
				if (allele.isEmpty())
					continue;

				if (allele.contains("/")) {

					// heterogygous

					String alleles12[] = null;
					try {
						alleles12 = new String[] { String.valueOf(allele.charAt(0)), String.valueOf(allele.charAt(2)) };

					} catch (Exception ex) {
						AppContext.debug("allele=" + allele);
						// ex.printStackTrace();
						// throw new RuntimeException(ex);
						if (allele.startsWith("/"))
							alleles12 = new String[] { String.valueOf(" "), String.valueOf(allele.charAt(1)) };
						else if (allele.endsWith("/"))
							alleles12 = new String[] { String.valueOf(allele.charAt(1)), String.valueOf(" ") };
						else
							alleles12 = new String[] { String.valueOf(" "), String.valueOf(" ") };
					}

					Integer allele1count = (Integer) mapAllele2Count.get(alleles12[0]);
					if (allele1count == null)
						allele1count = 0;
					allele1count = allele1count + 1;
					mapAllele2Count.put(alleles12[0], allele1count);

					Integer allele2count = (Integer) mapAllele2Count.get(alleles12[1]);
					if (allele2count == null)
						allele2count = 0;
					allele2count = allele2count + 1;
					mapAllele2Count.put(alleles12[1], allele2count);

					// AppContext.debug(subpop + " " + contigpos + " " + allele + "
					// mapAllele2Count=" + mapAllele2Count + " added alleles " + alleles12[0] + ", "
					// + alleles12[1]);

					mapSubpop2Allele2Count.put(subpop, mapAllele2Count);

					Map mapAllele2CountAll = (Map) mapSubpop2Allele2Count.get("all");
					if (mapAllele2CountAll == null) {
						mapAllele2CountAll = new HashMap();
						mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
					}

					Integer allele1countall = (Integer) mapAllele2CountAll.get(alleles12[0]);
					if (allele1countall == null)
						allele1countall = 0;
					allele1countall = allele1countall + 1;
					mapAllele2CountAll.put(alleles12[0], allele1countall);

					Integer allele2countall = (Integer) mapAllele2CountAll.get(alleles12[1]);
					if (allele2countall == null)
						allele2countall = 0;
					allele2countall = allele2countall + 1;
					mapAllele2CountAll.put(alleles12[1], allele2countall);

					mapSubpop2Allele2Count.put("all", mapAllele2CountAll);

				} else {

					// homozygous

					Integer allelecount = (Integer) mapAllele2Count.get(allele);
					if (allelecount == null)
						allelecount = 0;
					allelecount = allelecount + 2;
					mapAllele2Count.put(allele, allelecount);

					mapSubpop2Allele2Count.put(subpop, mapAllele2Count);

					Map mapAllele2CountAll = (Map) mapSubpop2Allele2Count.get("all");
					if (mapAllele2CountAll == null) {
						mapAllele2CountAll = new HashMap();
						mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
					}
					Integer allelecountall = (Integer) mapAllele2CountAll.get(allele);
					if (allelecountall == null)
						allelecountall = 0;
					allelecountall = allelecountall + 2;
					mapAllele2CountAll.put(allele, allelecountall);

					// AppContext.debug("i=" + i + " j=" + j + " subpop=" + subpop + " allele=" +
					// allele + " allelecount=" + allelecount + " allelecountall=" +
					// allelecountall);

					mapSubpop2Allele2Count.put("all", mapAllele2CountAll);
				}

				// genotype count

				Map mapGenotype2Count = (Map) mapSubpop2Genotype2Count.get(subpop);
				if (mapGenotype2Count == null) {
					mapGenotype2Count = new HashMap();
					mapSubpop2Genotype2Count.put(subpop, mapGenotype2Count);
				}

				Integer genotypecount = (Integer) mapGenotype2Count.get(allele);
				if (genotypecount == null)
					genotypecount = 0;
				genotypecount = genotypecount + 1;
				mapGenotype2Count.put(allele, genotypecount);

				mapSubpop2Genotype2Count.put(subpop, mapGenotype2Count);

				Map mapGenotype2CountAll = (Map) mapSubpop2Genotype2Count.get("all");
				if (mapGenotype2CountAll == null) {
					mapGenotype2CountAll = new HashMap();
					mapSubpop2Genotype2Count.put("all", mapGenotype2CountAll);
				}
				Integer genotypecountall = (Integer) mapGenotype2CountAll.get(allele);
				if (genotypecountall == null)
					genotypecountall = 0;
				genotypecountall = genotypecountall + 1;
				mapGenotype2CountAll.put(allele, genotypecountall);

				// AppContext.debug("i=" + i + " j=" + j + " subpop=" + subpop + " allele=" +
				// allele + " allelecount=" + allelecount + " allelecountall=" +
				// allelecountall);

				mapSubpop2Genotype2Count.put("all", mapGenotype2CountAll);

			}
			/*
			 * if(subpops.contains("")) { mapSubpop2Allele2Count.put("no subpop",
			 * mapSubpop2Allele2Count.get("")); mapSubpop2Allele2Count.remove("");
			 * mapSubpop2Genotype2Count.put("no subpop", mapSubpop2Genotype2Count.get(""));
			 * mapSubpop2Genotype2Count.remove(""); subpops.add("no subpop");
			 * subpops.remove(""); }
			 */

			mapPos2Subpop2Allele2Count.put(contigpos, mapSubpop2Allele2Count);
			mapPos2Subpop2Genotype2Count.put(contigpos, mapSubpop2Genotype2Count);
		}

		// AppContext.debug("varieties " + model.getSize());
		// AppContext.debug("mapPos2Subpop2Allele2Count=" + mapPos2Subpop2Allele2Count);
		// AppContext.debug("mapPos2Subpop2Genotype2Count=" +
		// mapPos2Subpop2Genotype2Count);

		return new AlleleFreqLineData[] { calcFreq(subpops, mapPos2Subpop2Allele2Count),
				calcFreq(subpops, mapPos2Subpop2Genotype2Count) };

		// updateAlleleFrequencyChart();

	}

	/**
	 * Calculate frequency from count
	 * 
	 * @param subpops
	 * @param mapPos2Subpop2Allele2Count
	 * @return
	 */
	private AlleleFreqLineData calcFreq(Collection subpops,
			Map<String, Map<String, Map<String, Integer>>> mapPos2Subpop2Allele2Count) throws Exception {

		AppContext.debug("calcFreq subpops:" + subpops);

		AlleleFreqLineData freqlines = new AlleleFreqLineData();

		freqlines.linecountmajormodel = new DefaultCategoryModel();
		freqlines.linecountminormodel = new DefaultCategoryModel();
		freqlines.line3rdallelemodel = new DefaultCategoryModel();
		// freqlines.line4thallelemodel = new DefaultCategoryModel();
		freqlines.linepercentmajormodel = new DefaultCategoryModel();
		freqlines.linepercentminormodel = new DefaultCategoryModel();
		freqlines.linepercent3rdmodel = new DefaultCategoryModel();
		// freqlines.linepercent4thmodel = new DefaultCategoryModel();

		freqlines.mapPos2Alleles = new HashMap();
		freqlines.mapPos2Subpop2AllelesCountPercentStr = new HashMap();

		// Map<String,List> mapPop2Majoralleles=new HashMap();
		freqlines.mapPop2Majoralleles = new LinkedHashMap(); // mapPop2Majoralleles;

		// Map<String,Integer> mapGroup2SubpopCount=new HashMap();
		// if(mapGroup2SubpopCount!=null) mapGroup2SubpopCount.put("all", 0);

		Map<String, Integer> mapPosSubTotal = new HashMap();
		Iterator<String> itSubpop = subpops.iterator();
		while (itSubpop.hasNext()) {
			String subpop = itSubpop.next();
			List<String> majorallelesOnly = new ArrayList();
			Iterator<String> itPos = mapPos2Subpop2Allele2Count.keySet().iterator();
			while (itPos.hasNext()) {
				String pos = itPos.next();
				String majallele = null;
				Map mapSub2Allele = mapPos2Subpop2Allele2Count.get(pos);
				if (mapSub2Allele == null) {
					majallele = "";
					majorallelesOnly.add("");
					continue;
				}

				Map<String, Integer> mapAllele2Count = (Map) mapSub2Allele.get(subpop);
				if (mapAllele2Count == null) {
					majallele = "";
					majorallelesOnly.add("");
					continue;
				}

				// get major, minor allele
				StringBuffer buffLabel = new StringBuffer();

				Map<Integer, List<String>> mapCount2Alleles = new TreeMap(Collections.reverseOrder());
				Iterator<String> itAl = mapAllele2Count.keySet().iterator();
				while (itAl.hasNext()) {
					String al = itAl.next();
					Integer cnt = mapAllele2Count.get(al);
					List<String> listAlleles = mapCount2Alleles.get(cnt);
					if (listAlleles == null) {
						listAlleles = new ArrayList();
						mapCount2Alleles.put(cnt, listAlleles);
					}
					listAlleles.add(al);
				}

				int allallelecount = 0;
				Iterator<Integer> itCount = mapCount2Alleles.keySet().iterator();
				while (itCount.hasNext()) {
					Integer cnt = itCount.next();
					allallelecount += cnt * mapCount2Alleles.get(cnt).size();
				}

				itCount = mapCount2Alleles.keySet().iterator();
				// AppContext.debug( subpop + ", pos=" + pos + ", count=" + allallelecount );

				// Iterator<String> itSortedalleles=mapCount2Allele.values().iterator();

				try {

					String minallele = null;
					String allele3rd = null;
					String allele4th = null;

					if (mapAllele2Count.keySet().size() == 1) {
						// pos = pos + " [" + mapCount2Allele.get(listCount.get(0)) + " 100%" + "]";
						Integer cnt = itCount.next();
						freqlines.linecountmajormodel.setValue(subpop, pos, cnt);
						freqlines.linepercentmajormodel.setValue(subpop, pos, 100);

						majallele = mapCount2Alleles.get(cnt).get(0);
						buffLabel.append(majallele + ":" + cnt + ":100");

					} else if (mapAllele2Count.keySet().size() == 2) {
						/*
						 * String majallele=null; String minallele=null;
						 */
						int major = (Integer) itCount.next();
						int minor = -1;
						majallele = mapCount2Alleles.get(major).get(0);
						if (mapCount2Alleles.get(major).size() > 1) {
							minor = major;
							minallele = mapCount2Alleles.get(major).get(1);
						} else {
							if (itCount.hasNext()) {
								minor = itCount.next();
								minallele = mapCount2Alleles.get(minor).get(0);
							}
						}
						if (itCount.hasNext())
							throw new RuntimeException("mapAllele2Count.keySet().size()==2 but >2 alleles at " + pos
									+ ":" + mapAllele2Count);

						/*
						 * int major = (Integer)itCount.next(); int minor=major; if(itCount.hasNext())
						 * minor=itCount.next();
						 */
						// int minor = (Integer)listCount.get(0);
						double percentmajor = major * 100.0 / allallelecount;
						double percentminor = minor * 100.0 / allallelecount;

						// pos = pos + " [" + mapCount2Allele.get(listCount.get(1)) + " " +
						// String.format( "%.2f", percentmajor) + "%" + ", " +
						// mapCount2Allele.get(listCount.get(0)) + " " + String.format( "%.2f",
						// percentminor) + "%]";

						freqlines.linecountmajormodel.setValue(subpop, pos, major);
						freqlines.linecountminormodel.setValue(subpop, pos, minor);
						freqlines.linepercentmajormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentmajor)));
						freqlines.linepercentminormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentminor)));

						buffLabel.append(majallele + ":" + major + ":" + String.format("%.2f", percentmajor) + ";"
								+ minallele + ":" + minor + ":" + String.format("%.2f", percentminor));

					} else if (mapAllele2Count.keySet().size() == 3) {
						// int allelecount = mapAllele2Count.keySet().size();
						/*
						 * String majallele=null; String minallele=null; String allele3rd = null;
						 */
						int major = (Integer) itCount.next();
						int minor = -1;
						int allele3 = -1;
						majallele = mapCount2Alleles.get(major).get(0);
						if (mapCount2Alleles.get(major).size() > 1) {
							minor = major;
							minallele = mapCount2Alleles.get(major).get(1);
							if (mapCount2Alleles.get(major).size() > 2) {
								allele3 = major;
								allele3rd = mapCount2Alleles.get(major).get(2);
							} else {
								if (itCount.hasNext()) {
									allele3 = itCount.next();
									allele3rd = mapCount2Alleles.get(allele3).get(0);
								}
							}
						} else {
							if (itCount.hasNext()) {
								minor = itCount.next();
								minallele = mapCount2Alleles.get(minor).get(0);
								if (mapCount2Alleles.get(minor).size() > 1) {
									allele3 = minor;
									allele3rd = mapCount2Alleles.get(minor).get(1);
								} else {
									if (itCount.hasNext()) {
										allele3 = itCount.next();
										allele3rd = mapCount2Alleles.get(allele3).get(0);
									}
								}
							}
						}

						if (itCount.hasNext())
							throw new RuntimeException(">3 alleles at " + pos + ":" + mapAllele2Count);

						double percentmajor = major * 100.0 / allallelecount;
						double percentminor = minor * 100.0 / allallelecount;
						double percent3rd = allele3 * 100.0 / allallelecount;

						// pos = pos + " [" + mapCount2Allele.get(listCount.get(allelecount-1)) + " " +
						// String.format( "%.2f", percentmajor) + "%, " +
						// mapCount2Allele.get(listCount.get(allelecount-2)) + " " + String.format(
						// "%.2f", percentminor) + "%, " +
						// mapCount2Allele.get(listCount.get(allelecount-3)) + " " + String.format(
						// "%.2f", percent3rd) + "%]";

						freqlines.linecountmajormodel.setValue(subpop, pos, major);
						freqlines.linecountminormodel.setValue(subpop, pos, minor);
						freqlines.linepercentmajormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentmajor)));
						freqlines.linepercentminormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentminor)));
						freqlines.line3rdallelemodel.setValue(subpop, pos, allele3);
						freqlines.linepercent3rdmodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percent3rd)));

						buffLabel.append(majallele + ":" + major + ":" + String.format("%.2f", percentmajor) + ";"
								+ minallele + ":" + minor + ":" + String.format("%.2f", percentminor) + ";" + allele3rd
								+ ":" + allele3 + ":" + String.format("%.2f", percent3rd));

					} else if (mapAllele2Count.keySet().size() > 3) {
						// int allelecount = mapAllele2Count.keySet().size();
						/*
						 * String majallele=null; String minallele=null; String allele3rd = null; String
						 * allele4th = null;
						 */
						int major = (Integer) itCount.next();
						int minor = -1;
						int allele3 = -1;
						int allele4 = -1;
						majallele = mapCount2Alleles.get(major).get(0);
						if (mapCount2Alleles.get(major).size() > 1) {
							minor = major;
							minallele = mapCount2Alleles.get(major).get(1);
							if (mapCount2Alleles.get(major).size() > 2) {
								allele3 = major;
								allele3rd = mapCount2Alleles.get(major).get(2);
								if (mapCount2Alleles.get(major).size() > 3) {
									allele4 = major;
									allele4th = mapCount2Alleles.get(major).get(3);
								}
							}
						} else {
							if (itCount.hasNext()) {
								minor = itCount.next();
								minallele = mapCount2Alleles.get(minor).get(0);
								if (mapCount2Alleles.get(minor).size() > 1) {
									allele3 = minor;
									allele3rd = mapCount2Alleles.get(minor).get(1);
									if (mapCount2Alleles.get(minor).size() > 2) {
										allele4 = minor;
										allele4th = mapCount2Alleles.get(minor).get(2);
									}
								} else {
									if (itCount.hasNext()) {
										allele3 = itCount.next();
										allele3rd = mapCount2Alleles.get(allele3).get(0);
										if (mapCount2Alleles.get(allele3).size() > 1) {
											allele4 = allele3;
											allele4th = mapCount2Alleles.get(allele3).get(1);
										} else {
											if (itCount.hasNext()) {
												allele4 = itCount.next();
												allele4th = mapCount2Alleles.get(allele4).get(0);
											}
										}
									}
								}
							}
						}

						// if(itCount.hasNext()) throw new RuntimeException(">3 alleles at " + pos + ":"
						// + mapAllele2Count);

						double percentmajor = major * 100.0 / allallelecount;
						double percentminor = minor * 100.0 / allallelecount;
						double percent3rd = allele3 * 100.0 / allallelecount;
						double percent4th = allele4 * 100.0 / allallelecount;

						// pos = pos + " [" + mapCount2Allele.get(listCount.get(allelecount-1)) + " " +
						// String.format( "%.2f", percentmajor) + "%, " +
						// mapCount2Allele.get(listCount.get(allelecount-2)) + " " + String.format(
						// "%.2f", percentminor) + "%, " +
						// mapCount2Allele.get(listCount.get(allelecount-3)) + " " + String.format(
						// "%.2f", percent3rd) + "%]";

						freqlines.linecountmajormodel.setValue(subpop, pos, major);
						freqlines.linecountminormodel.setValue(subpop, pos, minor);
						freqlines.line3rdallelemodel.setValue(subpop, pos, allele3);
						freqlines.line4thallelemodel = new DefaultCategoryModel();
						freqlines.line4thallelemodel.setValue(subpop, pos, allele4);

						freqlines.linepercentmajormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentmajor)));
						freqlines.linepercentminormodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percentminor)));
						freqlines.linepercent3rdmodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percent3rd)));
						freqlines.linepercent4thmodel = new DefaultCategoryModel();
						freqlines.linepercent4thmodel.setValue(subpop, pos,
								Double.valueOf(String.format("%.2f", percent4th)));

						buffLabel.append(majallele + ":" + major + ":" + String.format("%.2f", percentmajor) + ";"
								+ minallele + ":" + minor + ":" + String.format("%.2f", percentminor) + ";" + allele3rd
								+ ":" + allele3 + ":" + String.format("%.2f", percent3rd) + ";" + allele4th + ":"
								+ allele4 + ":" + String.format("%.2f", percent4th));

						if (allele4 > -1 && mapCount2Alleles.get(allele4).size() > 1) {
							for (int i = 1; i < mapCount2Alleles.get(allele4).size(); i++) {
								buffLabel.append(";" + mapCount2Alleles.get(allele4).get(i) + ":" + allele4 + ":"
										+ String.format("%.2f", allele4 * 100.0 / allallelecount));
							}
						}

						while (itCount.hasNext()) {
							int cnt = itCount.next();
							List cntalleles = mapCount2Alleles.get(cnt);
							Iterator<String> italleles = cntalleles.iterator();
							while (italleles.hasNext()) {
								buffLabel.append(";" + italleles.next() + ":" + cnt + ":"
										+ String.format("%.2f", cnt * 100.0 / allallelecount));
							}
							AppContext.error(">4 alleles at " + pos + ":" + mapAllele2Count);
							// throw new RuntimeException(">3 alleles at " + pos + ":" + mapAllele2Count);
						}

					}

					/*
					 * List listAllele = freqlines.mapPop2Majoralleles.get(subpop);
					 * if(listAllele==null) { listAllele=new ArrayList();
					 * freqlines.mapPop2Majoralleles.put(subpop, listAllele); // //listAllele }
					 * listAllele.add(majallele);
					 */

					majorallelesOnly.add(majallele);

				} catch (Exception ex) {
					ex.printStackTrace();

					AppContext.debug("mapCount2Alleles=" + mapCount2Alleles);
					AppContext.debug("mapAllele2Count=" + mapAllele2Count);
					AppContext.debug("allallelecount=" + allallelecount);

					throw new RuntimeException(ex);
				}

				mapPosSubTotal.put(pos + "-" + subpop, allallelecount);
				Map<String, String> subpop2labels = freqlines.mapPos2Subpop2AllelesCountPercentStr.get(pos);
				if (subpop2labels == null) {
					subpop2labels = new HashMap();
					freqlines.mapPos2Subpop2AllelesCountPercentStr.put(pos, subpop2labels);
				}
				subpop2labels.put(subpop, buffLabel.toString());

			}

			/*
			 * List listAllele = freqlines.mapPop2Majoralleles.get(subpop);
			 * if(listAllele==null) { listAllele=new ArrayList();
			 * freqlines.mapPop2Majoralleles.put(subpop, listAllele); }
			 * listAllele.addAll(majorallelesOnly);
			 */
			freqlines.mapPop2Majoralleles.put(subpop, majorallelesOnly);
		}

		Iterator<String> itPop = freqlines.mapPop2Majoralleles.keySet().iterator();
		while (itPop.hasNext()) {
			String pop = itPop.next();
			List majalleles = (List) freqlines.mapPop2Majoralleles.get(pop);
			if (majalleles.size() != mapPos2Subpop2Allele2Count.size()) {
				AppContext.debug("pop: majalleles.size()!=mapPos.size(): " + pop + " " + majalleles.size() + " != "
						+ mapPos2Subpop2Allele2Count.size());
				AppContext.debug("majalleles=" + majalleles);
				AppContext.debug("mapPos=" + mapPos2Subpop2Allele2Count.keySet());
			}
		}

		Iterator<String> itPos = freqlines.mapPos2Subpop2AllelesCountPercentStr.keySet().iterator();
		StringBuffer buffjsmarker = new StringBuffer();
		buffjsmarker.append("function() { var pos2sub2label={\n");
		while (itPos.hasNext()) {
			String postr = itPos.next();
			buffjsmarker.append("\"" + postr + "\" : {\n");
			Map<String, String> mapsub2labels = freqlines.mapPos2Subpop2AllelesCountPercentStr.get(postr);
			Iterator<String> itSub = mapsub2labels.keySet().iterator();
			while (itSub.hasNext()) {
				String sub = itSub.next();

				/*
				 * buffjsmarker.append("\""+sub+"\" : ["); String labels[]=
				 * mapsub2labels.get(sub).split(";"); for(int ilab=0; ilab<labels.length;
				 * ilab++) { buffjsmarker.append( "\"" + labels[ilab] + "\"" );
				 * if(ilab<labels.length-1) buffjsmarker.append(","); }
				 * buffjsmarker.append("] \n"); if(itSub.hasNext()) buffjsmarker.append(",\n");
				 */

				buffjsmarker.append("\"" + sub + "\" : '");
				String labels[] = mapsub2labels.get(sub).split(";");
				for (int ilab = 0; ilab < labels.length; ilab++) {
					String[] vals = labels[ilab].split(":");
					if (vals.length == 3)
						buffjsmarker.append(vals[0] + ": " + vals[1] + " <b>" + vals[2] + "%</b>");
					else {
						buffjsmarker.append("<b>" + labels[ilab] + "</b>");
						AppContext.debug("wrong allele,count,percent format for " + labels[ilab] + ", sub=" + sub);
					}
					if (ilab < labels.length - 1)
						buffjsmarker.append("<br>");
				}
				buffjsmarker.append("<br><b>Total:</b> " + mapPosSubTotal.get(postr + "-" + sub) + "<br>");
				buffjsmarker.append("'\n");
				if (itSub.hasNext())
					buffjsmarker.append(",");

			}
			buffjsmarker.append("}");
			if (itPos.hasNext())
				buffjsmarker.append(",\n");
		}
		buffjsmarker.append("};\n");

		// buffjsmarker.append("return '<b>' + this.x + ' ' + this.series.name +
		// '</b><br>' + pos2sub2label[this.x][this.series.name];}");
		buffjsmarker.append(
				"return '<b>' + this.x +  '</b>:  <span style=\"font-weight:bold;color:' + this.series.color + '\">' +  this.series.name + '</span></b><br>' + pos2sub2label[this.x][this.series.name] + '<br/>';}");

		// <span style="color:{series.color}">{series.name}</span>:

		// AppContext.debug(buffjsmarker.toString());

		freqlines.tooltipjs = buffjsmarker.toString();

		/*
		 * Tooltip ttc=new Tooltip(); ttc.setFormatter(new
		 * JavaScriptValue(buffjsmarker.toString()));
		 * this.chartAlleleFrequency.setTooltip(ttc);
		 */
		/*
		 * 
		 * StringBuffer buffjsmarker=new StringBuffer(); buffjsmarker.
		 * append("function() { var chr=0; var off=0; var val=this.x; if(val>345713663) "
		 * ); buffjsmarker.
		 * append(" { chr=12; off=345713663;} else if(val>316692557){chr=11; off=316692557;}  else if(val>293485270){chr=10; off=293485270;}  else if(val>270472550){chr=9; off=270472550;}  else if(val>242029528){chr=8; off=242029528;} "
		 * ); buffjsmarker.
		 * append(" else if(val>212331907){chr=7; off=212331907;}  else if(val>181083120){chr=6; off=181083120;}    else if(val>151124686){chr=5; off=151124686;}   else if(val>115621992){chr=4; off=115621992;} "
		 * ); buffjsmarker.
		 * append("   else if(val>79208173){chr=3; off=79208173;}   else if(val>43270923){chr=2; off=43270923;}   else {chr=1; off=0; };  return 'chr ' + chr + '-' +  (val-off) + ',  -logP=' + this.y.toFixed(2);}"
		 * );
		 * 
		 * Tooltip ttc=new Tooltip(); //ttc.setFormatter(new
		 * JavaScriptValue("function() { return this.x + ',' + this.y; }"));
		 * ttc.setFormatter(new JavaScriptValue(buffjsmarker.toString()));
		 * chartManhattanXY.setTooltip(ttc);
		 */

		return freqlines;

	}

	private class AlleleFreqLineData {
		CategoryModel linecountmajormodel;
		CategoryModel linecountminormodel;
		CategoryModel line3rdallelemodel;
		CategoryModel line4thallelemodel;
		CategoryModel linepercentmajormodel;
		CategoryModel linepercentminormodel;
		CategoryModel linepercent3rdmodel;
		CategoryModel linepercent4thmodel;
		String tooltipjs;

		Map<String, List> mapPos2Alleles;
		Map<String, Map<String, String>> mapPos2Subpop2AllelesCountPercentStr;
		public Map<String, List> mapPop2Majoralleles;

	}

	private void downloadBigListboxFlapjackZip(VariantAlignmentTableArraysImpl table, String filename) {

		try {
			String chr = this.selectChr.getValue();
			String refs[] = table.getReference();
			String markernames[] = null;

			StringBuffer buff = new StringBuffer();
			buff.append("# fjfile = MAP\n");
			// buff.append("POSITION").append(delimiter).append("MISMATCH").append(delimiter);
			Position[] positions = table.getPosition();
			String contigs[] = table.getContigs();

			markernames = new String[positions.length];

			for (int i = 0; i < positions.length; i++) {
				if (!refs[i].equals("-")) {
					int pos = positions[i].getPosition().intValue();
					chr = positions[i].getChr().toString();
					String contigname = null;
					if (contigs != null)
						contigname = contigs[i];
					else {
						try {
							Integer.valueOf(chr.toLowerCase().replace("chr", ""));
							contigname = "chr"
									+ String.format("%02d", Integer.valueOf(chr.toLowerCase().replace("chr", "")));
						} catch (Exception ex) {
							contigname = chr.toLowerCase();
						}

					}
					markernames[i] = "snp-" + contigname + "-" + String.format("%08d", pos);
					buff.append(markernames[i]).append("\t").append(contigname).append("\t").append(pos).append("\n");
				} else {
					// snpid="1" + String.format("%02d",
					// Integer.valueOf(AppContext.guessChrFromString(contig))) +
					// String.format("%08d", pos.intValue()) + "." +
					// pos.subtract(BigDecimal.valueOf(pos.longValue())).multiply(BigDecimal.valueOf(100)).intValue();
					BigDecimal pos = positions[i].getPosition();
					chr = positions[i].getChr().toString();
					String contigname = null;
					if (contigs != null)
						contigname = contigs[i];
					else {
						try {
							Integer.valueOf(chr.toLowerCase().replace("chr", ""));
							contigname = "chr"
									+ String.format("%02d", Integer.valueOf(chr.toLowerCase().replace("chr", "")));
						} catch (Exception ex) {
							contigname = chr.toLowerCase();
						}

					}
					markernames[i] = "snp-" + contigname + "-" + String.format("%08d", pos.intValue()) + "." + pos
							.subtract(BigDecimal.valueOf(pos.longValue())).multiply(BigDecimal.valueOf(100)).intValue(); // String.format("%08d",
																															// pos);
					buff.append(markernames[i]).append("\t").append(contigname).append("\t").append(pos).append("\n");
				}
			}

			// String filetype = "text/plain";
			// Filedownload.save( buff.toString(), filetype , filename + ".map");

			FileWriter writer = new FileWriter(filename + ".map");
			writer.append(buff.toString());
			writer.flush();
			writer.close();

			// AppContext.debug("map: "+ buff.toString() );
			// AppContext.debug("File download complete! Saved to: "+filename);
			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			AppContext.debug("snpallvars download complete!" + filename + ".map Downloaded to:"
					+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());

			buff = new StringBuffer();

			buff.append("# fjFile = GENOTYPE\n\t");

			for (int i = 0; i < markernames.length; i++) {
				buff.append(markernames[i]);
				if (i < markernames.length - 1)
					buff.append("\t");
			}
			buff.append("\n");

			Object[][] varalleles = table.getVaralleles();
			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
			// <BigDecimal,Variety> mapVarId2Var =
			// varietyfacade.getMapId2Variety(getDataset());
			Set ds = getDataset();
			Map<String, Map<BigDecimal, StockSample>> mapDs = varietyfacade.getMapId2Sample(ds);

			Map<String, Integer> mapVarid2Count = new HashMap();

			for (int i = 0; i < table.getVarid().length; i++) {
				Map<BigDecimal, StockSample> mapVarId2Sample = mapDs.get(table.getDataset()[i]);

				StockSample var = mapVarId2Sample.get(BigDecimal.valueOf(table.getVarid()[i]));

				String indvid = AppContext.createSampleUniqueName(var, ds);

				Integer varidcnt = mapVarid2Count.get(indvid);
				if (varidcnt == null) {
					mapVarid2Count.put(indvid, 1);
				} else {
					indvid = indvid + "-" + (varidcnt + 1);
					mapVarid2Count.put(indvid, (varidcnt + 1));
				}

				// if(mapVarId2Var.get(table.getVarid()[i])==null) throw new
				// RuntimeException("null variety with id=" + table.getVarid()[i])
				// String indvid= mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i])
				// ).getIrisId().replaceAll(" ","_");
				// String indvid= ((mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i])
				// ).getIrisId()==null)? mapVarId2Var.get(
				// BigDecimal.valueOf(table.getVarid()[i]) ).getAccession():
				// mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i])
				// ).getIrisId()).replaceAll(" ","_");
				buff.append(indvid).append("\t");

				// Family ID
				// Sample ID
				// Paternal ID
				// Maternal ID
				// Sex (1=male; 2=female; other=unknown)
				// Affection (0=unknown; 1=unaffected; 2=affected)
				// Genotypes (space or tab separated, 2 for each marker. 0=missing)

				for (int j = 0; j < refs.length; j++) {
					String allele1 = (String) varalleles[i][j];
					if (allele1.isEmpty() || allele1.equals("?"))
						buff.append("-");
					else
						buff.append(allele1);
					if (j < refs.length - 1)
						buff.append("\t");
				}
				buff.append("\n");
			}

			writer = new FileWriter(filename + ".genotype");
			writer.append(buff.toString());
			writer.flush();
			writer.close();

			String allzipfilenames[] = new String[] { filename + ".map", filename + ".genotype" };
			new CreateZipMultipleFiles(filename + "-flapjack-" + ".zip", allzipfilenames).create();
			Filedownload.save(new File(filename + "-flapjack-" + ".zip"), "application/zip");

			zksession = Sessions.getCurrent();
			AppContext.debug("snpallvars download complete!" + filename + ".ped Downloaded to:"
					+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void downloadBigListboxFlapjackZip(String filename) {
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
		downloadBigListboxFlapjackZip(table, filename);
	}
	
	public Callable<AsyncJobReport> callableDownloadBigListbox(final String filename, final String delimiter,
			final String format) {
		return new Callable<AsyncJobReport>() {
			@Override
			public AsyncJobReport call() throws Exception {
				String msg = "";
				String jobid = "";
				String url = "";
				Future future = null;
				try {

					GenotypeQueryParams params = fillGenotypeQueryParams();

					if (format.equals("plink"))
						params.setFilename(filename + ".plink");
					else if (format.equals("flapjack"))
						params.setFilename(filename + ".flapjack");
					else {
						params.setFilename(filename);
						params.setDelimiter(delimiter);
					}

					// Object req = Executions.getCurrent().getNativeRequest();
					// String reqstr="";
					// if(req !=null && req instanceof HttpServletRequest) {
					// HttpServletRequest servreq= (HttpServletRequest)req;
					// String forwardedfor= servreq.getHeader("x-forwarded-for");
					// if(forwardedfor!=null) reqstr=forwardedfor;
					// else reqstr= servreq.getRemoteAddr() + "-" + servreq.getRemoteHost();
					//
					// /*
					// String forwardedfor= servreq.getHeader("x-forwarded-for");
					// if(forwardedfor!=null) reqstr+="-" + forwardedfor;
					// */
					// }
					// String submitter=( (AppContext.isIRRILAN() ||
					// reqstr.contains(AppContext.getIRRIIp()))?reqstr+"-"+AppContext.createTempFilename():reqstr);

					params.setSubmitter(AppContext.getSubmitter());

					/*
					 * params.setSubmitter( Sessions.getCurrent().getLocalAddr() +"-"+
					 * Sessions.getCurrent().getLocalName() + "-" +
					 * Sessions.getCurrent().getRemoteAddr() + "-" +
					 * Sessions.getCurrent().getRemoteHost() + "-" +
					 * Sessions.getCurrent().getServerName() + reqstr );
					 */

					// report = genotype.querydownloadGenotypeAsync(params);

					jobsfacade_orig = (JobsFacade) AppContext.checkBean(jobsfacade_orig, "JobsFacade");
					JobsFacade jobsfacade = jobsfacade_orig;
					if (params.getSubmitter() == null) {
						msg = "Submitter ID required for long jobs.";
					} else if (jobsfacade.checkSubmitter(params.getSubmitter())) {
						msg = "You have a running long job. Please try again when that job is done.";
					} else {
						AsyncJob job = new AsyncJobImpl(new File(params.getFilename()).getName(), params.toString(),
								params.getSubmitter());
						if (jobsfacade.addJob(job)) {
							Future futureReportjob = genotype.querydownloadGenotypeAsync(params);
							// AsyncJobReport rep=(AsyncJobReport)futureReportjob.get();
							// AppContext.debug( (rep==null? "rep=null":rep.getMessage() ));
							// rep.
							job.setFuture(futureReportjob);
							future = futureReportjob;
							msg = jobsfacade.JOBSTATUS_SUBMITTED;
							jobid = job.getJobId();
							url = job.getUrl();
						} else {
							msg = jobsfacade.JOBSTATUS_REFUSED;
						}
					}
					AppContext.debug("callableDownloadBigListbox.. submitted");
				} catch (Exception ex) {
					ex.printStackTrace();
					// Messagebox.show("call():" + ex.getMessage());
					msg = ex.getMessage();

				}
				return new AsyncJobReport(jobid, msg, url, future);
			}
		};
	}
	
	private void downloadBigListbox(String filename, String delimiter) {
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
		downloadBigListbox(table, filename, delimiter);
	}
	
	// private void downloadBigListbox(String filename, String delimiter) {
	private void downloadBigListbox(VariantAlignmentTableArraysImpl table, String filename, String delimiter) {
		downloadBigListbox(table, filename, delimiter, false, false);
	}
	
	private void downloadBigListbox(VariantAlignmentTableArraysImpl table, String filename, String delimiter,
			boolean isAppend, boolean isAppendFirst) {

		try {

			// Object2StringMultirefsMatrixModel matrixmodel =
			// (Object2StringMultirefsMatrixModel)biglistboxArray.getModel();
			// VariantAlignmentTableArraysImpl table =
			// (VariantAlignmentTableArraysImpl)matrixmodel.getData();

			StringBuffer buff = new StringBuffer();
			String refs[] = table.getReference();

			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
			Map<BigDecimal, Object> mapVarid2Phenotype = null;
			String sPhenotype = null;
			String phenlabel = "";
			String phenfiller = "";
			String columnfillers = delimiter + delimiter + delimiter + delimiter + delimiter;
			if (listboxPhenotype.getSelectedIndex() > 0) {
				sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
				phenlabel = sPhenotype + delimiter;
				columnfillers += delimiter;
				phenfiller = delimiter;
			}

			if (!isAppend || isAppendFirst) {

				String strmismatch = "MISMATCH";
				if (this.listboxAlleleFilter.getSelectedIndex() > 0) {
					strmismatch = "MATCH";
				}

				String stririsgsor = "ASSAY ID";
				// if(this.listboxDataset.getSelectedItem().getValue().equals("hdra")) {
				// stririsgsor="GSOR ID";
				// }

				buff.append(this.listboxReference.getSelectedItem().getLabel().toUpperCase() + " POSITIONS")
						.append(delimiter).append(stririsgsor).append(delimiter).append("ACCESSION").append(delimiter)
						.append("SUBPOPULATION").append(delimiter).append(strmismatch).append(delimiter)
						.append(phenlabel);

				/*
				 * if(this.checkboxShowNPBPosition.isChecked())
				 * buff.append(this.listboxReference.getSelectedItem().getLabel().toUpperCase()
				 * + " POSITIONS").append(delimiter).append("IRIS ID").append(delimiter).append(
				 * "SUBPOPULATION").append(delimiter).append("MISMATCH").append(delimiter); else
				 * buff.append("VARIETY").append(delimiter).append("IRIS ID").append(delimiter).
				 * append("SUBPOPULATION").append(delimiter).append("MISMATCH").append(delimiter
				 * );
				 */

				String[] contigs = table.getContigs();
				// BigDecimal[] positions = table.getPosition();
				Position[] positions = table.getPosition();
				StringBuffer buffPos = new StringBuffer();

				// check if multiple contig

				boolean isMulticontig = false;
				String contig0 = positions[0].getContig();
				for (int i = 2; i < positions.length; i++) {
					if (!contig0.equals(positions[i].getContig())) {
						isMulticontig = true;
						break;
					}
				}

				for (int i = 0; i < positions.length; i++) {
					if (isMulticontig)
						buff.append(positions[i].getContig()).append("-");
					buff.append(positions[i].getPosition());
					if (i < positions.length - 1)
						buff.append(delimiter);
				}

				buff.append("\n" + listboxReference.getSelectedItem().getLabel().toUpperCase() + " ALLELES")
						.append(columnfillers); // .append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);

				/*
				 * if(this.checkboxShowNPBPosition.isChecked()) buff.append("\n" +
				 * listboxReference.getSelectedItem().getLabel().toUpperCase() +
				 * " ALLELES").append(delimiter).append(delimiter).append(delimiter).append(
				 * delimiter); else
				 * buff.append("\nREFERENCE").append(delimiter).append(delimiter).append(
				 * delimiter).append(delimiter);
				 */

				for (int i = 0; i < refs.length; i++) {

					String refnuc = refs[i];
					if (refnuc == null || refnuc.isEmpty()) {
						// tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-frozenCols);
						BigDecimal pos = table.getVariantStringData().getListPos().get(i).getPosition();
						if (table.getVariantStringData().getIndelstringdata() != null
								&& table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc() != null)
							refnuc = table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc().get(pos);
					}

					if (refnuc == null)
						buff.append("");
					else {
						refnuc = refnuc.substring(0, 1);
						buff.append(refnuc);
					}

					if (i < refs.length - 1)
						buff.append(delimiter);
				}
				buff.append("\n");

				Double refsmatch[] = table.getAllrefallelesmatch();

				if (checkboxShowNPBPosition.isChecked()) {
					buff.append("NIPPONBARE POSITION").append(columnfillers); // .append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);
					positions = table.getPositionNPB();
					for (int i = 0; i < positions.length; i++) {
						buff.append(positions[i]);
						if (i < positions.length - 1)
							buff.append(delimiter);
					}

					buff.append("\nNIPPONBARE ALLELES").append(delimiter).append(delimiter).append(delimiter)
							.append(delimiter); // .append(delimiter);
					// buff.append("REF " +
					// refnames[iref]).append(delimiter).append(delimiter).append(delimiter);
					if (refsmatch != null) {
						buff.append(refsmatch[0]);
					}
					buff.append(phenfiller);
					buff.append(delimiter);

					refs = table.getReferenceNPB();
					for (int i = 0; i < refs.length; i++) {

						String refnuc = refs[i];
						if (refnuc.isEmpty()) {
							// tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-frozenCols);
							BigDecimal pos = table.getVariantStringData().getListPos().get(i).getPosition();
							if (table.getVariantStringData().getIndelstringdata() != null && table
									.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc() != null)
								refnuc = table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc()
										.get(pos).substring(0, 1);
						}

						if (refnuc == null)
							buff.append("");
						else {
							refnuc = refnuc.substring(0, 1);
							buff.append(refnuc);
						}

						if (i < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");

				}

				if (this.checkboxShowAllRefAlleles.isChecked()) {
					String allrefsalleles[][] = table.getAllrefalleles();
					String refnames[] = table.getAllrefallelesnames();
					for (int iref = 0; iref < refnames.length; iref++) {

						buff.append("REF " + refnames[iref]).append(delimiter).append(delimiter).append(delimiter)
								.append(delimiter);

						if (refsmatch != null) {
							buff.append(refsmatch[iref]);
						}
						buff.append(phenfiller);
						buff.append(delimiter);

						String irefs[] = allrefsalleles[iref];
						for (int i = 0; i < refs.length; i++) {

							String refnuc = irefs[i];
							/*
							 * if(refnuc.isEmpty()) {
							 * //tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-
							 * frozenCols); BigDecimal pos =
							 * table.getVariantStringData().getListPos().get(i).getPos();
							 * if(table.getVariantStringData().getIndelstringdata()!=null &&
							 * table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc()!=
							 * null) refnuc =
							 * table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc().get
							 * (pos); }
							 */
							if (refnuc == null)
								buff.append("-");
							else
								buff.append(refnuc);
							if (i < refs.length - 1)
								buff.append(delimiter);
						}
						buff.append("\n");
					}

				}

				/*
				 * 
				 * REMOVED FIRST LINE IN THE CSV DOWNLOADABLE
				 * 
				 * String annots[] = table.getSNPGenomicAnnotation(fillGenotypeQueryParams());
				 * if (annots != null) { buff.append(
				 * "MSU7 EFFECTS (cds-Non-synonymous/cds-Synonymous/Cds/3'UTR/5'UTR/Exon/splice Acceptor/splice Donor/Gene-intron/Promoter)"
				 * ) .append(columnfillers); //
				 * .append(delimiter).append(delimiter).append(delimiter).append(delimiter).
				 * append(delimiter); for (int i = 0; i < annots.length; i++) {
				 * buff.append(annots[i]); if (i < annots.length - 1) buff.append(delimiter); }
				 * buff.append("\n"); }
				 *	
				 */

				String queryallele[] = table.getQueryallele();
				if (queryallele != null) {

					if (this.listboxVarietyAlleleFilter.getSelectedIndex() > 0) {
						buff.append("Query ");
						buff.append((String) listboxVarietyAlleleFilter.getSelectedItem().getValue());
					} else {
						buff.append("Query alleles");
					}
					buff.append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);
					for (int ia = 0; ia < queryallele.length; ia++) {
						buff.append(queryallele[ia]);
						if (ia + 1 < queryallele.length)
							buff.append(delimiter);
					}
					buff.append("\n");
				}

			}

			Object varalleles[][] = table.getVaralleles();
			AppContext.debug("mxn=" + varalleles.length + "x" + varalleles[0].length);
			AppContext.debug("positions = " + refs.length);
			AppContext.debug("varids = " + table.getVarname().length);

			Set ds = getDataset();
			Map<String, Map<BigDecimal, StockSample>> mapDs = varietyfacade.getMapId2Sample(ds);

			if (!isAppend) {

				for (int i = 0; i < table.getVarid().length; i++) {
					String varname = table.getVarname()[i];

					// if(delimiter.equals(",") && varname.contains(","))
					// varname = "\"" + varname + "\"";
					Map<BigDecimal, StockSample> mapVarId2Var = mapDs.get(table.getDataset()[i]);

					Variety var = mapVarId2Var.get(BigDecimal.valueOf(table.getVarid()[i]));
					String phenvalue = "";
					if (mapVarid2Phenotype != null) {
						phenvalue = delimiter;
						Object phenval = mapVarid2Phenotype.get(var.getVarietyId());
						if (phenval != null) {
							if (phenval instanceof String)
								phenvalue = (String) phenval + delimiter;
							else {
								phenvalue = String.format("%.2f", (Number) phenval);
								phenvalue = phenvalue.replace(".00", "");
								phenvalue += delimiter;
							}
						}
					}

					buff.append("\"").append(varname).append("\"").append(delimiter)
							.append((var.getIrisId() != null ? var.getIrisId() : "")).append(delimiter)
							.append((var.getAccession() != null ? var.getAccession() : "")).append(delimiter)
							.append((var.getSubpopulation() != null ? var.getSubpopulation() : "")).append(delimiter)
							.append(table.getVarmismatch()[i]).append(delimiter).append(phenvalue);
					for (int j = 0; j < refs.length; j++) {
						Object allele = varalleles[i][j];
						if (allele == null)
							buff.append("");
						else
							buff.append(varalleles[i][j]);
						if (j < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");
				}

				String filetype = "text/plain";
				if (delimiter.equals(","))
					filetype = "text/csv";
				Filedownload.save(buff.toString(), filetype, filename);
				// AppContext.debug("File download complete! Saved to: "+filename);
				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
				AppContext.debug("snpallvars download complete!" + filename + " Downloaded to:"
						+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());

			} else {
				BufferedWriter bw = null;
				if (isAppendFirst) {
					bw = new BufferedWriter(new FileWriter(filename));
					bw.append(buff);
					bw.flush();
				} else
					bw = new BufferedWriter(new FileWriter(filename, true));

				buff = new StringBuffer();
				for (int i = 0; i < table.getVarid().length; i++) {
					String varname = table.getVarname()[i];

					// if(delimiter.equals(",") && varname.contains(","))
					// varname = "\"" + varname + "\"";
					Map<BigDecimal, StockSample> mapVarId2Var = mapDs.get(table.getDataset()[i]);
					StockSample var = mapVarId2Var.get(BigDecimal.valueOf(table.getVarid()[i]));
					String phenvalue = "";
					if (mapVarid2Phenotype != null) {
						phenvalue = delimiter;
						Object phenval = mapVarid2Phenotype.get(var.getVarietyId());
						if (phenval != null) {
							if (phenval instanceof String)
								phenvalue = (String) phenval + delimiter;
							else {
								phenvalue = String.format("%.2f", (Number) phenval);
								phenvalue = phenvalue.replace(".00", "");
								phenvalue += delimiter;
							}
						}
					}

					buff.append("\"").append(varname).append("\"").append(delimiter)
							.append((var.getAssay() != null ? var.getAssay() : "")).append(delimiter)
							.append((var.getAccession() != null ? var.getAccession() : "")).append(delimiter)
							.append(var.getSubpopulation()).append(delimiter).append(table.getVarmismatch()[i])
							.append(delimiter).append(phenvalue);
					for (int j = 0; j < refs.length; j++) {
						Object allele = varalleles[i][j];
						if (allele == null)
							buff.append("");
						else
							buff.append(varalleles[i][j]);
						if (j < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");
					bw.append(buff);
					bw.flush();
					buff = new StringBuffer();
				}
				bw.close();

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private void download2VarsList(String filename, String delimiter) {
		AppContext.debug("downloading from 2vars table...");
		StringBuffer buff = new StringBuffer();

		// buff.append("CONTIG").append(delimiter).append("POSITION").append(delimiter).append("REFERENCE").append(delimiter);

		Iterator itHead = listboxSnpresult.getHeads().iterator();
		while (itHead.hasNext()) {
			Component comp = (Component) itHead.next();
			if (!comp.isVisible())
				continue;
			if (comp instanceof Listhead) {
				Listhead cols = (Listhead) comp;
				Iterator itHeadcol = cols.getChildren().iterator();
				while (itHeadcol.hasNext()) {
					Listheader header = (Listheader) itHeadcol.next();
					if (!header.isVisible())
						continue;
					buff.append(header.getLabel());
					if (itHeadcol.hasNext())
						buff.append(delimiter);
				}
			}
			if (comp instanceof Auxhead) {
				Auxhead cols = (Auxhead) comp;
				Iterator itHeadcol = cols.getChildren().iterator();
				while (itHeadcol.hasNext()) {
					Auxheader header = (Auxheader) itHeadcol.next();
					if (!header.isVisible())
						continue;
					buff.append(header.getLabel());
					if (itHeadcol.hasNext())
						buff.append(delimiter);
				}
			}
			if (comp instanceof Columns) {
				Columns cols = (Columns) comp;
				Iterator itCol = cols.getChildren().iterator();
				while (itCol.hasNext()) {
					Column col = (Column) itCol.next();
					if (!col.isVisible())
						continue;
					buff.append(col.getLabel());
					if (itCol.hasNext())
						buff.append(delimiter);
				}
			}
			buff.append("\n");
		}

		ListModel model = listboxSnpresult.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			Object[] row = (Object[]) model.getElementAt(i);

			for (int j = 0; j < row.length; j++) {

				// AppContext.debug(j + " " + row[j]);

				if (j == 0)
					continue;
				if (j == 1) {
					buff.append(((Position) row[j]).getContig());
					buff.append(delimiter);
					buff.append(((Position) row[j]).getPosition().toString().replaceAll(".00", ""));
					buff.append(delimiter);
					continue;
				}
				if (row[j] != null)
					buff.append(row[j].toString());
				else
					buff.append("");
				if (j < row.length - 1)
					buff.append(delimiter);
			}
			buff.append("\n");
		}

		try {
			String filetype = "text/plain";
			if (delimiter.equals(","))
				filetype = "text/csv";
			Filedownload.save(buff.toString(), filetype, filename);
			// AppContext.debug("File download complete! Saved to: "+filename);
			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			AppContext.debug("snp2vars downlaod complete!" + filename + " Downloaded to:" + zksession.getRemoteHost()
					+ "  " + zksession.getRemoteAddr());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void enableDownloadButtons(boolean enable) {
		this.buttonDownloadCsv.setDisabled(!enable);
		this.buttonDownloadTab.setDisabled(!enable);

		if (this.checkboxIndel.isChecked()) {
			// this.buttonDownloadPlink.setDisabled(false);
			// this.buttonDownloadFlapjack.setDisabled(false);
		} else {
			this.buttonDownloadPlink.setDisabled(!enable);
			this.buttonDownloadFlapjack.setDisabled(!enable);
		}
	}
	
	private void downloadBigListboxPlinkZip(String filename) {
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
		downloadBigListboxPlinkZip(table, filename);
	}
	
	private void downloadBigListboxPlinkZip(VariantAlignmentTableArraysImpl table, String filename) {

		generateBigListboxPlink((VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade"),
				fillGenotypeQueryParams(), selectChr.getValue(), getDataset(), table, filename);

		try {
			String allzipfilenames[] = new String[] { filename + ".map", filename + ".ped" };
			new CreateZipMultipleFiles(filename + "-plink-" + ".zip", allzipfilenames).create();
			Filedownload.save(new File(filename + "-plink-" + ".zip"), "application/zip");

			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			zksession = Sessions.getCurrent();
			AppContext.debug("snpallvars download complete!" + filename + ".ped Downloaded to:"
					+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


	@Listen("onClick = #buttonDownloadFlapjack")
	public void downloadFlapjack() {
		// downloadDelimited("snpfile.txt", "\t");
		if (biglistboxArray.isVisible())
			downloadBigListboxFlapjackZip(AppContext.getTempDir() + "snp3kvars-" + queryFilename());
		else if (this.divDownloadOnlyMsg.isVisible()) {
			if (this.radioWait.isSelected()) {
				if (varianttable == null)
					queryVariants();
				downloadBigListboxFlapjackZip((VariantAlignmentTableArraysImpl) this.varianttable,
						AppContext.getTempDir() + "snp3kvars-" + queryFilename());
				AppContext.resetTimer("to download");
			} else {
				try {
					Callable<AsyncJobReport> callreport = callableDownloadBigListbox(
							AppContext.getTempDir() + "snp3kvars-" + queryFilename(), null, "flapjack");
					AsyncJobReport report = callreport.call();
					if (report != null) {
						if (report.getMessage().equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
							this.labelDownloadProgressMsg.setValue("Job is submitted. Please monitor progress at ");
							this.aDownloadProgressURL.setLabel(report.getUrlProgress());
							this.aDownloadProgressURL.setHref(report.getUrlProgress());
							enableDownloadButtons(false);
						} else
							this.labelDownloadProgressMsg.setValue(report.getMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show(ex.getMessage());
				}
			}
		}

	}

	/**
	 * Download Tab format
	 */
	@Listen("onClick = #buttonDownloadTab")
	public void downloadTab() {
		if (biglistboxArray.isVisible())
			downloadBigListbox(AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".txt", "\t");
		else if (divPairwise.isVisible())
			download2VarsList(AppContext.getTempDir() + "snp2vars-" + queryFilename() + ".txt", "\t");
		else if (this.divDownloadOnlyMsg.isVisible()) {
			if (this.radioWait.isSelected()) {
				if (varianttable == null)
					queryVariants();
				downloadBigListbox((VariantAlignmentTableArraysImpl) this.varianttable,
						AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".txt", "\t");
				AppContext.resetTimer("to download");
			} else {
				try {
					Callable<AsyncJobReport> callreport = callableDownloadBigListbox(
							AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".txt", "\t", "tab");
					AsyncJobReport report = callreport.call();
					// AsyncJobReport report=null;
					if (report != null) {
						if (report.getMessage().equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
							this.labelDownloadProgressMsg.setValue("Job is submitted. Please monitor progress at ");
							this.aDownloadProgressURL.setLabel(report.getUrlProgress());
							this.aDownloadProgressURL.setHref(report.getUrlProgress());
							enableDownloadButtons(false);
						} else {
							this.labelDownloadProgressMsg.setValue(report.getMessage());
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show(ex.getMessage());
				}
			}
		}
	}

	@Listen("onClick = #buttonDownloadPlink")
	public void downloadPlink() {
		// downloadDelimited("snpfile.txt", "\t");
		if (biglistboxArray.isVisible())
			downloadBigListboxPlinkZip(AppContext.getTempDir() + "snp3kvars-" + queryFilename());
		else if (this.divDownloadOnlyMsg.isVisible()) {
			if (this.radioWait.isSelected()) {
				if (varianttable == null)
					queryVariants();
				downloadBigListboxPlinkZip((VariantAlignmentTableArraysImpl) this.varianttable,
						AppContext.getTempDir() + "snp3kvars-" + queryFilename());
				AppContext.resetTimer("to download");
			} else {
				try {
					// Callable<AsyncJobReport> callreport =
					// callableDownloadBigListbox(AppContext.getTempDir() + "snp3kvars-" +
					// queryFilename() + ".txt" ,"\t");
					Callable<AsyncJobReport> callreport = callableDownloadBigListbox(
							AppContext.getTempDir() + "snp3kvars-" + queryFilename(), null, "plink");
					AsyncJobReport report = callreport.call();
					// AsyncJobReport report=null;
					if (report != null) {
						if (report.getMessage().equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
							this.labelDownloadProgressMsg.setValue("Job is submitted. Please monitor progress at ");
							this.aDownloadProgressURL.setLabel(report.getUrlProgress());
							this.aDownloadProgressURL.setHref(report.getUrlProgress());
							enableDownloadButtons(false);
						} else
							this.labelDownloadProgressMsg.setValue(report.getMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show(ex.getMessage());
				}
			}
		}

	}

	/**
	 * Download CSV format
	 */
	@Listen("onClick = #buttonDownloadCsv")
	public void downloadCsv() {

		if (this.biglistboxArray.isVisible())
			downloadBigListbox(AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".csv", ",");
		else if (divPairwise.isVisible())
			download2VarsList(AppContext.getTempDir() + "snp2vars-" + queryFilename() + ".csv", ",");
		else if (this.divDownloadOnlyMsg.isVisible()) {
			AppContext.debug(radioWait.isSelected() + " " + radioWait.isChecked() + " " + this.radioAsync.isSelected()
					+ " " + radioAsync.isChecked());
			if (this.radioWait.isSelected() || radioWait.isChecked()) {
				if (varianttable == null)
					queryVariants();
				downloadBigListbox((VariantAlignmentTableArraysImpl) this.varianttable,
						AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".csv", ",");
				AppContext.resetTimer("to download");
			} else {
				try {
					Callable<AsyncJobReport> callreport = callableDownloadBigListbox(
							AppContext.getTempDir() + "snp3kvars-" + queryFilename() + ".csv", ",", "csv");
					AsyncJobReport report = callreport.call();
					// AsyncJobReport report=null;

					if (report != null) {
						if (report.getMessage().equals(JobsFacade.JOBSTATUS_SUBMITTED)) {
							this.labelDownloadProgressMsg.setValue("Job is submitted. Please monitor progress at ");
							this.aDownloadProgressURL.setLabel(report.getUrlProgress());
							this.aDownloadProgressURL.setHref(report.getUrlProgress());
							enableDownloadButtons(false);
						} else
							this.labelDownloadProgressMsg.setValue(report.getMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Messagebox.show("downloadCsv():" + ex.getMessage());
				}
			}
		}

	}

	// END CHART

}