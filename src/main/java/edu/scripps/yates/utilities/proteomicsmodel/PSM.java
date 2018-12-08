package edu.scripps.yates.utilities.proteomicsmodel;

import edu.scripps.yates.utilities.grouping.GroupablePeptide;

public interface PSM extends HasScores, HasRatios, HasAmounts, HasConditions, HasMSRun, GroupablePeptide, HasProteins,
		HasPTMs, HasPeptide {

	public Double getExperimentalMH();

	public Double getCalcMH();

	public Double getMassErrorPPM();

	public Double getTotalIntensity();

	public Integer getSpr();

	public Double getIonProportion();

	public Double getPi();

	/**
	 * Gets the peptide sequence including PTMs Nterm and Cterm aminoacids, as
	 * it is reported by the search engine
	 *
	 * @return
	 */
	public String getFullSequence();

	public int getUniqueIdentifier();

	public String getAfterSeq();

	public String getBeforeSeq();

	public Integer getChargeState();

	public String getScanNumber();

	public void setSearchEngine(String searchEngine);

	public String getSearchEngine();

	public Double getDeltaCn();

	public Double getXCorr();

	Double getRtInMinutes();
}
