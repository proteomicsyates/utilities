package edu.scripps.yates.utilities.proteomicsmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.scripps.yates.utilities.grouping.GroupableProtein;
import edu.scripps.yates.utilities.grouping.PeptideRelation;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PeptideEx;
import edu.scripps.yates.utilities.proteomicsmodel.staticstorage.StaticProteomicsModelStorage;
import gnu.trove.set.hash.THashSet;

public abstract class AbstractPSM implements PSM {

	private Set<Score> scores;
	private Set<Ratio> ratios;
	private Set<Amount> amounts;
	private Set<Condition> conditions;
	private MSRun msRun;
	private String sequence;
	private String identifier;
	private PeptideRelation relation;
	private Set<Protein> proteins;
	private List<PTM> ptms;
	private Double experimentalMH;
	private Double calcMH;
	private Double massErrorPPM;
	private Double totalIntensity;
	private Integer spr;
	private Double ionProportion;
	private Double pi;
	private String fullSequence;
	private Peptide peptide;
	private int uniqueIdentifier;
	private String afterSeq;
	private String beforeSeq;
	private Integer chargeState;
	private String scanNumber;
	private Double rtInMinutes;
	private String searchEngine;

	@Override
	public Set<Score> getScores() {
		return scores;
	}

	@Override
	public boolean addScore(Score score) {
		if (scores == null) {
			scores = new THashSet<Score>();
		}
		return scores.add(score);
	}

	@Override
	public Set<Ratio> getRatios() {
		return ratios;
	}

	@Override
	public boolean addRatio(Ratio ratio) {
		if (ratios == null) {
			ratios = new THashSet<Ratio>();
		}
		return ratios.add(ratio);
	}

	@Override
	public Set<Amount> getAmounts() {
		return amounts;
	}

	@Override
	public boolean addAmount(Amount amount) {
		if (amounts == null) {
			amounts = new THashSet<Amount>();
		}
		return amounts.add(amount);
	}

	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}

	@Override
	public boolean addCondition(Condition condition) {
		if (conditions == null) {
			conditions = new THashSet<Condition>();
		}
		return conditions.add(condition);
	}

	@Override
	public MSRun getMSRun() {
		return msRun;
	}

	@Override
	public void setMSRun(MSRun msRun) {
		this.msRun = msRun;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void setRelation(PeptideRelation relation) {
		this.relation = relation;
	}

	@Override
	public PeptideRelation getRelation() {
		return relation;
	}

	@Override
	public List<GroupableProtein> getGroupableProteins() {
		final List<GroupableProtein> ret = new ArrayList<GroupableProtein>();
		ret.addAll(getProteins());
		return ret;
	}

	@Override
	public Set<Protein> getProteins() {
		if (proteins == null) {
			proteins = new THashSet<Protein>();
		}
		return proteins;
	}

	@Override
	public boolean addProtein(Protein protein, boolean recursively) {
		if (protein != null) {
			if (proteins == null) {
				proteins = new THashSet<Protein>();
			}
			final boolean ret = proteins.add(protein);
			if (recursively) {
				protein.addPSM(this, false);
				protein.addPeptide(getPeptide(), false);
				if (getPeptide() != null) {
					getPeptide().addProtein(protein, false);
				}
			}
			return ret;
		}
		return false;
	}

	@Override
	public List<PTM> getPTMs() {
		return ptms;
	}

	@Override
	public boolean addPTM(PTM newPtm) {
		if (newPtm != null) {
			if (ptms == null) {
				ptms = new ArrayList<PTM>();
			}
			boolean found = false;
			for (final PTM ptm : ptms) {
				if (ptm.getName().equals(newPtm.getName())) {
					boolean anyPtmIsNew = false;
					for (final PTMSite newPtmSite : newPtm.getPTMSites()) {
						boolean ptmSiteFound = false;
						for (final PTMSite ptmSite : ptm.getPTMSites()) {
							if (newPtmSite.getPosition() == ptmSite.getPosition()) {
								ptmSiteFound = true;
							}
						}
						if (!ptmSiteFound) {
							anyPtmIsNew = true;
						}
					}
					if (!anyPtmIsNew) {
						found = true;
					}
				}
			}
			if (!found) {
				return ptms.add(newPtm);
			}
		}
		return false;
	}

	@Override
	public Double getExperimentalMH() {
		return experimentalMH;
	}

	@Override
	public Double getCalcMH() {
		return calcMH;
	}

	@Override
	public Double getMassErrorPPM() {
		return massErrorPPM;
	}

	@Override
	public Double getTotalIntensity() {
		return totalIntensity;
	}

	@Override
	public Double getIonProportion() {
		return ionProportion;
	}

	@Override
	public Double getPi() {
		return pi;
	}

	@Override
	public String getFullSequence() {
		return fullSequence;
	}

	@Override
	public Peptide getPeptide() {
		if (peptide == null) {
			if (StaticProteomicsModelStorage.containsPeptide(msRun, null, getFullSequence())) {
				peptide = StaticProteomicsModelStorage.getSinglePeptide(msRun, null, getFullSequence());
			} else {
				peptide = new PeptideEx(getSequence(), msRun);
				StaticProteomicsModelStorage.addPeptide(peptide, msRun, null);
			}
		}
		return peptide;
	}

	@Override
	public boolean setPeptide(Peptide peptide, boolean recursively) {
		if (peptide != null) {
			this.peptide = peptide;
			if (recursively) {
				peptide.addPSM(this, false);
				for (final Protein protein : getProteins()) {
					protein.addPeptide(peptide, false);
					peptide.addProtein(protein, false);
				}
			}
			return true;
		}
		return false;

	}

	@Override
	public int getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	@Override
	public String getAfterSeq() {
		return afterSeq;
	}

	@Override
	public String getBeforeSeq() {
		return beforeSeq;
	}

	@Override
	public Integer getChargeState() {
		return chargeState;
	}

	@Override
	public String getScanNumber() {
		return scanNumber;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public void setScanNumber(String scanNumber) {
		this.scanNumber = scanNumber;
	}

	public void setCalcMH(double calcMH) {
		this.calcMH = calcMH;
	}

	public void setTotalIntensity(Double intensity) {
		totalIntensity = intensity;
	}

	@Override
	public Integer getSpr() {
		return spr;
	}

	public void setSpr(Integer spr) {
		this.spr = spr;
	}

	public void setFullSequence(String fullSequence) {
		this.fullSequence = fullSequence;
	}

	public void setChargeState(Integer chargeState) {
		this.chargeState = chargeState;
	}

	public void setMassErrorPPM(Double massErrorPPM) {
		this.massErrorPPM = massErrorPPM;
	}

	public void setPi(Double pi) {
		this.pi = pi;
	}

	public void setIonProportion(Double ionProportion) {
		this.ionProportion = ionProportion;
	}

	public Double getRtInMinutes() {
		return rtInMinutes;
	}

	public void setRtInMinutes(Double rtInMinutes) {
		this.rtInMinutes = rtInMinutes;
	}

	public void setExperimentalMH(Double experimentalMH) {
		this.experimentalMH = experimentalMH;
	}

	@Override
	public String toString() {
		StringBuilder sb = null;
		if (getMSRun() != null) {
			sb = new StringBuilder();
			sb.append(" in MSrun: " + getMSRun().getRunId());
		}
		if (sb != null) {
			return getFullSequence() + sb.toString();
		}
		return getFullSequence();
	}

	@Override
	public String getSearchEngine() {
		return searchEngine;
	}

	@Override
	public void setSearchEngine(String searchEngine) {
		this.searchEngine = searchEngine;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
}
