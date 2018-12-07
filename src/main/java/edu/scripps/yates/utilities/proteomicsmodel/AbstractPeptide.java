package edu.scripps.yates.utilities.proteomicsmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.scripps.yates.utilities.fasta.FastaParser;
import edu.scripps.yates.utilities.grouping.GroupableProtein;
import edu.scripps.yates.utilities.grouping.PeptideRelation;
import edu.scripps.yates.utilities.proteomicsmodel.adapters.PTMAdapter;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.set.hash.THashSet;

public abstract class AbstractPeptide implements Peptide {

	private Set<Score> scores;
	private Set<Ratio> ratios;
	private Set<Amount> amounts;
	private Set<Condition> conditions;
	private Set<MSRun> msRuns;
	private String sequence;
	private PeptideRelation relation;
	private Set<Protein> proteins;
	private List<PSM> psms;
	private List<PTM> ptms;

	private String fullSequence;
	private String afterSeq;
	private String beforeSeq;
	private String searchEngine;
	private Integer spectrumCount;
	private int dbID;

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
	public Set<MSRun> getMSRuns() {
		return msRuns;
	}

	@Override
	public boolean addMSRun(MSRun msRun) {
		if (msRun != null) {
			if (msRuns == null) {
				msRuns = new THashSet<MSRun>();
			}
			return msRuns.add(msRun);
		}
		return false;
	}

	@Override
	public String getSequence() {
		return sequence;
	}

	@Override
	public String getIdentifier() {
		return getFullSequence();
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
				protein.addPeptide(this, false);
				if (getPSMs() != null) {
					for (final PSM psm : getPSMs()) {
						protein.addPSM(psm, false);
						psm.addProtein(protein, false);
					}
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
	public String getFullSequence() {
		return fullSequence;
	}

	@Override
	public List<PSM> getPSMs() {
		if (psms == null) {
			psms = new ArrayList<PSM>();
		}
		return psms;
	}

	@Override
	public boolean addPSM(PSM psm, boolean recursively) {
		if (psm != null) {
			if (psms == null) {
				psms = new ArrayList<PSM>();
			}
			final boolean ret = psms.add(psm);
			if (recursively) {

			}
			return ret;
		}
		return false;
	}

	@Override
	public String getAfterSeq() {
		return afterSeq;
	}

	@Override
	public String getBeforeSeq() {
		return beforeSeq;
	}

	public void setFullSequence(String fullSequence) {
		this.fullSequence = fullSequence;
		sequence = FastaParser.cleanSequence(fullSequence);
		afterSeq = FastaParser.getAfterSeq(fullSequence);
		beforeSeq = FastaParser.getBeforeSeq(fullSequence);
		final TIntDoubleHashMap ptMsFromSequence = FastaParser.getPTMsFromSequence(fullSequence, true);
		if (!ptMsFromSequence.isEmpty()) {
			for (final int position : ptMsFromSequence.keys()) {
				final double deltaMass = ptMsFromSequence.get(position);
				final String aa = getSequence().substring(position - 1, position);
				final PTM ptm = new PTMAdapter(deltaMass, aa, position).adapt();
				addPTM(ptm);
			}
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (getMSRuns() != null) {
			sb.append(" in run(s): ");
			for (final MSRun msRun : getMSRuns()) {
				if (!"".equals(sb.toString())) {
					sb.append(",");
				}
				sb.append(msRun.getRunId());
			}
		}
		return getIdentifier() + sb.toString();
	}

	@Override
	public String getSearchEngine() {
		return searchEngine;
	}

	@Override
	public void setSearchEngine(String searchEngine) {
		this.searchEngine = searchEngine;
	}

	@Override
	public Integer getSpectrumCount() {
		return spectrumCount;
	}

	@Override
	public void setSpectrumCount(Integer spc) {
		spectrumCount = spc;
	}

	@Override
	public int getDBId() {
		return dbID;
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

}
