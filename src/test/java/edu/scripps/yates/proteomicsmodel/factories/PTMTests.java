package edu.scripps.yates.proteomicsmodel.factories;

import org.junit.Test;

import edu.scripps.yates.utilities.proteomicsmodel.PSM;
import edu.scripps.yates.utilities.proteomicsmodel.PTMPosition;
import edu.scripps.yates.utilities.proteomicsmodel.Peptide;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PSMEx;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PTMEx;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PeptideEx;
import junit.framework.Assert;

public class PTMTests {
	@Test
	public void creatingPTMs() {
		final String seq = "PSEPTYIDES";
		final PTMEx ptm = new PTMEx(79.966, "S", 2);

		final String psmID = "asdf";
		final PSM psm = new PSMEx(psmID, seq, null, true, true);
		psm.setSequence(seq);
		psm.addPTM(ptm);
		final String fullSequence = psm.getFullSequence();
		Assert.assertEquals("PS(+79.966)EPTYIDES", fullSequence);
	}

	@Test
	public void creatingPTMs2() {
		final String seq = "PSEPTYIDES";
		final PTMEx ptm = new PTMEx(-79.96612, "S", 2);
		final PTMEx ptm2 = new PTMEx(79.96612, "Y", 6);
		final String psmID = "asdf";
		final PSM psm = new PSMEx(psmID, seq, null, true, true);
		psm.setSequence(seq);
		psm.addPTM(ptm);
		psm.addPTM(ptm2);
		final String fullSequence = psm.getFullSequence();
		Assert.assertEquals("PS(-79.966)EPTY(+79.966)IDES", fullSequence);
	}

	@Test
	public void creatingPTMs3() {
		final String seq = "PSEPTYIDES";
		final PTMEx ptm = new PTMEx(-79.96612, "S", 2);
		final PTMEx ptm2 = new PTMEx(79.96612, "Y", 6);
		final Peptide peptide = new PeptideEx(seq, seq);
		Assert.assertEquals("PSEPTYIDES", peptide.getSequence());
		peptide.setSequence(seq);
		peptide.addPTM(ptm);
		peptide.addPTM(ptm2);
		final String fullSequence = peptide.getFullSequence();
		Assert.assertEquals("PS(-79.966)EPTY(+79.966)IDES", fullSequence);
	}

	@Test
	public void creatingPTMs4() {
		final String seq = "PSEPTYIDES";
		final PTMEx ptm = new PTMEx(-10, null, 0);
		Assert.assertEquals(ptm.getPTMSites().get(0).getPTMPosition(), PTMPosition.NTERM);
		final Peptide peptide = new PeptideEx(seq, seq);
		Assert.assertEquals("PSEPTYIDES", peptide.getSequence());
		peptide.setSequence(seq);
		peptide.addPTM(ptm);
		final String fullSequence = peptide.getFullSequence();
		Assert.assertEquals("(-10)PSEPTYIDES", fullSequence);
	}

	@Test
	public void creatingPTMs5() {
		final String seq = "PSEPTYIDES";
		final PTMEx ptm = new PTMEx(-10, null, 11);
		Assert.assertEquals(ptm.getPTMSites().get(0).getPTMPosition(), PTMPosition.NONE);
		final Peptide peptide = new PeptideEx(seq, seq);
		Assert.assertEquals("PSEPTYIDES", peptide.getSequence());
		peptide.setSequence(seq);
		peptide.addPTM(ptm);
		final String fullSequence = peptide.getFullSequence();
		Assert.assertEquals("PSEPTYIDES(-10)", fullSequence);
	}
}
