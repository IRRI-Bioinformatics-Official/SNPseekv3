package user.ui.module.workspace;

import java.util.LinkedHashSet;
import java.util.Set;

public class WorkspaceUtil {
	
	public static Set getDataset(String input) {
		Set s = new LinkedHashSet();
		String[] ds = input.split(",");
		for (int i = 0; i < ds.length; i++)
			s.add(ds[i].trim());
		return s;

	}

}
