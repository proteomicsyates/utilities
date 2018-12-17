package edu.scripps.yates.utilities.proteomicsmodel.factories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.PTMSite;
import gnu.trove.set.hash.THashSet;
import uk.ac.ebi.pride.utilities.pridemod.ModReader;

public class PTMEx implements PTM, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442800986087569468L;
	private final String name;
	private final double massShift;
	private String cvId;
	private List<PTMSite> ptmSites;
	private String residues;
	private static final ModReader modReader = ModReader.getInstance();
	private static final Double PRECISION = 0.001;
	private static final String UNKNOWN = "Unknown";

	public PTMEx(String name, double massShift) {
		this.name = name;
		this.massShift = massShift;
	}

	public PTMEx(double massShift, char aa, int position) {
		final List<uk.ac.ebi.pride.utilities.pridemod.model.PTM> ptms = modReader.getPTMListByMonoDeltaMass(massShift,
				PRECISION);
		if (ptms != null && !ptms.isEmpty()) {
			name = ptms.get(0).getName();
			cvId = ptms.get(0).getAccession();

		} else {
			name = UNKNOWN;
		}
		addPtmSite(new PTMSiteEx(String.valueOf(aa), position));
		this.massShift = massShift;
	}

	@Override
	public Double getMassShift() {
		return massShift;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getCVId() {
		return cvId;
	}

	@Override
	public List<PTMSite> getPTMSites() {
		return ptmSites;
	}

	/**
	 * @param cvId
	 *            the cvId to set
	 */
	public void setCvId(String cvId) {
		this.cvId = cvId;
	}

	/**
	 * @param ptmSites
	 *            the ptmSites to set
	 */
	public void setPtmSites(List<PTMSite> ptmSites) {
		this.ptmSites = ptmSites;
	}

	public void addPtmSite(PTMSite ptmSite) {
		if (ptmSites == null)
			ptmSites = new ArrayList<PTMSite>();
		ptmSites.add(ptmSite);
	}

	@Override
	public String getResidues() {
		if (residues == null) {
			final Set<String> aas = new THashSet<String>();
			final List<PTMSite> ptmSites2 = getPTMSites();
			if (ptmSites2.size() == 1) {
				residues = ptmSites2.iterator().next().getAA();
				return residues;
			}
			for (final PTMSite ptmSite : ptmSites2) {
				if (!aas.contains(ptmSite.getAA())) {
					aas.add(ptmSite.getAA());
				}
			}
			final StringBuilder sb = new StringBuilder();
			aas.stream().forEach(aa -> sb.append(aa));
			residues = sb.toString();
		}
		return residues;
	}
}
