package user.ui.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.WorkspaceFacade;
import org.irri.iric.portal.admin.WorkspaceLoadLocal;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.portal.properties.WebVariableConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.zul.Nav;
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;

import user.ui.module.util.constants.ContentConstants;
import user.ui.module.util.constants.SessionConstants;
import user.ui.module.util.constants.UserConstants;

public class SidebarController extends SelectorComposer<Component> {

	private Session sess;

	
	@Wire
	private Nav search;

	@Wire
	private Include contentInclude;

	@Wire
	private Div container;

	@Wire
	private Navitem jbrowse;
	
	@Wire
	private Navitem dashboard;

	@Wire
	private Navitem jbrowse2;

	@Wire
	private Navitem gwas;

	@Wire
	private Navitem download;

	@Wire
	private Navitem myList;

	@Wire
	private Navitem orderSeeds;

	@Wire
	private Navitem traitGenes;

	private User user;

	private String paramstr;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	@Qualifier("WorkspaceFacade")
	private WorkspaceFacade workspace;

	private String page;

	private String src;

	private String from;

	private String phenotype;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		String message = (String) Sessions.getCurrent().getAttribute(SessionConstants.NOTIFICATION);

		if (message != null) {
			Clients.showNotification(message, "info", null, "middle_center", 10000, true);
			// Optionally, clear the attribute to prevent it from showing multiple times
			Sessions.getCurrent().removeAttribute(SessionConstants.NOTIFICATION);
		}

		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");
		workspace = (WorkspaceFacade) AppContext.checkBean(workspace, "WorkspaceFacade");

		AppContext.debug(AppContext.getFlatfilesDir() + "preloadSNPS/");
		WorkspaceLoadLocal.loadSNPLocalFile(AppContext.getFlatfilesDir() + "/preloadSNPS/", workspace, genotype);

