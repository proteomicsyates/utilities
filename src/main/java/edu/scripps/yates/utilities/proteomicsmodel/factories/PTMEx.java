package edu.scripps.yates.utilities.proteomicsmodel.factories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.PTMSite;
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
	private static final ModReader modReader = ModReader.getInstance();
	private static final Double PRECISION = 0.001;

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
			name = null;
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
}
