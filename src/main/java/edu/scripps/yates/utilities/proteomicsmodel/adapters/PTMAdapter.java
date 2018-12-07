package edu.scripps.yates.utilities.proteomicsmodel.adapters;

import java.util.List;

import org.apache.log4j.Logger;

import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.Score;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PTMEx;
import uk.ac.ebi.pride.utilities.pridemod.ModReader;

public class PTMAdapter implements edu.scripps.yates.utilities.pattern.Adapter<PTM> {
	private final Double massShift;
	private final int position;
	private final String aa;
	private final Score score;
	private final static Logger log = Logger.getLogger(PTMAdapter.class);
	private final uk.ac.ebi.pride.utilities.pridemod.model.PTM prideModPTM;
	private static final String MOD0 = "MOD:00000";
	private static final double PRECISION = 0.0001;

	public PTMAdapter(double massShift, String aa, int position) {
		this(massShift, aa, position, null);
	}

	public PTMAdapter(double massShift, char aa, int position) {
		this(massShift, String.valueOf(aa), position);
	}

	public PTMAdapter(String name, char aa, int position) {
		this(name, String.valueOf(aa), position);
	}

	public PTMAdapter(String name, String aa, int position) {
		this(name, aa, position, null);
	}

	public PTMAdapter(String name, char aa, int position, Score score) {
		this(name, String.valueOf(aa), position, score);
	}

	public PTMAdapter(String name, String aa, int position, Score score) {
		this.position = position;
		this.aa = aa;
		this.score = score;
		final ModReader modReader = ModReader.getInstance();
		final List<uk.ac.ebi.pride.utilities.pridemod.model.PTM> ptmListByMonoDeltaMass = modReader
				.getPTMListByPatternName(name);
		if (ptmListByMonoDeltaMass != null && !ptmListByMonoDeltaMass.isEmpty()) {
			prideModPTM = ptmListByMonoDeltaMass.get(0);
			massShift = prideModPTM.getMonoDeltaMass();
		} else {
			log.warn("PTM with name '" + name + "' is not recognized");
			prideModPTM = null;
			massShift = null;
		}
	}

	public PTMAdapter(double massShift, char aa, int position, Score score) {
		this(massShift, String.valueOf(aa), position, score);
	}

	public PTMAdapter(double massShift, String aa, int position, Score score) {
		this.massShift = massShift;
		this.position = position;
		this.aa = aa;
		this.score = score;
		final ModReader modReader = ModReader.getInstance();
		final List<uk.ac.ebi.pride.utilities.pridemod.model.PTM> ptmListByMonoDeltaMass = modReader
				.getPTMListByMonoDeltaMass(massShift, PRECISION);
		if (ptmListByMonoDeltaMass != null && !ptmListByMonoDeltaMass.isEmpty()) {
			prideModPTM = ptmListByMonoDeltaMass.get(0);
		} else {
			prideModPTM = null;
		}
	}

	@Override
	public PTM adapt() {
		String name = "unknown";
		if (prideModPTM != null) {
			name = prideModPTM.getName();
		}
		final PTMEx ptm = new PTMEx(name, massShift);
		if (prideModPTM != null) {
			ptm.setCvId(prideModPTM.getAccession());
		}
		ptm.addPtmSite(new PTMSiteAdapter(aa, position, score).adapt());
		return ptm;
	}

}
