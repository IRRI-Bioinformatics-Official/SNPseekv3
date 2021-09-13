package org.irri.iric.portal.gwas.domain;

import java.math.BigDecimal;

public interface GWASRun {

	BigDecimal getGwasRunId();

	String getTrait();

	String getSubpopulation();

	String getCoterm();

	BigDecimal getCotermId();

	String getCodefinition();

}
