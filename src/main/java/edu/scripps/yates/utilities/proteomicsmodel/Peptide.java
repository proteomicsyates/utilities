package edu.scripps.yates.utilities.proteomicsmodel;

import edu.scripps.yates.utilities.grouping.GroupablePeptide;

public interface Peptide extends HasRatios, HasScores, HasAmounts, HasConditions, HasPsms, HasMSRun, HasProteins,
		HasPTMs, GroupablePeptide {
	public int getDBId();

}
