package user.ui.module;

import javax.servlet.http.HttpSession;

import org.irri.iric.portal.AppContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;

@Controller
@Scope("session")
public class DownloadController extends SelectorComposer<Component> {

	public DownloadController() {
		super();
		AppContext.debug("created WorkspaceController:" + this);
	}

	private HttpSession getHttpSession() {
		return (HttpSession) Sessions.getCurrent().getNativeSession();
	}

}
