package org.irri.iric.portal.genotype.zkui;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class CheckboxDroplist extends GenericForwardComposer {

	private Component comp;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		this.comp = comp;

	}

	public Listbox getListbox() {
		return (Listbox) comp.getFellow("list");
	}

	public void onSelect$list(Event e) throws InterruptedException {
		Listbox listbox = (Listbox) comp.getFellow("checkboxdroplistGenotyperun");

		String str = "";

		for (Listitem li : listbox.getItems()) {
			if (!li.isSelected()) {
				continue;
			}
			if (!str.isEmpty()) {
				str += ", ";
			}
			str += li.getLabel();
		}
		Bandbox bandbox = (Bandbox) comp.getFellow("bandboxVarietyset");
		bandbox.setValue(str);
	}
}
