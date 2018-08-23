package edu.scripps.yates.utilities.sequence;

public class PTMInPeptide extends PositionInPeptide {

	private final Double deltaMass;

	public PTMInPeptide(int position, char aa, String peptideSequence, Double deltaMass) {
		super(position, aa, peptideSequence);
		this.deltaMass = deltaMass;
	}

	public Double getDeltaMass() {
		return deltaMass;
	}

}
