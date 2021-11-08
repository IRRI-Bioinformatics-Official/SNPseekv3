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
public class CreateLocusListController extends SelectorComposer<Component> {

	CookieController cookieController = new CookieController();
	SessionController sessionController = new SessionController();

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
	private Textbox txtboxEditListname;

	@Wire
	private Textbox txtboxEditNewList;

	@Wire
	Button cancelButton;

	@Wire
	private Window createWindow;

	public CreateLocusListController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		try {

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
		userList.setLocusList(txtboxEditNewList.getValue());

		Events.postEvent(Events.ON_CLOSE, createWindow, userList);

	}

}
