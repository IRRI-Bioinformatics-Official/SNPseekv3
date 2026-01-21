package user.ui.module;

import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.A;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import user.ui.module.util.constants.SessionConstants;

/**
 * A demo of Big listbox to handle 1 trillion data.
 * 
 * @author jumperchen
 */
public class HomeQueryController extends SelectorComposer<Div> {

	private User user;
	private Properties contentProp;

	@Wire
	private Label dsNumber;

	@Wire
	private A traitGenesCard;
	
	@Wire
	private A genotypeCard;
	
	@Wire
	private A varietiesCard;
	
	@Wire
	private A geneLociCard;
	
	@Wire
	private A downloadsCard;
	
	@Wire
	private A gwasCard;
	
	@Wire
	private A myListCard;
	
	@Wire
	private A jbrowseCard;
	
	@Wire
	private Label lbl_traitGenes;
	
	@Wire
	private Label lbl_myLst;
	
	@Wire
	private Label lbl_jbrowse;
	
	@Wire
	private Label lbl_download;
	
	@Wire
	private Label lbl_gwas;

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);
		contentProp = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		dsNumber.setValue("1");
		setLabelFeaturesVisibility(true);

		

		Notification.show("Announcement", "info", comp, "overlap_after", 3000);

		if (user != null) {
			dsNumber.setValue("13");
			setLabelFeaturesVisibility(false);
		}

		traitGenesCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = traitGenesCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateTraitGenes", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		myListCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = myListCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToMyList", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		jbrowseCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = jbrowseCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToJbrowse", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		genotypeCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = genotypeCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToGenotypeSearch", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		varietiesCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = varietiesCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToVarietiesSearch", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		geneLociCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = geneLociCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToGeneLociSearch", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		downloadsCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = downloadsCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToDownload", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
		
		gwasCard.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
		    public void onEvent(Event event) throws Exception {
		        System.out.println("click");
		        
		        // Try different ways to get genWin
		        Window genWin = (Window) Path.getComponent("//genWin");  // Note the double slash
		        
		        if (genWin == null) {
		            // Alternative: traverse up from current component
		            Component comp = gwasCard;
		            while (comp != null && !(comp instanceof Window && "genWin".equals(comp.getId()))) {
		                comp = comp.getParent();
		            }
		            genWin = (Window) comp;
		        }
		        
		        if (genWin != null) {
		            System.out.println("Found genWin, posting event");
		            Events.postEvent("onNavigateToGwas", genWin, null);
		        } else {
		            System.out.println("genWin not found!");
		        }
		    }
		});
	}

	private void setLabelFeaturesVisibility(boolean b) {
		lbl_traitGenes.setVisible(b);
		lbl_jbrowse.setVisible(b);
		lbl_myLst.setVisible(b);
		lbl_download.setVisible(b);
		lbl_gwas.setVisible(b);
	}

}