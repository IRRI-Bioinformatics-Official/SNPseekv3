package org.irri.iric.portal.genomics.zkui;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.MergedLoci;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkmax.zul.Biglistbox;
import org.zkoss.zkmax.zul.GoldenPanel;
import org.zkoss.zul.A;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import user.ui.module.GenotypeQueryController;

public class LocusListItemRenderer implements ListitemRenderer {

	// private Map mapUniquename2Description;

	private String prefixDesc = "";

	private static String STYLE_INTERESTING = "font-weight:bold;color:red";
	private static String STYLE_BORING = "";
	boolean showoverlap = true;

	private GenotypeQueryController controller;

	private GoldenPanel genotypePanel;

	/*
	 * public LocusGridRenderer(Map mapUniquename2Description) { super();
	 * this.mapUniquename2Description = mapUniquename2Description; }
	 */

	@Override
	public void render(Listitem listitem, Object data, int index) throws Exception {

		Locus locus = (Locus) data;
		listitem.setValue(locus);

		addListcell(listitem, locus.getUniquename());
		addListcell(listitem, locus.getContig());
		addListcell(listitem, locus.getFmin().toString());
		addListcell(listitem, locus.getFmax().toString());
		addListcell(listitem, locus.getStrand().toString());

		if (locus instanceof MergedLoci) {
			MergedLoci ml = (MergedLoci) locus;
			StringBuffer loci = new StringBuffer();
			if (ml.getMSU7Name() != null && !locus.getUniquename().startsWith("LOC_"))
				loci.append(ml.getMSU7Name());
			if (ml.getRAPRepName() != null
					&& !(locus.getUniquename().startsWith("Os0") || locus.getUniquename().startsWith("Os1"))) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getRAPRepName());
			}
			if (ml.getRAPPredName() != null
					&& !(locus.getUniquename().startsWith("Os0") || locus.getUniquename().startsWith("Os1"))) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getRAPPredName());
			}
			if (ml.getIRICName() != null && !locus.getUniquename().startsWith("OsNippo")) {
				if (loci.length() > 0)
					loci.append(",");
				loci.append(ml.getIRICName());
			}

			if (loci.length() == 0 && ml.getFGeneshName() != null)
				loci.append(ml.getFGeneshName());
			addListcell(listitem, loci.toString());
		} else
			addListcell(listitem, "");

		if (locus.getDescription() == null) {
			addListcell(listitem, "");
		} else {
			if (prefixDesc == null) {
				addListcell(listitem, locus.getDescription().split("\\s+", 2)[1]);
			} else {
				addListcell(listitem, prefixDesc + locus.getDescription());
			}
		}

		addListcellIcons(listitem, locus);
	}

	public LocusListItemRenderer(boolean showoverlap) {
		super();
		this.showoverlap = showoverlap;
	}

	public LocusListItemRenderer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocusListItemRenderer(String prefixDesc) {
		super();
		this.prefixDesc = prefixDesc;
	}

	public LocusListItemRenderer(GenotypeQueryController controller, GoldenPanel genotypePanel) {
		this.controller = controller;
		this.genotypePanel = genotypePanel;
	}

	private void addListcell(Listitem listitem, String value) {
		addListcell(listitem, value, STYLE_BORING);
	}

	private void addListcellIcons(Listitem listitem, Locus locus) {
		addListcellIcons(listitem, locus, STYLE_BORING);
	}

	private void addListcell(Listitem listitem, String value, String style) {
		Listcell lc = new Listcell();
		Label lb = new Label(value);
		if (!style.isEmpty())
			lb.setStyle(style);
		lb.setParent(lc);
		lc.setParent(listitem);

	}

	private void addListcellIcons(Listitem listitem, Locus locus, String style) {
		// Action icons cell
		Listcell actionCell = new Listcell();

		// Get Data icon
		A dataLink = new A();
		dataLink.setIconSclass("z-icon-database");
		dataLink.setTooltiptext("Get Data");
		dataLink.setStyle("cursor:pointer; margin-right:15px; font-size:16px; color:#1976D2;");
		dataLink.addEventListener(Events.ON_CLICK, event -> {
			// Handle get data action
			System.out.println("Get Data clicked for: " + locus.getUniquename());
			// Add your logic here, e.g.:
			// Events.postEvent("onGetData", listitem.getListbox(), locus);
		});

		// Search Genotype icon
		A genotypeLink = new A();
		genotypeLink.setIconSclass("z-icon-search");
		genotypeLink.setTooltiptext("Search Genotype");
		genotypeLink.setStyle("cursor:pointer; font-size:16px; color:#4CAF50;");
		genotypeLink.addEventListener(Events.ON_CLICK, event -> {
			System.out.println("Search Genotype clicked for: " + locus.getUniquename());

			// Validate
			if (controller == null) {
				System.out.println("ERROR: Controller is null!");
				return;
			}

			if (genotypePanel == null) {
				System.out.println("ERROR: GoldenPanel is null!");
				return;
			}

			// Show panel
			genotypePanel.setVisible(true);

			// Run search - this updates controller's components
			controller.performGenotypeSearch(locus);

			// Get controller's components
			Biglistbox controllerBiglistbox = controller.getBiglistboxArray();
			Grid controllerGrid = controller.getGridBiglistheader();

			// Find panel's components
			Biglistbox panelBiglistbox = findBiglistbox(genotypePanel);
			Grid panelGrid = findGrid(genotypePanel, "gridBiglistheader");

			System.out.println("Controller Biglistbox: " + controllerBiglistbox);
			System.out.println("Panel Biglistbox: " + panelBiglistbox);
			System.out.println("Controller Grid: " + controllerGrid);
			System.out.println("Panel Grid: " + panelGrid);

			// Copy Biglistbox model if different instances
			if (panelBiglistbox != null && controllerBiglistbox != null && panelBiglistbox != controllerBiglistbox) {
				System.out.println("Copying Biglistbox model...");
				panelBiglistbox.setMatrixRenderer(controllerBiglistbox.getMatrixRenderer());
				panelBiglistbox.setModel(controllerBiglistbox.getModel());
				panelBiglistbox.invalidate();
			}

			// Copy Grid properties if different instances
			if (panelGrid != null && controllerGrid != null && panelGrid != controllerGrid) {
				System.out.println("Copying Grid properties...");
				copyGridProperties(controllerGrid, panelGrid);
			}
		});

		actionCell.appendChild(dataLink);
		actionCell.appendChild(genotypeLink);
		listitem.appendChild(actionCell);
	}
	
	
	 /**
     * Find Grid by ID in component tree
     */
    private Grid findGrid(Component parent, String gridId) {
        if (parent == null) return null;
        
        if (parent instanceof Grid) {
            Grid grid = (Grid) parent;
            if (gridId == null || gridId.equals(grid.getId())) {
                return grid;
            }
        }
        
        for (Component child : parent.getChildren()) {
            Grid found = findGrid(child, gridId);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
    
    /**
     * Copy Grid properties from source to target
     */
    private void copyGridProperties(Grid source, Grid target) {
        if (source == null || target == null) return;
        
        // Copy visibility
        target.setVisible(source.isVisible());
        
        // Copy model if exists
        if (source.getModel() != null) {
            target.setModel(source.getModel());
        }
        
        // Copy row renderer if exists
        if (source.getRowRenderer() != null) {
            target.setRowRenderer(source.getRowRenderer());
        }
        
        // Refresh
        target.invalidate();
    }

	private GenotypeQueryController findController(Component parent) {
		if (parent == null) {
			return null;
		}

		// Check $composer attribute
		Object composer = parent.getAttribute("$composer");
		if (composer instanceof GenotypeQueryController) {
			return (GenotypeQueryController) composer;
		}

		// Check controller attribute
		Object controller = parent.getAttribute("controller");
		if (controller instanceof GenotypeQueryController) {
			return (GenotypeQueryController) controller;
		}

		// Search children
		for (Component child : parent.getChildren()) {
			GenotypeQueryController found = findController(child);
			if (found != null) {
				return found;
			}
		}

		return null;
	}

	private Biglistbox findBiglistbox(Component parent) {
		if (parent instanceof Biglistbox) {
			return (Biglistbox) parent;
		}
		for (Component child : parent.getChildren()) {
			Biglistbox found = findBiglistbox(child);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

}
