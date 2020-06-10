package edu.scripps.yates.utilities.luciphor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.fasta.FastaParser;
import edu.scripps.yates.utilities.proteomicsmodel.PSM;
import edu.scripps.yates.utilities.proteomicsmodel.factories.MSRunEx;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PSMEx;
import edu.scripps.yates.utilities.proteomicsmodel.utils.KeyUtils;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

public class LuciphorReader {
	private final static Logger log = Logger.getLogger(LuciphorReader.class);
	private final File luciphorFile;
	private Map<String, PSM> psms;
	private boolean loaded;
	private static final String PREDICTED_SEQUENCE = "Predicted_Sequence";
	private static final String SCANS = "Scans";
	private static final String FILE_NAMES = "Filenames";

	public LuciphorReader(File luciphorFile) {
		this.luciphorFile = luciphorFile;
	}

	public Map<String, PSM> getPSMs() {
		if (!loaded) {
			load();
		}
		return this.psms;
	}

	private void load() {
		try {
			this.psms = new THashMap<String, PSM>();
			log.info("Reading luciphor file '" + this.luciphorFile.getAbsolutePath() + "'");
			final List<String> lines = Files.readAllLines(this.luciphorFile.toPath());
			TObjectIntMap<String> indexesByHeader = null;
			// num lines with ambiguous psms
			int numLineswithRepeatedPSMs = 0;
			for (int numLine = 0; numLine < lines.size(); numLine++) {
				final String line = lines.get(numLine);
				final String[] split = line.split("\t");
				if (numLine == 0) {
					indexesByHeader = getIndexesByHeader(split);
					continue;
				}
				final String fullSequence = split[indexesByHeader.get(PREDICTED_SEQUENCE)];
				final String sequence = FastaParser.cleanSequence(fullSequence);
				final String scans = split[indexesByHeader.get(SCANS)];
				final TIntList scanNumbersList = getScanNumbers(scans);
				final String fileNames = split[indexesByHeader.get(FILE_NAMES)];
				final List<String> fileNameList = getFileNames(fileNames);
				final String chargeStates = split[indexesByHeader.get("Charge_States")];
				final TIntList chargeStatesList = getScanNumbers(chargeStates);
				if (scanNumbersList.size() != fileNameList.size()) {
					throw new IllegalArgumentException(
							"Error parsing Luciphor at line " + (numLine + 1) + ". Odd number of scan numbers '" + scans
									+ "' and file name lists '" + fileNames + "' for peptide " + fullSequence);
				}
				if (fileNameList.size() != chargeStatesList.size()) {
					throw new IllegalArgumentException("Error parsing Luciphor at line " + (numLine + 1)
							+ ". Odd number of file name lists '" + fileNames + "' and charge states '" + fileNames
							+ "' for peptide " + fullSequence);
				}
				for (int i = 0; i < fileNameList.size(); i++) {
					final String fileName = fileNameList.get(i);
					final int chargeState = chargeStatesList.get(i);
					final int scanNumber = scanNumbersList.get(i);
					final String psmID = KeyUtils.getInstance().getSpectrumKey(String.valueOf(scanNumber), fullSequence,
							sequence, chargeState, true, true);
					final PSMEx psm = new PSMEx(psmID, sequence, fullSequence, true, true);
					psm.setMSRun(new MSRunEx(fileName, fileName));
					psm.setScanNumber(String.valueOf(scanNumber));
					psm.setChargeState(chargeState);
					if (this.psms.containsKey(psm.getKey())) {
						numLineswithRepeatedPSMs++;
//						log.warn(psm.getKey() + "\t" + psm + "\t\t" + psms.get(psm.getKey()));
						System.out.println(numLine + " " + line);
					}
					this.psms.put(psm.getKey(), psm);
				}
			}
			System.out.println(numLineswithRepeatedPSMs + " lines with repeated PSMs on them");
			loaded = true;
			log.info(this.psms.size() + " PSMs read.");
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getFileNames(String fileNames) {
		final List<String> ret = new ArrayList<String>();
		final String[] split = fileNames.split(",");
		for (final String fileName : split) {
			ret.add(fileName);
		}
		return ret;
	}

	private TIntList getScanNumbers(String scans2) {
		final TIntList ret = new TIntArrayList();
		final String[] split = scans2.split(",");
		for (final String scan : split) {
			ret.add(Integer.valueOf(scan));
		}
		return ret;
	}

	private TObjectIntMap<String> getIndexesByHeader(String[] split) {
		final TObjectIntMap<String> ret = new TObjectIntHashMap<String>();
		for (int i = 0; i < split.length; i++) {
			ret.put(split[i], i);
		}
		return ret;
	}
}
