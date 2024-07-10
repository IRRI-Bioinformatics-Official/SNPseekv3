package user.ui.module;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
	private Navbar navbar;

	@Wire
	private Nav search;

	@Wire
	private Include contentInclude;
	
	@Wire
	private Div container;

	@Wire
	private Navitem collapseId;

	@Wire
	private Navitem jbrowse;

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

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());

		sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);

		Properties prop = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		if (prop == null) {
			prop = new Properties();
			InputStream contentManager = AppContext.class
					.getResourceAsStream("/" + UserConstants.ANONYMOUS + ".properties");
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
		orderSeeds.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_ORDERSEEDS).toString()));
		traitGenes.setVisible(Boolean.valueOf(prop.get(ContentConstants.SIDEBAR_TRAIT_GENES).toString()));

		contentInclude.setSclass("content");

		navbar.setCollapsed(false);

		// setSidebar(navbar, contentInclude);
	}

	/*
	 * public static void setSidebar(Navbar navbar, Include contentInclude) {
	 * mainNavbar = navbar; mainContentInclude = contentInclude;
	 * 
	 * }
	 * 
	 * public static void setSidebar(boolean toggle) { if (toggle) {
	 * mainNavbar.setCollapsed(false); mainContentInclude.setSclass("content"); }
	 * else { mainNavbar.setCollapsed(true);
	 * mainContentInclude.setSclass("content collapsed"); }
	 * 
	 * }
	 */

	@Listen("onClick =#menuitem_user")
	public void onbutton_addUser() {

		Session sess = Sessions.getCurrent();

		sess.removeAttribute("userCredential");

		Executions.sendRedirect("index.zul");

	}

	@Listen("onClick =#searchGenotype")
	public void searchGenotype() {
		collapsedSidebar();
		contentInclude.setSrc("/genotypeContent.zul");
		

	}

	private void collapsedSidebar() {
		navbar.setCollapsed(true);
		contentInclude.setSclass("content collapsed");
		
	}

	@Listen("onClick =#searchVariety")
	public void searchVariety() {
		collapsedSidebar();
		contentInclude.setSrc("/varietiesContent.zul");
	}

	@Listen("onClick =#searchGeneLoci")
	public void searchGeneLoci() {
		collapsedSidebar();
		contentInclude.setSrc("/geneLociContent.zul");
	}

	@Listen("onClick =#jbrowse")
	public void jbrowse() {
		collapsedSidebar();
		contentInclude.setSrc("/_jbrowse2.zul");
	}

	@Listen("onClick =#gwas")
	public void gwas() {
		collapsedSidebar();
		contentInclude.setSrc("/gwas.zul");
	}

	@Listen("onClick =#traitGenes")
	public void traitGenes() {
		collapsedSidebar();
		String paramstr = AppContext.getJBrowseDefaulttracks(AppContext.getDefaultDataset());
		// contentInclude.setSrc("/_ideo.zul?tracks=${paramstr}&amp;app=rice-ideogram");
		contentInclude.setSrc("/traitgenes.zul");

	}

	@Listen("onClick =#myList")
	public void myList() {
		collapsedSidebar();
		contentInclude.setSrc("/MyListContent.zul");
	}

	@Listen("onClick =#orderSeeds")
	public void orderSeeds() {
		collapsedSidebar();
		contentInclude.setSrc("/OrderContent.zul");
	}

	@Listen("onClick =#download")
	public void download() {
		collapsedSidebar();
		contentInclude.setSrc("/downloadContent.zul");
	}

	@Listen("onClick =#collapseId")
	public void onCollapse() {
		if (navbar.isCollapsed()) {
			navbar.setCollapsed(false);
			contentInclude.setSclass("content");
		} else {
			navbar.setCollapsed(true);
			contentInclude.setSclass("content collapsed");
		}
	}

}
