package org.irri.iric.portal.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImplAllelePvalue;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeFacade;

public class WorkspaceLoadLocal {

	public static void loadLocalFile(String folder, WorkspaceFacade workspace, GenotypeFacade genotype) {
		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		String organism = AppContext.getDefaultOrganism();

		boolean hasAllele = false;
		boolean hasPvalue = false;

		File directoryPath = new File(folder);
		// List of all files and directories
		File filesList[] = directoryPath.listFiles();

		Map<String, Map> mapChr2Pos2Pvalue = new HashMap();
		Map<String, Map> mapChr2Pos2Allele = new HashMap();

		Map<String, Set> mapChr2Set = new TreeMap();

		BufferedReader reader;

		for (File file : filesList) {
			try {
				reader = new BufferedReader(new FileReader(file));
				String line = reader.readLine();

				int isnp = 0;

				
				while (line != null) {
					

					try {
						String chrposline = line.trim();
						if (chrposline.isEmpty())
							continue;
						String chrpos[] = chrposline.split("\\s+");
						String chr = "";
						try {
							int intchr = Integer.valueOf(chrpos[0]);
							if (intchr > 9)
								chr = "chr" + intchr;
							else
								chr = "chr0" + intchr;
						} catch (Exception ex) {
							chr = chrpos[0].toLowerCase();
						}

						BigDecimal pos = null;
						try {
							pos = BigDecimal.valueOf(Long.valueOf(chrpos[1]));
						} catch (Exception ex) {
							AppContext.debug("Invalid position chrome position");
							continue;
						}

						Map<BigDecimal, String> mapPos2Allele = mapChr2Pos2Allele.get(chr);
						if (mapPos2Allele == null) {
							mapPos2Allele = new HashMap();
							mapChr2Pos2Allele.put(chr, mapPos2Allele);
						}
						Map<BigDecimal, Double> mapPos2Pvalue = mapChr2Pos2Pvalue.get(chr);
						if (mapPos2Pvalue == null) {
							mapPos2Pvalue = new HashMap();
							mapChr2Pos2Pvalue.put(chr, mapPos2Pvalue);
						}

						if (hasAllele) {
							mapPos2Allele.put(pos, chrpos[2]);
							if (hasPvalue) {
								try {
									mapPos2Pvalue.put(pos, Double.valueOf(chrpos[3]));
								} catch (Exception ex) {
									AppContext.debug("Invalid p-value " + chrpos[3]);
								}
							}
						} else if (hasPvalue) {
							try {
								mapPos2Pvalue.put(pos, Double.valueOf(chrpos[2]));
							} catch (Exception ex) {
								AppContext.debug("Invalid number " + chrpos[2]);
							}
						}

						Set setPos = mapChr2Set.get(chr);
						if (setPos == null) {
							setPos = new HashSet();
							mapChr2Set.put(chr, setPos);
						}
						setPos.add(pos);

					} catch (Exception ex) {
						AppContext.debug("onbuttonSaveSNP exception: ");
						ex.printStackTrace();
					}
					
					line = reader.readLine();
					isnp++;
				}

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Set<MultiReferencePosition> setSNPDBPos = new HashSet();
			// Set<MultiReferencePosition> setCoreSNPDBPos = new HashSet();

			Set setSNP = null;
			Set setChrSNP = new HashSet();
			Iterator<String> itChr = mapChr2Set.keySet().iterator();
			while (itChr.hasNext()) {
				String chr = itChr.next();
				setSNP = mapChr2Set.get(chr);

				if (hasAllele || hasPvalue) {
					Iterator<BigDecimal> itPos = setSNP.iterator();
					while (itPos.hasNext()) {
						BigDecimal ipos = itPos.next();
						setChrSNP.add(new MultiReferencePositionImplAllelePvalue(organism, chr, ipos,
								(String) mapChr2Pos2Allele.get(chr).get(ipos),
								(Double) mapChr2Pos2Pvalue.get(chr).get(ipos)));
					}

				} else {

					Iterator<BigDecimal> itPos = setSNP.iterator();
					while (itPos.hasNext()) {
						setChrSNP.add(new MultiReferencePositionImpl(organism, chr, itPos.next()));
					}
				}

			}

			onbuttonSaveSNPInChr(workspace, file.getName(), setChrSNP, null, null, hasAllele, hasPvalue);

		}
	}

	private static void onbuttonSaveSNPInChr(final WorkspaceFacade workspace, String filename, Set setSNP,
			Set setSNPDBPos, Set setCoreSNPDBPos, final boolean hasAllele, final boolean hasPvalue) {

		if (setCoreSNPDBPos == null && setSNPDBPos != null) {
			setCoreSNPDBPos = new HashSet(setSNPDBPos);
		}

		String newlistname = filename.replaceAll(":", "").trim();
		newlistname = FilenameUtils.removeExtension(newlistname);

		if (setSNPDBPos == null && setCoreSNPDBPos == null) {

			workspace.addSnpPositionList("ANY", newlistname, setSNP, hasAllele, hasPvalue);
				
			return;
		}


	}

}