		if (user != null) {
			File directory = new File(AppContext.getFlatfilesDir() + WebVariableConstants.USER_DIR + File.separator
					+ user.getEmail() + File.separator + WebVariableConstants.SNP_DIR);
			WorkspaceLoadLocal.loadSNPLocalFile(directory.getAbsolutePath(), workspace, genotype);
			WorkspaceLoadLocal.initUserVarietyList(user.getEmail(), workspace);
			WorkspaceLoadLocal.initUserList(WebVariableConstants.LOCUS_DIR, user.getEmail(), workspace);

		}
//		sess = Sessions.getCurrent();
//		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		Properties prop = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		if (prop == null) {
			prop = new Properties();
			InputStream contentManager = AppContext.class
					.getResourceAsStream("/ACCESS_" + UserConstants.ANONYMOUS + ".properties");
			try {

				prop.load(contentManager);

			} catch (IOException e) {

				e.printStackTrace();
			}

		}

		download.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_DOWNLOAD).toString()));
		gwas.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_GWAS).toString()));
		myList.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_MYLIST).toString()));
		jbrowse.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_JBROWSE).toString()));
		jbrowse2.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_JBROWSE2).toString()));
		orderSeeds.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_ORDERSEEDS).toString()));
		traitGenes.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_TRAIT_GENES).toString()));

		//contentInclude.setSclass("content");


		page = Executions.getCurrent().getParameter("page");
		src = Executions.getCurrent().getParameter("src");
		from = Executions.getCurrent().getParameter("from");
		phenotype = Executions.getCurrent().getParameter("phenotype");

		if (page != null) {

			AppContext.debug("from params.. " + page);
			if (page.equals("workspace.zul")) {
				myList();

			}

		}

	}

	@Listen("onClick =#menuitem_user")
	public void onbutton_addUser() {

		Session sess = Sessions.getCurrent();

		sess.removeAttribute("userCredential");

		Executions.sendRedirect("index.zul");

	}

	@Listen("onClick =#searchGenotype")
	public void searchGenotype() {
		contentInclude.setSrc("/genotypeContent.zul");

	}

	@Listen("onNavigateTraitGenes = #genWin")
	public void navigateToTraitGenes() {
		// Check if user is logged in
		if (sess == null)
			sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
			showLoginDialog();
		} else {
			contentInclude.setSrc("/traitgenes.zul");
		}
	}

	@Listen("onNavigateToMyList = #genWin")
	public void navigateToMyList() {
		if (sess == null)
			sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
			showLoginDialog();
		} else {
			if (src != null && from != null)
				contentInclude.setSrc("/MyListContent.zul?from=" + from + "&src" + src);
			else
				contentInclude.setSrc("/MyListContent.zul");
		}
	}

	@Listen("onNavigateToGenotypeSearch = #genWin")
	public void navigateToGenotypeSearch() {
		searchGenotype();
	}

	@Listen("onNavigateToVarietiesSearch = #genWin")
	public void onNavigateToVarietiesSearch() {
		searchVariety();
	}

	@Listen("onNavigateToGeneLociSearch = #genWin")
	public void onNavigateToGeneLociSearch() {
		searchGeneLoci();
	}

	@Listen("onNavigateToDownload = #genWin")
	public void onNavigateToDownload() {
		if (sess == null)
			sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
			showLoginDialog();
		} else {
			download();
		}
	}

	@Listen("onNavigateToGwas = #genWin")
	public void onNavigateToGwas() {
		if (sess == null)
			sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
			showLoginDialog();
		} else {
			gwas();
		}
	}

	@Listen("onNavigateToJbrowse = #genWin")
	public void navigateToJbrowse() {
		if (sess == null)
			sess = Sessions.getCurrent();

		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		if (user == null) {
			showLoginDialog();
		} else {
			contentInclude.setSrc("/jbrowse.zul");
		}
	}

	private void showLoginDialog() {
		Messagebox.show("Please login to access this feature. \n Proceed logging in?", "Login Required",
				new Messagebox.Button[] { Messagebox.Button.OK, Messagebox.Button.CANCEL }, Messagebox.EXCLAMATION,
				new org.zkoss.zk.ui.event.EventListener() {
					public void onEvent(Event evt) {
						if (evt.getName().equals("onOK")) {
							Executions.sendRedirect("login.zul");
						}
					}
				});
	}

	@Listen("onClick =#1k1")
	public void search1k1() {
		contentInclude.setSrc("/1k1Content.zul");

	}

	@Listen("onClick =#searchVariety")
	public void searchVariety() {
		contentInclude.setSrc("/varietiesContent.zul");
	}

	@Listen("onClick =#searchGeneLoci")
	public void searchGeneLoci() {
		contentInclude.setSrc("/geneLociContent.zul");
	}

	@Listen("onClick =#jbrowse")
	public void jbrowse() {
		contentInclude.setSrc("/jbrowse.zul");
	}

	@Listen("onClick =#jbrowse2")
	public void jbrowse2() {
		contentInclude.setSrc("/_jbrowse2.zul");
	}
	
	@Listen("onClick =#dashboard")
	public void dashboard() {
		contentInclude.setSrc("/home.zul");
	}

	@Listen("onClick =#gwas")
	public void gwas() {
		contentInclude.setSrc("/gwas.zul");
	}

	@Listen("onClick =#traitGenes")
	public void traitGenes() {
		String paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());
		// contentInclude.setSrc("/_ideo.zul?tracks=${paramstr}&amp;app=rice-ideogram");
		contentInclude.setSrc("/traitgenes.zul");

	}

	@Listen("onClick =#myList")
	public void myList() {
		if (src != null && from != null)
			contentInclude.setSrc("/MyListContent.zul?from=" + from + "&src" + src);
		else
			contentInclude.setSrc("/MyListContent.zul");
	}

	@Listen("onClick =#orderSeeds")
	public void orderSeeds() {
		contentInclude.setSrc("/OrderContent.zul");
	}

	@Listen("onClick =#download")
	public void download() {
		contentInclude.setSrc("/downloadContent.zul");
	}

}
