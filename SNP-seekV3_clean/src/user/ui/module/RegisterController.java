package user.ui.module;

import org.irri.iric.portal.DataLogins;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

public class RegisterController extends SelectorComposer<Component> {

	@Wire
	private Textbox txtboxEmail;

	@Wire
	private Textbox txtbox_firstname;
	@Wire
	private Textbox txtbox_lastname;

	@Wire
	private Textbox txtboxOrganization;

	@Wire
	private Textbox txtbox_password;

	@Wire
	private Textbox txtbox_password2;

//	private UserService u_serv;
//
//	private UserService userv;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

	}

	@Listen("onClick =#buttonSubmit")
	public void onbutton_addUser() {

//		userv = (UserService) AppContext.checkBean(userv, "UserService");
//
//		if (txtbox_password.getValue().equals(txtbox_password2.getValue())) {
//			User user = new User();
//
//			user.setEmailadd(txtboxEmail.getValue());
//			user.setLastname(txtbox_lastname.getValue());
//			user.setFirstname(txtbox_firstname.getValue());
//			user.setInstitution(txtboxOrganization.getValue());
//			user.setPassword(AES256.encrypt(txtbox_password.getValue()));
//
//			u_serv.save(user);
//		} else
//			Notification.show("Password does not match");

	}

}
