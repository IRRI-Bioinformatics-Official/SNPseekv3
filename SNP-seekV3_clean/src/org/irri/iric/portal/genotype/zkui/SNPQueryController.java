package org.irri.iric.portal.genotype.zkui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
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
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import oracle.sql.DATE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.ds.chado.domain.Gene;
import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.ds.chado.domain.IndelsAllvars;
import org.irri.iric.ds.chado.domain.IndelsAllvarsPos;
import org.irri.iric.ds.chado.domain.IndelsStringAllvars;
import org.irri.iric.ds.chado.domain.LocalAlignment;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MultiReferenceConversion;
import org.irri.iric.ds.chado.domain.Organism;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.SnpsStringAllvars;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.impl.LocalAlignmentImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferenceLocusImpl;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.irri.iric.portal.SimpleListModelExt;
import org.irri.iric.portal.WebserverPropertyConstants;
import org.irri.iric.portal.admin.AsyncJob;
import org.irri.iric.portal.admin.AsyncJobImpl;
import org.irri.iric.portal.admin.AsyncJobReport;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genomics.zkui.BlastResultListitemRenderer;
//import org.irri.iric.portal.genotype.domain.Gene;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.PhylotreeQueryParams;
import org.irri.iric.portal.genotype.PhylotreeService;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantStringService;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.genotype.service.IndelStringHDF5nRDBMSHybridService;
//import org.irri.iric.portal.genotype.service.IndelStringService;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
/*
import org.irri.iric.portal.genotype.service.GenotypeFacadeImpl;
import org.irri.iric.portal.genotype.views.Snp2lines;
import org.irri.iric.portal.genotype.views.Snp2linesHome;
import org.irri.iric.portal.genotype.views.Snp2linesId;
import org.irri.iric.portal.genotype.views.ViewCountVarrefMismatchId;
import org.irri.iric.portal.genotype.views.ViewSnpAllvars;
import org.irri.iric.portal.genotype.views.ViewSnpAllvarsId;
import org.irri.iric.portal.genotype.views.ViewSnpAllvarsPos;
import org.irri.iric.portal.genotype.views.ViewSnpAllvarsPosId;
*/
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.variety.service.Data;
import org.irri.iric.portal.variety.zkui.VarietyQueryController;
import org.irri.iric.portal.zk.ListboxMessageBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.chart.AxisLabels;
import org.zkoss.chart.Charts;
import org.zkoss.chart.ChartsEvent;
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
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
//import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Biglistbox;
//import org.zkoss.addon.Biglistbox;
//import org.zkoss.addon.MatrixComparatorProvider;
//import org.zkoss.addon.MatrixRenderer;
import org.zkoss.zkmax.zul.MatrixComparatorProvider;
import org.zkoss.zkmax.zul.MatrixModel;
import org.zkoss.zkmax.zul.event.CellClickEvent;
import org.zkoss.zkmax.zul.event.ScrollEventExt;
import org.zkoss.zkmax.zul.event.SortEventExt;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
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
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Splitter;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@Controller
@Scope(value = "session")
public class SNPQueryController extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(SNPQueryController.class);

	@Wire
	private Window win;

	// url address of tab frames
	private String urljbrowse, gfffile, urlphylo, urljbrowsephylo;

	// distance matrix from phylo and mds
	private List varpairDistance;

	private String vistaurl1;
	private String vistaurl2;
	private String vistaurl3;
	private String vistaurl4;
	private String vistaurlrev;

	private boolean DEFAULT_MISMATCHONLY = false;

	private int nAllelefreqLabels = 25; // 25;

	// bottom visible row in variant table display
	private int lastY;

	// number of frozen columns in variant table display
	int frozenCols = AppContext.getSnpMatrixFrozenCols(); // 4; // with dataset

	// display tall (for all varieties) or short (for few varieties) jbrowse frame
	private boolean tallJbrowse;

	// list of contigs for selected reference genomes
	private List listContigs;

	// list of gene loci for selected reference genomes
	private List listLoci;

	final private String classGenotypefacade = "GenotypeFacade";

	private Object2StringMultirefsMatrixModel biglistboxModel = null;
	private MatrixComparatorProvider matriccmpproviderAsc;
	private MatrixComparatorProvider matriccmpproviderDesc;

	// data for allele and genotype frequence chart
	private AlleleFreqLineData[] varFreqlines = null;
	private AlleleFreqLineData[] groupFreqlines = null;

	// holds SNP query result
	private VariantStringData queryRawResult;

	// holds SNP result for display
	private VariantStringData queryResult;

	// holds SNP result for variant table display
	private VariantTable varianttable;

	// number of visible rows in variant table display
	private int biglistboxRows = 16;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade varietyfacade;

	@Autowired
	@Qualifier("GenomicsFacade")
	private GenomicsFacade genomicsfacade;

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade_orig;
	@Autowired
	@Qualifier("GalaxyJobsFacade")
	private JobsFacade jobsfacade_galaxy;

	// attributes for SNP Query controller components
	@Wire
	private Listheader listHeaderVar2Reference;

	@Wire
	private Button buttonResetzoom;
	@Wire
	private Button buttonRefresh;

	@Wire
	private A aDownloadProgressURL;

	@Wire
	private Checkbox checkboxHeteroindels;
	@Wire
	private Div divHeteroindels;

	@Wire
	private Button buttonDownloadPairwiseTab;
	@Wire
	private Button buttonDownloadPairwiseCsv;
	@Wire
	private Div divPairwise;

	@Wire
	private Listbox listboxPhenotype;

	@Wire
	private Listbox listboxAlleleFilter;

	@Wire
	private Listbox listboxVarietyAlleleFilter;

	@Wire
	private Button buttonDownloadFasta;

	@Wire
	private Label labelDownloadOnlyMsg;
	@Wire
	private Vbox divDownloadOnlyMsg;
	@Wire
	private Radio radioAsync;
	@Wire
	private Radio radioWait;
	@Wire
	private Label labelDownloadProgressMsg;

	@Wire
	private Div divTablePanel;
	@Wire
	private Charts chartAlleleFrequency;
	@Wire
	private Charts chartGroupAlleleFrequency;
	@Wire
	private Charts chartGroupPhenotypeBox;

	@Wire
	private Hbox hboxAlleleFrequency;
	@Wire
	private Vbox vboxGroupAlleleFrequency;

	@Wire
	private Radio radioShowGenotypeCount;
	@Wire
	private Radio radioShowGenotypeFrequency;

	@Wire
	private Radio radioShowAlleleFrequency;
	@Wire
	private Radio radioShowAlleleCount;
	@Wire
	private Radio radioMajorAlleles;
	@Wire
	private Radio radioMinorAlleles;
	@Wire
	private Radio radio3rdAlleles;
	@Wire
	private Radio radio4thAlleles;

	@Wire
	private Radio radioLegacyTrait;
	@Wire
	private Radio radioCoTrait;

	@Wire
	private Radio radioGroupShowGenotypeCount;
	@Wire
	private Radio radioGroupShowGenotypeFrequency;

	@Wire
	private Radio radioGroupShowAlleleFrequency;
	@Wire
	private Radio radioGroupShowAlleleCount;
	@Wire
	private Radio radioGroupMajorAlleles;
	@Wire
	private Radio radioGroupMinorAlleles;
	@Wire
	private Radio radioGroup3rdAlleles;
	@Wire
	private Radio radioGroup4thAlleles;

	@Wire
	private Button buttonSNPSorter;
	@Wire
	private Listbox listboxSNPListAlleles;

	@Wire
	private Tab tabVistaNPB;
	@Wire
	private Iframe iframeNPBvsIR6421;
	@Wire
	private Iframe iframeNPBvs9311;
	@Wire
	private Iframe iframeNPBvsDJ123;
	@Wire
	private Iframe iframeNPBvsKasalath;
	@Wire
	private Tab tabNPBvsIR6421;
	@Wire
	private Tab tabNPBvs9311;
	@Wire
	private Tab tabNPBvsDJ123;
	@Wire
	private Tab tabNPBvsKasalath;

	@Wire
	private Label labelChrExample;
	@Wire
	private Label labelChr;

	@Wire
	private Auxheader auxheader3Header;
	@Wire
	private Column column4Header;

	@Wire
	private Auxhead auxheadAlleles1;
	@Wire
	private Auxheader auxheaderAlleles1;
	@Wire
	private Auxhead auxheadAlleles2;
	@Wire
	private Auxheader auxheaderAlleles2;
	@Wire
	private Auxhead auxheadAlleles3;
	@Wire
	private Auxheader auxheaderAlleles3;
	@Wire
	private Auxhead auxheadAlleles4;
	@Wire
	private Auxheader auxheaderAlleles4;
	@Wire
	private Auxheader auxheader3Phenotype;
	@Wire
	private Auxheader auxheaderBiglistheaderMismatch;

	@Wire
	private Auxhead auxheadMultirefPairwise;
	@Wire
	private Auxheader auxheaderMultirefPairwise;
	@Wire
	private Listheader listheader1MultirefPairwise;
	@Wire
	private Listheader listheader2MultirefPairwise;
	@Wire
	private Listheader listheader3MultirefPairwise;

	@Wire
	private Listheader listheaderVar1MultirefPairwise;
	@Wire
	private Listheader listheaderVar2MultirefPairwise;

	@Wire
	private Listbox listboxReference;

	@Wire
	private Checkbox checkboxShowAllRefAlleles;

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

	@Wire
	private Tab tabVistaRev;
	@Wire
	private Tabpanel tabpanelVistaRev;
	@Wire
	private Iframe iframeVistaRev;

	@Wire
	private Button buttonDownloadNewick;

	@Wire
	private Label labelLocusExample;
	@Wire
	private Hbox divAlignIndels;
	@Wire
	private Div divIndelLegend;
	@Wire
	private Biglistbox biglistboxArray;
	@Wire
	private Biglistbox biglistboxArrayLarge;
	@Wire
	private Grid gridBiglistheader;

	@Wire
	private Radiogroup radiogroupShowsnps;

	@Wire
	private Radio radioAllSnps;
	@Wire
	private Radio radioNonsynSnps;
	@Wire
	private Radio radioNonsynHighlights;

	@Wire
	private Radio radioNonsynSnpsPlusSplice;

	@Wire
	private Div tip;
	@Wire
	private Textbox content;
	@Wire
	private Tabpanel tabpanelTable;

	@Wire
	private Label labelFilterResult;

	@Wire
	private Checkbox checkboxSNP;
	@Wire
	private Checkbox checkboxIndel;

	@Wire
	private Vbox buttonsDownloadNodisplay;

	@Wire
	private Button buttonReset;
	@Wire
	private Intbox intStart;
	@Wire
	private Intbox intStop;
	@Wire
	private Combobox comboGene;
	@Wire
	private Combobox selectChr;
	@Wire
	private Listbox listboxSnpresult;
	@Wire
	private Textbox msgbox;
	@Wire
	private Button buttonSearch;
	@Wire
	private Label labelLength;
	@Wire
	private Combobox comboVar1;
	@Wire
	private Combobox comboVar2;
	@Wire
	private Checkbox checkboxMismatchOnly;

	@Wire
	private Checkbox checkboxAllvarieties;
	@Wire
	private Listbox listboxSubpopulation;

	@Wire
	private Listbox listboxMyVarieties;

	@Wire
	private Div divNucleotide;
	@Wire
	private Div divNucleotideIndels;

	@Wire
	private Div divMismatch;

	@Wire
	private Iframe iframeAlignment;
	@Wire
	private Iframe iframeJbrowse;
	@Wire
	private Label msgJbrowse;
	@Wire
	private Iframe iframeJbrowsePhylo;
	@Wire
	private Label msgJbrowsePhylo;

	@Wire
	private Button buttonDownloadCsv;
	@Wire
	private Button buttonDownloadTab;
	@Wire
	private Button buttonDownloadPlink;
	@Wire
	private Button buttonDownloadFlapjack;
	@Wire
	private Auxhead auxheadpos;
	@Wire
	private Radio radioColorAllele;
	@Wire
	private Radio radioColorMismatch;

	@Wire
	private Iframe iframePhylotree;
	@Wire
	private Tab tabJbrowse;
	@Wire
	private Tab tabGalaxy;
	@Wire
	private Tab tabMSA;
	@Wire
	private Tab tabPhylo;
	@Wire
	private Listbox selectPhyloTopN;

	@Wire
	private Listbox selectPhyloMindist;

	@Wire
	private Listbox listboxMyLocusList;

	@Wire
	private Intbox intPhyloMindist;

	@Wire
	private Tab tabJBrowsePhylo;
	@Wire
	private Tab tabTable;
	@Wire
	private Tab tabMDS;
	@Wire
	private Tab tabTableLarge;
	@Wire
	private Tabbox tabboxDisplay;

	@Wire
	private Checkbox checkboxExactMismatch;
	@Wire
	private Label labelSpliceSNP;

	@Wire
	private Listbox selectAllele;
	@Wire
	private Listbox listboxPosition;
	@Wire
	private Listbox listboxAllele;

	@Wire
	private Button buttonFilterAllele;
	@Wire
	private Button buttonClearFilterAllele;
	@Wire
	private Hbox hboxFilterAllele;
	@Wire
	private Hbox hboxDownload;
	@Wire
	private Vbox vboxFilterAllele;
	@Wire
	private Listbox listboxMySNPList;
	@Wire
	private Button buttonCreateVarlist;
	@Wire
	private Textbox textboxVarlistName;
	@Wire
	private Radio radioGraySynonymous;
	@Wire
	private Radio radioNonsynOnly;

	@Wire
	private Button buttonRenderTree;

	@Wire
	private Button buttonDownloadNoDisplayCSV;
	@Wire
	private Button buttonDownloadNoDisplayTab;

	@Wire
	private Button buttonDownloadNoDisplayPlink;
	@Wire
	private Button buttonDownloadNoDisplayHapmap;

	@Wire
	private Checkbox checkboxAlignIndels;

	@Wire
	private Div divShowNPBPositions;
	@Wire
	private Checkbox checkboxShowNPBPosition;

	@Wire
	private Auxhead auxhead1Header;
	@Wire
	private Auxheader auxheader11Header;
	@Wire
	private Auxhead auxhead2Header;
	@Wire
	private Auxheader auxheader21Header;
	@Wire
	private Auxhead auxheadAlleleFilter;
	@Wire
	private Auxheader auxheaderAlleleFilter;

	@Wire
	private Listbox listboxMissingAllele;

	@Wire
	private Hbox hboxColorcodeSNPQuery;
	@Wire
	private Hbox hboxColorcodeGenotypeMatch;

	@Wire
	private Label labelExampleVariety;

	@Wire
	private Listbox listboxDatasetSnpOps;

	@Wire
	private Hbox hboxPolluxUrl;

	@Wire
	private Listbox listboxSnpeff;
	@Wire
	private Tab tabSnpeff;

	@Wire
	private Radio radioUseAccession;
	@Wire
	private Radio radioUseVarname;

	private Long phyloid = null;
	private Map mapVariety2Phyloorder;

	@Wire
	private Label labelMDSPhenotype;
	@Wire
	private Listbox listboxHighlightVariety;
	@Wire
	private Listbox listboxHighlightVarietyList;
	@Wire
	private Charts chartMDS;

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

	@Wire
	private Iframe iframeHaplotypeTree;
	@Wire
	private Listbox listboxGroupAlleleMatrix;
	@Wire
	private Listhead listheadGroupAlleleMatrix;
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
	private Auxheader auxheaderIrisid;

	private Map mapVars2PropSnpstr[];

	private Set<String> prevDataset = new HashSet<String>();

	@Wire
	private Listbox listboxVarietyset;
	@Wire
	private Bandbox bandboxVarietyset;
	@Wire
	private Listbox listboxVariantset;
	@Wire
	private Bandbox bandboxVariantset;
	@Wire
	private Listbox listboxRunid;
	@Wire
	private Bandbox bandboxRunid;
	@Wire
	private Div divRunid;

	private Map<String, Radio> mapVariantset2Varietyset = new HashMap();
	private Map<String, Radio> mapVarietyset2Runid = new HashMap();

	@Wire
	private Checkbox checkboxAdvanceOptions;
	@Wire
	private Hbox advanceOptions1;
	@Wire
	private Hbox advanceOptions2;
	@Wire
	private Hbox advanceOptions3;
	@Wire
	private Hbox advanceOptions4;

	@Wire
	private Listbox listboxAlignment;
	@Wire
	private Listbox listboxAlignRef;
	@Wire
	private Listbox listboxAlignTarget;
	@Wire
	private Listbox listboxAlignContig;
	@Wire
	private Button buttonAlign;
	@Wire
	private Intbox intboxAlignStart;
	@Wire
	private Intbox intboxAlignEnd;
	@Wire
	private Listbox listboxTargetReference;

	@Wire
	private Splitter splitter;

	@Wire
	private Include includeGalaxy;

	Map<BigDecimal, Object> mapVarid2Phenotype;

	private String sPhenotype;

	GenotypeQueryParams params;

	private boolean refreshgalaxy;

	public SNPQueryController() {
		super();
		AppContext.debug("created SNPQueryController " + this);

	}

	@Override
	public void doAfterCompose(Window comp) throws Exception {

		super.doAfterCompose(comp);

		log.info("Search Genotype Page Load: ");
		try {

			win.getPage().addEventListener(Events.ON_DESKTOP_RECYCLE, new EventListener() {
				@Override
				public void onEvent(Event event) throws Exception {
					// TODO Auto-generated method stub
					AppContext.debug("win.getPage Events.ON_DESKTOP_RECYCLE,");
					refresh();
				}
			});

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");

			updateLists(true);

			comboVar1.setModel(getVarAccessionsModel());
			comboVar2.setModel(getVarAccessionsModel());

			listboxSubpopulation.setModel(getSubPopulationModel());
			listboxSubpopulation.setSelectedIndex(1);

			comboGene.setModel(populateComboGene());
			listboxVarietyAlleleFilter.setModel(getVarAccessionsModel());

//			List<String> listReference = genotype.getReferenceGenomes();
			List<String> listReference = new ArrayList<>();
			listboxReference.setModel(new SimpleListModel<String>(listReference));
			if (listReference.size() > 0)
				listboxReference.setSelectedIndex(0);

			setChrCombo();

			// Set setVarietyset=genotype.gets
			SimpleListModel<String> listmodel2 = new SimpleListModel<String>(genotype.getVarietysets());
			listmodel2.setMultiple(true);
			listboxVarietyset.setModel(listmodel2);
			listboxVarietyset.setSelectedIndex(0);

			setVarietyset();
			setVariantset();

			listboxPhenotype.setModel(getPhenotypeListModel());

			gridBiglistheader.setModel(new SimpleListModel<String>(new ArrayList<String>()));
			biglistboxArray.setModel(new FakerMatrixModel(0, 0));

			listboxHighlightVariety.setModel(new SimpleListModel<String>(AppContext.createUniqueUpperLowerStrings(
					genotype.getVarnames(VarietyFacade.DATASET_SNPINDELV2_IUPAC), false, true)));
			listboxHighlightVariety.setSelectedIndex(0);

			listboxSnpresult.getPagingChild().setMold("os");
			listboxSnpresult.setPagingPosition("both");

			buttonDownloadFasta.setVisible(AppContext.isLocalhost());

			// initFrom
			initFrom();

			prevDataset.add("3k");
			fillVariantsetListbox();

			// Initialize tab events for tabs
			tabVista.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					getSession().putValue("reference", listboxReference.getSelectedItem().getLabel());
					getSession().putValue("contig", selectChr.getValue());
					getSession().putValue("start", intStart.getValue());
					getSession().putValue("end", intStop.getValue());
					openNewTab("_exttab.zul?tab=vista");
				}
			});

			tabHaplotype.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					getSession().putValue("dataset", getDataset());
					getSession().putValue("contig", selectChr.getValue());
					getSession().putValue("queryparams", fillGenotypeQueryParams());
					Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray
							.getModel();
					getSession().putValue("table", (VariantAlignmentTableArraysImpl) matrixmodel.getData());
					getSession().putValue("queryfilename", queryFilename());

					openNewTab("_exttab.zul?tab=haplotype");
				}
			});

			tabMDS.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					if (queryResult == null)
						return;
					getSession().putValue("queryresult", queryResult);
					getSession().putValue("dataset", getDataset());
					openNewTab("_exttab.zul?tab=mds");
				}
			});

			tabSnpeff.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					if (queryResult == null)
						return;
					getSession().putValue("snpeff", queryResult);
					openNewTab("_exttab.zul?tab=snpeff");
				}
			});

			tabJbrowse.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					onselectTabJbrowse(true);
				}
			});

			tabHaplotype.addEventListener(org.zkoss.zk.ui.event.Events.ON_RIGHT_CLICK, new EventListener() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					try {
						onselectTabHaplotype(true);
					} catch (Exception ex) {
						AppContext.debug(ex.getMessage());
					}
				}
			});

			log.info("Genotype zulpage end.");

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
			throw ex;
		}

	}

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

	private void setVariantset() {
		Set<String> selVS = new HashSet<String>();
		selVS.add(AppContext.getDefaultVariantset()); // "3kfiltered");
		setVariantset(selVS);
	}

	private void setVarietyset() {
		Set<String> selDS = new HashSet<String>();
		selDS.add(AppContext.getDefaultDataset());
		setVarietyset(selDS);
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
					listboxAlleleFilter.setSelectedIndex(listboxAlleleFilter.getItemCount() - 2);
					listboxSubpopulation.setSelectedIndex(1);
					checkboxAllvarieties.setChecked(false);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	private ListModel<String> getPhenotypeListModel() {
		Set<String> setPhenotype = new HashSet<String>();
		setPhenotype = varietyfacade.getPhenotypeDefinitions(AppContext.getDefaultDataset()).keySet();
		TreeSet<String> ts = new TreeSet<String>();
		for (String phen : setPhenotype) {
			ts.add(phen.toLowerCase());
		}

		java.util.List<String> listPhenotype = new java.util.ArrayList<String>();
		listPhenotype.add("");
		listPhenotype.add("Create phenotype list...");
		listPhenotype.addAll(workspace.getVarietyQuantPhenotypelistNames(varietyfacade.DATASET_DEFAULT));
		listPhenotype.addAll(workspace.getVarietyCatPhenotypelistNames("3k"));
		listPhenotype.addAll(ts);

		return new SimpleListModel<String>(listPhenotype);
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

	private ListModel<String> getVarAccessionsModel() {
		List<String> varaccessions1 = genotype.getVaraccessions(VarietyFacade.DATASET_SNPINDELV2_IUPAC);
		java.util.List<String> varaccessions = AppContext.createUniqueUpperLowerStrings(varaccessions1, true, false);

		ListModel<String> varAccessionModel = new SimpleListModel<String>(varaccessions);

		return varAccessionModel;
	}

	private ListModel<String> populateComboGene() {
		List<String> genenames = genotype.getGenenames();
		ListModel<String> geneComboModel = new SimpleListModelExt(genenames);

		return geneComboModel;
	}

	@Listen("onClick =#buttonRefresh")
	public void onclickRefresh() {
		AppContext.debug("refreshing listboxes");
		refresh();
	}

	public void refresh() {
		AppContext.debug("refreshing listboxes by controller");

		try {
			if (queryResult != null) {
				Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(queryResult,
						params);
				biglistboxArray.setMatrixRenderer(renderer);
				biglistboxModel = new Object2StringMultirefsMatrixModel(varianttable,
						params /* fillGenotypeQueryParams() */, varietyfacade.getMapId2Sample(params.getDataset()),
						gridBiglistheader, mapVarid2Phenotype, sPhenotype);
				biglistboxModel.setHeaderRows(biglistboxRows, lastY);
				biglistboxArray.setModel(biglistboxModel);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			updateLists();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onSelect =#tabGalaxy")
	public void onselectTabgalaxy() {

		GenotypeQueryParams p = fillGenotypeQueryParams();
		String filename = "snp3kvars-" + queryFilename();
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
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

	}

	public HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}

	private static int getRandomNumberInRange(int min, int max) {

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	@Listen("onClick =#buttonAlign")
	public void onclickAlign() {

		listboxAlignment.setItemRenderer(new BlastResultListitemRenderer());

		List locusalignmentresult = new ArrayList();
		String qacc = listboxAlignContig.getSelectedItem().getLabel();
		String target = listboxAlignTarget.getSelectedItem().getLabel();

		int lenq = intboxAlignEnd.getValue() - intboxAlignStart.getValue();
		int sqtart = intboxAlignStart.getValue() + getRandomNumberInRange(0, 100000);

		AppContext.debug("qacc=" + qacc);
		locusalignmentresult.add(new LocalAlignmentImpl(qacc, "chr01|" + target.toLowerCase(), 0.0,
				intboxAlignStart.getValue() + getRandomNumberInRange(0, 10),
				intboxAlignEnd.getValue() - getRandomNumberInRange(0, 10), sqtart,
				sqtart + lenq + getRandomNumberInRange(0, 100)));
		locusalignmentresult.add(new LocalAlignmentImpl(qacc, "chr01|" + target.toLowerCase(), 0.0,
				intboxAlignStart.getValue() + getRandomNumberInRange(0, 10),
				intboxAlignEnd.getValue() - getRandomNumberInRange(0, 10), sqtart,
				sqtart + lenq + getRandomNumberInRange(0, 100)));
		locusalignmentresult.add(new LocalAlignmentImpl(qacc, "chr01|" + target.toLowerCase(), 0.0,
				intboxAlignStart.getValue() + getRandomNumberInRange(0, 10),
				intboxAlignEnd.getValue() - getRandomNumberInRange(0, 10), sqtart,
				sqtart + lenq + getRandomNumberInRange(0, 100)));
		locusalignmentresult.add(new LocalAlignmentImpl(qacc, "chr02|" + target.toLowerCase(), 0.0,
				intboxAlignStart.getValue() + getRandomNumberInRange(0, 10),
				intboxAlignEnd.getValue() - getRandomNumberInRange(0, 10), sqtart,
				sqtart + lenq + getRandomNumberInRange(0, 100)));

		listboxAlignment.setModel(new SimpleListModel(locusalignmentresult));

	}

	private static Listitem getListItem(Listbox lb, Object value) {
		for (Listitem li : lb.getItems()) {
			if (value instanceof String) {
				if (li.getValue().toString().toLowerCase().equals(((String) value).toLowerCase()))
					return li;
			} else if (li.getValue().equals(value))
				return li;
		}
		return null;
	}

	@Listen("onSelect =#listboxAlignment")
	public void onselectAlign(Event e) throws InterruptedException {
		AppContext.debug("onSelect =#listboxAlignment");
		LocalAlignment item = (LocalAlignment) listboxAlignment.getSelectedItem();
		String contorg[] = item.getSacc().split("\\|");
		intStart.setValue(item.getSstart().intValue());
		intStop.setValue(item.getSend().intValue());
		Listitem li = getListItem(listboxTargetReference, contorg[1]);
		if (li != null)
			listboxTargetReference.setSelectedItem(li);
		// li= getListItem(listboxTargetReference,contorg[0]);
		selectChr.setValue(contorg[0]);

	}

	@Listen("onClick =#checkboxAdvanceOptions")
	public void showAdvanceOptions() {
		boolean show = checkboxAdvanceOptions.isChecked();
		advanceOptions1.setVisible(show);
		// advanceOptions2.setVisible(show);
		advanceOptions3.setVisible(show);
		advanceOptions4.setVisible(show);
	}

	@Listen("onOpen =#listLocuslistNames")
	public void onopenlocuslist() {
		AppContext.debug("listLocuslistNames open");
	}

	@Listen("onFocus =#listLocuslistNames")
	public void onfocuslocuslist() {
		AppContext.debug("listLocuslistNames onFocus");
	}

	void updateLists() {
		updateLists(false);
	}

	void updateLists(boolean create) {
		AppContext.debug("workspace=" + workspace);

		AppContext.debug("listboxMyLocusList.getEventHandlerNames()=" + listboxMyLocusList.getEventHandlerNames());
		AppContext.debug("listboxMyLocusList.getEventListenerMap()=" + listboxMyLocusList.getEventListenerMap());
		// listboxMyLocusList.getEventListenerMap().

		List listVarlistNames = new ArrayList();
		listVarlistNames.add("");
		listVarlistNames.addAll(workspace.getVarietylistNames());
		listVarlistNames.add("create new list...");

		AppContext.debug("listVarlistNames=" + listVarlistNames.size());

		java.util.List listSNPlistNames = new java.util.ArrayList(); // fac.getSNPinVarieties(100);
		listSNPlistNames.add("");
		listSNPlistNames.addAll(workspace.getSnpPositionListNames());
		listSNPlistNames.add("create new list...");
		AppContext.debug("listSNPlistNames=" + listSNPlistNames.size());

		java.util.List listSNPlistAlleleNames = new java.util.ArrayList(); // fac.getSNPinVarieties(100);
		listSNPlistAlleleNames.add("");
		listSNPlistAlleleNames.addAll(workspace.getSnpPositionAlleleListNames());
		listSNPlistAlleleNames.add("create new list...");

		AppContext.debug("listSNPlistAlleleNames=" + listSNPlistAlleleNames.size());

		java.util.List listLocuslistNames = new java.util.ArrayList(); // fac.getSNPinVarieties(100);

		listLocuslistNames.add("");
		listLocuslistNames.addAll(workspace.getLocuslistNames());
		listLocuslistNames.add("create new list...");
		AppContext.debug("listLocuslistNames=" + listLocuslistNames.size());

		if (create) {
			listboxMySNPList.setModel(new ListModelList(listSNPlistNames));
			listboxMyLocusList.setModel(new ListModelList(listLocuslistNames));
			listboxAlleleFilter.setModel(new ListModelList(listSNPlistAlleleNames));
			listboxSNPListAlleles.setModel(new ListModelList(listSNPlistAlleleNames));
			listboxHighlightVarietyList.setModel(new ListModelList(listVarlistNames));
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
			if (listSNPlistAlleleNames.size() != listboxSNPListAlleles.getRows()) {
				((ListModelList) listboxSNPListAlleles.getModel()).clear();
				((ListModelList) listboxSNPListAlleles.getModel()).addAll(listSNPlistAlleleNames);
			}
			if (listVarlistNames.size() != listboxHighlightVarietyList.getRows()) {
				((ListModelList) listboxHighlightVarietyList.getModel()).clear();
				((ListModelList) listboxHighlightVarietyList.getModel()).addAll(listVarlistNames);
			}
			if (listVarlistNames.size() != listboxMyVarieties.getRows()) {
				((ListModelList) listboxMyVarieties.getModel()).clear();
				((ListModelList) listboxMyVarieties.getModel()).addAll(listVarlistNames);
			}
		}

		listboxMyLocusList.setSelectedIndex(0);
		listboxMySNPList.setSelectedIndex(0);
		listboxAlleleFilter.setSelectedIndex(0);
		listboxSNPListAlleles.setSelectedIndex(0);
		listboxHighlightVarietyList.setSelectedIndex(0);
		listboxMyVarieties.setSelectedIndex(0);

	}

	@Listen("onSelect = #listboxVarietyset")
	public void onSelectcheckboxdroplistGenotyperun(Event e) throws InterruptedException {

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

	@Listen("onClick =#radioLegacyTrait")
	public void onclickLegacy() {
		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.add("");
		listPhenotype.add("Create phenotype list...");
		listPhenotype.addAll(workspace.getVarietyQuantPhenotypelistNames("3k"));
		listPhenotype.addAll(workspace.getVarietyCatPhenotypelistNames("3k"));

		if (radioLegacyTrait.isSelected()) {
			Set<String> setPhenotype = new HashSet();

			setPhenotype = varietyfacade
					.getTraits(AppContext.getStringValues(listboxVarietyset.getSelectedItems()), true).keySet();
			TreeSet<String> ts = new TreeSet<String>();
			for (String phen : setPhenotype) {
				ts.add(phen.toLowerCase());
			}
			listPhenotype.addAll(ts);

			listboxPhenotype.setModel(new SimpleListModel(listPhenotype));
		}
		listboxPhenotype.setSelectedIndex(0);
	}

	@Listen("onClick =#radioCoTrait")
	public void onclickCOTerm() {
		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.add("");
		listPhenotype.add("Create phenotype list...");
		listPhenotype.addAll(workspace.getVarietyQuantPhenotypelistNames("3k"));
		listPhenotype.addAll(workspace.getVarietyCatPhenotypelistNames("3k"));
		if (radioCoTrait.isSelected()) {
			Set<String> setPhenotype = new HashSet();
			setPhenotype = varietyfacade
					.getTraits(AppContext.getStringValues(listboxVarietyset.getSelectedItems()), false).keySet();
			// TreeSet<String> ts = new TreeSet<String>();
			listPhenotype.addAll(setPhenotype);

			listboxPhenotype.setModel(new SimpleListModel(listPhenotype));

		}

		listboxPhenotype.setSelectedIndex(0);
	}

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

	@Listen("onOpen = #bandboxVarietyset")
	public void onOpencheckboxdroplistGenotyperun(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen() + " text=" + bandboxVarietyset.getText() + "  value="
				+ bandboxVarietyset.getValue());
		if (e.isOpen()) {
			setVarietyset(getDataset());
			return;
		}
		fillVariantsetListbox();

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

	private void setVarietyset(Set s) {
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

	private void setRunids(Set s) throws Exception {
		String str = "";
		for (Object li : s) {
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.toString();
		}
		AppContext.debug("s=" + s + " str=" + str);
		bandboxRunid.setValue(str);
		Set setsel = new HashSet();
		for (Listitem li : listboxRunid.getItems()) {
			if (s.contains(Integer.valueOf(li.getLabel().trim().split(" ", 2)[0].trim()))) {
				setsel.add(li);
			}
		}
		listboxRunid.setSelectedItems(setsel);
	}

	@Listen("onSelect = #listboxVariantset")
	public void onSelectlistboxvariantset(Event e) throws InterruptedException {

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

	@Listen("onOpen = #bandboxVariantset")
	public void onOpenvariantset(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen());

		if (e.isOpen()) {

			setVariantset(getVariantset());
			return;
		}

		try {

			// String[] vs=bandboxVariantset.getText().split(",");
			Set setvs = new HashSet();
			// for(int i=0; i<vs.length; i++) setvs.add(vs[i].trim());

			String strval = "";
			Component bandpopup = bandboxVariantset.getChildren().get(0).getChildren().get(0);
			for (Component radios : bandpopup.getChildren()) {
				if (radios instanceof Radio) {
					Radio r = (Radio) radios;
					if (r.isSelected()) {
						setvs.add(r.getLabel());
						if (strval.isEmpty())
							strval = r.getLabel();
						else
							strval += ", " + r.getLabel();
					}
				}
			}
			bandboxVariantset.setValue(strval);

			update_runslist();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

	@Listen("onSelect = #listboxRunid")
	public void onSelectlistboxrunid(Event e) throws InterruptedException {

		Set srid = new LinkedHashSet();
		String rid[] = bandboxRunid.getValue().split(",");
		for (int i = 0; i < rid.length; i++)
			srid.add(rid[i].trim());
		srid.remove("");
		String str = "";
		for (Listitem li : listboxRunid.getItems()) {
			String ridstr = li.getLabel().trim().split(" ", 2)[0].trim();
			if (!li.isSelected()) {
				srid.remove(ridstr);
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}

			srid.add(ridstr);
		}
		bandboxRunid.setValue(srid.toString().replace("[", "").replace("]", ""));
	}

	// ******************************* ACTION BUTTONS HANDLER
	// *********************************************************

	private Set getDataset() {
		Set s = new LinkedHashSet();
		String[] ds = bandboxVarietyset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;
	}

	private Set getVariantset() {
		Set s = new LinkedHashSet();
		String[] ds = bandboxVariantset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;
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

	@Listen("onSelect =#tabSnpeff")
	public void onselectSnpeff() {

		if (queryResult == null)
			return;
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		List listSnpeffs = genotype.getSnpEffects(queryResult.getListPos());
		listboxSnpeff.setItemRenderer(new SNPEffListitemRenderer());
		this.listboxSnpeff.setModel(new SimpleListModel(listSnpeffs));
	}

	/**
	 * Search button clicked, Query variants
	 * 
	 * @param filename  if not null, download result in delimited format, else
	 *                  display in table
	 * @param delimiter delimiter of download file
	 */
	@Listen("onClick = #buttonSearch")
	// public void queryVariants(String filename, String delimiter) {
	public void queryVariants() {

		AppContext.debug("Index listboxMyLocusList: " + listboxMyLocusList.getSelectedIndex() + " -> "
				+ listboxMyLocusList.getSelectedItem());
		Clients.showBusy("Processing..");
		GenotypeFacade.snpQueryMode mode = null;

		try {

			// initialize SNP Query displays

			// empty table from previous results
			listboxSnpresult.setModel(new SimpleListModel(new java.util.ArrayList()));

			// hide the tables
			divPairwise.setVisible(false);
			gridBiglistheader.setVisible(false);
			biglistboxArray.setVisible(false);

			// snpallvarsresult.setWidth("1000px");
			listboxSnpresult.setWidth("1000px");

			tabTable.setSelected(true);
			tabTableLarge.setVisible(false);
			tabJbrowse.setVisible(false);
			tabGalaxy.setVisible(false);
			tabHaplotype.setVisible(false);
			tabSnpeff.setVisible(false);
			tabMSA.setVisible(false);
			tabPhylo.setVisible(false);
			tabMDS.setVisible(false);
			tabJBrowsePhylo.setVisible(false);
			tabVistaNPB.setVisible(false);
			tabVistaRev.setVisible(false);
			tabVista.setVisible(false);

			vistaurl1 = null;
			vistaurl2 = null;
			vistaurl3 = null;
			vistaurl4 = null;

			haplofilename = null;
			this.tabHaploGroupAlleles.setDisabled(true);
			this.tabHaploTree.setDisabled(true);
			this.tabHaploAutogroups.setDisabled(true);

			refreshgalaxy = true;
			gfffile = null;
			urljbrowse = null;
			urlphylo = null;
			mapVariety2Phyloorder = null;
			tallJbrowse = false;
			varpairDistance = null;
			urljbrowsephylo = null;
			queryResult = null;
			hboxFilterAllele.setVisible(false);
			hboxDownload.setVisible(false);
			checkboxCreateHaplotype.setChecked(false);
			this.auxheadAlleleFilter.setVisible(false);
			hboxAlleleFrequency.setVisible(false);

			this.varianttable = null;
			this.radioWait.setSelected(false);
			this.radioAsync.setSelected(true);
			this.buttonDownloadCsv.setDisabled(false);
			this.buttonDownloadTab.setDisabled(false);
			this.buttonDownloadPlink.setDisabled(false);
			this.buttonDownloadFlapjack.setDisabled(false);
			this.divDownloadOnlyMsg.setVisible(false);
			this.labelDownloadProgressMsg.setValue("");
			this.aDownloadProgressURL.setHref("");
			this.aDownloadProgressURL.setLabel("");
			divIndelLegend.setVisible(false);

			labelFilterResult.setValue("");
			lastY = 0;

			iframePhylotree.setSrc(null);
			iframePhylotree.invalidate();
			iframePhylotree.setVisible(false);

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, classGenotypefacade);
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

			if ((mode = validateValues()) == null)
				return;

			// gather form entries
			params = fillGenotypeQueryParams();

			String selcontig = params.getsChr();

			if (this.listboxMySNPList.getSelectedIndex() > 0)
				msgbox.setValue("SEARCHING: in " + this.listboxReference.getSelectedItem().getLabel() + " contig "
						+ selcontig + " , list " + listboxMySNPList.getSelectedItem().getLabel());
			else if (intStart.getValue() != null && intStop.getValue() != null && !selcontig.isEmpty())
				msgbox.setValue("SEARCHING: in " + this.listboxReference.getSelectedItem().getLabel() + " contig "
						+ selcontig + " [" + intStart.getValue() + "-" + intStop.getValue() + "]");
			else
				msgbox.setValue(
						"SEARCHING: in " + this.listboxReference.getSelectedItem().getLabel() + " contig " + selcontig);

			// initialize empty SNP list
			List listSNPs = new java.util.ArrayList();

			// all varieties query
			if (mode == GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS) {

				if (params.isbDownloadOnly()) {
					if (!divDownloadOnlyMsg.isVisible()) {
						varianttable = null;

						String strindelmsg = "";
						if (!AppContext.isIRRILAN() && params.isbIndel()) {
							params.setbIndel(false);
							this.checkboxIndel.setChecked(false);
							strindelmsg = "\nIndels are currently disabled for long queries.";
						}

						labelDownloadOnlyMsg.setPre(true);

						Set vs = getVariantset();
						labelDownloadOnlyMsg.setValue("Query longer than " + AppContext.getMaxlengthUni(vs) / 1000
								+ "kb region, or SNP List above " + AppContext.getMaxSNPList(vs)
								+ ", or Locus List above " + AppContext.getMaxLocusList(vs) + " items in " + vs
								+ " won't be displayed, but available only for download." + strindelmsg);

						Object req = Executions.getCurrent().getNativeRequest();
						String reqstr = "";
						if (req != null && req instanceof HttpServletRequest) {
							HttpServletRequest servreq = (HttpServletRequest) req;
							reqstr = "-" + servreq.getRemoteAddr() + "-" + servreq.getRemoteHost();

							String forwardedfor = servreq.getHeader("x-forwarded-for");
							if (forwardedfor != null)
								reqstr += "-" + forwardedfor;
							if (reqstr.contains(AppContext.getIRRIIp()))
								hboxPolluxUrl.setVisible(true);
							else
								hboxPolluxUrl.setVisible(false);
						}

						divDownloadOnlyMsg.setVisible(true);
						this.hboxDownload.setVisible(true);
						this.checkboxCreateHaplotype.setVisible(true);
						return;
					} else {
						if (!AppContext.isIRRILAN()) {
							params.setbIndel(false);
							this.checkboxIndel.setChecked(false);
						}
					}
				} else {
					divDownloadOnlyMsg.setVisible(false);
					this.checkboxCreateHaplotype.setVisible(false);
				}

				if (params.isbDownloadOnly() && this.divDownloadOnlyMsg.isVisible() && this.radioAsync.isSelected()) {

					return;

				} else {

					// Delegate query to API
					GenotypeQueryParams params2 = params;
					params2.setIncludedSnps(false, false, false);

					queryRawResult = genotype.queryGenotype(params2);

					// format result to table
					varianttable = new VariantAlignmentTableArraysImpl();
					varianttable = genotype.fillGenotypeTable(varianttable, queryRawResult, params);

					AppContext.resetTimer("to format to table..");

				}

				// query variants
				queryResult = varianttable.getVariantStringData();
				mapVariety2Phyloorder = null;
				listSNPs = queryRawResult.getListVariantsString();

				varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");

				// Map<BigDecimal, Object> mapVarid2Phenotype = null;
				mapVarid2Phenotype = null;
				sPhenotype = null;
				if (params.getPhenotype() != null && !params.getPhenotype().isEmpty()) {
					sPhenotype = params.getPhenotype();
					mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
				}

				// set table column width
				setTableSize();

				// set table model and renderer
				Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(queryResult,
						params);
				biglistboxArray.setMatrixRenderer(renderer);
				biglistboxModel = new Object2StringMultirefsMatrixModel(varianttable,
						params /* fillGenotypeQueryParams() */, varietyfacade.getMapId2Sample(params.getDataset()),gridBiglistheader,
						mapVarid2Phenotype, sPhenotype);
				biglistboxModel.setHeaderRows(biglistboxRows, lastY);
				biglistboxArray.setModel(biglistboxModel); // strRef));

				matriccmpproviderAsc = new Object2StringMatrixComparatorProvider<Object[]>(true);
				matriccmpproviderDesc = new Object2StringMatrixComparatorProvider<Object[]>(false);
				biglistboxArray.setSortAscending(matriccmpproviderAsc);
				biglistboxArray.setSortDescending(matriccmpproviderDesc);

				// biglistboxArray.invalidate();
				biglistboxArrayLarge.setSortAscending(matriccmpproviderAsc);
				biglistboxArrayLarge.setSortDescending(matriccmpproviderDesc);
				biglistboxArrayLarge.setWidth("1100px");
				biglistboxArrayLarge.setRowHeight("10px");
				biglistboxArrayLarge.setColWidth("10px");
				biglistboxArrayLarge.setStyle("font-size:6px");

				// setup header-rows (the frozen list at the left of the bigtable)
				gridBiglistheader.setWidth("200px");
				gridBiglistheader.getRows().setHeight("30px");
				gridBiglistheader.setOddRowSclass(biglistboxArray.getSclass());
				gridBiglistheader.setSclass(biglistboxArray.getOddRowSclass());

				if (sPhenotype != null) {
					auxheader3Phenotype.setLabel(sPhenotype);
					gridBiglistheader.getColumns().getLastChild().setVisible(true);
				} else {
					auxheader3Phenotype.setLabel("");
					gridBiglistheader.getColumns().getLastChild().setVisible(false);
				}
				gridBiglistheader.setRowRenderer(new BiglistRowheaderRenderer());
				biglistboxModel.setHeaderRows(biglistboxRows, lastY);
				Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray
						.getModel();

				List headerlist = model.getRowHeaderHeaderList();
				auxheadAlleles1.setVisible(false);
				auxheadAlleles2.setVisible(false);
				auxheadAlleles3.setVisible(false);
				auxheadAlleles4.setVisible(false);

				AppContext.debug("headers: " + headerlist);

				// display or hide header rows based on reference and reference alleles options
				biglistboxRows = 20;

				if (!params.getOrganism().equals(AppContext.getDefaultOrganism())) {

					// reference is not nipponbare
					if (params.isbShowNPBPositions()) {
						this.auxheader11Header.setLabel(params.getOrganism() + " positions");
						this.auxheader21Header.setLabel(params.getOrganism() + " allele");
						this.auxheader3Header.setLabel("Nipponbare positions");
						this.column4Header.setLabel("Nipponbare");
						this.auxhead1Header.setVisible(true);
						this.auxhead2Header.setVisible(true);
						biglistboxRows = 16;

						if (headerlist.size() > 4) {
							this.auxheaderAlleles1.setLabel((String) headerlist.get(4));
							this.auxheadAlleles1.setVisible(true);
							biglistboxRows--;
						}
						if (headerlist.size() > 5) {
							this.auxheaderAlleles2.setLabel((String) headerlist.get(5));
							this.auxheadAlleles2.setVisible(true);
							biglistboxRows--;
						}
						if (headerlist.size() > 6) {
							this.auxheaderAlleles3.setLabel((String) headerlist.get(6));
							this.auxheadAlleles3.setVisible(true);
							biglistboxRows--;
						}

					} else {
						this.auxheader3Header.setLabel(params.getOrganism() + " positions");
						this.column4Header.setLabel(params.getOrganism() + " allele");
						this.auxhead1Header.setVisible(false);
						this.auxhead2Header.setVisible(false);

						if (headerlist.size() > 2) {
							this.auxheaderAlleles1.setLabel((String) headerlist.get(2));
							this.auxheadAlleles1.setVisible(true);
							biglistboxRows--;
						}
						if (headerlist.size() > 3) {
							this.auxheaderAlleles2.setLabel((String) headerlist.get(3));
							this.auxheadAlleles2.setVisible(true);
							biglistboxRows--;
						}
						if (headerlist.size() > 4) {
							this.auxheaderAlleles3.setLabel((String) headerlist.get(4));
							this.auxheadAlleles3.setVisible(true);
							biglistboxRows--;
						}
						if (headerlist.size() > 5) {
							this.auxheaderAlleles4.setLabel((String) headerlist.get(5));
							this.auxheadAlleles4.setVisible(true);
							biglistboxRows--;
						}
					}

				} else {

					// reference is nipponbare

					this.auxheader3Header.setLabel("Nipponbare positions");
					this.column4Header.setLabel("Nipponbare");
					this.auxhead1Header.setVisible(false);
					this.auxhead2Header.setVisible(false);

					if (headerlist.size() > 2) {
						this.auxheaderAlleles1.setLabel((String) headerlist.get(2));
						this.auxheadAlleles1.setVisible(true);
						biglistboxRows--;
					}
					if (headerlist.size() > 3) {
						this.auxheaderAlleles2.setLabel((String) headerlist.get(3));
						this.auxheadAlleles2.setVisible(true);
						biglistboxRows--;
					}
					if (headerlist.size() > 4) {
						this.auxheaderAlleles3.setLabel((String) headerlist.get(4));
						this.auxheadAlleles3.setVisible(true);
						biglistboxRows--;
					}
					if (headerlist.size() > 5) {
						this.auxheaderAlleles4.setLabel((String) headerlist.get(5));
						this.auxheadAlleles4.setVisible(true);
						biglistboxRows--;
					}

					if (headerlist.size() > 6 || params.isbAlleleFilter() || params.isVarAlleleFilter()) {
						this.auxheadAlleleFilter.setVisible(true);
						if (this.listboxVarietyAlleleFilter.getSelectedIndex() > 0)
							auxheaderAlleleFilter.setLabel(
									"Query: " + (String) listboxVarietyAlleleFilter.getSelectedItem().getValue());
						else
							auxheaderAlleleFilter
									.setLabel("Query: " + (String) listboxAlleleFilter.getSelectedItem().getValue());
						biglistboxRows--;
					}

				}
				// adjust row header lines
				gridBiglistheader.setModel(new BiglistRowheaderModel(model.getRowHeaderList(biglistboxRows)));

				// set message
				msgbox.setValue(new String(msgbox.getValue() + varianttable.getMessage()).replace("\n\n", "\n"));

				auxheaderIrisid.setLabel("Assay");

				if (queryResult == null)
					throw new RuntimeException("queryResult==null");
				if (queryResult.getMapIdx2Pos() == null)
					throw new RuntimeException("queryResult.getMapIdx2Pos==null");

				divIndelLegend.setVisible(checkboxIndel.isChecked());

				if (params.isbAlleleFilter() || params.isVarAlleleFilter()) {
					hboxColorcodeSNPQuery.setVisible(false);
					hboxColorcodeGenotypeMatch.setVisible(true);
					auxheaderBiglistheaderMismatch.setLabel("Match");
				} else {
					hboxColorcodeSNPQuery.setVisible(true);
					hboxColorcodeGenotypeMatch.setVisible(false);
					auxheaderBiglistheaderMismatch.setLabel("Mismatch");
				}
				divAlignIndels.setVisible(AppContext.isLocalhost());

				gridBiglistheader.setVisible(true);
				biglistboxArray.setVisible(true);

				updateAlleleFrequencyChart();

				if (params.getlStart() == null || params.getlEnd() == null)
					updateVistaTab(params.getOrganism(), params.getsChr(), params.getPoslist());
				else
					updateVistaTab(params.getOrganism(), params.getsChr(), params.getlStart().intValue(),
							params.getlEnd().intValue());

				AppContext.resetTimer("to display to table..");

				AppContext.logSystemStatus();

				tallJbrowse = true;

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

						listboxPosition.setModel(new SimpleListModel(listPosition));
						labelFilterResult.setValue("None .. " + listSNPs.size() + " varieties");
						hboxFilterAllele.setVisible(true);
						hboxDownload.setVisible(true);
					}
				}
			} else {
				// SNPs on 2 varieties in region

				varianttable = new VariantAlignmentTableArraysImpl();

				String var1 = comboVar1.getValue().trim().toUpperCase();
				String var2 = comboVar2.getValue().trim().toUpperCase();

				params.setPairwiseComparison(var1, var2, this.radioUseAccession.isSelected());

				varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");

				Variety varobj1 = null;
				Variety varobj2 = null;
				if (this.radioUseVarname.isSelected()) {
					if (var1.endsWith("]"))
						varobj1 = varietyfacade.getGermplasmByAccession(var1.split("\\[")[1].replace("]", "").trim(),
								getDataset()); // .iterator().next();
					else
						varobj1 = (Variety) varietyfacade.getGermplasmByName(var1, getDataset()).iterator().next();

					if (var2.endsWith("]"))
						varobj2 = varietyfacade.getGermplasmByAccession(var2.split("\\[")[1].replace("]", "").trim(),
								getDataset()); // .iterator().next();
					else
						varobj2 = (Variety) varietyfacade.getGermplasmByName(var2, getDataset()).iterator().next();
				} else {
					varobj1 = varietyfacade.getGermplasmByAccession(var1.trim(), getDataset()); // .iterator().next();
					varobj2 = varietyfacade.getGermplasmByAccession(var2.trim(), getDataset()); // .iterator().next();
				}

				queryRawResult = genotype.compare2Varieties(varobj1.getVarietyId(), varobj2.getVarietyId(), params);
				varianttable = genotype.fillGenotypeTable(varianttable, queryRawResult, params);
				queryResult = varianttable.getVariantStringData();

				// update table header

				String var1header = this.comboVar1.getValue();
				String var2header = this.comboVar2.getValue();

				if (queryResult.getListVariantsString().size() == 2) {
					var1header = ((Variety) varietyfacade.getMapId2Variety(params.getDataset())
							.get(queryResult.getListVariantsString().get(0).getVar())).getName();
					var2header = ((Variety) varietyfacade.getMapId2Variety(params.getDataset())
							.get(queryResult.getListVariantsString().get(1).getVar())).getName();
				}

				if (params.isbShowNPBPositions()) {
					auxheaderMultirefPairwise.setLabel(params.getOrganism().toUpperCase());
					auxheadMultirefPairwise.setVisible(true);
					listheader1MultirefPairwise.setVisible(true);
					listheader2MultirefPairwise.setVisible(true);
					listheader3MultirefPairwise.setVisible(true);
				} else {
					auxheadMultirefPairwise.setVisible(false);
					listheader1MultirefPairwise.setVisible(false);
					listheader2MultirefPairwise.setVisible(false);
					listheader3MultirefPairwise.setVisible(false);
				}

				listheaderVar1MultirefPairwise.setLabel(var1header);
				listheaderVar2MultirefPairwise.setLabel(var2header);
				listHeaderVar2Reference.setLabel(params.getOrganism() + " REFERENCE");

				listboxSnpresult.setItemRenderer(new PairwiseListItemRenderer(queryResult, params));
				listboxSnpresult.setModel(new SimpleListModel(((VariantAlignmentTableArraysImpl) varianttable)
						.getCompare2VarsList(this.selectChr.getValue(), params)));
				divPairwise.setVisible(true);

				listSNPs = queryRawResult.getListVariantsString();

				gfffile = "tmp_" + AppContext.createTempFilename() + ".gff";
				create2VarietiesGFF(listSNPs, gfffile, queryResult.getListPos(), queryResult.getRefnuc());

				msgbox.setValue(new String(msgbox.getValue() + " ... RESULT: " + listboxSnpresult.getModel().getSize()
						+ " positions" + varianttable.getMessage()).replace("\n\n", "\n"));

				tallJbrowse = false;

			}

			if (mode == GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS && listSNPs.size() > 0) {
				// non-empty all-variety query, display JBrowse, Phylo and Alignment tabs
				if (this.listboxReference.getSelectedItem().getLabel().equals(AppContext.getDefaultOrganism()))
					tabSnpeff.setVisible(
							true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_SNP_EFFECT));
				else
					tabSnpeff.setVisible(false);
				if (listboxMySNPList.getSelectedIndex() > 0 || this.listboxMyLocusList.getSelectedIndex() > 0
						|| this.listboxAlleleFilter.getSelectedIndex() > 0) {
					tabJbrowse.setVisible(false);
					tabGalaxy.setVisible(true);
					tabHaplotype.setVisible(
							true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_HAPLOTYPE));
					tabTableLarge.setVisible(false);
					tabPhylo.setVisible(false);
					tabMDS.setVisible(false);
					tabMSA.setVisible(false);
					tabVista.setVisible(false);
				} else {
					if (checkboxIndel.isChecked() && !checkboxAlignIndels.isChecked())
						if (AppContext.isRice())
							tabMSA.setVisible(false);
					// else tabMSA.setVisible(true);

					String chr = selectChr.getValue().trim();
					if (!chr.isEmpty()) {
						updateJBrowse(chr, intStart.getValue().toString(), intStop.getValue().toString(), "");
						// TODO FIX THIS AFTER TEST
						// tabGalaxy.setVisible(true);
						tabGalaxy.setVisible(true);
						tabJbrowse.setVisible(
								true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_JBROWSE));
						tabHaplotype.setVisible(
								true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_HAPLOTYPE));

						if (AppContext.isRice()) {
							if (tallJbrowse) {
								update_phylotree(chr.toUpperCase().replace("CHR0", "").replace("CHR", ""),
										intStart.getValue().toString(), intStop.getValue().toString(), listSNPs.size());
								// tabPhylo.setVisible(true);
								tabMDS.setVisible(
										true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_MDS));
							}
						}
						// tabTableLarge.setVisible(true);

					}
					hboxDownload.setVisible(true);
					if (AppContext.isRice())
						tabVista.setVisible(
								true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_COMPARE));
					
				}
				// alleleFreqlines=null;
				calculateAlleleFrequencies();
				hboxAlleleFrequency.setVisible(true);
				divTablePanel.setVisible(true);
				divPairwise.setVisible(false);

				onScrollYTable(0);

				splitter.setOpen(false);

			} else if (mode == GenotypeFacade.snpQueryMode.SNPQUERY_VARIETIES && listSNPs.size() > 0) {

				// show two-varieties table

				tabJbrowse.setVisible(true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_JBROWSE));
				tabHaplotype
						.setVisible(true && AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_HAPLOTYPE));
				tabSnpeff.setVisible(
						this.listboxReference.getSelectedItem().getLabel().equals(AppContext.getDefaultOrganism())
								&& AppContext.showItem(WebserverPropertyConstants.SHOW_GENOTYPE_SNP_EFFECT));
				msgJbrowse.setVisible(false);
				divTablePanel.setVisible(false);
				// listboxSnpresult.setVisible(true);
				splitter.setOpen(false);

			} else {
				tabJbrowse.setVisible(false);
				tabGalaxy.setVisible(false);
				tabHaplotype.setVisible(false);
				tabSnpeff.setVisible(false);
				tabPhylo.setVisible(false);
				tabMDS.setVisible(false);
				tabMSA.setVisible(false);
				iframeJbrowse.setVisible(false);
				msgJbrowse.setVisible(false);
				divTablePanel.setVisible(false);
				tabVista.setVisible(false);
			}

		} catch (InvocationTargetException ex) {
			ex.getCause().printStackTrace();
			Messagebox.show(ex.getCause().getMessage(), "InvocationTargetException", Messagebox.OK, Messagebox.ERROR);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);

		} finally {
			// render after
			oncheckShowsnps();
			Clients.clearBusy();
		}

	}

	private void setTableSize() {
		biglistboxArray.setRowHeight("30.59px");
		biglistboxArray.setColWidth("35px");
		int width = 1050;
		int biglistcolwidthOverview = 10;

		if (checkboxIndel.isChecked() && !checkboxAlignIndels.isChecked()) {
			// display indel in description format

			// adjust column width to fill 1000px
			if (queryResult.getListPos().size() < 25)
				width = ((queryResult.getListPos().size() + 6) * 35) + 25;

		} else {
			// show MSA format
			if (queryResult.getListPos().size() < 25) {
				width = ((queryResult.getListPos().size() + 6) * 35) + 25;
			}

		}
		biglistboxArray.setWidth(width + "px");

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

	private boolean is3k() {
		Set s = getDataset();
		return s.contains("3k");
	}

	/**
	 * Validate values in form
	 * 
	 * @return GenotypeFacade.snpQueryMode, null if invalid
	 */
	private GenotypeFacade.snpQueryMode validateValues() {

		String var1 = comboVar1.getValue().trim().toUpperCase();
		String var2 = comboVar2.getValue().trim().toUpperCase();
		Set setVarieties = null;
		String selcontig = selectChr.getValue();

		String sSubpopulation = null;
		GenotypeFacade.snpQueryMode mode = null;

		if (checkboxAllvarieties.isChecked()) {
			mode = GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS;
		} else if (listboxSubpopulation.getSelectedIndex() > 0) {
			mode = GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS;
			setVarieties = genotype.getVarietiesForSubpopulation(listboxSubpopulation.getSelectedItem().getLabel(),
					getDataset());

			if (listboxSubpopulation.getSelectedIndex() > 1)
				sSubpopulation = listboxSubpopulation.getSelectedItem().getLabel();

		} else if (listboxMyVarieties.getSelectedIndex() > 0) {
			mode = GenotypeFacade.snpQueryMode.SNPQUERY_ALLVARIETIESPOS;
			setVarieties = workspace.getVarieties(listboxMyVarieties.getSelectedItem().getLabel());
		} else {
			mode = GenotypeFacade.snpQueryMode.SNPQUERY_VARIETIES;
			if (var1.isEmpty() || var2.isEmpty()) {
				Messagebox.show("Two varieties are required for comparison, or check All Varieties",
						"INVALID VARIETIES VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
				return null;
			}
		}

		String genename = comboGene.getValue().trim().toUpperCase();
		// gfffile = "tmp_" + AppContext.createTempFilename() + ".gff";

		AppContext.startTimer();

		String sLocus = null;
		if (!genename.isEmpty()) {
			// fill the contig,start,stop boxes
			Locus gene = this.guessGene(comboGene.getValue().trim());
			if (gene == null)
				return null;
			intStart.setValue(gene.getFmin());
			intStop.setValue(gene.getFmax());
			selectChr.setValue(gene.getContig().replace("0", "").toUpperCase());
			comboGene.setValue(gene.getUniquename());
			sLocus = gene.getUniquename();

		}

		Set snpposlist = null;
		Set locuslist = null;

		AppContext.debug("Index listboxMySNPList: " + listboxMySNPList.getSelectedIndex() + " -> "
				+ listboxMySNPList.getSelectedItem());
		AppContext.debug("Index listboxAlleleFilter: " + listboxAlleleFilter.getSelectedIndex() + " -> "
				+ listboxAlleleFilter.getSelectedItem());
		AppContext.debug("Index listboxMyLocusList: " + listboxMyLocusList.getSelectedIndex() + " -> "
				+ listboxMyLocusList.getSelectedItem());

		if ((listboxMySNPList.getSelectedIndex()) > 0 && (listboxMySNPList.getSelectedItem() != null)) {

			String chrlistname[] = listboxMySNPList.getSelectedItem().getLabel().split(":");
			snpposlist = workspace.getSnpPositions(chrlistname[0].trim(), chrlistname[1].trim());

			int maxsize = AppContext.getMaxSNPListDownload(getVariantset());

			if (snpposlist.size() > maxsize) {
				Messagebox.show("Limit SNP list size to " + maxsize, "QUERY NOT ALLOWED", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

		} else if ((listboxAlleleFilter.getSelectedIndex() > 0) && (listboxAlleleFilter.getSelectedItem() != null)) {

			String chrlistname[] = listboxAlleleFilter.getSelectedItem().getLabel().split(":");
			snpposlist = workspace.getSnpPositions(chrlistname[0].trim(), chrlistname[1].trim());
			int maxsize = AppContext.getMaxSNPListDownload(getVariantset());

			if (snpposlist.size() > maxsize) {
				Messagebox.show("Limit SNP list size to " + maxsize, "QUERY NOT ALLOWED", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

		} else if ((listboxMyLocusList.getSelectedIndex() > 0) && (listboxMyLocusList.getSelectedItem() != null)) {

			locuslist = workspace.getLoci(listboxMyLocusList.getSelectedItem().getLabel().trim());
			int maxsize = AppContext.getMaxLocusListDownload(getVariantset());

			if (locuslist.size() > maxsize) {
				Messagebox.show("Limit locus list size to " + maxsize, "QUERY NOT ALLOWED", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

		} else if (selcontig.equals("whole genome")) {
			if (this.listboxSubpopulation.getSelectedIndex() > 0) {
				Messagebox.show("Limit to 100 varieties (using a list) for every whole genome download",
						"OPERATION NOT ALLOWED", Messagebox.OK, Messagebox.EXCLAMATION);
				return null;
			}
		} else {

			if (selcontig.isEmpty()) {
				Messagebox.show("Please select chromosome/contig", "INVALID POSITION VALUE", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

			if (genotype.getFeature(selcontig, this.listboxReference.getSelectedItem().getLabel()) == null) {
				Messagebox.show(
						"Contig " + selcontig + " not found in " + listboxReference.getSelectedItem().getLabel(),
						"INVALID CONTIG VALUE", Messagebox.OK, Messagebox.EXCLAMATION);
				return null;
			}
			;

			// int chrlen = genotype.getFeatureLength( selectChr.getValue());
			int chrlen = genotype.getFeatureLength(selcontig, this.listboxReference.getSelectedItem().getLabel()); // .getFeatureLength(
																													// selectChr.getValue());

			if (intStop == null || intStart == null || !intStop.isValid() || !intStart.isValid()
					|| intStop.getValue() == null || intStart.getValue() == null) {
				Messagebox.show("Please provide start and end positions", "INVALID POSITION VALUE", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

			if (intStop.getValue() > chrlen || intStart.getValue() > chrlen) {
				Messagebox.show("Positions should be less than length", "INVALID POSITION VALUE", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}
			if (intStop.getValue() < 1 || intStart.getValue() < 1) {
				Messagebox.show("Positions should be positive integer", "INVALID POSITION VALUE", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}
			if (intStop.getValue() < intStart.getValue()) {
				Messagebox.show("End should be greater than or equal to start", "INVALID POSITION VALUE", Messagebox.OK,
						Messagebox.EXCLAMATION);
				return null;
			}

			int maxlength = -1;
			String limitmsg = "";
			maxlength = AppContext.getMaxlengthUniDownload(getVariantset());
			limitmsg = "Limit to " + (maxlength / 1000) + " kbp range for " + getVariantset() + " all varieties query";

			int querylength = intStop.getValue() - intStart.getValue();
			if (querylength > maxlength) {

				Messagebox.show(limitmsg, "OPERATION NOT ALLOWED", Messagebox.OK, Messagebox.EXCLAMATION);
				msgbox.setStyle("font-color:red");
				return null;

			}
		}
		return mode;
	}

	@Listen("onClick = #buttonDownloadNoDisplayTab")
	public void downloadNoDisplayTab() {
		Clients.showBusy("Fetching from Database");
		downloadGenome("snp3k", "\t");
		Clients.clearBusy();

	}

	@Listen("onClick = #buttonDownloadNoDisplayCSV")
	public void downloadNoDisplayCsv() {
		AppContext.debug("downloading csv");
		Clients.showBusy("Fetching from Database");
		downloadGenome("snp3k", ",");
		Clients.clearBusy();
	}

	@Listen("onClick = #buttonDownloadNoDisplayPlink")
	public void downloadNoDisplayPlink() {
		AppContext.debug("downloading plink");
		Clients.showBusy("Fetching from Database");
		downloadGenome("snp3k", "plink");
		Clients.clearBusy();
	}

	@Listen("onClick = #buttonDownloadNoDisplayHapmap")
	public void downloadNoDisplayHapmap() {
		AppContext.debug("downloading hapmap");
		Clients.showBusy("Fetching from Database");
		downloadGenome("snp3k", "hapmap");
		Clients.clearBusy();
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

		GenotypeQueryParams params = new GenotypeQueryParams(setVarieties, contig, lStart, lStop,
				checkboxSNP.isChecked(), checkboxIndel.isChecked(), getVariantset(), getDataset(), runs,
				checkboxMismatchOnly.isChecked(), snpposlist, sSubpopulation, sLocus, checkboxAlignIndels.isChecked(),
				checkboxShowAllRefAlleles.isChecked());

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

		params.setbHeteroIndel(checkboxHeteroindels.isChecked());
		params.setColLoci(locuslist);
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

		// params.setbCountMissingAs05(params.isbAlleleFilter());

		params.setbWaitResult(this.radioWait.isSelected());

		params.setDatasetPosOps(listboxDatasetSnpOps.getSelectedItem().getLabel());

		// if (listboxPhenotype.getSelectedItem() != null) {
		// params.setPhenotype(listboxPhenotype.getSelectedItem().getLabel());
		// }
		//
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
			// params.setbDownloadOnly(checkboxCreateHaplotype.isChecked());
			params.setbGenerateHapmap(checkboxCreateHaplotype.isChecked());
		} else {
			params.setbGenerateHapmap(false);
		}

		return params;

	}

	/**
	 * Clears all inputs
	 */
	@Listen("onClick = #buttonReset")
	public void reset() {

		try {
			comboVar1.setValue("");
			comboVar2.setValue("");
			this.radioUseAccession.setSelected(true);
			checkboxMismatchOnly.setChecked(DEFAULT_MISMATCHONLY);
			checkboxAllvarieties.setChecked(true);

			this.listboxMyVarieties.setSelectedIndex(0);
			// this.comboSubpopulation.setValue("");
			listboxSubpopulation.setSelectedIndex(0);

			intStart.setValue(null);
			intStop.setValue(null);
			comboGene.setValue("");
			selectChr.setValue("");
			this.listboxMySNPList.setSelectedIndex(0);
			this.listboxMyLocusList.setSelectedIndex(0);

			// this.checkboxCoreSNP.setChecked(false);
			// this.listboxDataset.setSelectedIndex(0);
			// this.listboxSnpset.setSelectedIndex(2);
			// this.listboxGenotyperun.setSelectedItems(new HashSet());
			this.selectPhyloTopN.setSelectedIndex(0);

			this.radioColorMismatch.setSelected(true);
			this.listboxPhenotype.setSelectedIndex(0);

			msgbox.setValue("Select varieties, then set chromosome region or gene locus");

			this.listboxPhenotype.setSelectedIndex(0);
			this.checkboxIndel.setChecked(false);

			setchrlength();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// *********************************************** HANDLE QUERY INTERFACE EVENTS
	// **********************************************

	/**
	 * Enable/Disable controls valid for 3k data only
	 * 
	 * @param set
	 */
	private void set3kAllOnly(boolean set) {

		/*
		 * if(set) { this.listboxDataset.setSelectedIndex(0);
		 * listboxDataset.setDisabled(true); this.listboxSnpset.setSelectedIndex(0);
		 * listboxSnpset.setDisabled(true); this.listboxDatasetSnpOps.setVisible(false);
		 * } else { listboxDataset.setDisabled(false);
		 * listboxDataset.setSelectedIndex(0); listboxSnpset.setDisabled(false);
		 * listboxSnpset.setSelectedIndex(2); }
		 */

	}

	@Listen("onClick =#checkboxShowAllRefAlleles")
	public void onclickShowallrefss() {
		if (listboxReference.getSelectedItem().getLabel().equals(AppContext.REFERENCE_NIPPONBARE())) {
			set3kAllOnly(false);
			if (checkboxShowAllRefAlleles.isChecked()) {
				this.checkboxIndel.setChecked(false);
				this.checkboxIndel.setDisabled(true);
				// this.checkboxCoreSNP.setChecked(false);
				// checkboxCoreSNP.setDisabled(true);

				// set3kAllOnly(true);

				/*
				 * this.listboxMySNPList.setSelectedIndex(0);
				 * listboxMySNPList.setDisabled(true);
				 * this.listboxMyLocusList.setSelectedIndex(0);
				 * listboxMyLocusList.setDisabled(true);
				 * this.listboxAlleleFilter.setSelectedIndex(0);
				 * this.listboxAlleleFilter.setDisabled(true);
				 */
			} else {
				this.checkboxIndel.setDisabled(false);
				// checkboxCoreSNP.setDisabled(false);
				// listboxMySNPList.setDisabled(false);
				// listboxMyLocusList.setDisabled(false);
				// this.listboxAlleleFilter.setDisabled(false);
				// set3kAllOnly(false);
			}
		} else {
			this.checkboxIndel.setDisabled(true);
			set3kAllOnly(true);
		}

	}

	@Listen("onClick =#checkboxIndel")
	public void onclickIndels() {
		// this.buttonDownloadPlink.setDisabled(checkboxIndel.isChecked());
		// this.buttonDownloadFlapjack.setDisabled(checkboxIndel.isChecked());
		/*
		 * if(checkboxIndel.isChecked()) { // disable display of other references for
		 * INDELS this.checkboxShowAllRefAlleles.setChecked(false);
		 * this.checkboxShowAllRefAlleles.setDisabled(true); } else {
		 * this.checkboxShowAllRefAlleles.setDisabled(false); }
		 */
	}

	@Listen("onClick =#radioColorMismatch")
	public void onclickcolorMismatch() {
		try {
			if (biglistboxArray == null)
				return;
			Object2StringMultirefsMatrixRenderer renderer = (Object2StringMultirefsMatrixRenderer) this.biglistboxArray
					.getMatrixRenderer();
			// Object2StringMultirefsMatrixModel model =
			// (Object2StringMultirefsMatrixModel)this.biglistboxArray.getModel();
			// FakerMatrixModel model = (FakerMatrixModel)this.biglistboxArray.getModel();

			Object2StringMultirefsMatrixModel model = null;
			MatrixModel model1 = biglistboxArray.getModel();
			if (model1 instanceof Object2StringMultirefsMatrixModel) {
				model = (Object2StringMultirefsMatrixModel) model1;
			} else
				return;

			if (model == null || model.getData() == null)
				return;

			Object2StringMultirefsMatrixRenderer Object2StringMultirefsMatrixModel = new Object2StringMultirefsMatrixRenderer(
					model.getData().getVariantStringData(), fillGenotypeQueryParams());
			biglistboxArray.setMatrixRenderer(renderer);
			this.divMismatch.setVisible(true);
			this.divNucleotide.setVisible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onClick =#radioColorAllele")
	public void onclickcolorAllele() {
		try {
			if (biglistboxArray == null)
				return;
			Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) this.biglistboxArray
					.getModel();
			if (model == null || model.getData() == null)
				return;

			Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(
					model.getData().getVariantStringData(), fillGenotypeQueryParams());
			biglistboxArray.setMatrixRenderer(renderer);
			this.divNucleotide.setVisible(true);
			this.divMismatch.setVisible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onSelect =#listboxReference")
	public void onselectReference() {

		try {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			String selOrg = listboxReference.getSelectedItem().getLabel();

			// get contigs for selected reference
			listContigs = AppContext.createUniqueUpperLowerStrings(genotype.getContigsForReference(selOrg), true, true);

			// get loci for reference
			listLoci = AppContext.createUniqueUpperLowerStrings(genotype.getLociForReference(selOrg), true, true);

			AppContext.debug(selectChr.getChildren().size() + " contigs, " + listLoci.size() + " loci in combobox ");

			selectChr.setModel(new SimpleListModel(listContigs));
			comboGene.setModel(new SimpleListModel(listLoci));

			if (selOrg.equals(Organism.REFERENCE_9311))
				labelChr.setValue("Chromosome/Scaffold");
			else if (selOrg.equals(AppContext.REFERENCE_NIPPONBARE()) || selOrg.equals(Organism.REFERENCE_KASALATH))
				labelChr.setValue("Chromosome");
			else
				labelChr.setValue("Scaffold");

			if (selOrg.equals(Organism.REFERENCE_9311))
				labelChrExample.setValue("(ex. 9311_chr01, scaffold01_4619) ");
			else if (selOrg.equals(Organism.REFERENCE_KASALATH))
				labelChrExample.setValue("(ex. chr01, um) ");
			// else if(selOrg.equals(AppContext.getDefaultOrganism()))
			// labelChrExample.setValue("(ex. 9311_chr01, scaffold01_4619) " );
			else
				labelChrExample.setValue("(ex. " + listContigs.get(1).toString().toLowerCase() + ") ");

			if (selOrg.equals(AppContext.REFERENCE_NIPPONBARE()))
				labelLocusExample.setValue("(ex. loc_os01g01010)");
			else if (selOrg.equals(Organism.REFERENCE_IR64))
				labelLocusExample.setValue("(ex. osir64_00001g0100)");
			else if (selOrg.equals(Organism.REFERENCE_9311))
				labelLocusExample.setValue("(ex. os9311_01g010100)");
			else if (selOrg.equals(Organism.REFERENCE_KASALATH))
				labelLocusExample.setValue("(ex. oskasal01g010050)");
			else if (selOrg.equals(Organism.REFERENCE_DJ123))
				labelLocusExample.setValue("(ex. osdj12_00001g010100)");

			selectChr.setValue("");
			comboGene.setValue("");

			// hide/disable some features if reference is not Nipponbare
			if (!selOrg.equals(AppContext.REFERENCE_NIPPONBARE())) {
				checkboxShowNPBPosition.setChecked(false);
				// divShowNPBPositions.setVisible(true);

				// this.comboGene.setDisabled(true);
				listboxMySNPList.setSelectedIndex(0);
				this.listboxMySNPList.setDisabled(true);
				listboxMyLocusList.setSelectedIndex(0);
				this.listboxMyLocusList.setDisabled(true);
				this.listboxAlleleFilter.setSelectedIndex(0);
				this.listboxAlleleFilter.setDisabled(true);
				this.listboxVarietyAlleleFilter.setSelectedIndex(0);
				this.listboxVarietyAlleleFilter.setDisabled(true);

				// this.comboGene.setValue("");
				// this.comboGene.setDisabled(true);

				this.checkboxIndel.setDisabled(true);
				this.checkboxIndel.setChecked(false);

				this.radioAllSnps.setSelected(true);
				this.radioAllSnps.setDisabled(true);
				this.radioNonsynSnps.setDisabled(true);
				this.radioNonsynHighlights.setDisabled(true);
				this.radioNonsynSnpsPlusSplice.setDisabled(true);

				// this.checkboxCoreSNP.setChecked(false);
				// this.checkboxCoreSNP.setDisabled(true);

				this.checkboxShowAllRefAlleles.setChecked(true);
				checkboxShowAllRefAlleles.setDisabled(true);

				// enable only 3kall
				// this.listboxGenotyperun.setSelectedIndex(0);
				// listboxGenotyperun.setDisabled(true);

				/*
				 * this.listboxDataset.setSelectedIndex(0); listboxDataset.setDisabled(true);
				 * this.listboxSnpset.setSelectedIndex(0); listboxSnpset.setDisabled(true);
				 */
				// this.listboxDataset.setSelectedIndex(0);

			} else {
				checkboxShowNPBPosition.setChecked(false);
				divShowNPBPositions.setVisible(false);
				// this.comboGene.setDisabled(false);
				this.listboxMySNPList.setDisabled(false);
				this.listboxMyLocusList.setDisabled(false);
				this.listboxAlleleFilter.setDisabled(false);
				this.listboxVarietyAlleleFilter.setDisabled(false);

				this.comboGene.setValue("");
				this.comboGene.setDisabled(false);
				this.radioAllSnps.setDisabled(false);
				this.radioNonsynHighlights.setDisabled(false);
				this.radioNonsynSnpsPlusSplice.setDisabled(false);
				this.checkboxIndel.setDisabled(false);

				// this.checkboxCoreSNP.setDisabled(false);
				this.checkboxShowAllRefAlleles.setChecked(false);
				checkboxShowAllRefAlleles.setDisabled(false);

				// enable , select 3kfiltered
				// this.listboxGenotyperun.setSelectedIndex(1);
				// listboxGenotyperun.setDisabled(true);
				/*
				 * listboxDataset.setDisabled(false); this.listboxDataset.setSelectedIndex(0);
				 * this.listboxSnpset.setSelectedIndex(2); listboxSnpset.setDisabled(false);
				 */
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Update SNP types to display, no requery just update in displays
	 */
	@Listen("onCheck = #radiogroupShowsnps")
	public void oncheckShowsnps() {
		if (this.queryRawResult == null)
			return;

		if (this.divPairwise.isVisible()) {
			try {
				GenotypeQueryParams params = fillGenotypeQueryParams();
				updatePairwiseListbox(params);
			} catch (Exception ex) {
				ex.printStackTrace();
				Messagebox.show(ex.getMessage(), "Exception in oncheckShowsnps.updatePairwiseListbox", Messagebox.OK,
						Messagebox.ERROR);
			}
		}

		if (biglistboxArray.isVisible()) {
			try {
				GenotypeQueryParams params = fillGenotypeQueryParams();
				updateBiglistboxArray(params);
			} catch (Exception ex) {
				ex.printStackTrace();
				Messagebox.show(ex.getMessage(), "Exception in oncheckShowsnps.updateBiglistboxArray", Messagebox.OK,
						Messagebox.ERROR);
			}

			setTableSize();
		}
	}

	private void updatePairwiseListbox(GenotypeQueryParams params) {
		listboxSnpresult.setItemRenderer(new PairwiseListItemRenderer(queryResult, params));
		listboxSnpresult.setModel(new SimpleListModel(((VariantAlignmentTableArraysImpl) varianttable)
				.getCompare2VarsList(this.selectChr.getValue(), params)));
		divPairwise.setVisible(true);

		String msgs[] = msgbox.getValue().split("...");
		if (msgs.length > 0)
			msgbox.setValue(msgs[0] + " ... RESULT: " + listboxSnpresult.getModel().getSize() + " positions");
		else
			msgbox.setValue(msgbox.getValue() + " ... RESULT: " + listboxSnpresult.getModel().getSize() + " positions");

	}

	public void onScrollYTable(int y) {
		onScrollYTable(new ScrollEventExt("onScrollY", biglistboxArray, 0, y));
	}

	@Listen("onScrollY = #biglistboxArray")
	public void onScrollYTable(ScrollEventExt event) {

		int firstrow = event.getY();
		if (firstrow % 2 == 0) {
			gridBiglistheader.setOddRowSclass(biglistboxArray.getSclass());
			gridBiglistheader.setSclass(biglistboxArray.getOddRowSclass());
		} else {
			gridBiglistheader.setSclass(biglistboxArray.getSclass());
			gridBiglistheader.setOddRowSclass(biglistboxArray.getOddRowSclass());
		}

		lastY = event.getY();
		biglistboxModel.setHeaderRows(biglistboxRows, lastY);

		Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();

		gridBiglistheader.setModel(new BiglistRowheaderModel(model.getRowHeaderList(biglistboxRows, lastY)));

	}

	@Listen("onSort =#biglistboxArray")
	public void onsortBiglistbox(SortEventExt event) {

		Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		getSession().putValue("variantTable", model.getData());
		gfffile = null;
		gridBiglistheader.setModel(new BiglistRowheaderModel(model.getRowHeaderList(biglistboxRows, lastY)));
	}

	/**
	 * Handle create variety list from table
	 */

	@Listen("onClick = #buttonCreateVarlist")
	public void onclickCreateVarlist() {
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");

		Map<String, Variety> mapName2Var = varietyfacade.getMapVarname2Variety(getDataset());
		Set setVarieties = new LinkedHashSet();
		Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object varname = ((Object[]) model.getElementAt(i).get(0))[0];
			setVarieties.add(mapName2Var.get(varname));
		}
		workspace.addVarietyList(textboxVarlistName.getValue().trim(), setVarieties, getDataset());
		textboxVarlistName.setValue("");

	}

	/**
	 * Handle variety list selections
	 */
	@Listen("onSelect = #listboxMyVarieties")
	public void onselectMyVarieties() {
		if (listboxMyVarieties.getSelectedIndex() > 0) {
			listboxSubpopulation.setSelectedIndex(0);
			checkboxAllvarieties.setChecked(false);
			comboVar1.setValue("");
			comboVar2.setValue("");

			if (listboxMyVarieties.getSelectedItem().getLabel().equals("create new list...")) {
				Executions.sendRedirect("_workspace.zul?from=variety&src=snp");
			}
		}
	}

	/**
	 * Handle locus list selections
	 */
	@Listen("onSelect = #listboxMyLocusList")
	public void onselectMyLocusList() {
		try {
			if (listboxMyLocusList.getSelectedIndex() > 0) {

				if (listboxMyLocusList.getSelectedItem().getLabel().equals("create new list...")) {
					Executions.sendRedirect("_workspace.zul?from=locus&src=snp");
				} else {
					this.selectChr.setValue("");
					this.comboGene.setValue("");
					this.intStart.setValue(null);
					this.intStop.setValue(null);
					listboxMySNPList.setSelectedIndex(0);

					this.listboxReference.setSelectedIndex(0);
					this.checkboxShowAllRefAlleles.setChecked(false);
					this.checkboxShowAllRefAlleles.setDisabled(true);

					this.checkboxShowNPBPosition.setChecked(false);
					this.divShowNPBPositions.setVisible(false);

					this.checkboxIndel.setChecked(false);
					this.checkboxIndel.setDisabled(true);

				}
			} else {

				this.divShowNPBPositions.setVisible(false);

				this.checkboxIndel.setDisabled(false);

			}
			/*
			 * } catch(InvocationTargetException ex) { ex.printStackTrace();
			 * AppContext.debug("caused by:"); ((Exception
			 * )ex.getCause()).printStackTrace();
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Handle SNP allele fileter selections
	 */
	@Listen("onSelect = #listboxAlleleFilter")
	public void onselectlistboxAlleleFilter() {

		if (listboxAlleleFilter.getSelectedIndex() > 0) {
			listboxMySNPList.setSelectedIndex(0);
			checkboxMismatchOnly.setChecked(DEFAULT_MISMATCHONLY);
			checkboxMismatchOnly.setDisabled(true);
			listboxVarietyAlleleFilter.setSelectedIndex(0);

			if (listboxAlleleFilter.getSelectedItem().getLabel().equals("create new list...")) {
				Executions.sendRedirect("_workspace.zul?from=snp&src=snp&hasallele=true");
			} else {
				updateSnplistSelection(listboxAlleleFilter);
				this.checkboxIndel.setChecked(false);
				this.checkboxIndel.setDisabled(true);
			}
		} else {
			this.checkboxShowAllRefAlleles.setDisabled(false);
			this.listboxReference.setDisabled(false);
			checkboxMismatchOnly.setChecked(DEFAULT_MISMATCHONLY);
			checkboxMismatchOnly.setDisabled(false);
			this.checkboxIndel.setDisabled(false);
		}

	}

	@Listen("onSelect = #listboxVarietyAlleleFilter")
	public void onselectlistboxVarietyAlleleFilter() {

		if (listboxVarietyAlleleFilter.getSelectedIndex() > 0) {
			// listboxMySNPList.setSelectedIndex(0);
			checkboxMismatchOnly.setChecked(DEFAULT_MISMATCHONLY);
			checkboxMismatchOnly.setDisabled(true);
			listboxAlleleFilter.setSelectedIndex(0);
			this.checkboxIndel.setChecked(false);
			this.checkboxIndel.setDisabled(true);

		} else {
			this.checkboxShowAllRefAlleles.setDisabled(false);
			this.listboxReference.setDisabled(false);
			checkboxMismatchOnly.setChecked(DEFAULT_MISMATCHONLY);
			checkboxMismatchOnly.setDisabled(false);
			this.checkboxIndel.setDisabled(false);
		}

	}

	/**
	 * Handle SNP list selections
	 */
	@Listen("onSelect = #listboxMySNPList")
	public void onselectMySNPList() {
		if (listboxMySNPList.getSelectedIndex() > 0) {
			listboxAlleleFilter.setSelectedIndex(0);

			if (listboxMySNPList.getSelectedItem().getLabel().equals("create new list...")) {
				Executions.sendRedirect("_workspace.zul?from=snp&src=snp");
			} else {
				updateSnplistSelection(listboxMySNPList);
				this.checkboxIndel.setChecked(false);
				this.checkboxIndel.setDisabled(true);

			}
		} else {
			this.checkboxShowAllRefAlleles.setDisabled(false);
			this.listboxReference.setDisabled(false);
			this.checkboxIndel.setDisabled(false);

		}
	}

	private void updateSnplistSelection(Listbox listboxsnp) {

		String selChr = listboxsnp.getSelectedItem().getLabel().split(":")[0].toUpperCase();
		if (selChr.equals("ANY")) {
			selectChr.setValue("");
		} else {
			try {
				this.selectChr.setValue(selChr.toLowerCase());
				listboxMyLocusList.setSelectedIndex(0);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		this.comboGene.setValue("");
		this.intStart.setValue(null);
		this.intStop.setValue(null);

		this.listboxReference.setSelectedIndex(0);
		listboxReference.setDisabled(true);

		this.checkboxShowAllRefAlleles.setChecked(false);
		this.checkboxShowAllRefAlleles.setDisabled(true);

		this.checkboxShowNPBPosition.setChecked(false);

		this.divShowNPBPositions.setVisible(false);
		// }
	}

	@Listen("onClick = #radioUseAccession")
	public void onclickUseaccession() {
		if (radioUseAccession.isSelected()) {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
			List list1 = genotype.getVaraccessions(getDataset());
			List combolists = AppContext.createUniqueUpperLowerStrings(list1, true, true);
			this.comboVar1.setModel(new SimpleListModel(combolists));
			comboVar1.setValue("");
			this.comboVar2.setModel(new SimpleListModel(combolists));
			comboVar2.setValue("");

			labelExampleVariety.setValue((is3k() ? "(ex. IRGC122151)" : "(ex. IRGC121423,NSFTV100)"));

			this.listboxVarietyAlleleFilter
					.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(list1, false, true)));
			listboxVarietyAlleleFilter.setSelectedIndex(0);

		}
	}

	@Listen("onClick = #radioUseVarname")
	public void onclickUsevarname() {
		if (radioUseVarname.isSelected()) {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
			List list1 = genotype.getVarnames(getDataset());
			List combolists = AppContext.createUniqueUpperLowerStrings(list1, true, true);
			this.comboVar1.setModel(new SimpleListModel(combolists));
			comboVar1.setValue("");
			this.comboVar2.setModel(new SimpleListModel(combolists));
			comboVar2.setValue("");

			labelExampleVariety.setValue((is3k() ? "(ex. loto)" : "(ex. malagkit puti)"));

			this.listboxVarietyAlleleFilter
					.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(list1, false, true)));

			listboxVarietyAlleleFilter.setSelectedIndex(0);

		}

	}

	@Listen("onClick = #checkboxAllvarieties")
	public void oncheckAllVarieties() {
		if (checkboxAllvarieties.isChecked()) {
			listboxSubpopulation.setSelectedIndex(0);
			listboxMyVarieties.setSelectedIndex(0);
			comboVar1.setValue("");
			comboVar2.setValue("");
		}
	}

	@Listen("onSelect = #listboxSubpopulation")
	public void onselectSubpopulation() {
		if (listboxSubpopulation.getSelectedIndex() > 0) {
			checkboxAllvarieties.setChecked(false);
			listboxMyVarieties.setSelectedIndex(0);
			comboVar1.setValue("");
			comboVar2.setValue("");

			if (listboxSubpopulation.getSelectedItem().getLabel().equals("all varieties")) {
				checkboxAllvarieties.setChecked(true);
			} else {
				checkboxAllvarieties.setChecked(false);
			}
		}
	}

	public void onselectVar1() {
		onchangedVar1();
	}

	private void checkMultipleVarnames(final Combobox combo) {

		String varname = combo.getValue();

		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");

		Collection colVars = null;
		if (this.radioUseVarname.isSelected())
			colVars = varietyfacade.getGermplasmsByName(varname, getDataset());
		else {
			colVars = new HashSet();
			colVars.add(varietyfacade.getGermplasmByAccession(varname, getDataset()));
		}

		if (colVars.size() > 1) {
			List listvars = new ArrayList();

			Iterator<Variety> itVar = colVars.iterator();
			while (itVar.hasNext()) {
				Variety var = itVar.next();
				listvars.add(var.getName().toUpperCase() + "  [" + var.getAccession() + "]");
			}
			;

			try {
				ListboxMessageBox.show("Select variety accession", "Multiple Varieties", listvars,
						new org.zkoss.zk.ui.event.EventListener() {
							@Override
							public void onEvent(Event e) throws Exception {

								if (e.getName().equals(ListboxMessageBox.ON_OK)) {
									Object selobj = ListboxMessageBox.getObjSel();
									AppContext.debug("mainselected " + selobj);
									if (selobj != null) {

										combo.setValue(selobj.toString());
									}
								}
							}
						});

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Listen("onChange = #comboVar1")
	public void onchangedVar1() {
		if (!comboVar1.getValue().isEmpty() || !comboVar2.getValue().isEmpty()) {
			checkboxAllvarieties.setChecked(false);
			listboxMyVarieties.setSelectedIndex(0);
			listboxSubpopulation.setSelectedIndex(0);
		}
		if (!comboVar1.getValue().isEmpty()) {
			checkMultipleVarnames(comboVar1);
		}
	}

	public void onselectVar2() {
		onchangedVar2();
	}

	@Listen("onChange = #comboVar2")
	public void onchangedVar2() {
		if (!comboVar1.getValue().isEmpty() || !comboVar2.getValue().isEmpty()) {
			checkboxAllvarieties.setChecked(false);
			listboxMyVarieties.setSelectedIndex(0);
			listboxSubpopulation.setSelectedIndex(0);
		}
		if (!comboVar2.getValue().isEmpty()) {
			checkMultipleVarnames(comboVar2);
		}

	}

	@Listen("onChange = #comboGene")
	public void onchangeGene() {
		onSelectGene();
	}

	@Listen("onSelect = #comboGene")
	public void onSelectGene() {
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
	 * Chromosome selected
	 */
	@Listen("onSelect = #selectChr")
	public void setchrlength() {

		try {

			AppContext.debug("Index listboxMyLocusList: " + listboxMyLocusList.getSelectedIndex() + " -> "
					+ listboxMyLocusList.getSelectedItem());

			if (selectChr.getValue().trim().isEmpty())
				return;
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, classGenotypefacade);

			listboxMySNPList.setSelectedIndex(0);
			comboGene.setValue("");

			if (selectChr.getValue().equals("whole genome")) {
				this.intStart.setValue(null);
				this.intStop.setValue(null);

				this.intStart.setDisabled(true);
				intStop.setDisabled(true);
				this.buttonSearch.setDisabled(true);

				buttonsDownloadNodisplay.setVisible(true);

				this.listboxSubpopulation.setSelectedIndex(0);
				listboxSubpopulation.setDisabled(true);
				this.comboGene.setValue("");
				comboGene.setDisabled(true);
				this.listboxMySNPList.setSelectedIndex(0);
				listboxMySNPList.setDisabled(true);

			} else {
				Integer length = getchrLength();
				labelLength.setValue(length + " bp");
				this.buttonSearch.setDisabled(false);
				this.intStart.setDisabled(false);
				intStop.setDisabled(false);

				buttonsDownloadNodisplay.setVisible(false);

				listboxSubpopulation.setDisabled(false);

				if (listboxReference.getSelectedItem().getLabel().equals(AppContext.getDefaultOrganism())) {
					comboGene.setDisabled(false);
					listboxMySNPList.setDisabled(false);
				}
			}

		} catch (Exception ex) {

			ex.printStackTrace();
		}
	}

	@Listen("onChange = #intStart")
	public void onchangedStart() {
		listboxMySNPList.setSelectedIndex(0);
		comboGene.setValue("");
	}

	@Listen("onChange = #intStop")
	public void onchangedStop() {
		listboxMySNPList.setSelectedIndex(0);
		comboGene.setValue("");
	}

	private Integer getchrLength() {
		return genotype.getFeatureLength(selectChr.getValue(), listboxReference.getSelectedItem().getLabel());
	}

	@Listen("onClick = #checkboxAlignIndels")
	public void onclickAlignIndels() {

		GenotypeQueryParams params = fillGenotypeQueryParams();
		params.setbAlignIndels(checkboxAlignIndels.isChecked());
		updateBiglistboxArray(params);
	}

	private void updateBiglistboxArray(GenotypeQueryParams params) {

		try {
			varianttable = genotype.fillGenotypeTable(new VariantAlignmentTableArraysImpl(), queryRawResult, params);
			queryResult = varianttable.getVariantStringData();
			mapVariety2Phyloorder = null;
			this.msgbox.setValue(varianttable.getMessage());

			if (checkboxIndel.isChecked() && !checkboxAlignIndels.isChecked())
				biglistboxArray.setColWidth("60px");
			else
				biglistboxArray.setColWidth("30px");

			AppContext.debug("updateBiglistboxArray: queryResult=" + queryResult.getListVariantsString().size() + " x "
					+ queryResult.getListPos().size());

			Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(queryResult,
					params);
			biglistboxArray.setMatrixRenderer(renderer);
			biglistboxModel = new Object2StringMultirefsMatrixModel(varianttable,
					params /* fillGenotypeQueryParams() */, varietyfacade.getMapId2Sample(getDataset()),
					gridBiglistheader);
			biglistboxModel.setHeaderRows(biglistboxRows, lastY);
			biglistboxArray.setModel(biglistboxModel); // strRef));

			biglistboxArray.setWidth("100%");
			gridBiglistheader.invalidate();

			this.tabTable.setSelected(true);

			// create new gff for
			urljbrowse = null;
			gfffile = null;

			if (tabJbrowse.isVisible())
				updateJBrowse(selectChr.getValue().trim(), intStart.getValue().toString(),
						intStop.getValue().toString(), this.comboGene.getValue());

			this.iframePhylotree.setSrc(null);

			Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) this.biglistboxArray
					.getModel();
			int poscol = -1;
			List listPos = (List) model.getHeadAt(0);
			List listPosition = new ArrayList();
			for (int i = 0; i < model.getColumnSize(); i++) {
				if (listPos.get(i).toString().isEmpty())
					continue;
				listPosition.add(listPos.get(i).toString());

			}
			listboxPosition.setModel(new SimpleListModel(listPosition));
			labelFilterResult.setValue("None .. " + queryResult.getListVariantsString().size() + " varieties");

			updateAlleleFrequencyChart();

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Exception in updateBiglistboxArray", Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onSelect = #tabMDS")
	public void onselectTabMDS() {
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
				varnames.add(
						((Variety) varietyfacade.getMapId2Variety(getDataset()).get(varid)).getName().toUpperCase());
			}
			listvarnames.addAll(varnames);
			listboxHighlightVariety.setModel(new SimpleListModel(listvarnames));

			onselectPhenotype();
		}
	}

	@Listen("onSelect =#listboxPhenotype")
	public void onselectPhenotype() {
		if (listboxPhenotype.getSelectedItem() != null)
			if (listboxPhenotype.getSelectedItem().getLabel().equals("Create phenotype list...")) {
				Executions.sendRedirect("_workspace.zul?from=variety&src=snp&phenotype=true");
				return;
			}

		if (this.tabTable.isSelected())
			onclickClearFilterAllele();
		else if (this.tabMDS.isSelected()) {
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
		} else if (this.tabHaplotype.isSelected()) {
			if (this.tabHaploGroupAlleles.isSelected()) {
				try {
					displayGroupallelePhenotpes();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
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

	private void onselectPhenotype(VariantTable newtable) {

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

		biglistboxModel = new Object2StringMultirefsMatrixModel(newtable, fillGenotypeQueryParams(),
				varietyfacade.getMapId2Sample(getDataset()), gridBiglistheader, mapVarid2Phenotype, sPhenotype);

		Object2StringMultirefsMatrixRenderer renderer = new Object2StringMultirefsMatrixRenderer(
				biglistboxModel.getData().getVariantStringData(), fillGenotypeQueryParams());
		biglistboxArray.setMatrixRenderer(renderer);
		biglistboxModel.setHeaderRows(biglistboxRows, lastY);
		this.biglistboxArray.setModel(biglistboxModel);
		gridBiglistheader.setModel(new BiglistRowheaderModel(biglistboxModel.getRowHeaderList(biglistboxRows)));

		onScrollYTable(0);

	}

	// ***************************** HANDLE ALLELE FILTER **************************

	@Listen("onSelect = #listboxPosition")
	public void onselectposition() {

		String selpos = listboxPosition.getSelectedItem().getLabel();
		if (!selpos.isEmpty()) {

			try {

				// get alleles for position
				// get position column index

				Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) this.biglistboxArray
						.getModel();
				int poscol = -1;
				List listPos = (List) model.getHeadAt(0);
				for (int i = 0; i < model.getColumnSize(); i++) {
					if (listPos.get(i).toString().equals(selpos)) {
						poscol = i;
						break;
					}
				}

				Set setAlleles = new TreeSet();
				for (int i = 0; i < model.getSize(); i++) {
					Object allele = ((Object[]) model.getElementAt(i).get(poscol))[poscol];
					setAlleles.add(allele.toString());
				}

				List listAllele = new ArrayList();
				listAllele.addAll(setAlleles);
				listboxAllele.setModel(new SimpleListModel(listAllele));
				listboxAllele.setSelectedIndex(0);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} else
			listboxAllele.setModel(new SimpleListModel(new ArrayList()));

	}

	@Listen("onSelect = #listboxSNPListAlleles")
	public void onSelectlistboxSNPListAlleles() {

		String selitem = listboxSNPListAlleles.getSelectedItem().getLabel();
		if (selitem.startsWith("create new list")) {
			Executions.sendRedirect("_workspace.zul?from=snp&src=snp");
		}

	}

	@Listen("onClick = #buttonFilterAllele")
	public void onclickFilterAllele() {

		try {

			String selpos = "";

			if (listboxPosition.getSelectedItem() != null)
				selpos = listboxPosition.getSelectedItem().getLabel();

			if (selpos.isEmpty()) {

				onselectPhenotype(varianttable);

			} else {

				BigDecimal pos = null;

				int selPosIdx = listboxPosition.getSelectedIndex();
				int poscolumnidx = -1;
				int headercolumnidx = -1;
				if (this.listboxPhenotype.getSelectedIndex() > 0) {
					headercolumnidx = selPosIdx - 1;
					poscolumnidx = selPosIdx;
				} else {
					headercolumnidx = selPosIdx - 1;
					poscolumnidx = selPosIdx - 1;
				}

				try {
					pos = BigDecimal.valueOf(Double.valueOf(selpos.replace(".00", "").replace(".0", "")));
				} catch (Exception ex) {
				}
				;

				String alleleFilter = listboxAllele.getSelectedItem().getLabel();

				boolean include = false;
				List listInclude = new ArrayList();
				List listVarids = new ArrayList();
				List listMismatch = new ArrayList();
				List listNames = new ArrayList();

				Object2StringMultirefsMatrixModel origModel = (Object2StringMultirefsMatrixModel) this.biglistboxArray
						.getModel();
				VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) origModel.getData();
				Object[][] varalleles = table.getVaralleles();

				for (int i = 0; i < origModel.getSize(); i++) {

					Object alleleTable = null;
					Object alleleTable2 = null;
					Object alleleTable3 = null;
					if (headercolumnidx < 5) {
						alleleTable = ((Object[]) origModel.getElementAt(i).get(selPosIdx))[selPosIdx];
					} else
						alleleTable = ((Object[]) origModel.getElementAt(i).get(poscolumnidx))[poscolumnidx];

					if (alleleTable.toString().equals(alleleFilter.toString())) {
						listInclude.add(varalleles[i]);
						listVarids.add(table.getVarid()[i]);
						listNames.add(table.getVarname()[i]);
						listMismatch.add(table.getVarmismatch()[i]);

					}
				}

				String filteredvaralleles[][] = new String[listInclude.size()][];
				Long filteredvarids[] = new Long[listInclude.size()];
				Double filteredvarmis[] = new Double[listInclude.size()];
				String filteredvarnames[] = new String[listInclude.size()];
				for (int ifil = 0; ifil < listInclude.size(); ifil++) {
					filteredvaralleles[ifil] = (String[]) listInclude.get(ifil);
					filteredvarids[ifil] = (Long) listVarids.get(ifil);
					filteredvarnames[ifil] = (String) listNames.get(ifil);
					filteredvarmis[ifil] = (Double) listMismatch.get(ifil);
				}

				VariantAlignmentTableArraysImpl newtable = new VariantAlignmentTableArraysImpl(table);
				newtable.setAllelestring(filteredvaralleles);
				newtable.setVarids(filteredvarids);
				newtable.setVarmismatch(filteredvarmis);
				newtable.setVarnames(filteredvarnames);

				onselectPhenotype(newtable);

				getSession().putValue("variantTable", newtable);

				String activefilters = labelFilterResult.getValue().split("\\.\\.")[0].replace("Active Filter:", "")
						.replace("None", "").trim();

				if (!activefilters.isEmpty())
					activefilters += ",";
				activefilters += selpos + "=" + alleleFilter;

				labelFilterResult.setValue(activefilters + " .. " + listInclude.size() + "/"
						+ queryRawResult.getListVariantsString().size() + " varieties");

				gridBiglistheader.setModel(new BiglistRowheaderModel(biglistboxModel.getRowHeaderList(biglistboxRows)));

				varFreqlines = null;

				haplofilename = null;
				this.tabHaploGroupAlleles.setDisabled(true);
				this.tabHaploTree.setDisabled(true);
				this.tabHaploAutogroups.setDisabled(true);

				gfffile = null;
				iframePhylotree.setSrc(null);

			}

			calculateAlleleFrequencies();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick = #buttonClearFilterAllele")
	public void onclickClearFilterAllele() {

		try {

			onselectPhenotype(varianttable);

			if (queryRawResult != null)
				if (queryRawResult.getListVariantsString() != null)
					labelFilterResult
							.setValue("None .. " + queryRawResult.getListVariantsString().size() + " varieties");
				else
					labelFilterResult.setValue("None ..");
			varFreqlines = calculateAlleleFrequencies();

			gfffile = null;
			haplofilename = null;
			this.tabHaploGroupAlleles.setDisabled(true);
			this.tabHaploTree.setDisabled(true);
			this.tabHaploAutogroups.setDisabled(true);
			iframePhylotree.setSrc(null);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onSelect = #tabMSA")
	public void onselectTabMSA() {

		iframeAlignment.invalidate();
	}

	@Listen("onSelect = #tabTableLarge")
	public void onselectTabTableLarge() {

		biglistboxArrayLarge.setMatrixRenderer(biglistboxArray.getMatrixRenderer());
		biglistboxArrayLarge.setModel(biglistboxArray.getModel());
		biglistboxArrayLarge.setVisible(true);

	}

	@Listen("onFocus =#comboVar1")
	public void onfocusVarieties1() {
		onfocusVarieties();
	}

	@Listen("onFocus =#comboVar2")
	public void onfocusVarieties2() {
		onfocusVarieties();
	}

	@Listen("onFocus =#listboxVarietyAlleleFilter")
	public void onfocusVarieties3() {
		onfocusVarieties();
	}

	@Listen("onFocus =#listboxSubpopulation")
	public void onfocusVarieties4() {
		onfocusVarieties();
	}

	@Listen("onFocus =#listboxPhenotype")
	public void onfocusVarieties5() {
		onfocusVarieties();
	}

	public void onfocusVarieties() {

		Set dataset = getDataset();
		Set setTmpDS = new HashSet(dataset);
		setTmpDS.removeAll(prevDataset);
		if (setTmpDS.isEmpty())
			return;
		prevDataset = dataset;
		List listsubs = new ArrayList();
		listsubs.add("");
		listsubs.add("all varieties");

		this.listboxMyVarieties.setSelectedIndex(0);
		this.comboVar1.setValue("");
		this.comboVar2.setValue("");

		listsubs.addAll(varietyfacade.getSubpopulations(dataset));
		this.listboxSubpopulation.setModel(new SimpleListModel(listsubs));
		listboxSubpopulation.setSelectedIndex(1);

		List listvars = null;
		if (this.radioUseAccession.isSelected()) {
			if (!dataset.contains("gq92") && !dataset.contains("wissuwa")) {
				listvars = varietyfacade.getAccessions(dataset);
			} else {
				listvars = varietyfacade.getVarietyNames(dataset);
				this.radioUseVarname.setSelected(true);
			}
		} else
			listvars = varietyfacade.getVarietyNames(dataset);

		AppContext.debug((listvars == null ? "listvars==null for " + dataset
				: "listvars=" + listvars.size() + " for " + dataset));

		java.util.List varnames = AppContext.createUniqueUpperLowerStrings(listvars, true, false);

		this.comboVar1.setModel(new SimpleListModel(varnames));
		this.comboVar2.setModel(new SimpleListModel(varnames));

		this.listboxVarietyAlleleFilter
				.setModel(new SimpleListModel(AppContext.createUniqueUpperLowerStrings(listvars, false, true)));
		listboxVarietyAlleleFilter.setSelectedIndex(0);

		listboxDatasetSnpOps.setVisible(dataset.contains(VarietyFacade.DATASET_SNP_ALL));

		msgbox.setValue(listvars.size() + (radioUseAccession.isSelected() ? " accessions" : " varieties") + ", "
				+ (listsubs.size() - 2) + " subpopulations");
		if (dataset.contains(VarietyFacade.DATASET_SNP_GOPAL92)) {
			this.listboxAutogroup.setSelectedIndex(0); // pamk
		} else {
			this.listboxAutogroup.setSelectedIndex(1); // calinski
		}

		Map mapUIsettings = varietyfacade.getUIforDataset(getDataset());
		labelExampleVariety.setValue((String) mapUIsettings.get("labelExampleVariety"));

		Set setPhenotype = varietyfacade.getPhenotypeDefinitions(dataset).keySet();
		java.util.List listPhenotype = new java.util.ArrayList();
		listPhenotype.add("");
		listPhenotype.addAll(setPhenotype);
		listboxPhenotype.setModel(new SimpleListModel(listPhenotype));
		this.listboxPhenotype.setSelectedIndex(0);

	}

	private boolean enableIndel() {
		Set selDataset = getDataset();
		for (GenotypeRunPlatform p : genotype.getGenotyperuns("indel")) {
			if (selDataset.contains(p.getDataset())) {
				return true;
			}
		}
		return false;

	}

	@Listen("onSelect =#listboxGenotyperun")
	public void onselectGenotyperun() {
		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		boolean enableIndel = enableIndel();

		if (enableIndel)
			this.checkboxIndel.setDisabled(false);
		else {
			this.checkboxIndel.setChecked(false);
			this.checkboxIndel.setDisabled(true);
		}

		boolean enableNonsyn = genotype.hasNonsyn(getVariantset());
		if (enableNonsyn) {
			radioAllSnps.setDisabled(false);
			radioNonsynHighlights.setDisabled(false);
			radioNonsynSnpsPlusSplice.setDisabled(false);

		} else {
			radioAllSnps.setSelected(true);
			radioAllSnps.setDisabled(true);
			radioNonsynHighlights.setDisabled(true);
			radioNonsynSnpsPlusSplice.setDisabled(true);
		}

		if (enableIndel) {
			checkboxShowAllRefAlleles.setDisabled(false);
			this.listboxReference.setDisabled(false);
		} else {

			this.checkboxShowAllRefAlleles.setChecked(false);
			checkboxShowAllRefAlleles.setDisabled(true);
			this.listboxReference.setSelectedIndex(0);
			this.listboxReference.setDisabled(true);

		}

	}

	// ********************************************** HANDLE JBROWSE TAB EVENTS
	// ********************************************

	public void openJbrowseTab(String url) {
		getSession().putValue("jbrowse", url);
		openNewTab("_exttab.zul?tab=jbrowse");
	}

	public void openNewTab(String url) {
		AppContext.debug("openning " + url + " in newtab");

		Executions.getCurrent().sendRedirect(url, "_blank");

	}

	/**
	 * JBrowse tab selected
	 */
	@Listen("onSelect = #tabJbrowse")
	public void onselectTabJbrowse() {
		onselectTabJbrowse(false);
	}

	public void onselectTabJbrowse(boolean newTab) {

		AppContext.debug("onselectTabJbrowse gfffile=" + gfffile + "\nurljbrowse=" + urljbrowse);

		if (urljbrowse != null) {

			if (urljbrowse.contains("BAM,") && urljbrowse.contains("SNP Coverage,")) {
				AppContext.debug("loading " + urljbrowse);

				if (newTab) {
					openJbrowseTab(urljbrowse);
				} else {
					msgJbrowse.setVisible(true);
					iframeJbrowse.setVisible(true);
				}
				msgJbrowse.setVisible(true);
				iframeJbrowse.setVisible(true);

				return;
			}
			;

			if (gfffile != null) {
				// same data, don't recreate gff
				return;
			}

			AppContext.debug("loading " + urljbrowse);

			Clients.showBusy("Loading JBrowse");
			if (tallJbrowse) {
				gfffile = "tmp_" + AppContext.createTempFilename() + ".gff";

				int jbrowsebp_margin = (intStop.getValue() - intStart.getValue()) * AppContext.getJBrowseMargin();
				int start = intStart.getValue() - jbrowsebp_margin;
				if (start < 0)
					start = 1;

				VariantStringData queryDisplayData = queryResult;

				if (queryDisplayData != null) {

					try {

						if (AppContext.showGenotypeTrack()) {

							List listGFF = new ArrayList();
							if (this.checkboxSNP.isChecked() && checkboxIndel.isChecked()) {
								List listGFFSNP = new ArrayList();
								List listGFFIndel = new ArrayList();

								Iterator<SnpsStringAllvars> itSnpstring = queryDisplayData.getListVariantsString()
										.iterator();
								while (itSnpstring.hasNext()) {
									SnpsStringAllvars snpstring = itSnpstring.next();
									if (snpstring instanceof IndelsStringAllvars)
										listGFFIndel.add(snpstring);
									else
										listGFFSNP.add(snpstring);
								}
								AppContext.debug("listGFFIndel.size=" + listGFFIndel.size() + ", listGFFSNP.size="
										+ listGFFSNP.size());
								listGFF.addAll(createSNPPointStringVarietyGFFFromTable(listGFFSNP, 1,
										BigDecimal.valueOf(start),
										BigDecimal.valueOf(intStop.getValue() + jbrowsebp_margin),
										radioColorAllele.isSelected()));
								listGFF.addAll(
										createIndelStringVarietyGFFFromTable(listGFFIndel, 1, BigDecimal.valueOf(start),
												BigDecimal.valueOf(intStop.getValue() + jbrowsebp_margin),
												radioColorAllele.isSelected() || !checkboxMismatchOnly.isChecked()));
							} else if (this.checkboxSNP.isChecked()) {
								listGFF = createSNPStringVarietyGFFFromTable(queryDisplayData.getListVariantsString(),
										1, BigDecimal.valueOf(start),
										BigDecimal.valueOf(intStop.getValue() + jbrowsebp_margin),
										radioColorAllele.isSelected());
							} else if (this.checkboxIndel.isChecked()) {
								listGFF = createIndelStringVarietyGFFFromTable(queryDisplayData.getListVariantsString(),
										1, BigDecimal.valueOf(start),
										BigDecimal.valueOf(intStop.getValue() + jbrowsebp_margin),
										radioColorAllele.isSelected());
							}

							writeGFF(listGFF, AppContext.getTempDir() + gfffile);

							AppContext.debug(AppContext.getTempDir() + gfffile + " done..");

							AppContext.waitForFile(AppContext.getTempDir() + gfffile);

							AppContext.resetTimer("write gff");
							AppContext.resetTimer("viewing " + urljbrowse.replace("GFF_FILE", gfffile));

							if (newTab) {
								try {
									urljbrowse = urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B")
											.replace("}", "%7D");
									openJbrowseTab(urljbrowse);
								} catch (Exception ex) {
									AppContext.debug(ex.getMessage());
								}
							} else {
								urljbrowse = urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B").replace("}",
										"%7D");
								iframeJbrowse.setSrc(urljbrowse);
								iframeJbrowse.invalidate();
								msgJbrowse.setVisible(true);
								iframeJbrowse.setVisible(true);
							}
						} else {

							AppContext.resetTimer("viewing " + urljbrowse);
							if (newTab) {
								try {
									urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
									openJbrowseTab(urljbrowse);
								} catch (Exception ex) {
									AppContext.debug(ex.getMessage());
								}
							} else {
								urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
								iframeJbrowse.setSrc(urljbrowse);
								iframeJbrowse.invalidate();
								msgJbrowse.setVisible(true);
								iframeJbrowse.setVisible(true);
							}
						}

						divMismatch.setVisible(false);
						divNucleotide.setVisible(false);
						divNucleotideIndels.setVisible(false);

						if (radioColorAllele.isSelected()) {
							if (this.checkboxIndel.isChecked())
								divNucleotideIndels.setVisible(true);
							else
								divNucleotide.setVisible(true);
						}
						if (this.radioColorMismatch.isSelected())
							divMismatch.setVisible(true);
					} catch (Exception ex) {
						ex.printStackTrace();
						Messagebox.show(ex.getMessage(), "Load JBrowse Exception", Messagebox.OK,
								Messagebox.EXCLAMATION);
					}
				} else {
					throw new RuntimeException("onselectTabJbrowse: queryResult!=null");
				}
			} else {

				if (newTab) {
					try {
						if (AppContext.showGenotypeTrack()) {
							AppContext.resetTimer("viewing "
									+ urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B").replace("}", "%7D"));
							urljbrowse = urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B").replace("}",
									"%7D");
							openJbrowseTab(urljbrowse);

						} else {
							AppContext.resetTimer("viewing " + urljbrowse.replace("{", "%7B").replace("}", "%7D"));
							urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
							openJbrowseTab(urljbrowse);
						}
					} catch (Exception ex) {
						AppContext.debug(ex.getMessage());
					}
				} else {
					if (AppContext.showGenotypeTrack()) {
						AppContext.resetTimer("viewing "
								+ urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B").replace("}", "%7D"));
						urljbrowse = urljbrowse.replace("GFF_FILE", gfffile).replace("{", "%7B").replace("}", "%7D");
						iframeJbrowse.setSrc(urljbrowse);
					} else {
						AppContext.resetTimer("viewing " + urljbrowse.replace("{", "%7B").replace("}", "%7D"));
						urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
						iframeJbrowse.setSrc(urljbrowse);
					}
					iframeJbrowse.invalidate();
					//
					msgJbrowse.setVisible(true);
					iframeJbrowse.setVisible(true);
				}
			}

			Clients.clearBusy();

			AppContext.debug("gfffile=" + gfffile);
		} else if (newTab) {
			openJbrowseTab(urljbrowse);
		}

		AppContext.debug("onselectTabJbrowse gfffile=" + gfffile);

	}

	/**
	 * JBrowse using phylotree ordering, visible only after Phylotree is computed
	 */
	@Listen("onSelect = #tabJBrowsePhylo")
	public void onselectTabJbrowsePhylo() {

		if (phyloid == null)
			return;

		Clients.showBusy("Calculating JBrowsePhylo");
		if (urljbrowsephylo == null) {

			if (tallJbrowse) {
				updateJBrowsePhylo(selectChr.getValue().trim(), intStart.getValue().toString(),
						intStop.getValue().toString(), "");
				genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

				if (gfffile == null)
					gfffile = "tmp_" + AppContext.createTempFilename() + ".gff";

				Map mapVariety2Order = (Map) getSession().getAttribute("phyloorder");

				List newpagelist;

				int jbrowsebp_margin = (intStop.getValue() - intStart.getValue()) * AppContext.getJBrowseMargin();

				newpagelist = this.queryResult.getListVariantsString();

				List listGFF = createSNPStringVarietyGFF(newpagelist, mapVariety2Order, 1,
						BigDecimal.valueOf(intStart.getValue() - jbrowsebp_margin),
						BigDecimal.valueOf(intStop.getValue() + jbrowsebp_margin), false);

				String phylofile = AppContext.getTempDir() + gfffile + ".phylo.gff";

				AppContext.resetTimer("writing  " + phylofile);
				writeGFF(listGFF, phylofile);
			}
		}

		if (urljbrowsephylo != null) {

			AppContext.debug("tabJbrowsePhylo selected, urljbrowsephylo=" + urljbrowsephylo);

			iframeJbrowsePhylo.setSrc(urljbrowsephylo.replace("GFF_FILE", gfffile));
			msgJbrowsePhylo.setVisible(true);
			iframeJbrowsePhylo.setVisible(true);

		}
		Clients.clearBusy();

	}

	private void updateJBrowsePhylo(String chr, String start, String end, String locus) {
		// return;

		chr = chr.trim();
		String chrpad = chr;
		if (chrpad.length() == 1)
			chrpad = "0" + chr;
		if (!chrpad.startsWith("chr"))
			chrpad = "chr" + chrpad;

		if (iframeJbrowsePhylo == null)
			throw new RuntimeException("jbrowsephylo==null");

		String displaymode = "%22displayMode%22:%22normal%22,%22maxHeight%22:%222000%22";

		if (tallJbrowse) {
			iframeJbrowsePhylo.setStyle("width:1500px;height:1000px;border:0px inset;");
			displaymode = "%22displayMode%22:%22compact%22,%22maxHeight%22:%222000%22";
		}

		String rendertype = "";

		if (locus.isEmpty())
			msgJbrowsePhylo.setValue("Chromosome " + chr + " [" + start + ".." + end + "]");
		else
			msgJbrowsePhylo.setValue(locus + "  Chromosome " + chr + " [" + start + ".." + end + "]");

		iframeJbrowsePhylo.setScrolling("yes");
		iframeJbrowsePhylo.setAlign("left");

		String urltemplate = "..%2F..%2Ftemp%2FGFF_FILE.phylo.gff";

		String snp3kcore = "";
		if (checkboxSNP.isChecked()) {
			if (getVariantset().contains("3kcore"))
				snp3kcore = "msu7coresnpsv2%2C";
		}
		if (checkboxIndel.isChecked())
			snp3kcore += "msu7indelsv2%2C";
		String tracks = AppContext.getJBrowseDefaulttracks(getVariantset()) + "," + snp3kcore;

		if (tallJbrowse) {
			// for all varieties
			rendertype = "%22JBrowse%2FView%2FTrack%2FAlignments2Variety%22";

			urljbrowsephylo = AppContext.getJbrowseDir() + "/?loc=" + chrpad + AppContext.getJbrowseContigSuffix() + ":"
					+ start + ".." + end + "&tracks=" + tracks
					+ "SNP%20Genotyping&addStores={%22url%22%3A{%22type%22%3A%22JBrowse%2FStore%2FSeqFeature%2FGFF3Variety%22%2C%22urlTemplate%22%3A%22"
					+ urltemplate + "%22}}&addTracks=[{%22label%22%3A%22SNP%20Genotyping%22%2C%22type%22%3A"
					+ rendertype + "%2C%22store%22%3A%22url%22%2C%20" + displaymode
					+ ",%22metadata%22:{%22Description%22%3A%20%22Varieties%20SNP%20Genotyping%20in%20the%20region.%20Each%20row%20is%20a%20variety.%20Red%20means%20there%20is%20variation%20with%20the%20reference%22}"
					+ ",%22fmtDetailValue_Name%22%3A%20%22function(name)%20%7B%20return%20%27%3Ca%20target%3D%5C%22variety%5C%22%20href%3D%5C%22/"
					+ AppContext.getHostDirectory()
					+ "/_variety.zul%3Fname%3D%27%2Bname%2B%27%5C%22%3E%27%2Bname%2B%27%3C%2Fa%3E%27%3B%20%7D%22%20"
					+ "%2C%20%22style%22%3A{%22showLabels%22%3A%22false%22%2C%22textFont%22%3A%22normal%208px%20Univers%2CHelvetica%2CArial%2Csans-serif%22}}]&highlight=";

		}

	}

	/**
	 * Class to write GFF file from SNP for use in JBrowse
	 * 
	 * @author lmansueto
	 *
	 */
	private class GFF {
		private String left;
		private String right;
		private long start;
		private long end;
		private long position;

		public GFF(String left, String right, long start, long end, long position) {
			super();
			this.left = left;
			this.right = right;
			this.start = start;
			this.end = end;
			this.position = position;
		}

		@Override
		public String toString() {

			return this.left + start + "\t" + end + this.right;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}

		Long getPosition() {
			return this.position;
		}

		String getLeft() {
			return this.left;
		}

		String getRight() {
			return this.right;
		}

	}

	/**
	 * Sort GFF by start (to be used by JBrowse all variety track)
	 * 
	 * @author LMansueto
	 *
	 */
	private class GFFStartComparator implements Comparator {

		@Override
		public int compare(Object o1, Object o2) {

			GFF snp1 = (GFF) o1;
			GFF snp2 = (GFF) o2;

			if (snp1.getStart() < snp2.getStart())
				return -1;
			if (snp1.getStart() > snp2.getStart())
				return 1;
			if (snp1.getEnd() < snp2.getEnd())
				return 1;
			if (snp1.getEnd() > snp2.getEnd())
				return -1;
			return 0;
		}
	}

	/**
	 * calculate position boundary
	 * 
	 * @param setPositions
	 * @param start
	 * @param end
	 * @return
	 */
	private Map<Long, long[]> createMapGFFPosBounds(Set<Long> setPositions, BigDecimal start, BigDecimal end) {

		Iterator<Long> itPos = setPositions.iterator();

		Map<Long, Long> mapPrevPos = new java.util.HashMap<Long, Long>();
		Map<Long, Long> mapNextPos = new java.util.HashMap<Long, Long>();

		long lPos[] = new long[setPositions.size()];
		char cRef[] = new char[setPositions.size()];

		int iPosCount = 0;
		// BigDecimal prevPos=start;
		StringBuffer bufPos = new StringBuffer();
		long prevpos = start.longValue();
		if (itPos.hasNext()) {
			// SnpsAllvarsPos pos = itPos.next();
			// long curpos = pos.getPos().longValue();

			long curpos = itPos.next();

			lPos[iPosCount] = curpos;
			// cRef[iPosCount]=pos.getRefnuc().charAt(0);

			mapPrevPos.put(curpos, prevpos);
			mapNextPos.put(prevpos, curpos);
			prevpos = curpos;
			bufPos.append(curpos).append(",");
		}

		Map<Long, long[]> mapPos2Bounds = new java.util.HashMap<Long, long[]>();

		iPosCount++;
		while (itPos.hasNext()) {
			// SnpsAllvarsPos pos = itPos.next();
			// long curpos = pos.getPos().longValue();

			long curpos = itPos.next();

			mapPrevPos.put(curpos, prevpos);
			mapNextPos.put(prevpos, curpos);

			lPos[iPosCount] = curpos;
			// if(pos.getRefnuc()==null || pos.getRefnuc().isEmpty())
			// cRef[iPosCount]=' ';
			// else cRef[iPosCount]=pos.getRefnuc().charAt(0);

			// cRef[iPosCount]=pos.getRefnuc().charAt(0);

			mapPos2Bounds.put(prevpos,
					new long[] { (mapPrevPos.get(prevpos) + prevpos) / 2, (mapNextPos.get(prevpos) + prevpos) / 2 });

			prevpos = curpos;
			bufPos.append(curpos).append(",");
			iPosCount++;

		}
		mapNextPos.put(prevpos, end.longValue());
		mapPos2Bounds.put(prevpos,
				new long[] { (mapPrevPos.get(prevpos) + prevpos) / 2, (mapNextPos.get(prevpos) + prevpos) / 2 });

		return mapPos2Bounds;

	}

	/**
	 * get order of varities for display in JBrowse
	 * 
	 * @return
	 */
	private Map createMapVar2Order() {

		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		Map<String, Variety> mapName2Var = varietyfacade.getMapVarname2Variety(getDataset());
		Map<BigDecimal, Integer> mapVar2Order = new HashMap();
		Object2StringMultirefsMatrixModel model = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		for (int i = 0; i < model.getSize(); i++) {
			Object varname = ((Object[]) model.getElementAt(i).get(0))[0];
			mapVar2Order.put(mapName2Var.get(varname).getVarietyId(), i);
		}
		return mapVar2Order;
	}

	/**
	 * convert GFF position to a point (for indel display)
	 * 
	 * @param listNPB
	 * @return
	 */
	private List convertGFFReferencePosPoint(List listNPB) {

		List newGFF = null;
		if (queryResult.isNipponbareReference()) {
			newGFF = listNPB;
		} else {
			newGFF = new ArrayList();

			Map mapMSU7Pos2ConvertedPos = queryResult.getMapMSU7Pos2ConvertedPos();

			String newSrc = this.selectChr.getValue();
			String org = this.listboxReference.getSelectedItem().getLabel();

			/*
			 * if(org.equals(AppContext.getDefaultOrganism())) newSrc +=
			 * AppContext.getJbrowseContigSuffix(); //"|msu7"; else
			 * if(org.equals("IR64-21")) newSrc += "|ir6421v1"; else if(org.equals("93-11"))
			 * newSrc += "|9311v1"; else if(org.equals("DJ123")) newSrc += "|dj123v1"; else
			 * if(org.equals("Kasalath")) newSrc += "|kasv1";
			 */

			Set<Long> setPositions = new TreeSet();
			Iterator<GFF> itGFF = listNPB.iterator();
			while (itGFF.hasNext()) {
				GFF npbGFF = itGFF.next();

				if (npbGFF.getStart() != npbGFF.getPosition().longValue()) {
					AppContext.info("GFF start " + npbGFF.getStart() + "!= GFF position " + npbGFF.getPosition());
					// continue;
				}

				MultiReferenceConversion refpos = (MultiReferenceConversion) mapMSU7Pos2ConvertedPos
						.get(BigDecimal.valueOf(npbGFF.getPosition()));
				if (refpos == null)
					continue;

				Long newpos = refpos.getToPosition().longValue();
				Long newEnd = newpos + (npbGFF.getEnd() - npbGFF.getStart());

				String npbleft[] = npbGFF.getLeft().split("\t");
				String newRight = npbGFF.getRight()
						.replace("Position=" + npbGFF.getPosition().longValue(), "Position=" + newpos).replace("\n", "")
						+ ";Contig=" + refpos.getToContig() + ";Organism=" + refpos.getToOrganism() + ";NPBPosition="
						+ npbGFF.getPosition().longValue() + ";NPBContig=" + npbleft[0] + "\n";
				GFF convertedGFF = new GFF(newSrc + "\t" + npbleft[1] + "\t" + npbleft[2] + "\t", newRight, newpos,
						newEnd, newpos);
				newGFF.add(convertedGFF);

			}

		}
		return newGFF;

	}

	/**
	 * convert GFF position to a point (for SNP display)
	 * 
	 * @param listNPB
	 * @return
	 */

	private List convertGFFReferencePosInterval(List listNPB) {

		List newGFF = null;
		if (queryResult.isNipponbareReference()) {
			newGFF = listNPB;
		} else {
			newGFF = new ArrayList();

			Map mapMSU7Pos2ConvertedPos = queryResult.getMapMSU7Pos2ConvertedPos();

			Set<Long> setPositions = new TreeSet();
			Iterator<GFF> itGFF = listNPB.iterator();
			while (itGFF.hasNext()) {
				GFF npbGFF = itGFF.next();
				MultiReferenceConversion refpos = (MultiReferenceConversion) mapMSU7Pos2ConvertedPos
						.get(BigDecimal.valueOf(npbGFF.getPosition()));
				// MultiReferencePosition refpos =
				// (MultiReferencePosition)mapMSU7Pos2ConvertedPos.get(BigDecimal.valueOf(
				// npbGFF.getPosition() ));
				if (refpos == null)
					continue;
				setPositions.add(refpos.getToPosition().longValue());
			}

			Map<Long, long[]> mapPos2Bounds = createMapGFFPosBounds(setPositions,
					BigDecimal.valueOf(Long.valueOf(intStart.getValue())),
					BigDecimal.valueOf(Long.valueOf(intStop.getValue())));

			String newSrc = this.selectChr.getValue();
			String org = this.listboxReference.getSelectedItem().getLabel();

			/*
			 * if(org.equals(AppContext.getDefaultOrganism())) newSrc +=
			 * AppContext.getJbrowseContigSuffix(); //"|msu7"; else
			 * if(org.equals("IR64-21")) newSrc += "|ir6421v1"; else if(org.equals("93-11"))
			 * newSrc += "|9311v1"; else if(org.equals("DJ123")) newSrc += "|dj123v1"; else
			 * if(org.equals("Kasalath")) newSrc += "|kasv1";
			 */

			Iterator<GFF> itGFF2 = listNPB.iterator();
			while (itGFF2.hasNext()) {
				GFF npbGFF = itGFF2.next();
				MultiReferenceConversion refpos = (MultiReferenceConversion) mapMSU7Pos2ConvertedPos
						.get(BigDecimal.valueOf(npbGFF.getPosition()));

				if (refpos == null)
					continue;

				String npbleft[] = npbGFF.getLeft().split("\t");
				String newRight = npbGFF.getRight()
						.replace("Position=" + npbGFF.getPosition().longValue(),
								"Position=" + refpos.getToPosition().longValue())
						.replace("\n", "") + ";Contig=" + refpos.getToContig() + ";Organism=" + refpos.getToOrganism()
						+ ";NPBPosition=" + npbGFF.getPosition().longValue() + ";NPBContig=" + npbleft[0] + "\n";

				long[] bounds = mapPos2Bounds.get(refpos.getToPosition().longValue());
				GFF convertedGFF = new GFF(newSrc + "\t" + npbleft[1] + "\t" + npbleft[2] + "\t", newRight, bounds[0],
						bounds[1], refpos.getToPosition().longValue());
				newGFF.add(convertedGFF);
				// AppContext.debug( npbGFF + " --> " + convertedGFF);

			}
		}

		return newGFF;

	}

	private List createSNPStringVarietyGFFFromTable(List<SnpsStringAllvars> listSNPs, int bpgap, BigDecimal start,
			BigDecimal end, boolean showAll) {
		return createSNPStringVarietyGFF(listSNPs, createMapVar2Order(), bpgap, start, end, showAll);
	}

	/**
	 * 
	 * @param listSNPs  SNPString for all variety
	 * @param var2order
	 * @param bpgap
	 * @param start
	 * @param end
	 * @param showAll   showAll alleles
	 * @return
	 */
	private List createSNPStringVarietyGFF(List<SnpsStringAllvars> listSNPs, Map var2order, int bpgap, BigDecimal start,
			BigDecimal end, boolean showAll) {

		VariantStringData queryDisplayData = queryResult;

		List listSnpPos = queryDisplayData.getListPos(); // genotype.getSnpsposlist() ;
		Iterator<SnpsAllvarsPos> itPos = listSnpPos.iterator();

		Map<Long, Long> mapPrevPos = new java.util.HashMap<Long, Long>();
		Map<Long, Long> mapNextPos = new java.util.HashMap<Long, Long>();

		long lPos[] = new long[listSnpPos.size()];
		char cRef[] = new char[listSnpPos.size()];

		int iPosCount = 0;
		// BigDecimal prevPos=start;
		StringBuffer bufPos = new StringBuffer();
		long prevpos = start.longValue();
		if (itPos.hasNext()) {
			SnpsAllvarsPos pos = itPos.next();
			long curpos = pos.getPosition().longValue();

			lPos[iPosCount] = curpos;
			cRef[iPosCount] = pos.getRefnuc().charAt(0);

			mapPrevPos.put(curpos, prevpos);
			mapNextPos.put(prevpos, curpos);
			prevpos = curpos;
			bufPos.append(curpos).append(",");
		}

		Map<Long, long[]> mapPos2Bounds = new java.util.HashMap<Long, long[]>();

		iPosCount++;
		while (itPos.hasNext()) {
			SnpsAllvarsPos pos = itPos.next();
			long curpos = pos.getPosition().longValue();
			mapPrevPos.put(curpos, prevpos);
			mapNextPos.put(prevpos, curpos);

			lPos[iPosCount] = curpos;
			if (pos.getRefnuc() == null || pos.getRefnuc().isEmpty())
				cRef[iPosCount] = ' ';
			else
				cRef[iPosCount] = pos.getRefnuc().charAt(0);

			// cRef[iPosCount]=pos.getRefnuc().charAt(0);

			mapPos2Bounds.put(prevpos,
					new long[] { (mapPrevPos.get(prevpos) + prevpos) / 2, (mapNextPos.get(prevpos) + prevpos) / 2 });

			prevpos = curpos;
			bufPos.append(curpos).append(",");
			iPosCount++;

		}
		mapNextPos.put(prevpos, end.longValue());
		mapPos2Bounds.put(prevpos,
				new long[] { (mapPrevPos.get(prevpos) + prevpos) / 2, (mapNextPos.get(prevpos) + prevpos) / 2 });

		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		Map<BigDecimal, Variety> mapId2Variety = varietyfacade.getMapId2Variety(getDataset());

		java.util.List listGFF = new java.util.ArrayList();

		try {

			Iterator<SnpsStringAllvars> itsnpstring = listSNPs.iterator();

			String ref = listboxReference.getSelectedItem().getLabel();
			String contigsuff = "";
			if (ref.equals(Organism.REFERENCE_9311))
				contigsuff = "|9311v1";
			else if (ref.equals(Organism.REFERENCE_DJ123))
				contigsuff = "|dj123v1";
			else if (ref.equals(Organism.REFERENCE_IR64))
				contigsuff = "|ir6421v1";
			else if (ref.equals(Organism.REFERENCE_KASALATH))
				contigsuff = "|kasv1";

			while (itsnpstring.hasNext()) {
				SnpsStringAllvars snpvars = itsnpstring.next();

				if (!var2order.containsKey(snpvars.getVar()))
					continue;

				Set setposAcceptor = snpvars.getAcceptorPosset();
				Set setposDonor = snpvars.getDonorPosset();

				// Set setNonsyn = snpvars.getNonsynPosset();
				Set setNonsyn = snpvars.getNonsynPosset();

				// AppContext.debug(snpvars.getVar() + " setNonsyn=" + setNonsyn );

				Map<Position, Character> mapPos2allele2 = snpvars.getMapPos2Allele2();

				Long intchr = snpvars.getChr();
				String chr = null;
				if (intchr == null) {
					chr = snpvars.getContig(); // .getChr().toString();
					try {
						intchr = Long.valueOf(chr);
						if (intchr < 10)
							chr = "chr0" + intchr;
						else
							chr = "chr" + intchr;
					} catch (Exception ex) {

					}
				} else {
					if (intchr < 10)
						chr = "chr0" + intchr;
					else
						chr = "chr" + intchr;
				}

				chr = chr + contigsuff;

				/*
				 * if(chr.length()==1) chr= "chr0" + chr + AppContext.getJbrowseContigSuffix();
				 * //"|msu7"; else chr= "chr" + chr + AppContext.getJbrowseContigSuffix();
				 * //"|msu7";
				 */

				String order = var2order.get(snpvars.getVar()).toString();
				Variety var = mapId2Variety.get(snpvars.getVar());
				if (var == null)
					throw new RuntimeException(snpvars.getVar() + " not in mapId2Variety");

				String splicestr = "";

				String colormode = "";
				if (this.radioColorMismatch.isSelected())
					colormode = ";colormode=1";
				else
					colormode = ";colormode=0";

				for (int iPos = 0; iPos < listSnpPos.size(); iPos++) {

					char charAtPos = snpvars.getVarnuc().substring(iPos, iPos + 1).charAt(0);
					String synstr = "";

					if (setNonsyn != null) {
						if (setNonsyn.contains(listSnpPos.get(iPos)))
							synstr = ";Synonymous=0";
						else {
							synstr = ";Synonymous=1";
							// AppContext.debug("synonymous snpIdx=" + iPos );
						}
					}
					String al2 = "";
					if (mapPos2allele2 != null) {
						Character allele2 = mapPos2allele2.get(listSnpPos.get(iPos));
						if (allele2 != null)
							al2 = "/" + allele2;
					}

					if (showAll || (charAtPos != '0' && cRef[iPos] != charAtPos)) {

						splicestr = "";

						if (setposAcceptor != null) {
							if (setposAcceptor.contains(BigDecimal.valueOf(lPos[iPos])))
								splicestr = ";Acceptor=1";
							else
								splicestr = ";Acceptor=0";
						}

						if (setposDonor != null) {
							if (setposDonor.contains(BigDecimal.valueOf(lPos[iPos])))
								splicestr += ";Donor=1";
							else
								splicestr += ";Donor=0";
						}

						String ismissing = ";Missing=0";
						if (charAtPos == '?')
							ismissing = ";Missing=1";

						BigDecimal snpid = ((SnpsAllvarsPos) listSnpPos.get(iPos)).getSnpFeatureId();

						String line_right = "\t.\t.\t.\tName=" + var.getName() +
						// ";ID=VAR" + var.getVarietyId() + "-CHR" + snpvars.getChr() + "-" + lPos[iPos]
						// +
								";ID=VAR" + var.getVarietyId() + "-SNP" + snpid + // + "-" + lPos[iPos] +
								";AlleleRef=" + cRef[iPos] + ";AlleleAlt=" + charAtPos + al2 + ";Position=" + lPos[iPos]
								+ synstr + splicestr + colormode + ismissing + ";order=" + order + "\n";

						String datasrc = "IRIC";
						if (var.getDataset() != null) {
							if (var.getDataset().equals(VarietyFacade.DATASET_SNP_HDRA))
								datasrc = "HDRA";
							else if (var.getDataset().equals(VarietyFacade.DATASET_SNP_GOPAL92))
								datasrc = "GQ92";
							else
								datasrc = var.getDataset().toUpperCase();
						}

						long bounds[] = mapPos2Bounds.get(lPos[iPos]);
						listGFF.add(new GFF(chr + "\t" + datasrc + "\tsnp\t", line_right, bounds[0], bounds[1],
								lPos[iPos]));
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return listGFF;
	}

	/**
	 * Display SNP as points in JBrowse
	 * 
	 * @param listSNPs
	 * @param bpgap
	 * @param start
	 * @param end
	 * @param showAll
	 * @return
	 */
	private List createSNPPointStringVarietyGFFFromTable(List<SnpsStringAllvars> listSNPs, int bpgap, BigDecimal start,
			BigDecimal end, boolean showAll) {
		return createSNPPointStringVarietyGFF(listSNPs, createMapVar2Order(), bpgap, start, end, showAll);
	}

	private List createSNPPointStringVarietyGFF(List<SnpsStringAllvars> listSNPs, Map var2order, int bpgap,
			BigDecimal start, BigDecimal end, boolean showAll) {

		VariantStringData queryDisplayData = queryResult;

		// List listSnpPos = queryRawResult.getListPos(); // genotype.getSnpsposlist() ;
		List listSnpPos = queryDisplayData.getSnpstringdata().getListPos(); // genotype.getSnpsposlist() ;
		Iterator<SnpsAllvarsPos> itPos = listSnpPos.iterator();

		long lPos[] = new long[listSnpPos.size()];
		char cRef[] = new char[listSnpPos.size()];

		int iPosCount = 0;
		long prevpos = start.longValue();
		while (itPos.hasNext()) {
			SnpsAllvarsPos pos = itPos.next();
			long curpos = pos.getPosition().longValue();
			lPos[iPosCount] = curpos;
			if (!pos.getRefnuc().isEmpty())
				cRef[iPosCount] = pos.getRefnuc().charAt(0);
			else
				cRef[iPosCount] = ' ';
			iPosCount++;
		}

		Map mapPos2NonsynAlleles = queryDisplayData.getSnpstringdata().getMapPos2NonsynAlleles();
		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		Map<BigDecimal, Variety> mapId2Variety = varietyfacade.getMapId2Variety(getDataset());

		java.util.List listGFF = new java.util.ArrayList();

		try {

			Iterator<SnpsStringAllvars> itsnpstring = listSNPs.iterator();

			while (itsnpstring.hasNext()) {
				SnpsStringAllvars snpvars = itsnpstring.next();

				Set setposNonsyn = snpvars.getNonsynPosset();

				Set setposAcceptor = snpvars.getAcceptorPosset();
				Set setposDonor = snpvars.getDonorPosset();

				Map<Position, Character> mapPos2allele2 = snpvars.getMapPos2Allele2();

				String chr = snpvars.getChr().toString();
				if (chr.length() == 1)
					chr = "chr0" + chr + AppContext.getJbrowseContigSuffix(); // "|msu7";
				else
					chr = "chr" + chr + AppContext.getJbrowseContigSuffix(); // "|msu7";

				String order = var2order.get(snpvars.getVar()).toString();
				Variety var = mapId2Variety.get(snpvars.getVar());
				if (var == null)
					throw new RuntimeException(snpvars.getVar() + " not in mapId2Variety");

				String colormode = "";
				if (this.radioColorMismatch.isSelected())
					colormode = ";colormode=1";
				else
					colormode = ";colormode=0";

				for (int iPos = 0; iPos < listSnpPos.size(); iPos++) {

					Integer snpIdx = iPos;
					char charAtPos = snpvars.getVarnuc().substring(snpIdx, snpIdx + 1).charAt(0);

					String synstr = "";

					if (mapPos2NonsynAlleles != null) {
						Set setnonsyn = (Set) mapPos2NonsynAlleles.get(listSnpPos.get(snpIdx));
						if (setnonsyn == null || !setnonsyn.contains(charAtPos)) {
							synstr = ";Synonymous=1";
							// AppContext.debug("synonymous mergedidx=" + iPos + ", snpidx=" + snpIdx);
						} else
							synstr = ";Synonymous=0";
					}

					String al2 = "";
					if (mapPos2allele2 != null) {
						Character allele2 = mapPos2allele2.get(listSnpPos.get(snpIdx));
						if (allele2 != null)
							al2 = "/" + allele2;
					}

					if (showAll || (charAtPos != '0' && cRef[iPos] != charAtPos)) {

						String splicestr = "";
						if (setposAcceptor != null) {
							if (setposAcceptor.contains(BigDecimal.valueOf(lPos[iPos])))
								splicestr = ";Acceptor=1";
							else
								splicestr = ";Acceptor=0";
						}

						if (setposDonor != null) {
							if (setposDonor.contains(BigDecimal.valueOf(lPos[iPos])))
								splicestr += ";Donor=1";
							else
								splicestr += ";Donor=0";
						}

						String line_right = "\t.\t.\t.\tName=" + var.getName() + ";ID=VAR" + var.getVarietyId() + "-CHR"
								+ snpvars.getChr() + "-" + lPos[iPos] + ";AlleleRef=" + cRef[iPos] + ";AlleleAlt="
								+ charAtPos + al2 + ";Position=" + lPos[iPos] + synstr + splicestr + colormode
								+ ";order=" + order + "\n";

						String datasrc = "IRIC";
						if (var.getDataset() != null) {
							// && var.getDataset().equals(VarietyFacade.DATASET_SNP_HDRA)) datasrc="HDRA";
							datasrc = var.getDataset().toUpperCase();
						}

						// AppContext.debug("snpidx=" + iPos + ", pos=" +lPos[iPos] + ", strref="+
						// cRef[iPos] + ", gff=" + line_right);

						listGFF.add(new GFF(chr + "\t" + datasrc + "\tsnp\t", line_right, lPos[iPos], lPos[iPos],
								lPos[iPos]));
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return listGFF;
	}

	/**
	 * Display Indels as intervals in JBrowse
	 * 
	 * @param listSNPs
	 * @param bpgap
	 * @param start
	 * @param end
	 * @param showAll
	 * @return
	 */
	private List createIndelStringVarietyGFFFromTable(List listSNPs, int bpgap, BigDecimal start, BigDecimal end,
			boolean showAll) {
		return createIndelStringVarietyGFF(listSNPs, createMapVar2Order(), bpgap, start, end, showAll);
	}

	private List createIndelStringVarietyGFF(List listSNPs, Map var2order, int bpgap, BigDecimal start, BigDecimal end,
			boolean showAll) {

		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		Map<BigDecimal, Variety> mapId2Variety = varietyfacade.getMapId2Variety(getDataset());
		java.util.List listGFF = new java.util.ArrayList();

		try {

			Iterator<IndelsStringAllvars> itsnpstring = listSNPs.iterator();

			while (itsnpstring.hasNext()) {
				IndelsStringAllvars snpvars = itsnpstring.next();

				Set setposNonsyn = snpvars.getNonsynPosset();
				Set setposAcceptor = snpvars.getAcceptorPosset();
				Set setposDonor = snpvars.getDonorPosset();

				Map<Position, Character> mapPos2allele2 = snpvars.getMapPos2Allele2();

				// long longsnpvar = snpvars.getVar().longValueExact();
				// long longsnppos = snpvars.getPos().longValueExact();

				String chr = "";
				if (snpvars.getChr() == null) {
					chr = snpvars.getContig() + AppContext.getJbrowseContigSuffix();
				} else {
					chr = snpvars.getChr().toString();
					if (chr.length() == 1)
						chr = "chr0" + chr + AppContext.getJbrowseContigSuffix(); // "|msu7";
					else
						chr = "chr" + chr + AppContext.getJbrowseContigSuffix(); // "|msu7";
				}

				String order = var2order.get(snpvars.getVar()).toString();
				Variety var = mapId2Variety.get(snpvars.getVar());
				if (var == null)
					throw new RuntimeException(snpvars.getVar() + " not in mapId2Variety");

				String colormode = "";
				if (this.radioColorMismatch.isSelected())
					colormode = ";colormode=1";
				else
					colormode = ";colormode=0";

				Map mapPos2Indels = snpvars.getMapPos2Indels();
				Map<BigDecimal, IndelsAllvarsPos> mapIndelId2Indel = queryRawResult.getIndelstringdata()
						.getMapIndelId2Indel();
				Iterator<Position> itPos = mapPos2Indels.keySet().iterator();
				while (itPos.hasNext()) {
					Position pos = itPos.next();
					IndelsAllvars indels = (IndelsAllvars) mapPos2Indels.get(pos);

					String type = "";
					int allele1len = 0;
					String allele1 = "";
					if (indels.getAllele1() != null) {
						IndelsAllvarsPos indel = mapIndelId2Indel.get(indels.getAllele1());

						int indeltype = IndelStringHDF5nRDBMSHybridService.getIndelType(indel.getInsString());

						switch (indeltype) {

						case VariantStringService.INDELTYPE_DELETION:
							allele1 = "del " + indel.getInsString();
							allele1len = Integer.valueOf(indel.getInsString());
							type = "deletion";
							break;
						case VariantStringService.INDELTYPE_EXTENDDELETION:
							allele1 = "del " + indel.getInsString();
							allele1len = Integer.valueOf(indel.getInsString());
							type = "deletion";
							break;
						case VariantStringService.INDELTYPE_SNP:
							allele1 = "snp ->" + indel.getInsString();
							allele1len = 1;
							type = "snp";
							break;
						case VariantStringService.INDELTYPE_SUBSTITUTION:
							allele1 = "sub ->" + indel.getInsString();
							allele1len = indel.getInsString().length();
							type = "substitution";
							break;
						case VariantStringService.INDELTYPE_REFERENCE:
							allele1 = "ref";
							allele1len = 1;
							type = "reference";
							if (!showAll)
								continue;
							break;
						case VariantStringService.INDELTYPE_INSERTION:
							allele1 = indel.getInsString();
							allele1len = indel.getInsString().length();
							type = "insertion";
							break;
						case VariantStringService.INDELTYPE_MISSING:
							continue;
						}

					}
					int allele2len = 0;
					String allele2 = "";
					String type2 = "";
					if (indels.getAllele2() != null) {

						IndelsAllvarsPos indel2 = mapIndelId2Indel.get(indels.getAllele2());
						int indeltype2 = IndelStringHDF5nRDBMSHybridService.getIndelType(indel2.getInsString());

						switch (indeltype2) {

						case VariantStringService.INDELTYPE_EXTENDDELETION:
							allele2 = "/del " + indel2.getInsString();
							allele2len = Integer.valueOf(indel2.getInsString());
							type2 = "deletion";
							break;
						case VariantStringService.INDELTYPE_DELETION:
							allele2 = "/del " + indel2.getInsString();
							allele2len = Integer.valueOf(indel2.getInsString());
							type2 = "deletion";
							break;
						case VariantStringService.INDELTYPE_SNP:
							allele2 = "/snp ->" + indel2.getInsString();
							allele2len = 1;
							type2 = "snp";
							break;
						case VariantStringService.INDELTYPE_SUBSTITUTION:
							allele2 = "/sub ->" + indel2.getInsString();
							allele2len = indel2.getInsString().length();
							type2 = "substitution";
							break;
						case VariantStringService.INDELTYPE_REFERENCE:
							allele2 = "/ref";
							allele2len = 1;
							type2 = "reference";
							if (!showAll)
								continue;
							break;
						case VariantStringService.INDELTYPE_INSERTION:
							allele2 = "/" + indel2.getInsString();
							allele2len = indel2.getInsString().length();
							type2 = "insertion";
							break;
						case VariantStringService.INDELTYPE_MISSING:
							continue;

						}
					}

					int maxlen = allele1len;
					if (allele2len > maxlen)
						maxlen = allele2len;

					String allele12 = allele1;
					if (!allele2.equals("/" + allele1)) {
						allele12 = allele1 + allele2;
					}

					String line_right = "\t.\t.\t.\tName=" + var.getName() +
					// ";ID=VAR" + var.getVarietyId() + "-CHR" + snpvars.getChr() + "-" + pos +
					// ";ID=VAR" + var.getVarietyId() + "-" + snpvars.getContig() + "-" +
					// pos.getPosition() +
							";ID=VAR" + var.getVarietyId() + "-" + pos.getContig() + "-" + pos.getPosition().longValue()
							+ ";AlleleAlt=" + allele12 + ";Contig=" + pos.getContig() + ";Position=" + pos.getPosition()
							+ colormode + ";order=" + order + "\n";

					if (!type.equals(type2)) {
						type = type + "/" + type2;
					}

					// listGFF.add(new GFF( chr + "\tIRIC\t" + type + "\t", line_right,
					// pos.longValue(), pos.longValue() + maxlen -1, pos.longValue() ));
					listGFF.add(new GFF(chr + "\tIRIC\t" + type + "\t", line_right, pos.getPosition().longValue(),
							pos.getPosition().longValue() + maxlen - 1, pos.getPosition().longValue()));

				}

				if (snpvars.getVarnuc() != null) {
					List<SnpsAllvarsPos> listSnpPos = queryRawResult.getSnpstringdata().getListPos();

					Map<Position, String> mapIndelpos2Refnuc = null;
					if (queryRawResult.hasIndel())
						mapIndelpos2Refnuc = queryRawResult.getIndelstringdata().getMapIndelpos2Refnuc();

					Set tempsetall = new TreeSet();
					Set tempsetsnps = new TreeSet();
					for (int iMergedPos = 0; iMergedPos < listSnpPos.size(); iMergedPos++) {

						int iPos = iMergedPos;
						SnpsAllvarsPos pos = listSnpPos.get(iMergedPos);

						String strRef = null;

						if (mapIndelpos2Refnuc != null)
							strRef = mapIndelpos2Refnuc.get(pos);
						if (strRef == null)
							strRef = pos.getRefnuc();

						// no ref value
						if (strRef == null || strRef.isEmpty())
							continue;

						char cRef = strRef.charAt(0);
						char charAtPos = snpvars.getVarnuc().substring(iPos, iPos + 1).charAt(0);
						String synstr = "1";

						if (setposNonsyn != null && setposNonsyn.contains(listSnpPos.get(iPos)))
							synstr = "0";
						String al2 = "";
						if (mapPos2allele2 != null) {
							Character allele2 = mapPos2allele2.get(listSnpPos.get(iPos));
							if (allele2 != null)
								al2 = "/" + allele2;
						}

						String splicestr = "";
						if (setposAcceptor != null) {
							if (setposAcceptor.contains(pos))
								splicestr = ";Acceptor=1";
							else
								splicestr = ";Acceptor=0";
						}

						if (setposDonor != null) {
							if (setposDonor.contains(pos))
								splicestr += ";Donor=1";
							else
								splicestr += ";Donor=0";
						}

						if (showAll
								|| (charAtPos != '0' && cRef != charAtPos && charAtPos != '.' && charAtPos != ' ')) {

							String line_right = "\t.\t.\t.\tName=" + var.getName() + ";ID=VAR" + var.getVarietyId()
									+ "-" + snpvars.getContig() + "-" + pos.getPosition() + ";AlleleRef=" + cRef
									+ ";AlleleAlt=" + charAtPos + al2 + ";Position=" + pos.getPosition()
									+ ";Synonymous=" + synstr + splicestr + colormode + ";order=" + order + "\n";

							listGFF.add(new GFF(chr + "\tIRIC\tsnp\t", line_right, pos.getPosition().longValue(),
									pos.getPosition().longValue(), pos.getPosition().longValue()));
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return listGFF;
	}

	/**
	 * Write SNP GFF to file to be displayed in JBrowse
	 * 
	 * @param listGFF
	 * @param filename
	 */
	private void writeGFF(List listGFF, String filename) {

		java.util.Collections.sort(listGFF, new GFFStartComparator());

		try {
			// File file = new File(AppContext.getTempDir() + filename);
			File file = new File(filename);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			// BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(fw);

			int linecount = 0;

			Iterator<GFF> itGFF = listGFF.iterator();
			StringBuffer buff = new StringBuffer();
			buff.append("##gff-version 3\n#Note: See http://song.sourceforge.net\n\n");

			while (itGFF.hasNext()) {
				GFF gff = itGFF.next();

				buff.append(gff.toString());

				if (linecount > 100) {
					buff.append("###\n");
					// pw.write(buff.toString());
					// AppContext.debug(buff.toString());
					pw.append(buff.toString());

					// AppContext.debug(buff.toString());

					buff.delete(0, buff.length());
					buff = new StringBuffer();
					pw.flush();
					linecount = 0;
				}
				linecount++;

			}

			// pw.write(buff.toString());
			pw.append(buff.toString());
			// buff.delete(0, buff.length());
			// buff = new StringBuffer();
			pw.flush();
			pw.close();

			AppContext.debug("temgff written in: " + file.getAbsolutePath());
			log.info("temgff written in: " + file.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void create2VarietiesGFF(List<SnpsStringAllvars> listSNPs, String filename, List<SnpsAllvarsPos> listPos,
			String refnuc) {
		create2VarietiesGFF(listSNPs, filename, false, listPos, refnuc);
	}

	/**
	 * Create SNP GFF for 2-varieties query
	 * 
	 * @param listSNPs
	 * @param filename
	 * @param mismatchOnly
	 */
	private void create2VarietiesGFF(List<SnpsStringAllvars> listSNPs, String filename, boolean mismatchOnly,
			List<SnpsAllvarsPos> listPos, String refnuc) {

		// if(!checkShowsnp.isChecked()) return;

		StringBuffer buff = new StringBuffer();

		varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
		Map<BigDecimal, Variety> mapId2Var = varietyfacade.getMapId2Variety(getDataset());

		try {
			File file = new File(AppContext.getTempDir() + filename);
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			if (listSNPs.size() == 2) {

				int linecount = 0;

				SnpsStringAllvars var1variants = listSNPs.get(0);
				SnpsStringAllvars var2variants = listSNPs.get(1);

				if (var1variants instanceof IndelsStringAllvars) {
					IndelsStringAllvars var1indels = (IndelsStringAllvars) var1variants;
					// var1indels.get
					var1indels.getMapPos2Indels().values();
				}

				String chr = var1variants.getContig() + AppContext.getJbrowseContigSuffix();

				for (int ipos = 0; ipos < refnuc.length(); ipos++) {

					if (true) {
						// snps

						Character refNuc = refnuc.charAt(ipos);
						Character var2Nuc = var2variants.getVarnuc().charAt(ipos);
						Character var1Nuc = var1variants.getVarnuc().charAt(ipos);

						String strPos = listPos.get(ipos).getPosition().toString().replace(".00", "").replace(".0", "");

						if (!mismatchOnly || !var2Nuc.equals(var1Nuc)) {

							String datasrc = "IRIC";
							Variety var = mapId2Var.get(var1variants.getVar());
							if (var.getDataset() != null) {
								// && var.getDataset().equals(VarietyFacade.DATASET_SNP_HDRA)) datasrc="HDRA";
								datasrc = var.getDataset().toUpperCase();
							}

							buff.append(chr);
							buff.append("\t" + datasrc + "\tsnp\t");
							buff.append(strPos);
							buff.append("\t");
							buff.append(strPos);
							buff.append("\t.\t.\t.\tName=" + var.getName() + ";ID=" + var1variants.getVar() + "-" + chr
									+ "-" + strPos + ";AlleleRef=" + refNuc + ";AlleleAlt=" + var1Nuc + ";Position="
									+ strPos + "\n");

							buff.append(chr);
							buff.append("\t" + datasrc + "\tsnp\t");
							buff.append(strPos);
							buff.append("\t");
							buff.append(strPos);
							buff.append("\t.\t.\t.\tName=" + mapId2Var.get(var2variants.getVar()).getName() + ";ID="
									+ var2variants.getVar() + "-" + chr + "-" + strPos + ";AlleleRef=" + refNuc
									+ ";AlleleAlt=" + var2Nuc + ";Position=" + strPos + "\n");
						}
					}

					if (linecount > 100) {
						buff.append("###\n");
						bw.write(buff.toString());
						buff.delete(0, buff.length());
						buff = new StringBuffer();
						bw.flush();
						linecount = 0;
					}
					linecount++;
				}
			}

			bw.write(buff.toString());
			buff.delete(0, buff.length());
			buff = new StringBuffer();
			bw.flush();
			bw.close();

			AppContext.debug("temgff written in: " + file.getAbsolutePath());
			log.info("temgff written in: " + file.getAbsolutePath());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Prepare JBRowse URL. JBrowse is not fetched/rendered until the tab is
	 * clicked, but the URL is already formulated
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 * @param locus
	 */
	private void updateJBrowse(String chr, String start, String end, String locus) {
		// return;

		chr = chr.trim();
		String chrpad = chr;
		if (chrpad.length() == 1)
			chrpad = "0" + chr;

		gfffile = null;

		String org = this.listboxReference.getSelectedItem().getLabel();

		if (org.equals(AppContext.getDefaultOrganism()))
			chrpad = "loc=" + chr; // + AppContext.getJbrowseContigSuffix(); //"|msu7";
		else if (org.equals("IR64-21"))
			chrpad = "data=ir6421v1&loc=" + chr + "|ir6421v1";
		else if (org.equals("93-11"))
			chrpad = "data=9311v1&loc=" + chr + "|9311v1";
		else if (org.equals("DJ123"))
			chrpad = "data=dj123v1&loc=" + chr + "|dj123v1";
		else if (org.equals("Kasalath"))
			chrpad = "data=kasv1&loc=" + chr + "|kasv1";


		if (iframeJbrowse == null)
			throw new RuntimeException("jbrowse==null");

		String displaymode = "%22displayMode%22:%22normal%22,%22maxHeight%22:%222000%22";

		if (tallJbrowse) {
			iframeJbrowse.setStyle("width:1500px;height:1000px;border:0px inset;");
			displaymode = "%22displayMode%22:%22compact%22,%22maxHeight%22:%222000%22";
		} else
			iframeJbrowse.setStyle("width:1500px;height:800px;border:0px inset;");

		String rendertype = "";

		if (locus.isEmpty())
			msgJbrowse.setValue("Chromosome " + chr + " [" + start + ".." + end + "]");
		else
			msgJbrowse.setValue(locus + "  Chromosome " + chr + " [" + start + ".." + end + "]");

		iframeJbrowse.setScrolling("yes");
		iframeJbrowse.setAlign("left");

		String urltemplate = "..%2F..%2Ftemp%2F" + "GFF_FILE";
		if (AppContext.isASTI()) {
			urltemplate = AppContext.getHostname() + "%2F" + AppContext.getTempFolder() + "GFF_FILE";
		}


		String snp3kcore = "";
		if (checkboxSNP.isChecked()) {
			if (getVariantset().contains("3kcore"))
				snp3kcore = "msu7coresnpsv2%2C";
		}
		if (checkboxIndel.isChecked())
			snp3kcore += "msu7indelsv2%2C";

		if (true) {

			if (tallJbrowse) {
				// for all varieties
				rendertype = "%22JBrowse%2FView%2FTrack%2FAlignments2VarietyMismatchSynIndels%22";

				String showTracks = AppContext.getJBrowseDefaulttracks(getVariantset()) + "%2C" + snp3kcore;

				if (org.equals(AppContext.getDefaultOrganism())) {
				}
	
				else if (org.equals("IR64-21"))
					showTracks = "DNA,ir6421v1gffv2,";
				else if (org.equals("93-11"))
					showTracks = "DNA%2C9311v1gffv2,";
				else if (org.equals("DJ123"))
					showTracks = "DNA%2C,dj123v1gffv2,";
				else if (org.equals("Kasalath"))
					showTracks = "DNA%2Ckasv1gffrapv2,";

				urljbrowse = AppContext.getJbrowseDir() + "/?" + chrpad + ":" + start + ".." + end + "&tracks="
						+ showTracks; 
				
			} else {
				if (radioColorAllele.isSelected()) {
					rendertype = "CanvasFeatures2Vars";
				} else if (radioColorMismatch.isSelected()) {
					rendertype = "CanvasFeatures2VarsMismatch";
				}
				
				String showTracks = AppContext.getJBrowseDefaulttracks(getVariantset()) + "," + snp3kcore;
				if (org.equals(AppContext.getDefaultOrganism())) {
				} else if (org.equals("IR64-21"))
					showTracks = "DNA,ir6421v1gff,";
				else if (org.equals("93-11"))
					showTracks = "DNA%2C9311v1gff,";
				else if (org.equals("DJ123"))
					showTracks = "DNA%2C,dj123v1gff,";
				else if (org.equals("Kasalath"))
					showTracks = "DNA%2Ckasrapv1rengff,";

				// for 2 varieties
				urljbrowse = AppContext.getJbrowseDir() + "/?" + chrpad + ":" + start + ".." + end +
						"&tracks=" + showTracks;
				

			}

			urljbrowse = urljbrowse.replace("{", "%7B").replace("}", "%7D");
		}

	}

	// ********************************************** HANDLE PHYLOGENETIC TREE
	// EVENTS *************************************************************

	@Listen("onSelect = #tabPhylo")
	public void onselectTabPhylo() {
		// getSession().putValue("variantdata", queryResult );
		// getSession().putValue("buttonnewick", buttonDownloadNewick );
	}

	@Listen("onClick = #buttonRenderTree")
	public void onclickRenderTree() {

		urljbrowsephylo = null;
		boolean success = false;
		mapVariety2Phyloorder = null;
		urlphylo = "/jsp/phylotreeGermplasms.jsp?dataset=true";
		// phyloid= Long.valueOf(AppContext.createTempFilename());
		// urlphylo = "/jsp/phylotreeGermplasms.jsp?phyloid=" + phyloid;
		getSession().removeValue("newick");
		buttonDownloadNewick.setVisible(false);
		tabJBrowsePhylo.setVisible(false);
		if (urlphylo != null) {

			// Clients.showBusy("Computing Phylogenetic Tree");

			String newick = "";
			int nvars = 3023;
			long pairs = 0;
			int topn = -1;
			String finalurl = AppContext.getHostname() + "/" + AppContext.getHostDirectory() + "/jsp/phylo.jsp";
			if (AppContext.getHostDirectory().isEmpty())
				finalurl = AppContext.getHostname() + "/jsp/phylo.jsp";
			Object[] newicknodes = null;

			newicknodes = genotype.constructPhylotree(queryResult, new PhylotreeQueryParams(fillGenotypeQueryParams(),
					PhylotreeService.PHYLOTREE_METHOD_TOPN, -1, 1.0, 1.0));
			newick = (String) newicknodes[0];

			if (!newick.isEmpty()) {

				newick = "'" + newick.replace("\"", "").replace(":-", ":") + "'";

				
				nvars = Integer.valueOf(newicknodes[1].toString());
				pairs = Integer.valueOf(newicknodes[2].toString());
				mapVariety2Phyloorder = (Map) newicknodes[3];

				try {
					getSession().putValue("newick", newick);
					getSession().putValue("nvars", nvars);
				
					String newickfile = "newick" + AppContext.createTempFilename();
					String outfile = AppContext.getTempDir() + newickfile;
					BufferedWriter br = new BufferedWriter(new FileWriter(outfile));
					br.write(newick);
					br.close();

					br = new BufferedWriter(new FileWriter(outfile + ".xml"));
					br.write(((String) newicknodes[4]).replace("<branch_length>-", "<branch_length>"));
					br.close();

					finalurl += "?newick=" + newickfile + "&nvars=" + nvars + "&pairs=" + pairs;
					AppContext.debug("newick file available in " + outfile + "\n" + finalurl);

					success = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			iframePhylotree.setSrc(finalurl);
			// Clients.clearBusy();
		}

		if (success) {
			buttonDownloadNewick.setVisible(true);
			iframePhylotree.setVisible(true);
			// tabJBrowsePhylo.setVisible(true);

		} else
			Messagebox.show("Phylogenetic tree construction failed.");

		urlphylo = null;

	}

	/**
	 * Prepare phylogenetic tree URL. Tree is not computed/displayed until the tab
	 * is clicked
	 * 
	 * @param chr
	 * @param start
	 * @param end
	 */

	private void update_phylotree(String chr, String start, String end, int nvars) {

		int height = nvars * 21;
		int width = nvars * 30;

		int treescale = 1;

		iframePhylotree.setStyle("height:" + Integer.toString(height) + "px;width:1400px");
		iframePhylotree.setScrolling("yes");

		String topN = "-1";
		if (!selectPhyloTopN.getSelectedItem().getLabel().equals("all"))
			topN = selectPhyloTopN.getSelectedItem().getLabel();

		urlphylo = "/jsp/phylotreeGermplasms.jsp?scale=" + treescale + "&chr=" + chr + "&start=" + start + "&end=" + end
				+ "&topn=" + topN + "&tmpfile=GFF_FILE&mindist=" + intPhyloMindist.getValue();

	}

	@Listen("onClick = #buttonDownloadNewick")
	public void onselectdownloadnewick() {

		String newick = (String) Sessions.getCurrent().getAttribute("newick");

		if (newick != null) {
			try {
				String filetype = "text/plain";
				Filedownload.save(newick, filetype, "newick_" + AppContext.createTempFilename() + ".newick");
				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
				AppContext.debug("newick downlaod complete! Downloaded to:" + zksession.getRemoteHost() + "  "
						+ zksession.getRemoteAddr());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else
			Messagebox.show("Phylogenetic tree not yet computed");
	}

	// **************************************************** HANDLE DOWNLOAD SNPS
	// *******************************

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

	public static Map orderMap(Map orig) {
		Map map = new TreeMap();
		Iterator it = orig.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			map.put(orig.get(key), key);
		}
		return map;
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
			if (listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreegroup")) {
				Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray
						.getModel();
				VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
				double maxk = Math.min(15, table.getVariantStringData().getMapVariety2Mismatch().size());
				double curpos = 3;

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
			org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			AppContext.debug("newick downlaod complete! Downloaded to:" + zksession.getRemoteHost() + "  "
					+ zksession.getRemoteAddr());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onClick = #buttonHaploDisplayTree")
	public void onselectHaplotree() throws Exception {

		if (haplofilename == null)
			return;
		if (new File(AppContext.getTempDir() + haplofilename + ".ped.hctree.html").exists())
			return;
		iframeHaplotypeTree.setVisible(false);
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		double kheight = 0;
		if (this.listboxKgroupMethod.getSelectedItem().getValue().equals("cuttreeheight_norm")) {
			try {
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

	@Listen("onSelect = #tabHaplotype")
	public void tabDisplayHaloptypeimage() throws Exception {
		onselectTabHaplotype(false);
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
		buttonHaplotypelog.setVisible(false);
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
				displayGroupallelePhenotpes();

				try {
					updateGroupAlleleFrequencyChart();
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

	@Listen("onClick =#checkboxOutlierGroupPhenotypeBox")
	public void oncheckOutlier() {
		try {
			displayGroupallelePhenotpes();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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

	@Listen("onPlotClick = #chartGroupPhenotypeStacked")
	public void onclickQualGroupPhenotype(ChartsEvent event) {

		Point point = event.getPoint();
		// point.update(event.getXAxis(), event.getYAxis(), point.getHigh());
		// point.setSelected(false);
		// AppContext.debug("onPlotClick " + point.getX() + ", " + point.getY() + ", " +
		// event.getSeries().getName() + ", " + event.getPointIndex() + ", " +
		// event.getCategory().toString() + " , " +
		// event.getSeries() + ", " + event.getCategory().getClass() + ", " +
		// event.getPoint().getParent() );
		AppContext.debug("onPlotClick " + event.getCategory() + "," + event.getSeries().getName());
		// display varieties

		if (mapKgroupCat2Varieties == null)
			return;

		AppContext.debug("mapKgroupCat2Varieties.key=" + mapKgroupCat2Varieties.keySet());
		try {
			String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
			List listVars = new ArrayList();
			Map varid2var = varietyfacade.getMapId2Variety(getDataset());
			Iterator<String> itVars = mapKgroupCat2Varieties
					.get(event.getSeries().getName() + "-" + event.getCategory()).iterator();
			while (itVars.hasNext()) {
				String var = itVars.next();
				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
				Variety varobj = (Variety) varid2var.get(varid);
				listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
						mapVarid2Phenotype.get(varid) });
			}
			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
			listboxGroupVarietyPhenotypeStacked.setVisible(true);
			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + event.getCategory() + ", "
					+ sPhenotype + " = " + event.getSeries().getName());
			labelGroupVarietyPhenotype.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onSelection = #chartGroupPhenotypeBox")
	public void onselectQuanGroupPhenotype(ChartsSelectionEvent event) {
		// doing the zooming in function
		double ymin = event.getYAxisMin().doubleValue();
		double ymax = event.getYAxisMax().doubleValue();
		int kgroup = event.getXAxisMax().intValue() + 1;

		AppContext.debug("group " + kgroup + ", " + ymin + " - " + ymax);

		if (mapKgroupCat2Varieties == null)
			return;
		try {

			String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
			List listVars = new ArrayList();
			Map varid2var = varietyfacade.getMapId2Variety(getDataset());
			Iterator<String> itVars = mapKgroupCat2Varieties.get(String.format("%d", kgroup)).iterator();
			while (itVars.hasNext()) {
				String var = itVars.next();
				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
				Object phenval = mapVarid2Phenotype.get(varid);
				if (phenval != null && phenval instanceof Number) {
					double dval = ((Number) phenval).doubleValue();
					if (dval <= ymax && dval >= ymin) {
						Variety varobj = (Variety) varid2var.get(varid);
						listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
								phenval });
					}
				}
			}

			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
			listboxGroupVarietyPhenotypeStacked.setVisible(true);
			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + kgroup + ", " + sPhenotype
					+ " between " + String.format("%.2f", ymin) + " and " + String.format("%.2f", ymax));
			labelGroupVarietyPhenotype.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Listen("onClick =#buttonAddPhenValuesToMatrix")
	public void onclickaddphenvalues() {
		StringBuffer buff = new StringBuffer();
		Map mapKgroup2Values = new HashMap();
		List lHeader = new ArrayList();
		String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
		if (this.divGroupPhenoCat.isVisible()) {
			buff.append("divGroupPhenoCat: ");
			Iterator<String> itgroupcat = new TreeSet(mapKgroupCat2Varieties.keySet()).iterator();
			Set<String> setCats = new TreeSet();
			Map<String, Map> mapKgroup2Cats = new TreeMap();
			while (itgroupcat.hasNext()) {
				String gk = itgroupcat.next();
				int vars = ((Collection) mapKgroupCat2Varieties.get(gk)).size();
				buff.append(gk + "\t" + vars + "\n");
				String k = gk.split("\\-")[1];
				String cat = gk.split("\\-")[0];
				setCats.add(cat);
				Map mapCat2Vals = mapKgroup2Cats.get(k);
				if (mapCat2Vals == null) {
					mapCat2Vals = new HashMap();
					mapKgroup2Cats.put(k, mapCat2Vals);
				}
				mapCat2Vals.put(cat, vars);
			}
			for (String cat : setCats) {
				lHeader.add(sPhenotype + ":" + cat);
			}
			buff.append("\n");
			Iterator<String> itk = mapKgroup2Cats.keySet().iterator();
			while (itk.hasNext()) {
				String k = itk.next();
				List listVals = new ArrayList();
				buff.append(k).append("\t");
				for (String cat : setCats) {
					Integer nvar = (Integer) mapKgroup2Cats.get(k).get(cat);
					if (nvar == null)
						listVals.add("");
					else
						listVals.add(nvar);
					buff.append(nvar).append("\t");
				}
				mapKgroup2Values.put(k, listVals);
				buff.append("\n");
			}

		} else if (this.divGroupPhenoQuantBox.isVisible()) {
			try {
				BoxPlotModel model = (BoxPlotModel) this.chartGroupPhenotypeBox.getModel();
				Iterator itseries = model.getSeries().iterator();
				buff.append("divGroupPhenoQuantBox: ");
				lHeader.add(sPhenotype + " Q1");
				lHeader.add(sPhenotype + " median");
				lHeader.add(sPhenotype + " Q3");
				while (itseries.hasNext()) {
					Comparable ser = (Comparable) itseries.next();
					int dcount = model.getDataCount(ser);
					// buff.append( ((Series)ser ).getName() ).append(":\n");
					buff.append(ser.toString()).append(":\n");
					for (int i = 0; i < dcount; i++) {
						double q1 = model.getQ1(ser, i).doubleValue();
						double q2 = model.getMedian(ser, i).doubleValue();
						double q3 = model.getQ3(ser, i).doubleValue();
						buff.append(q1).append("\t").append(q2).append("\t").append(q3).append("\n");
						List lVal = new ArrayList();
						lVal.add(q1);
						lVal.add(q2);
						lVal.add(q3);
						mapKgroup2Values.put(Integer.toString(i + 1), lVal);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (this.chartGroupPhenotypeErrorbars.isVisible()) {

			List<Point> lmean = chartGroupPhenotypeErrorbars.getSeries(0).getData();
			Series series2 = chartGroupPhenotypeErrorbars.getSeries(1);
			List<Point> lstd = series2.getData();
			buff.append("chartGroupPhenotypeErrorbars: ");
			lHeader.add(sPhenotype + " mean");
			lHeader.add(sPhenotype + " variance");
			for (int i = 0; i < lmean.size(); i++) {
				buff.append(lmean.get(i).getX() + "\t" + lstd.get(i).getLow() + "\t" + lstd.get(i).getHigh() + "\t"
						+ lmean.get(i).toJSONString() + "\t" + lstd.get(i).getX() + "\t" + lstd.get(i).getY() + "\t"
						+ lstd.get(i).toJSONString() + "\n");
				List lVal = new ArrayList();

				double mean = Double.valueOf(lmean.get(i).toJSONString());
				lVal.add(mean);
				// lVal.add(lstd.get(i).getX());
				lVal.add(Math.pow(lstd.get(i).getY().doubleValue() - mean, 2));
				mapKgroup2Values.put(Integer.toString(i + 1), lVal);
			}
		}

		AppContext.debug("buttonAddPhenValuesToMatrix " + buff.toString());

		appendToGroupMatrix(lHeader, mapKgroup2Values);
	}

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

	@Listen("onSelection = #chartGroupPhenotypeErrorbars")
	public void onselectQuanGroupPhenotypeError(ChartsSelectionEvent event) {
		// doing the zooming in function
		double ymin = event.getYAxisMin().doubleValue();
		double ymax = event.getYAxisMax().doubleValue();
		int kgroup = event.getXAxisMax().intValue() + 1;

		AppContext.debug("group " + kgroup + ", " + ymin + " - " + ymax);

		if (mapKgroupCat2Varieties == null)
			return;
		try {

			String sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
			Map mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
			List listVars = new ArrayList();
			Map varid2var = varietyfacade.getMapId2Variety(getDataset());
			Iterator<String> itVars = mapKgroupCat2Varieties.get(String.format("%d", kgroup)).iterator();
			while (itVars.hasNext()) {
				String var = itVars.next();
				BigDecimal varid = AppContext.getVarunique2Id(var, true, true, true, varid2var);
				Object phenval = mapVarid2Phenotype.get(varid);
				if (phenval != null && phenval instanceof Number) {
					double dval = ((Number) phenval).doubleValue();
					if (dval <= ymax && dval >= ymin) {
						Variety varobj = (Variety) varid2var.get(varid);
						listVars.add(new Object[] { varobj.getName(), varobj.getAccession(), varobj.getSubpopulation(),
								phenval });
					}
				}
			}

			listboxGroupVarietyPhenotypeStacked.setItemRenderer(new GroupVarietyPhenotypeListitemRenderer());
			listboxGroupVarietyPhenotypeStacked.setModel(new SimpleListModel(listVars));
			listheaderGroupVarietyPhenotypeStackedPhenotype.setLabel(sPhenotype);
			listboxGroupVarietyPhenotypeStacked.setVisible(true);
			labelGroupVarietyPhenotype.setValue(listVars.size() + " varieties in Group " + kgroup + ", " + sPhenotype
					+ " between " + String.format("%.2f", ymin) + " and " + String.format("%.2f", ymax));
			labelGroupVarietyPhenotype.setVisible(true);
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

	@Listen("onClick =#radioGroupPhenotypeBox")
	public void onClickradioGroupPhenotypeBox() {
		divGroupPhenoQuantBox.setVisible(false);
		chartGroupPhenotypeErrorbars.setVisible(false);
		try {
			displayGroupallelePhenotpes();
			divGroupPhenoQuantBox.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick =#radioGroupPhenotypeErrorbars")
	public void radioGroupPhenotypeErrorbars() {
		divGroupPhenoQuantBox.setVisible(false);
		chartGroupPhenotypeErrorbars.setVisible(false);
		try {
			displayGroupallelePhenotpes();
			chartGroupPhenotypeErrorbars.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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

	@Listen("onClick = #buttonDownloadGroupMatrix")
	public void onclickDownloadGroupMatrix() {
		try {
			SimpleListModel m = (SimpleListModel) listboxGroupAlleleMatrix.getModel();
			Listhead listHead = (Listhead) listboxGroupAlleleMatrix.getListhead(); // .getChildren().iterator().next();
			Iterator<Component> itlistheader = (Iterator<Component>) listHead.getChildren().iterator();
			StringBuffer buff = new StringBuffer();
			while (itlistheader.hasNext()) {
				Listheader l = (Listheader) itlistheader.next();
				buff.append(l.getLabel());
				if (itlistheader.hasNext())
					buff.append("\t");
			}
			buff.append("\n");

			ListitemRenderer rend = listboxGroupAlleleMatrix.getItemRenderer();
			Map mapKgroup2AppendValues = ((VargroupListItemRenderer) rend).getMapKgroup2AppendValues();

			for (int i = 0; i < m.getSize(); i++) {
				List<String> el = (List) m.getElementAt(i);
				Iterator<String> itel = el.iterator();
				String kgroup = itel.next();
				buff.append(kgroup).append("\t");
				while (itel.hasNext()) {
					Object o = itel.next();
					String al = "";
					if (al == null)
						al = "";
					else if (o instanceof String) {
						if (o.equals("0"))
							al = "";
						else
							al = (String) o;
					} else if (o instanceof Double) {
						// if(o.equals(0)) al="";
						// else al= String.format("0.2f", (Double)o);
						al = String.format("%.02f", ((Double) o).doubleValue());
					} else if (o instanceof Number) {
						// if(o.equals(0)) al="";
						// else al=o.toString();
						al = o.toString();
					}
					buff.append(al);
					if (itel.hasNext())
						buff.append("\t");
				}

				if (mapKgroup2AppendValues != null) {
					List lVals = (List) mapKgroup2AppendValues.get(kgroup);
					Iterator itV = lVals.iterator();
					while (itV.hasNext()) {
						Object o = itV.next();
						String al = "";
						if (o == null) {
						} else if (o instanceof String) {
							if (o.equals("0"))
								al = "";
							else
								al = (String) o;
						} else if (o instanceof Double) {
							// if(o.equals(0)) al="";
							// else al= String.format("0.2f", (Double)o);
							al = String.format("%.02f", (Double) o);
						} else if (o instanceof Number) {
							// if(o.equals(0)) al="";
							// else al=o.toString();
							al = o.toString();
						}
						buff.append("\t").append(al);
					}
				}

				if (i < m.getSize() + 1)
					buff.append("\n");
			}

			Filedownload.save(buff.toString(), "text/plain", "snp3kgroups-" + queryFilename());

		} catch (Exception ex) {
			ex.printStackTrace();
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

	@Listen("onCellClick =#biglistboxArray")
	public void oncellclickbiglistbox(Event e) {
		CellClickEvent ev = (CellClickEvent) e;

		List lisobj = (List) this.biglistboxArray.getSelectedObject();
		Object[] objarr = (Object[]) lisobj.get(0);
		String varname = (String) objarr[0];

		AppContext.debug("onCellClick " + ev.getName() + "  data=" + ev.getData().toString() + "  colidx="
				+ ev.getColumnIndex() + ", rowidx=" + ev.getRowIndex() + ", variety=" + varname);

		if (getDataset().equals(VarietyFacade.DATASET_SNPINDELV2_IUPAC)
				&& this.listboxReference.getSelectedItem().getLabel().equals(AppContext.getDefaultOrganism())) {
			Position posclick = varianttable.getVariantStringData().getListPos()
					.get(Math.max(0, ev.getColumnIndex() - this.frozenCols));

			BigDecimal varid = varianttable.getVariantStringData().getListVariantsString().get(ev.getRowIndex())
					.getVar();
			// Variety var =
			// varietyfacade.getMapId2Variety(VarietyFacade.DATASET_SNPINDELV2_IUPAC).get(varid);
			Set svar = new HashSet();
			svar.add(VarietyFacade.DATASET_SNPINDELV2_IUPAC);
			Variety var = varietyfacade.getGermplasmByName(varname, svar).iterator().next();

			AppContext.debug("selected " + var.getName() + ", " + posclick.getContig() + "-" + posclick.getPosition());

			this.urljbrowse = AppContext.getJbrowseDir() + "/?loc=" + posclick.getContig() + ":"
					+ (posclick.getPosition().intValue() - 50) + ".." + (posclick.getPosition().intValue() + 50)
					+ "&tracks=DNA," + var.getName() + " " + var.getIrisId() + " BAM," + var.getName() + " "
					+ var.getIrisId() + " SNP Coverage," + var.getName() + " " + var.getIrisId() + " VCF";
			AppContext.debug("openning: " + urljbrowse);
			this.iframeJbrowse.setSrc(urljbrowse);
			iframeJbrowse.invalidate();
			// this.tabJbrowse.setSelected(true);
			// this.iframeJbrowse.setSrc(urljbrowse);
			// Events.sendEvent("onSelect", this.tabJbrowse,null);
			tabJbrowse.setSelected(true);

		} else {
			Messagebox.show("BAM/VCF files viewable only for 3kRGP dataset with Nipponbare as reference");
		}

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

	/*
	 * public Callable<AsyncJobReport> callableDownloadBigListboxPlinkZip(final
	 * String filename) { return new Callable<AsyncJobReport>() {
	 * 
	 * @Override public AsyncJobReport call() throws Exception { AsyncJobReport
	 * report=null; try { GenotypeQueryParams params=fillGenotypeQueryParams();
	 * 
	 * params.setSubmitter( Sessions.getCurrent().getLocalAddr() +"-"+
	 * Sessions.getCurrent().getLocalName() + "-" +
	 * Sessions.getCurrent().getRemoteAddr() + "-" +
	 * Sessions.getCurrent().getRemoteHost() + "-" +
	 * Sessions.getCurrent().getServerName() );
	 * 
	 * params.setFilename(filename + ".plink"); //report =
	 * genotype.querydownloadGenotypeAsync(params); enableDownloadButtons(false); }
	 * catch(Exception ex) { ex.printStackTrace(); //Messagebox.show("call():" +
	 * ex.getMessage()); report=new AsyncJobReport("", ex.getMessage(),"",null); }
	 * return report; } }; }
	 */

	/*
	 * public Callable<String> callableWithView() { return new Callable<String>() {
	 * 
	 * @Override public String call() throws Exception { Thread.sleep(2000); return
	 * "views/html"; } }; }
	 */

	@Listen("onClick = #buttonDownloadPairwiseCsv")
	public void downloadPairwiseCsv() {
		if (divPairwise.isVisible())
			download2VarsList(AppContext.getTempDir() + "snp2vars-" + queryFilename() + ".csv", ",");
	}

	@Listen("onClick = #buttonDownloadPairwiseTab")
	public void downloadPairwiseTab() {
		if (divPairwise.isVisible())
			download2VarsList(AppContext.getTempDir() + "snp2vars-" + queryFilename() + ".txt", ",");
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

	private void downloadBigListboxPlinkZip(String filename) {
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
		downloadBigListboxPlinkZip(table, filename);
	}

	private void downloadBigListboxFlapjackZip(String filename) {
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
		downloadBigListboxFlapjackZip(table, filename);
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

	public static void generateBigListboxPlink(VarietyFacade varietyfacade, GenotypeQueryParams p, String chr, Set ds,
			VariantTable table1, String filename) {
		generateBigListboxPlink(varietyfacade, p, chr, ds, table1, filename, null, null);
	}

	public static void generateBigListboxPlink(VarietyFacade varietyfacade, GenotypeQueryParams p, String chr, Set ds,
			VariantTable table1, String filename, Map mapSample2Pheno, String phenonames) {
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) table1;
		// MAP files
		// The fields in a MAP file are:
		// Chromosome
		// Marker ID
		// Genetic distance
		// Physical position
		//
		// PED files
		// The fields in a PED file are
		// Family ID
		// Sample ID
		// Paternal ID
		// Maternal ID
		// Sex (1=male; 2=female; other=unknown)
		// Affection (0=unknown; 1=unaffected; 2=affected)
		// Genotypes (space or tab separated, 2 for each marker. 0=missing)
		//

		// -HapMap file format:
		// The current release consists of text-table files only, with the following
		// columns:
		//
		// Col1: refSNP rs# identifier at the time of release (NB: it might merge
		// with another rs# in the future)
		// Col2: SNP alleles according to dbSNP
		// Col3: chromosome that SNP maps to
		// Col4: chromosome position of SNP, in basepairs on reference sequence
		// Col5: strand of reference sequence that SNP maps to
		// Col6: version of reference sequence assembly (currently NCBI build36)
		// Col7: HapMap genotyping center that produced the genotypes
		// Col8: LSID for HapMap protocol used for genotyping
		// Col9: LSID for HapMap assay used for genotyping
		// Col10: LSID for panel of individuals genotyped
		// Col11: QC-code, currently 'QC+' for all entries (for future use)
		// Col12 and on: observed genotypes of samples, one per column, sample
		// identifiers in column headers (Coriell catalog numbers, example:
		// NA10847).

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

					/*
					 * buff.append(chr).append("\t").append( "1" + String.format("%02d",
					 * Integer.valueOf(AppContext.guessChrFromString(contigs[i]))) +
					 * String.format("%08d", pos) ) .append("\t0\t").append(pos).append("\n");
					 * //buff.append(chr).append("\t").append("snp" + chr + "-" + pos)
					 * .append("\t0\t").append(pos+1).append("\n"); else
					 * buff.append(chr).append("\t").append( "1" + String.format("%02d",
					 * Integer.valueOf(chr.toLowerCase().replace("chr", ""))) +
					 * String.format("%08d", pos) ) .append("\t0\t").append(pos).append("\n");
					 */
					/*
					 * if(contigs!=null) buff.append(chr).append("\t").append( "1" +
					 * String.format("%02d",
					 * Integer.valueOf(AppContext.guessChrFromString(contigs[i]))) +
					 * String.format("%08d", pos) ) .append("\t0\t").append(pos).append("\n");
					 * //buff.append(chr).append("\t").append("snp" + chr + "-" + pos)
					 * .append("\t0\t").append(pos+1).append("\n"); else
					 * buff.append(chr).append("\t").append( "1" + String.format("%02d",
					 * Integer.valueOf(chr.toLowerCase().replace("chr", ""))) +
					 * String.format("%08d", pos) ) .append("\t0\t").append(pos).append("\n");
					 */
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

			// AppContext.debug("map: "+ buff.toString() );
			// AppContext.debug("File download complete! Saved to: "+filename);

			// org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
			// AppContext.debug("snpallvars download complete!"+ filename +".map Downloaded
			// to:" + zksession.getRemoteHost() + " " + zksession.getRemoteAddr() );

			buff = new StringBuffer();

			Object[][] varalleles = table.getVaralleles();
			// Map<BigDecimal,Variety> mapVarId2Var =
			// varietyfacade.getMapId2Variety(getDataset());
			// Set ds = getDataset();
			Map<String, Map<BigDecimal, StockSample>> mapDs = varietyfacade.getMapId2Sample(ds);

			AppContext.debug("writing pedfile " + table.getVarid().length + " x " + positions.length);
			AppContext.debug("table.getVaralleles()=" + table.getVaralleles().length + " x "
					+ (table.getVaralleles().length > 0 ? table.getVaralleles()[0].length : 0));

			StringBuffer buffSample = new StringBuffer();
			for (int i = 0; i < table.getVarid().length; i++) {

				// if(mapVarId2Var.get(table.getVarid()[i])==null) throw new
				// RuntimeException("null variety with id=" + table.getVarid()[i])
				/*
				 * String irisid= mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i])
				 * ).getIrisId(); if(irisid==null || irisid.isEmpty() || irisid.equals("NA"))
				 * irisid=mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i])
				 * ).getAccession(); if(irisid==null || irisid.isEmpty() || irisid.equals("NA"))
				 * irisid=mapVarId2Var.get( BigDecimal.valueOf(table.getVarid()[i]) ).getName();
				 * String indvid=irisid.replaceAll(" ","_"); if(getDataset().contains("hdra"))
				 * indvid+="_"+table.getVarid()[i];
				 */
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
				// Family ID
				// Sample ID
				// Paternal ID
				// Maternal ID
				// Sex (1=male; 2=female; other=unknown)
				// Affection (0=unknown; 1=unaffected; 2=affected)
				// Genotypes (space or tab separated, 2 for each marker. 0=missing)

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

	/**
	 * Download entire genome SNPs for varieties in list, to filename using
	 * delimiter
	 * 
	 * @param filename
	 * @param delimiter
	 */
	private void downloadGenome(String filename, String delimiter) {

		try {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			Set setVarieties = workspace.getVarieties(listboxMyVarieties.getSelectedItem().getLabel());
			GenotypeQueryParams params = new GenotypeQueryParams(setVarieties, selectChr.getValue(), null, null,
					checkboxSNP.isChecked(), checkboxIndel.isChecked(), getVariantset(), getDataset(),
					new HashSet(getGenotyperun()), checkboxMismatchOnly.isChecked(), null, null, null,
					checkboxAlignIndels.isChecked(), this.checkboxShowAllRefAlleles.isChecked());
			// params.setSnpSet((String)listboxSnpset.getSelectedItem().getValue());
			params.setDelimiter(delimiter);
			params.setFilename(filename);
			// params.setDataset( getDataset());
			genotype.downloadGenotypeGenome(params);
		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), null, Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick = #buttonDownloadFasta")
	public void onclickDownloadFasta() {

		try {
			GenotypeQueryParams params = fillGenotypeQueryParams();
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			String filename = null;
			Collection colLoci = params.getColLoci();
			if (colLoci == null || colLoci.isEmpty()) {
				colLoci = new ArrayList();
				colLoci.add(new MultiReferenceLocusImpl(params.getOrganism(), params.getsChr(),
						params.getlStart().intValue(), params.getlEnd().intValue(), 1, params.getsLocus()));
				if (params.getsLocus() != null && !params.getsLocus().isEmpty())
					filename = params.getsChr() + "_" + params.getlStart() + "-" + params.getlEnd() + "_"
							+ params.getsLocus();
				else
					filename = params.getsChr() + "_" + params.getlStart() + "-" + params.getlEnd();
			} else {
				filename = listboxMyLocusList.getSelectedItem().getLabel();
			}

			String tmpdir = AppContext.getTempDir() + "fasta-" + AppContext.createTempFilename();
			String locusfilenames[] = new String[colLoci.size() * 2];

			Iterator<Locus> itLocus = colLoci.iterator();
			int locuscount = 0;
			while (itLocus.hasNext()) {
				Locus loc = itLocus.next();
				String locusfilename = loc.getUniquename();
				if (locusfilename == null || locusfilename.isEmpty()) {
					locusfilename = loc.getChr() + "_" + loc.getFmin() + "-" + loc.getFmax();
				}
				locusfilename += ".fasta";
				new File(tmpdir).mkdir();
				locusfilename = tmpdir + "/" + locusfilename;
				locusfilenames[locuscount] = locusfilename;

				genotype.downloadFastaMSAPerLocus(params, loc, locusfilename);

				locuscount++;

				locusfilenames[locuscount] = locusfilename.replace(".fasta", ".csv");

				locuscount++;
			}

			new CreateZipMultipleFiles(filename + "-fasta" + ".zip", locusfilenames).create();
			Filedownload.save(new File(filename + "-fasta" + ".zip"), "application/zip");
			AppContext.debug("File download complete! Saved to: " + filename + "-fasta-" + ".zip");

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage());

		}

	}

	// ********************************************* HANDLE ALLELEFREQUENCY CHART
	// EVENTS *********************************************************

	@Listen("onClick=#radioGroupShowGenotypeFrequency")
	public void onclickradioGroupShowGenotypeFrequency() {
		this.updateGroupAlleleFrequencyChart();
		radioGroupMajorAlleles.setLabel("Major genotypes");
		radioGroupMinorAlleles.setLabel("Minor genotypes");
		radioGroup3rdAlleles.setLabel("3rd genotypes");
		radioGroup4thAlleles.setLabel("4th genotypes");
		radioGroup4thAlleles.setVisible(groupFreqlines[1].linepercent4thmodel != null);
	}

	@Listen("onClick=#radioGroupShowGenotypeCount")
	public void onclickradioGroupShowGenotypeCount() {
		this.updateGroupAlleleFrequencyChart();
		radioGroupMajorAlleles.setLabel("Major genotypes");
		radioGroupMinorAlleles.setLabel("Minor genotypes");
		radioGroup3rdAlleles.setLabel("3rd genotypes");
		radioGroup4thAlleles.setLabel("4th genotypes");
		radioGroup4thAlleles.setVisible(groupFreqlines[1].line4thallelemodel != null);
	}

	@Listen("onClick=#radioGroupShowAlleleFrequency")
	public void onclickradioGroupShowAlleleFrequency() {
		this.updateGroupAlleleFrequencyChart();
		radioGroupMajorAlleles.setLabel("Major alleles");
		radioGroupMinorAlleles.setLabel("Minor alleles");
		radioGroup3rdAlleles.setLabel("3rd alleles");
		radioGroup4thAlleles.setLabel("4th alleles");
		radioGroup4thAlleles.setVisible(groupFreqlines[0].linepercent4thmodel != null);
	}

	@Listen("onClick=#radioGroupShowAlleleCount")
	public void onclickradioGroupShowAlleleCount() {
		this.updateGroupAlleleFrequencyChart();
		radioGroupMajorAlleles.setLabel("Major alleles");
		radioGroupMinorAlleles.setLabel("Minor alleles");
		radioGroup3rdAlleles.setLabel("3rd alleles");
		radioGroup4thAlleles.setLabel("4rd alleles");

		radioGroup4thAlleles.setVisible(groupFreqlines[0].line4thallelemodel != null);

	}

	@Listen("onClick=#radioGroupMajorAlleles")
	public void onclickradioGroupMajorAlleles() {
		this.updateGroupAlleleFrequencyChart();
	}

	@Listen("onClick=#radioGroupMinorAlleles")
	public void onclickradioGroupMinorAlleles() {
		this.updateGroupAlleleFrequencyChart();
	}

	@Listen("onClick=#radioGroup3rdAlleles")
	public void onclickradioGroup3rdAlleles() {
		this.updateGroupAlleleFrequencyChart();
	}

	@Listen("onClick=#radioGroup4thAlleles")
	public void onclickradioGroup4thAlleles() {
		this.updateGroupAlleleFrequencyChart();
	}

	@Listen("onClick=#radioShowGenotypeFrequency")
	public void onclickradioShowGenotypeFrequency() {
		this.updateAlleleFrequencyChart();
		radioMajorAlleles.setLabel("Major genotypes");
		radioMinorAlleles.setLabel("Minor genotypes");
		radio3rdAlleles.setLabel("3rd genotypes");
		radio4thAlleles.setLabel("4th genotypes");
		radio4thAlleles.setVisible(varFreqlines[1].linepercent4thmodel != null);
	}

	@Listen("onClick=#radioShowGenotypeCount")
	public void onclickradioShowGenotypeCount() {
		this.updateAlleleFrequencyChart();
		radioMajorAlleles.setLabel("Major genotypes");
		radioMinorAlleles.setLabel("Minor genotypes");
		radio3rdAlleles.setLabel("3rd genotypes");
		radio4thAlleles.setLabel("4th genotypes");
		radio4thAlleles.setVisible(varFreqlines[1].line4thallelemodel != null);
	}

	@Listen("onClick=#radioShowAlleleFrequency")
	public void onclickradioShowAlleleFrequency() {
		this.updateAlleleFrequencyChart();
		radioMajorAlleles.setLabel("Major alleles");
		radioMinorAlleles.setLabel("Minor alleles");
		radio3rdAlleles.setLabel("3rd alleles");
		radio4thAlleles.setLabel("4th alleles");
		radio4thAlleles.setVisible(varFreqlines[0].linepercent4thmodel != null);
	}

	@Listen("onClick=#radioShowAlleleCount")
	public void onclickradioShowAlleleCount() {
		this.updateAlleleFrequencyChart();
		radioMajorAlleles.setLabel("Major alleles");
		radioMinorAlleles.setLabel("Minor alleles");
		radio3rdAlleles.setLabel("3rd alleles");
		radio4thAlleles.setLabel("4rd alleles");

		radio4thAlleles.setVisible(varFreqlines[0].line4thallelemodel != null);

	}

	@Listen("onClick=#radioMajorAlleles")
	public void onclickradioMajorAlleles() {
		this.updateAlleleFrequencyChart();
	}

	@Listen("onClick=#radioMinorAlleles")
	public void onclickradioMinorAlleles() {
		this.updateAlleleFrequencyChart();
	}

	@Listen("onClick=#radio3rdAlleles")
	public void onclickradio3rdAlleles() {
		this.updateAlleleFrequencyChart();
	}

	@Listen("onClick=#radio4thAlleles")
	public void onclickradio4thAlleles() {
		this.updateAlleleFrequencyChart();
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

	private void updateGroupAlleleFrequencyChart() {

		// if(allelefreqlines==null) calculateAlleleFrequencies();
		try {

			CategoryModel selmodel = null;
			String tooltipjs = null;
			boolean show4th = false;
			AlleleFreqLineData allelefreqlines = groupFreqlines[0];
			AlleleFreqLineData genotypefreqlines = groupFreqlines[1];
			if (radioGroupShowAlleleFrequency.isSelected()) {

				if (radioGroupMajorAlleles.isSelected())
					selmodel = allelefreqlines.linepercentmajormodel;
				else if (radioGroupMinorAlleles.isSelected())
					selmodel = allelefreqlines.linepercentminormodel;
				else if (radioGroup3rdAlleles.isSelected())
					selmodel = allelefreqlines.linepercent3rdmodel;
				else if (radioGroup4thAlleles.isSelected())
					selmodel = allelefreqlines.linepercent4thmodel;

				chartGroupAlleleFrequency.getYAxis().setTitle("Group Allele frequency (%)");
				chartGroupAlleleFrequency.setTitle("Group Allele frequency (%)");

				tooltipjs = allelefreqlines.tooltipjs;
				show4th = allelefreqlines.line4thallelemodel != null;

			} else if (radioGroupShowAlleleCount.isSelected()) {
				if (radioGroupMajorAlleles.isSelected())
					selmodel = allelefreqlines.linecountmajormodel;
				else if (radioGroupMinorAlleles.isSelected())
					selmodel = allelefreqlines.linecountminormodel;
				else if (radioGroup3rdAlleles.isSelected())
					selmodel = allelefreqlines.line3rdallelemodel;
				else if (radioGroup4thAlleles.isSelected())
					selmodel = allelefreqlines.line4thallelemodel;

				chartGroupAlleleFrequency.getYAxis().setTitle("Group Allele count");
				chartGroupAlleleFrequency.setTitle("Group Allele count");
				tooltipjs = allelefreqlines.tooltipjs;
				show4th = allelefreqlines.line4thallelemodel != null;
			} else if (radioGroupShowGenotypeFrequency.isSelected()) {

				if (radioGroupMajorAlleles.isSelected())
					selmodel = genotypefreqlines.linepercentmajormodel;
				else if (radioGroupMinorAlleles.isSelected())
					selmodel = genotypefreqlines.linepercentminormodel;
				else if (radioGroup3rdAlleles.isSelected())
					selmodel = genotypefreqlines.linepercent3rdmodel;
				else if (radioGroup4thAlleles.isSelected())
					selmodel = genotypefreqlines.linepercent4thmodel;

				chartGroupAlleleFrequency.getYAxis().setTitle("Group Genotype frequency (%)");
				chartGroupAlleleFrequency.setTitle("Group Genotype frequency");
				tooltipjs = genotypefreqlines.tooltipjs;
				show4th = genotypefreqlines.line4thallelemodel != null;

			} else if (radioGroupShowGenotypeCount.isSelected()) {
				if (radioGroupMajorAlleles.isSelected())
					selmodel = genotypefreqlines.linecountmajormodel;
				else if (radioGroupMinorAlleles.isSelected())
					selmodel = genotypefreqlines.linecountminormodel;
				else if (radioGroup3rdAlleles.isSelected())
					selmodel = genotypefreqlines.line3rdallelemodel;
				else if (radioGroup4thAlleles.isSelected())
					selmodel = genotypefreqlines.line4thallelemodel;

				chartGroupAlleleFrequency.getYAxis().setTitle("Group Genotype count");
				chartGroupAlleleFrequency.setTitle("Group Genotype count");
				tooltipjs = genotypefreqlines.tooltipjs;
				show4th = genotypefreqlines.line4thallelemodel != null;

			}

			List listcols = new ArrayList();
			Iterator<String> itpop = allelefreqlines.mapPop2Majoralleles.keySet().iterator();
			while (itpop.hasNext()) {
				String subpop = itpop.next();
				String col = "black";
				try {
					col = Data.getKgroupColor(Integer.valueOf(subpop));
				} catch (Exception ex) {
					// col="black";
				}
				listcols.add(col);
			}
			chartGroupAlleleFrequency.setColors(listcols);
			AppContext.debug("chartcolor=" + listcols);

			Tooltip ttc = new Tooltip();
			ttc.setFormatter(new JavaScriptValue(tooltipjs));
			this.chartGroupAlleleFrequency.setTooltip(ttc);

			radioGroup4thAlleles.setVisible(show4th);

			chartGroupAlleleFrequency.setModel(selmodel);
			AxisLabels xlabels = chartGroupAlleleFrequency.getXAxis().getLabels();
			xlabels.setRotation(-75);
			xlabels.setAlign("right");
			chartGroupAlleleFrequency.getXAxis().setTickInterval(null);

			PlotLine plotLine = new PlotLine();
			plotLine.setValue(0);
			plotLine.setWidth(1);
			plotLine.setColor("#808080");
			chartGroupAlleleFrequency.getYAxis().addPlotLine(plotLine);

			Legend legend = chartGroupAlleleFrequency.getLegend();
			legend.setLayout("vertical");
			legend.setAlign("right");
			legend.setVerticalAlign("middle");
			legend.setBorderWidth(0);

			double min = 0;
			double max = queryRawResult.getListPos().size(); // genotypefreqlines.linecountmajormodel.getSeries(0).
																// //chartGroupAlleleFrequency.getXAxisSize();
			long interval = Math.max(1,
					Double.valueOf(Math.ceil(Double.valueOf((max - min) / nAllelefreqLabels))).longValue());
			AppContext.debug("max=" + max + "  min=" + min + "  (max-min)/" + nAllelefreqLabels + "="
					+ Double.valueOf(Math.ceil(Double.valueOf((max - min) / nAllelefreqLabels))).longValue()
					+ " allelefrqchartGroup xlabelsteps=" + interval);
			xlabels.setStep(interval);

		} catch (Exception ex) {
			ex.printStackTrace();
			Messagebox.show(ex.getMessage(), "Exception", Messagebox.OK, Messagebox.ERROR);
		}

	}

	@Listen("onClick = #buttonHaploUpdate")
	public void onclickUpdateHaplo() {
		try {
			haplofilename = null;
			tabDisplayHaloptypeimage();
			this.tabHaploHaploview.setSelected(true);
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

	public class Object2StringMatrixComparatorProvider<T> implements MatrixComparatorProvider<Object[]> {
		private int _x = -1;

		private boolean _acs;

		private Object2StringComparator _cmpr;

		public Object2StringMatrixComparatorProvider(boolean asc) {
			_acs = asc;
			_cmpr = new Object2StringComparator(this);
		}

		@Override
		public Comparator<Object[]> getColumnComparator(int columnIndex) {
			this._x = columnIndex;

			// AppContext.debug("sort by column " + columnIndex);

			return _cmpr;

		}

		// a real String comparator
		public class Object2StringComparator implements Comparator<Object[]> {
			private Object2StringMatrixComparatorProvider _mmc;

			public int getColumn() {
				return _mmc._x;
			}

			public Object2StringComparator(Object2StringMatrixComparatorProvider mmc) {
				_mmc = mmc;
			}

			@Override
			public int compare(Object[] o1, Object[] o2) {

				// try {
				// AppContext.debug( "o1_0=" + o1.get(0) + " o2_0=" + o2.get(0));
				// AppContext.debug( "o1[" + _mmc._x + "]=" + o1[_mmc._x] + ", o2[" + _mmc._x +
				// "]=" + o2[_mmc._x]);
				// AppContext.debug( "o1[" + _mmc._x + "]=" + o1.get(_mmc._x) + ", o2[" +
				// _mmc._x + "]=" + o2.get(_mmc._x));

				// return o1.get(_mmc._x).compareTo(o2.get(_mmc._x)) * (_acs ? 1 : -1);

				Object o1x = o1[_mmc._x];
				Object o2x = o2[_mmc._x];
				// returno1x.toString().compareTo( o2x.toString() ) * (_acs ? 1 : -1);

				String s1 = "";
				if (o1x != null)
					s1 = o1x.toString();
				String s2 = "";
				if (o2x != null)
					s2 = o2x.toString();

				// AppContext.debug(s1 + "\t" + s2);
				/*
				 * if(o1x==null && o2x==null) return 0; if(o1x==null) return -1*(_acs ? 1 : -1);
				 * if(o2x==null) return (_acs ? 1 : -1);
				 */

				if (s1.isEmpty() && s2.isEmpty())
					return 0;
				if (s1.isEmpty())
					return -1 * (_acs ? 1 : -1);
				if (s2.isEmpty())
					return (_acs ? 1 : -1);

				try {
					return ((Comparable) o1x).compareTo(o2x) * (_acs ? 1 : -1);
				} catch (Exception ex) {
					// ex.printStackTrace();
					try {
						return Double.valueOf(s1).compareTo(Double.valueOf(s2)) * (_acs ? 1 : -1);
					} catch (Exception ex2) {
						// ex2.printStackTrace();
						try {
							return s1.compareTo(s2) * (_acs ? 1 : -1);
						} catch (Exception ex3) {
							// ex3.printStackTrace();
							return 0;
						}
					}
				}
			}
		}
	}

	class BiglistRowheaderRenderer implements RowRenderer {

		@Override
		public void render(Row row, Object data, int index) throws Exception {

			Object[] obj = (Object[]) data;

			if (obj[3] == null || ((String) obj[3]).isEmpty()) {
				addListcell(row, obj[0]);
				addListcell(row, (obj[1] == null ? "" : obj[1]));
				addListcell(row, (obj[2] == null ? "" : obj[2]));
				addListcell(row, (obj[3] == null ? "" : obj[3]));
				addListcell(row, (obj[4] == null ? "" : obj[4]));
				if (obj.length > 5)
					addListcell(row, (obj[5] == null ? "" : obj[5]));
				if (obj.length > 6)
					addListcell(row, (obj[6] == null ? "" : obj[6]));
			} else {
				String color = Data.getSubpopulationColor((String) obj[3]);
				addListcell(row, obj[0], color);
				addListcell(row, (obj[1] == null ? "" : obj[1]), color);
				addListcell(row, (obj[2] == null ? "" : obj[2]), color);
				addListcell(row, (obj[3] == null ? "" : obj[3]), color);
				addListcell(row, (obj[4] == null ? "" : obj[4]), color);
				if (obj.length > 5)
					addListcell(row, obj[5]);
				if (obj.length > 6)
					addListcell(row, obj[6]);
			}

		}

		private void addListcell(Row listitem, Object value) {
			addListcell(listitem, value, null);
		}

		private void addListcell(Row listitem, Object value, String color) {

			Label lb = new Label((value == null ? "" : value.toString()));
			if (color == null || color.isEmpty())
				;
			else
				lb.setStyle("color:" + color);

			// Label lb = new Label(value.toString());
			lb.setParent(listitem);

		}
	}

	// ************************************************** HANDLE VISTA TAB EVENTS
	// ************************************************************

	private void updateVistaSubtabs(String ref, String contig, long start, long end, String org1, String org2,
			String org3, String org4) {
		this.tabVista1.setLabel(ref + " vs " + org1);
		this.tabVista2.setLabel(ref + " vs " + org2);
		this.tabVista3.setLabel(ref + " vs " + org3);
		this.tabVista4.setLabel(ref + " vs " + org4);

		vistaurl1 = createVistaRef2Othersurl(ref, contig, start, end, org1);
		vistaurl2 = createVistaRef2Othersurl(ref, contig, start, end, org2);
		vistaurl3 = createVistaRef2Othersurl(ref, contig, start, end, org3);
		vistaurl4 = createVistaRef2Othersurl(ref, contig, start, end, org4);

		tabVista1.setSelected(true);
		this.iframeVista1.setSrc(vistaurl1);
	}

	private void updateVistaTab(String ref, String contig, Collection colpos) {
		if (ref.toLowerCase().equals("any") || ref.toLowerCase().equals("locus"))
			return;
		Long min = Long.MAX_VALUE;
		Long max = Long.valueOf(0);
		if (colpos != null && !colpos.isEmpty()) {
			Iterator itPos = colpos.iterator();
			while (itPos.hasNext()) {
				Object objpos = itPos.next();
				BigDecimal pos = null;
				if (objpos instanceof BigDecimal)
					pos = (BigDecimal) objpos;
				else if (objpos instanceof Position)
					pos = ((Position) objpos).getPosition();
				if (min > pos.longValue())
					min = pos.longValue();
				if (max < pos.longValue())
					max = pos.longValue();
			}
		}
		updateVistaTab(ref, contig, Integer.valueOf(Math.max(0, min.intValue())),
				Integer.valueOf(max.intValue() + 100));
	}

	private void updateVistaTab(String ref, String contig, Integer start, Integer end) {
		if (ref.equals(AppContext.REFERENCE_NIPPONBARE()))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_9311))
			updateVistaSubtabs(ref, contig, start, end, AppContext.REFERENCE_NIPPONBARE(), Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_IR64))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, AppContext.REFERENCE_NIPPONBARE(),
					Organism.REFERENCE_KASALATH, Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_KASALATH))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					AppContext.REFERENCE_NIPPONBARE(), Organism.REFERENCE_DJ123);
		else if (ref.equals(Organism.REFERENCE_DJ123))
			updateVistaSubtabs(ref, contig, start, end, Organism.REFERENCE_9311, Organism.REFERENCE_IR64,
					Organism.REFERENCE_KASALATH, AppContext.REFERENCE_NIPPONBARE());
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

	@Listen("onClick =#tabVista")
	public void onclickTabvista() {

		updateVistaTab(this.listboxReference.getSelectedItem().getLabel(), this.selectChr.getValue(),
				this.intStart.getValue(), this.intStop.getValue());

	}

	@Listen("onClick =#tabVistaRev")
	public void onclickTabvistaRev() {
		if (this.vistaurlrev != null) {
			this.iframeVistaRev.setSrc(vistaurlrev);
			AppContext.debug("displaying: " + vistaurlrev);

			this.vistaurlrev = null;
		}
	}

	private String createVistaNPBurl(String org, String npbcontig, long npbstart, long npbend) {
		
		String url = "";
		if (org.equalsIgnoreCase("DJ123")) {
			url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=21641&base=4143&run=21641&pos="
					+ npbcontig + ":" + npbstart + "-" + npbend + "&genes=user&indx=0&cutoff=50";
		} else if (org.equalsIgnoreCase("IR64-21")) {
			url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37870&base=4143&run=37870&pos="
					+ npbcontig + ":" + npbstart + "-" + npbend + "&genes=user&indx=0&cutoff=50";
		} else if (org.equalsIgnoreCase("93-11")) {
			url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37668&base=4143&run=37668&pos="
					+ npbcontig + ":" + npbstart + "-" + npbend + "&genes=user&indx=0&cutoff=50";
		} else if (org.equalsIgnoreCase("Kasalath")) {
			url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37665&base=4143&run=37665&pos="
					+ npbcontig + ":" + npbstart + "-" + npbend + "&genes=user&indx=0&cutoff=50";
		}

		AppContext.debug("displaying: " + url);
		return url;

	}

	public static String createVistaRef2Othersurl(String ref, String refcontig, long refstart, long refend,
			String target) {
		String url = "";

		if (ref.equals(AppContext.REFERENCE_NIPPONBARE())) {
			if (target.equals(Organism.REFERENCE_9311))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37668";
			else if (target.equals(Organism.REFERENCE_IR64))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37870";
			else if (target.equals(Organism.REFERENCE_KASALATH))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=37665";
			else if (target.equals(Organism.REFERENCE_DJ123))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat06&run=21641";
		} else if (ref.equals(Organism.REFERENCE_9311)) {
			if (target.equals(AppContext.REFERENCE_NIPPONBARE()))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat07&run=37668&base=4144";
			else if (target.equals(Organism.REFERENCE_IR64))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat07&run=36371";
			else if (target.equals(Organism.REFERENCE_KASALATH))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat07&run=37794";
			else if (target.equals(Organism.REFERENCE_DJ123))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat07&run=36169";
		} else if (ref.equals(Organism.REFERENCE_IR64)) {
			if (target.equals(Organism.REFERENCE_9311))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat08&run=36371&base=4146";
			else if (target.equals(AppContext.REFERENCE_NIPPONBARE()))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat08&run=37870&base=4146";
			else if (target.equals(Organism.REFERENCE_KASALATH))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat08&run=35323";
			else if (target.equals(Organism.REFERENCE_DJ123))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat08&run=36564";
		} else if (ref.equals(Organism.REFERENCE_KASALATH)) {
			if (target.equals(Organism.REFERENCE_9311))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat11&run=37794&base=3591";
			else if (target.equals(Organism.REFERENCE_IR64))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat11&run=35323&base=3591";
			else if (target.equals(AppContext.REFERENCE_NIPPONBARE()))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat11&run=37665&base=3591";
			else if (target.equals(Organism.REFERENCE_DJ123))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?base=orySat11&run=38038";
		} else if (ref.equals(Organism.REFERENCE_DJ123)) {
			if (target.equals(Organism.REFERENCE_9311))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?run=36169&base=4153";
			else if (target.equals(Organism.REFERENCE_IR64))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?run=36564&base=4153";
			else if (target.equals(Organism.REFERENCE_KASALATH))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?run=38038&base=4153";
			else if (target.equals(AppContext.REFERENCE_NIPPONBARE()))
				url = "http://pipeline.lbl.gov/tbrowser/tbrowser/?run=21641&base=4153";
		}
		url += "&pos=" + refcontig + ":" + refstart + "-" + refend + "&genes=user&indx=0&cutoff=50";

		return url;

	}

	@Listen("onClick =#tabVistaNPB")
	public void onclickTabVistaNPB() {
		onclickTabNPBvsIR6421();
	}

	@Listen("onClick =#tabNPBvsIR6421")
	public void onclickTabNPBvsIR6421() {
		this.iframeNPBvsIR6421
				.setSrc(createVistaNPBurl("IR64-21", selectChr.getValue(), intStart.getValue(), intStop.getValue()));
	}

	@Listen("onClick =#tabNPBvs9311")
	public void onclicktabNPBvs9311() {
		this.iframeNPBvs9311
				.setSrc(createVistaNPBurl("93-11", selectChr.getValue(), intStart.getValue(), intStop.getValue()));

	}

	@Listen("onClick =#tabNPBvsKasalath")
	public void onclickTabNPBvsKas() {
		this.iframeNPBvsKasalath
				.setSrc(createVistaNPBurl("Kasalath", selectChr.getValue(), intStart.getValue(), intStop.getValue()));

	}

	@Listen("onClick =#tabNPBvsDJ123")
	public void onclickTabNPBvsDJ123() {
		this.iframeNPBvsDJ123
				.setSrc(createVistaNPBurl("DJ123", selectChr.getValue(), intStart.getValue(), intStop.getValue()));

	}

	@Listen("onSelect = #listboxHighlightVariety")
	public void on_select_highlight_vars() {
		listboxHighlightVarietyList.setSelectedIndex(0);
		listboxPhenotype.setSelectedIndex(0);
		if (listboxHighlightVariety.getSelectedIndex() > 0) {
			Set sethigh = new HashSet();
			sethigh.add(listboxHighlightVariety.getSelectedItem().getLabel().toUpperCase());
			show_mds_fromtable(chartMDS, sethigh);
		} else
			show_mds_fromtable(chartMDS);
	}

	@Listen("onSelect = #listboxHighlightVarietyList")
	public void on_select_highlight_list() {
		listboxHighlightVariety.setSelectedIndex(0);
		listboxPhenotype.setSelectedIndex(0);
		if (listboxHighlightVarietyList.getSelectedIndex() > 0) {
			workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");
			Set sethigh = new HashSet();
			Iterator itset = workspace.getVarieties(listboxHighlightVarietyList.getSelectedItem().getLabel().trim())
					.iterator();
			while (itset.hasNext()) {
				Object h = itset.next();
				if (h instanceof Variety) {
					sethigh.add(((Variety) h).getName().toUpperCase());
				} else if (h instanceof String) {
					sethigh.add(((String) h).toUpperCase());
				} else
					throw new RuntimeException(h.getClass().getCanonicalName() + " should be variety or variety name");
			}
			show_mds_fromtable(chartMDS, sethigh);
		} else
			show_mds_fromtable(chartMDS);

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

}