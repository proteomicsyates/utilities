package edu.scripps.yates.utilities.proteomicsmodel.utils;

import java.util.Collections;
import java.util.Comparator;

import edu.scripps.yates.utilities.fasta.FastaParser;
import edu.scripps.yates.utilities.fasta.dbindex.IndexedProtein;
import edu.scripps.yates.utilities.grouping.GroupableProtein;
import edu.scripps.yates.utilities.grouping.ProteinGroup;
import edu.scripps.yates.utilities.proteomicsmodel.PSM;

public class KeyUtils {
	private static KeyUtils instance;

	protected KeyUtils() {

	}

//	/**
//	 * Gets sequence key using charge or not, but not distinguishing PTMs on the
//	 * sequence
//	 * 
//	 * @param psm
//	 * @param chargeStateSensible
//	 * @return
//	 */
	// DISABLED TO ALWAYS CONSIDER CHARGE AND MODIFIED SEQUENCE
//	public String getSequenceChargeKey(PSM psm, boolean chargeStateSensible) {
//		return getSequenceChargeKey(psm, false, chargeStateSensible);
//	}

	public String getSequenceChargeKey(PSM psm, boolean distinguishModifiedPeptides, boolean chargeStateSensible) {

		String seq = null;

		if (distinguishModifiedPeptides) {
			seq = psm.getFullSequence();
		} else {
			seq = FastaParser.cleanSequence(psm.getFullSequence());

		}
		if (chargeStateSensible) {
			return seq + "-" + psm.getChargeState();
		} else {
			return seq;
		}
	}

	// public static String getProteinKey(DTASelectProtein protein) {
	// final String locus = protein.getLocus();
	// return FastaParser.getACC(locus).getFirstelement();
	// }

	// public static String getProteinKey(String locus) {
	// return FastaParser.getACC(locus).getFirstelement();
	// }

	public String getProteinKey(IndexedProtein indexedProtein, boolean ignoreACCFormat) {
		String fastaDefLine = indexedProtein.getFastaDefLine();

		if (ignoreACCFormat) {
			if (fastaDefLine.startsWith(">")) {
				fastaDefLine = fastaDefLine.substring(1);
			}
			if (fastaDefLine.contains(" ")) {
				return fastaDefLine.substring(0, fastaDefLine.indexOf(" "));
			} else {
				return fastaDefLine;
			}
		}
		return FastaParser.getACC(fastaDefLine).getAccession();
	}

	/**
	 * Gets spectrum key with distinguishModifiedSequences=true
	 * 
	 * @param psm
	 * @param chargeSensible
	 * @return
	 */
	// DISABLED TO ALWAYS CONSIDER THE CHARGE AND THE MODIFIED SEQUENCE
//	public String getSpectrumKey(PSM psm, boolean chargeSensible) {
//		return getSpectrumKey(psm, chargeSensible, true);
//
//	}

	/**
	 *
	 * @param psm
	 * @param chargeSensible               if true, then, the charge will be
	 *                                     considered for differentiating peptides
	 *                                     with different charge states. If false,
	 *                                     peptides with different charge states
	 *                                     will have the same key
	 * @param distinguishModifiedSequences
	 * @return
	 */
	public String getSpectrumKey(PSM psm, boolean distinguishModifiedSequences, boolean chargeSensible) {

		final StringBuilder sb = new StringBuilder();
		if (psm.getScanNumber() != null) {
			sb.append(psm.getScanNumber());
		}
		if (!"".equals(sb.toString())) {
			sb.append("-");
		}
		if (distinguishModifiedSequences) {
			if (psm.getFullSequence() != null) {
				sb.append(psm.getFullSequence());
			}
		} else {
			if (psm.getSequence() != null) {
				sb.append(psm.getSequence());
			}
		}

		if (chargeSensible) {
			if (!"".equals(sb.toString())) {
				sb.append("-");
			}
			sb.append(psm.getChargeState());
		}
		return sb.toString();
	}
	// /**
	// * returns ionSerieTypeName+numIon+spectrumKey
	// *
	// * @param ratio
	// * @param peptide
	// * @param chargeSensible
	// * if true, then, the charge will be considered for
	// * differentiating peptides with different charge states. If
	// * false, peptides with different charge states will have the
	// * same key
	// * @return
	// */
	// public static String getIonKey(IsoRatio ratio, QuantifiedPSMInterface
	// quantPSM, boolean chargeSensible) {
	// String ionSerieTypeName = "";
	// if (ratio.getIonSerieType() != null) {
	// ionSerieTypeName = ratio.getIonSerieType().name();
	// }
	// String numIon = "";
	// if (ratio.getNumIon() > 0)
	// numIon = String.valueOf(ratio.getNumIon());
	// String ret = ionSerieTypeName + numIon;
	// if (!"".equals(ret)) {
	// ret += "-";
	// }
	// ret += getSpectrumKey(quantPSM, chargeSensible);
	// return ret;
	//
	// }

	public String getGroupKey(ProteinGroup group) {
		Collections.sort(group, new Comparator<GroupableProtein>() {

			@Override
			public int compare(GroupableProtein o1, GroupableProtein o2) {
				return o1.getAccession().compareTo(o2.getAccession());
			}
		});
		String key = "";
		for (final GroupableProtein protein : group) {
			if (!"".equals(key))
				key += ",";
			key += protein.getAccession();
		}
		return key;
	}

	// DISABLED BECAUSE WE NEED TO CONSIDER THE CHARGE
//	public String getSequenceKey(PSM quantPSM, boolean distinguishModifiedSequence) {
//		if (distinguishModifiedSequence) {
//			return quantPSM.getFullSequence();
//		} else {
//			return FastaParser.cleanSequence(quantPSM.getSequence());
//		}
//	}

	public static KeyUtils getInstance() {
		if (instance == null) {
			instance = new KeyUtils();
		}
		return instance;
	}
}
