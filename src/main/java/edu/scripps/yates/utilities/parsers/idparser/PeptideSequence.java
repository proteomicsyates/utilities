package edu.scripps.yates.utilities.parsers.idparser;

import java.util.ArrayList;
import java.util.List;

import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PTMEx;
import edu.scripps.yates.utilities.staticstorage.StaticStrings;

public class PeptideSequence {
	private final String sequence;
	private char beforeSeq;
	private char afterSeq;
	private final List<PTM> modifications = new ArrayList<PTM>();
	private final String rawSequence;
	public static final char NULL_SEQ = '-';

	public PeptideSequence(String sequenceToParse) {
		rawSequence = StaticStrings.getUniqueInstance(sequenceToParse);
		sequence = StaticStrings.getUniqueInstance(parseSequence(sequenceToParse));
	}

	private String parseSequence(String sequenceToParse) {
		final StringBuilder sequence = new StringBuilder();
		String betweenDots = sequenceToParse;
		if (sequenceToParse.contains(".")) {
			betweenDots = "";
			beforeSeq = sequenceToParse.substring(0, sequenceToParse.indexOf(".")).charAt(0);
			afterSeq = sequenceToParse.substring(sequenceToParse.lastIndexOf(".") + 1).charAt(0);
			betweenDots = sequenceToParse.substring(sequenceToParse.indexOf(".") + 1, sequenceToParse.lastIndexOf("."));
		}
		// parse modifications over betweenDots string

		int modPosition = 0;
		int previousmod = 0;
		while (betweenDots.contains("(") && betweenDots.contains(")")) {
			final int openParenthesis = betweenDots.indexOf("(");
			modPosition += openParenthesis;
			final int closeParenthesis = betweenDots.indexOf(")");
			// before the open parenthesis
			sequence.append(betweenDots.subSequence(0, openParenthesis));
			final Double modificationShift = Double
					.valueOf(betweenDots.substring(openParenthesis + 1, closeParenthesis));

			final int modifiedAAPosition = openParenthesis - 1;// modPosition -
			// 1 -
			// previousmod;
			char charAt;
			// System.out.println(sequenceToParse + "\t" + betweenDots);
			if (modifiedAAPosition >= 0) {
				charAt = betweenDots.charAt(modifiedAAPosition);
			} else {
				if (modPosition == 0) {
					charAt = beforeSeq;
				} else {
					charAt = modifications.get(modifications.size() - 1).getPTMSites().get(0).getAA().charAt(0);
				}
			}
			addModification(modificationShift, modPosition, charAt);

			betweenDots = betweenDots.substring(closeParenthesis + 1);
			previousmod = openParenthesis;
		}

		sequence.append(betweenDots);
		return sequence.toString();
	}

	/**
	 *
	 * @param modificationShift
	 * @param modPosition
	 *            position of the modification, starting by 1 at the first AA
	 * @param aa
	 */
	private void addModification(Double modificationShift, int modPosition, char aa) {
		modifications.add(new PTMEx(modificationShift, aa, modPosition));
	}

	/**
	 * @return the sequence with no modifications
	 */
	public String getSequence() {
		return sequence.toString();
	}

	/**
	 * @return the sequence with no modifications
	 */
	public String getRawSequence() {
		return rawSequence;
	}

	/**
	 * @return the beforeSeq
	 */
	public char getBeforeSeq() {
		return beforeSeq;
	}

	/**
	 * @return the afterSeq
	 */
	public char getAfterSeq() {
		return afterSeq;
	}

	/**
	 * @return the modifications
	 */
	public List<PTM> getModifications() {
		return modifications;
	}

}
