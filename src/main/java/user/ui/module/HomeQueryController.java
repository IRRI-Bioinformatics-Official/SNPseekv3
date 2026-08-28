package user.ui.module;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.irri.iric.ds.chado.domain.model.User;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.google.GoogleAnalyticsService;
import org.irri.iric.portal.google.PageAnalytics;
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
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
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
	
	@Wire
	private Label totalVisits;
	
	@Wire
	private Label aboutTitle;
	
	@Wire
	private Html aboutText;
	
	@Wire
	private Div providerDiv;
	
	@Wire
	private Image providerLogo;
	
	@Wire
	private Label providerText;
	
	@Wire
	private A providerLink;

	@Wire
	private Label datasetDesc;

	@Wire
	private Label varietiesValue;

	@Wire
	private Label varietiesDesc;

	@Wire
	private Label snpsValue;

	@Wire
	private Label snpsDesc;

	/**
	 * Initializes Controller to Genotype Module (GenotypeContent.zul)
	 * 
	 */
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		Session sess = Sessions.getCurrent();
		user = (User) sess.getAttribute(SessionConstants.USER_CREDENTIAL);
		contentProp = (Properties) sess.getAttribute(SessionConstants.CONTENT_MANAGER);

		dsNumber.setValue(AppContext.getStatDatasetValueAnon());
		datasetDesc.setValue(AppContext.getStatDatasetDesc());
		varietiesValue.setValue(AppContext.getStatVarietiesValue());
		varietiesDesc.setValue(AppContext.getStatVarietiesDesc());
		snpsValue.setValue(AppContext.getStatSnpsValue());
		snpsDesc.setValue(AppContext.getStatSnpsDesc());
		setLabelFeaturesVisibility(true);
		
		// About section - property-driven per deployment
		aboutTitle.setValue(AppContext.getAboutTitle());
		aboutText.setContent(AppContext.getAboutText());
		
		String providerLogoSrc = AppContext.getProviderLogo();
		String providerTextVal = AppContext.getProviderText();
		String providerLinkHref = AppContext.getProviderLink();
		
		boolean hasProvider = (providerLogoSrc != null && !providerLogoSrc.isEmpty())
			|| (providerTextVal != null && !providerTextVal.isEmpty());
		providerDiv.setVisible(hasProvider);
		if (hasProvider) {
			if (providerLogoSrc != null && !providerLogoSrc.isEmpty()) {
				providerLogo.setSrc(providerLogoSrc);
				providerLogo.setVisible(true);
			} else {
				providerLogo.setVisible(false);
			}
			providerText.setValue(providerTextVal != null ? providerTextVal : "");
			if (providerLinkHref != null && !providerLinkHref.equals("#")) {
				providerLink.setHref(providerLinkHref);
			} else {
				providerLink.setHref("#");
			}
		}

		// Google Analytics 
		
		if (AppContext.isEnableGoogleAnalytics()) {
			String propertyId = AppContext.getGooglePropertyId(); // e.g., "1234
			
			GoogleAnalyticsService service = new GoogleAnalyticsService(propertyId, "ga-credentials.json");
	        
	        try {
	            
	            long totalSessions = service.getTotalSessions(30);
	
	            totalVisits.setValue(String.valueOf(totalSessions));
	            
	            
	        } catch (IOException e) {
	            System.err.println("Error fetching analytics data: " + e.getMessage());
	            e.printStackTrace();
	        }
		} else {
			totalVisits.setValue("0");
		}

		

		Notification.show("Announcement", "info", comp, "overlap_after", 3000);

		if (user != null) {
			dsNumber.setValue(AppContext.getStatDatasetValueUser());
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