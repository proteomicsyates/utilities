package edu.scripps.yates.utilities.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Same class as {@link PositionInProtein}, but just to distinguish them.<br>
 * The difference is that the key here is the peptide sequence and there is a
 * validation in the constructor that checks if the position is under the length
 * of the sequence
 * 
 * @author Salva
 *
 */
public class PositionInPeptide extends PositionInProtein {

	public PositionInPeptide(int position, String peptideSequence) {
		super(position, peptideSequence);
		if (position <= 0 || position > peptideSequence.length()) {
			throw new IllegalArgumentException(
					"Position " + position + " cannot belong to peptide sequence " + peptideSequence);
		}
	}

	public static List<PositionInPeptide> parseStringToPositionInPeptide(String string, String separator) {
		List<PositionInPeptide> ret = new ArrayList<PositionInPeptide>();
		List<String> subStrings = new ArrayList<String>();
		if (string.contains(separator)) {
			String[] split = string.split(separator);
			for (String string2 : split) {
				subStrings.add(string2);
			}
		} else {
			subStrings.add(string);
		}
		for (String string2 : subStrings) {
			if (string2.contains(SEPARATOR)) {
				try {
					String[] split = string2.split(SEPARATOR);
					PositionInPeptide positionInProtein = new PositionInPeptide(Integer.valueOf(split[1]), split[2]);
					ret.add(positionInProtein);
				} catch (NumberFormatException e) {

				}
			}
		}
		return ret;
	}
}
