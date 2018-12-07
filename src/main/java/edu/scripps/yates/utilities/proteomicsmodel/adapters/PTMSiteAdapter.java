package edu.scripps.yates.utilities.proteomicsmodel.adapters;

import edu.scripps.yates.utilities.proteomicsmodel.PTMSite;
import edu.scripps.yates.utilities.proteomicsmodel.Score;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PTMSiteEx;

public class PTMSiteAdapter implements edu.scripps.yates.utilities.pattern.Adapter<PTMSite> {
	private final String aa;
	private final int position;
	private final Score score;

	public PTMSiteAdapter(String aa, int position, Score score) {
		this.aa = aa;
		this.position = position;
		this.score = score;
	}

	public PTMSiteAdapter(String aa, int position) {
		this(aa, position, null);
	}

	@Override
	public PTMSite adapt() {
		final PTMSiteEx ptmSite = new PTMSiteEx(aa, position);
		if (score != null)
			ptmSite.setScore(score);
		return ptmSite;
	}

}
