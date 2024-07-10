package org.irri.iric.portal.gwas.dao;

import java.util.List;

import org.irri.iric.portal.gwas.domain.GWASRun;

public interface GWASRunDAO {

	List<GWASRun> getGWASRuns();

	GWASRun getGWASRunByTrait(String trait);

	GWASRun getGWASRunByCoterm(String coterm);

	GWASRun getGWASRunByCodefinition(String codefinition);
}
