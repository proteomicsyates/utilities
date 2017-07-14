package edu.scripps.yates.utilities.sequence;

import java.util.ArrayList;
import java.util.List;

import edu.scripps.yates.utilities.strings.StringUtils;

public class ProteinSequenceUtils {

	/**
	 * Returns the positions in which the peptide sequence is found in the
	 * protein sequence.<br>
	 * This functions is equals to StringUtils.allPositionsOf(peptideSequence,
	 * proteinSequence);
	 * 
	 * @param peptideSequence
	 * @param proteinSequence
	 * @param proteinACC
	 * @return
	 */
	public static List<PositionInProtein> getPositionsOfPeptideSequenceInProteinSequence(String peptideSequence,
			String proteinSequence, String proteinACC) {
		List<PositionInProtein> ret = new ArrayList<PositionInProtein>();

		List<Integer> allPositionsOf = StringUtils.allPositionsOf(peptideSequence, proteinSequence);
		for (Integer position : allPositionsOf) {
			ret.add(new PositionInProtein(position, proteinACC));
		}

		return ret;
	}

	/**
	 * Returns the positions in which the aminoacids in aas contained in the
	 * peptide sequence are in the protein sequence
	 * 
	 * @param aas
	 * @param peptideSequence
	 * @param proteinSequence
	 * @param proteinACC
	 * @return
	 */
	public static List<PositionInProtein> getPositionsInProteinForSites(char[] aas, String peptideSequence,
			String proteinSequence, String proteinACC) {
		List<PositionInProtein> ret = new ArrayList<PositionInProtein>();
		for (char aa : aas) {
			List<Integer> positionsInPeptide = StringUtils.allPositionsOf(peptideSequence, aa);
			for (Integer positionInPeptide : positionsInPeptide) {

				List<Integer> positionsInProtein = StringUtils.allPositionsOf(proteinSequence, peptideSequence);
				for (Integer positionInProtein : positionsInProtein) {
					int positionOfSiteInProtein = positionInProtein + positionInPeptide - 1;
					ret.add(new PositionInProtein(positionOfSiteInProtein, proteinACC));
				}
			}
		}
		return ret;
	}
}
