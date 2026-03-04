package org.irri.iric.portal.email;

import org.irri.iric.portal.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;

public class SendEmailController extends SelectorComposer<Vlayout> {

	private static final long serialVersionUID = 1L;

	@Wire
	private Textbox txtTo;

	@Wire
	private Textbox txtBody;

	@Wire
	private Textbox txtBcc;
	
	@Wire
	private Textbox txtSubject;

	@Override
	public void doAfterCompose(Vlayout comp) throws Exception {
		super.doAfterCompose(comp);

	}

	@Listen("onClick=#btnSend")
	public void sendEmail() {
		try {
			MailUtils.sendReplyWithAttachments(txtTo.getValue(), txtBcc.getValue(), txtSubject.getValue(), txtBody.getValue(),  null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
