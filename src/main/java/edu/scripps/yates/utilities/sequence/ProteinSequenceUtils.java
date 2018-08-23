package edu.scripps.yates.utilities.sequence;

import java.util.ArrayList;
import java.util.List;

import edu.scripps.yates.utilities.strings.StringUtils;
import gnu.trove.list.array.TIntArrayList;

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
		final List<PositionInProtein> ret = new ArrayList<PositionInProtein>();

		final TIntArrayList allPositionsOf = StringUtils.allPositionsOf(proteinSequence, peptideSequence);
		allPositionsOf.forEach(
				position -> ret.add(new PositionInProtein(position, proteinSequence.charAt(position - 1), proteinACC)));

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
		final List<PositionInProtein> ret = new ArrayList<PositionInProtein>();
		for (final char aa : aas) {
			final TIntArrayList positionsInPeptide = StringUtils.allPositionsOf(peptideSequence, aa);
			for (final int positionInPeptide : positionsInPeptide.toArray()) {

				final TIntArrayList positionsInProtein = StringUtils.allPositionsOf(proteinSequence, peptideSequence);
				for (final int positionInProtein : positionsInProtein.toArray()) {
					final int positionOfSiteInProtein = positionInProtein + positionInPeptide - 1;
					ret.add(new PositionInProtein(positionOfSiteInProtein,
							proteinSequence.charAt(positionOfSiteInProtein - 1), proteinACC));
				}
			}
		}
		return ret;
	}
}
