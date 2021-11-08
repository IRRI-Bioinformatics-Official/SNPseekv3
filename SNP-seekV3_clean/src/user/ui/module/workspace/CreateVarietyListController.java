package user.ui.module.workspace;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.SnpsAllvarsPos;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.zk.CookieController;
import org.irri.iric.portal.zk.ListboxMessageBox;
import org.irri.iric.portal.zk.SessionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@Controller
@Scope("session")
public class CreateVarietyListController extends SelectorComposer<Component> {

	CookieController cookieController = new CookieController();
	SessionController sessionController = new SessionController();

	private boolean isMsgboxEventSuccess = false;
	private boolean isDoneModal = false;

	@Autowired
	private ListItemsDAO listitemsdao;
	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;
	@Autowired
	private VarietyFacade variety;
	@Autowired
	private GenotypeFacade genotype;
	@Autowired
	private GenomicsFacade genomics;

	@Wire
	private Checkbox checkboxSavedata;

	@Wire
	private Checkbox checkboxAutoconfirm;
	@Wire
	private Checkbox checkboxVerifySNP;

	@Wire
	private Label labelMsgSNP;

	@Wire
	private Radiogroup rgPhenotype;
	@Wire
	private Listheader listheaderPosition;

	@Wire
	private Listbox listboxListnames;
	@Wire
	private Listbox listboxVarieties;
	@Wire
	private Listheader listheaderPhenotype;

	@Wire
	private Listbox listboxPositions;
	@Wire
	private Listbox listboxLocus;
	@Wire
	private Button buttonQueryIric;
	@Wire
	private Button buttonCreate;
	@Wire
	private Button buttonSave;
	@Wire
	private Button buttonCancel;
	@Wire
	private Button buttonDelete;
	@Wire
	private Vbox vboxEditNewList;
	@Wire
	private Textbox txtboxEditNewList;
	@Wire
	private Textbox txtboxEditListname;
	@Wire
	private Button buttonDownload;

	@Wire
	private Button buttonUpload;

	@Wire
	private Radio radioVariety;
	@Wire
	private Radio radioSNP;
	@Wire
	private Radio radioLocus;
	@Wire
	private Listbox selectChromosome;
	@Wire
	private Div divMsgVariety;
	@Wire
	private Div divMsgSNP;

	@Wire
	private Div divMsgLocus;
	@Wire
	private Label labelNItems;

	@Wire
	private Div divSetOps;
	@Wire
	private Button buttonUnion;
	@Wire
	private Button buttonIntersect;
	@Wire
	private Button buttonAminusB;
	@Wire
	private Button buttonBminusA;
	@Wire
	private Textbox textboxResultSet;

	@Wire
	private Vbox vboxListMembers;

	@Wire
	private Textbox textboxFrom;

	@Wire
	private Checkbox checkboxSNPAlelle;
	@Wire
	private Checkbox checkboxSNPPValue;
	@Wire
	private Label labelMsgFormat;
	@Wire
	private Div divSNPMoreData;

	@Wire
	private Hbox hboxDataset;

	@Wire
	private Label labelVarietyFormat;

	@Wire
	private Div divHasPhenotype;

	@Wire
	private Textbox textboxPhenotypename;

	@Wire
	private Radio radioQuantitative;
	@Wire
	private Radio radioCategorical;
	@Wire
	private Radio radioNoPhenotype;
	@Wire
	private Listbox listboxVariantset;
	@Wire
	private Listbox listboxDataset;
	@Wire
	private Bandbox bandboxVarietyset;

	@Wire
	Button cancelButton;

	@Wire
	private Window createWindow;

	public CreateVarietyListController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		try {

			variety = (VarietyFacade) AppContext.checkBean(variety, "VarietyFacade");
			List listDatasets = variety.getDatasets();

			SimpleListModel m = new SimpleListModel(listDatasets);
			m.setMultiple(true);
			listboxDataset.setModel(m);
			listboxDataset.setSelectedIndex(0);

			AppContext.debug("doAfterCompose ..done");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Listen("onClick =#cancelButton")
	public void cancelbutton() {
		Events.postEvent(Events.ON_CLOSE, createWindow, null);

	}

	@Listen("onClick =#buttonSave")
	public void createbutton() {

		CustomList userList = new CustomList();
		userList.setListname(txtboxEditListname.getValue());
		userList.setDataset(listboxDataset.getSelectedItem().getValue());
		userList.setPhenotype(rgPhenotype.getSelectedIndex());
		userList.setVarietyList(txtboxEditNewList.getValue());

		Events.postEvent(Events.ON_CLOSE, createWindow, userList);

	}

	@Listen("onSelect = #listboxDataset")
	public void onSelectcheckboxdroplistGenotyperun(Event e) throws InterruptedException {

		String str = "";

		for (Listitem li : listboxDataset.getItems()) {
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

	@Listen("onOpen = #bandboxVarietyset")
	public void onOpencheckboxdroplistGenotyperun(OpenEvent e) throws InterruptedException {
		AppContext.debug("e.isOpen()=" + e.isOpen() + " text=" + bandboxVarietyset.getText() + "  value="
				+ bandboxVarietyset.getValue());
		if (e.isOpen()) {
			setVarietyset(getDataset());
			return;
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
		for (Listitem li : listboxDataset.getItems()) {
			if (s.contains(li.getLabel())) {
				setsel.add(li);
			}
		}
		listboxDataset.setSelectedItems(setsel);
	}

	private Set getDataset() {
		Set s = new LinkedHashSet();
		String[] ds = bandboxVarietyset.getText().split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;

	}
}
