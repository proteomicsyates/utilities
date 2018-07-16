package edu.scripps.yates.utilities.model.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.grouping.GroupableProtein;
import edu.scripps.yates.utilities.grouping.PeptideRelation;
import edu.scripps.yates.utilities.proteomicsmodel.Amount;
import edu.scripps.yates.utilities.proteomicsmodel.Condition;
import edu.scripps.yates.utilities.proteomicsmodel.MSRun;
import edu.scripps.yates.utilities.proteomicsmodel.PSM;
import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.PTMSite;
import edu.scripps.yates.utilities.proteomicsmodel.Peptide;
import edu.scripps.yates.utilities.proteomicsmodel.Protein;
import edu.scripps.yates.utilities.proteomicsmodel.Ratio;
import edu.scripps.yates.utilities.proteomicsmodel.Score;
import gnu.trove.set.hash.THashSet;

public class PeptideEx implements Peptide {

	private final Set<PSM> psms = new THashSet<PSM>();
	private final List<PTM> ptms = new ArrayList<PTM>();
	private final Set<Ratio> ratios = new THashSet<Ratio>();
	private final Set<Amount> amounts = new THashSet<Amount>();
	private Set<Score> scores;
	private final String sequence;
	private final Set<Protein> proteins = new THashSet<Protein>();
	private final Set<Condition> conditions = new THashSet<Condition>();
	private MSRun msRun;
	private PeptideRelation relation;
	private static Logger log = Logger.getLogger(PeptideEx.class);

	public PeptideEx(String sequence, MSRun msrun) {
		this.sequence = sequence;
		msRun = msrun;
	}

	@Override
	public int getDBId() {
		return -1;
	}

	@Override
	public void addPSM(PSM psm) {
		if (psm != null) {
			final String runId = psm.getMSRun().getRunId();

			for (final PSM psm2 : psms) {
				if (!psm2.getMSRun().getRunId().equals(runId)) {
					throw new IllegalArgumentException("A peptide should belong to PSMs from the same RUN id\n"
							+ "Should be the same run id: '" + psm2.getMSRun().getRunId() + "'\t'" + runId + "'");
				}
			}
			if (!psms.contains(psm)) {
				psms.add(psm);
				psm.setPeptide(this);
				// add ptms
				if (psm.getPTMs() != null) {
					for (final PTM ptm : psm.getPTMs()) {
						addPTM(ptm);
					}
				}
			}
		}
	}

	@Override
	public Set<Ratio> getRatios() {
		return ratios;
	}

	@Override
	public void addRatio(Ratio ratio) {
		if (ratio != null && !ratios.contains(ratio)) {
			ratios.add(ratio);
		}
	}

	@Override
	public Set<Amount> getAmounts() {
		return amounts;
	}

	@Override
	public void addAmount(Amount amount) {
		if (amount != null && !amounts.contains(amount))
			amounts.add(amount);
	}

	@Override
	public Set<Score> getScores() {
		return scores;
	}

	@Override
	public void addScore(Score score) {
		if (scores == null) {
			scores = new THashSet<Score>();
		}
		if (score != null) {
			scores.add(score);
		}
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param scores
	 *            the scores to set
	 */
	public void setScores(Set<Score> scores) {
		this.scores = scores;
	}

	@Override
	public Set<Protein> getProteins() {
		return proteins;
	}

	@Override
	public void addProtein(Protein protein) {

		if (protein != null && !proteins.contains(protein)) {
			proteins.add(protein);
			protein.addPeptide(this);
			for (final PSM psm : getPSMs()) {
				protein.addPSM(psm);
				psm.addProtein(protein);
			}
		}
	}

	@Override
	public Set<Condition> getConditions() {
		return conditions;
	}

	@Override
	public void addCondition(Condition newCondition) {
		if (newCondition != null) {
			boolean found = false;
			for (final Condition condition : conditions) {
				if (condition.getName().equals(newCondition.getName())) {
					if (condition.getProject().getName().equals(newCondition.getProject().getName()))
						found = true;
				}

			}
			if (!found) {
				conditions.add(newCondition);
				// set condition to amounts
				for (final Amount amount : getAmounts()) {
					if (amount.getCondition() == null && amount instanceof AmountEx) {
						((AmountEx) amount).setCondition(newCondition);
					}
				}
			}
		}
	}

	@Override
	public MSRun getMSRun() {
		return msRun;
	}

	@Override
	public String toString() {
		return sequence + " in run: " + getMSRun().getRunId();
	}

	@Override
	public Set<PSM> getPSMs() {
		return psms;
	}

	@Override
	public void setMSRun(MSRun msRun) {
		this.msRun = msRun;
	}

	private void addPTM(PTM ptm) {
		if (ptm != null) {// check if we already have that PTM
			boolean found = false;
			for (final PTM ptm2 : ptms) {
				if (areSameSites(ptm2.getPTMSites(), ptm.getPTMSites())) {
					// same sites
					if (ptm2.getMassShift() != null && ptm.getMassShift() != null) {
						if (ptm2.getMassShift().equals(ptm.getMassShift())) {
							// same sites and same mass shifts
							found = true;
							break;
						}
					} else if (ptm2.getName().equalsIgnoreCase(ptm.getName())) {
						// same sites and same names
						found = true;
						break;
					}
				}
			}
			if (!found) {
				ptms.add(ptm);
			}
		}
	}

	private boolean areSameSites(List<PTMSite> ptmSites, List<PTMSite> ptmSites2) {
		for (final PTMSite ptmSite : ptmSites) {
			final int position = ptmSite.getPosition();
			boolean found = false;
			for (final PTMSite ptmSite2 : ptmSites) {
				if (ptmSite2.getPosition() == position) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@Override
	public List<PTM> getPTMs() {
		return ptms;
	}

	@Override
	public String getIdentifier() {
		return String.valueOf(hashCode());
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

}
