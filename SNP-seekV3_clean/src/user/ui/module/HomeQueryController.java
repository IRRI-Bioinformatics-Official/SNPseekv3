package user.ui.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.irri.iric.ds.chado.domain.CvTermUniqueValues;
import org.irri.iric.ds.chado.domain.Gene;
import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.SnpsEffect;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.ds.chado.domain.model.VSnpeff;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.ContentPropertyConstants;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.irri.iric.portal.SimpleListModelExt;
import org.irri.iric.portal.User;
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
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Biglistbox;
import org.zkoss.zkmax.zul.GoldenLayout;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zkmax.zul.MatrixComparatorProvider;
import org.zkoss.zkmax.zul.Portallayout;
import org.zkoss.zkmax.zul.event.ScrollEventExt;
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
import org.zkoss.zul.Grid;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Span;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;
import org.zkoss.zul.Window.Mode;

import user.ui.module.util.FakerMatrixModel;
import user.ui.module.util.Object2StringMatrixComparatorProvider;
import user.ui.module.util.constants.ContentConstants;
import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * 
 * @author jumperchen
 */
public class HomeQueryController extends SelectorComposer<Window> {

	private User user;
	private Properties contentProp;
	
	@Wire
	private Label dsNumber;
	

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		
		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);
		contentProp = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);
		
		if (user != null) {
			dsNumber.setValue("14");
		}else
			dsNumber.setValue("1");
		

	}

}