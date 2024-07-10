package user.ui.module.workspace;

import java.util.ArrayList;
import java.util.List;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.variety.VarietyFacade;
import org.irri.iric.portal.zk.CookieController;
import org.irri.iric.portal.zk.SessionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@Controller
@Scope("session")
public class CreateSNPListController extends SelectorComposer<Component> {

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
	private Checkbox checkboxSNPAlelle;
	@Wire
	private Checkbox checkboxSNPPValue;
	@Wire
	private Checkbox checkboxVerifySNP;

	@Wire
	private Listbox listboxVariantset;

	@Wire
	private Textbox txtboxEditListname;

	@Wire
	private Listbox selectChromosome;

	@Wire
	private Textbox txtboxEditNewList;

	@Wire
	Button cancelButton;

	@Wire
	private Window createWindow;

	public CreateSNPListController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		try {
			listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");
			List listVS = new ArrayList();
			listVS.addAll(listitemsdao.getSnpsets());

			List listContigs = new ArrayList();
			listContigs.add("");
			listContigs.add("chr01");
			listContigs.add("chr02");
			listContigs.add("chr03");
			listContigs.add("chr04");
			listContigs.add("chr05");
			listContigs.add("chr06");
			listContigs.add("chr07");
			listContigs.add("chr08");
			listContigs.add("chr09");
			listContigs.add("chr10");
			listContigs.add("chr11");
			listContigs.add("chr12");
			listContigs.add("ANY");

			selectChromosome.setModel(new SimpleListModel(listContigs));
			selectChromosome.setSelectedIndex(listContigs.size() - 1);

			if (listVS.size() < 4)
				listboxVariantset.setRows(listVS.size());
			SimpleListModel listmodelvs = new SimpleListModel(listVS);
			listmodelvs.setMultiple(true);
			listboxVariantset.setModel(listmodelvs);

			checkboxSNPAlelle.setChecked(false);
			checkboxSNPPValue.setChecked(false);

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
		userList.setSnpList(txtboxEditNewList.getValue());
		userList.setChromosome(selectChromosome.getSelectedItem().getValue());
		userList.setSnpAllele(checkboxSNPAlelle.isChecked() ? true : false);
		userList.setSnpPvalue(checkboxSNPPValue.isChecked() ? true : false);
		userList.setVerifySnp(checkboxVerifySNP.isChecked() ? true : false);
		
		Events.postEvent(Events.ON_CLOSE, createWindow, userList);

	}

}
