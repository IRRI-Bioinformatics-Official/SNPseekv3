package org.irri.iric.portal;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

public class FooterController extends SelectorComposer<Div> {
	
	@Wire
	private Label appVersion;
	
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		appVersion.setValue( AppContext.getAppVersion());
		    
	}	

}
