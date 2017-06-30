package edu.scripps.yates.utilities.sequence;

import java.util.ArrayList;
import java.util.List;

public class PositionInProtein {
	private final int position;
	private final String proteinACC;
	private final int peptidePosition;

	public PositionInProtein(int position, String proteinACC, int peptidePosition) {
		this.position = position;
		this.proteinACC = proteinACC;
		this.peptidePosition = peptidePosition;
	}

	public int getPosition() {
		return position;
	}

	public String getProteinACC() {
		return proteinACC;
	}

	@Override
	public String toString() {
		return proteinACC + "$" + position;
	}

	@Override
	public int hashCode() {
		return -1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PositionInProtein) {
			PositionInProtein positionInProtein = (PositionInProtein) obj;
			if (positionInProtein.position == position && positionInProtein.proteinACC.equals(proteinACC)) {
				return true;
			}
			return false;
		}
		return super.equals(obj);
	}

	public static List<PositionInProtein> parseString(String string, String separator) {
		List<PositionInProtein> ret = new ArrayList<PositionInProtein>();
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
			if (string2.contains("$")) {
				try {
					String[] split = string2.split("$");
					PositionInProtein positionInProtein = new PositionInProtein(Integer.valueOf(split[1]), split[2],
							-1);
					ret.add(positionInProtein);
				} catch (NumberFormatException e) {

				}
			}
		}
		return ret;
	}
}
