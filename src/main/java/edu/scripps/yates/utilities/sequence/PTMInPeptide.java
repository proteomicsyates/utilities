package edu.scripps.yates.utilities.sequence;

import java.text.DecimalFormat;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PTMInPeptide extends PositionInPeptide {

	private final Double deltaMass;
	private final static DecimalFormat formatter3Decimals = new DecimalFormat("+#.###");

	public PTMInPeptide(int position, char aa, String peptideSequence, Double deltaMass) {
		super(position, aa, peptideSequence);
		this.deltaMass = deltaMass;
	}

	public Double getDeltaMass() {
		return deltaMass;
	}

	public String toStringExtended() {
		if (deltaMass != null) {
			return getProteinACC() + SEPARATOR + getAa() + getPosition() + formatter3Decimals.format(deltaMass);
		} else {
			return super.toString();
		}
	}

	@Override
	public int hashCode() {
		if (deltaMass != null) {
			int hash = 23;
			hash = hash * 31 + getPosition();
			hash = hash * 31 + getProteinACC().hashCode();
			hash = hash * 31 + HashCodeBuilder.reflectionHashCode(deltaMass, false);
			return hash;
		}
		return super.hashCode();
	}
}
