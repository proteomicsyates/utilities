package edu.scripps.yates.utilities.proteomicsmodel;

import edu.scripps.yates.utilities.grouping.GroupablePeptide;

public interface Peptide extends HasRatios, HasScores, HasAmounts, HasConditions, HasPsms, HasMsRuns, HasProteins,
		HasPTMs, GroupablePeptide {
	public int getDBId();

	public String getFullSequence();

	public String getAfterSeq();

	public String getBeforeSeq();

	public void setSearchEngine(String searchEngine);

	public String getSearchEngine();
}
