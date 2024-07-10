package org.sibol.eves.controller;

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
import org.zkoss.zul.Include;
import org.zkoss.zul.Messagebox;

public class SidebarController extends SelectorComposer<Component> {

	private Session sess;

	@Wire
	private Navbar navbar;

	@Wire
	private Include contentInclude;

	@Wire
	private Navitem collapseId;

	@Wire
	private Navitem search;

	private boolean toggle;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		toggle = true;

	}

	@Listen("onClick =#menuitem_user")
	public void onbutton_addUser() {

		Session sess = Sessions.getCurrent();

		sess.removeAttribute("userCredential");

		Executions.sendRedirect("index.zul");

	}

	@Listen("onClick =#search")
	public void search() {
		contentInclude.setSrc("search.zul");
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
