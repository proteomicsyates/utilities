package edu.scripps.yates.proteomicsmodel.factories;

import org.junit.Test;

import edu.scripps.yates.utilities.proteomicsmodel.PTM;
import edu.scripps.yates.utilities.proteomicsmodel.Peptide;
import edu.scripps.yates.utilities.proteomicsmodel.factories.PeptideEx;
import junit.framework.Assert;

public class PeptideExTests {
	@Test
	public void test1() {
		final String fullSequence = "R.AFTAPEVLQNQS(79.9663)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("Phospho", ptm.getName());
	}

	@Test
	public void test2() {
		final String fullSequence = "R.AFTAPEVLQNQS(451.1889)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("biotin-maleimide", ptm.getName());
	}

	@Test
	public void test3() {
		final String fullSequence = "R.AFTAPEVLQNQS(523.2746)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("AHA", ptm.getName());
	}

	@Test
	public void test4() {
		final String fullSequence = "R.AFTAPEVLQNQS(10.008269)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("Label:13C(6)15N(4)", ptm.getName());
	}

	@Test
	public void test5() {
		final String fullSequence = "R.AFTAPEVLQNQS(8.014199)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("Label:13C(6)15N(2)", ptm.getName());
	}

	@Test
	public void test6() {
		final String fullSequence = "R.AFTAPEVLQNQK(114.042927)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("Dicarbamidomethyl", ptm.getName());
	}

	@Test
	public void test7() {
		final String fullSequence = "R.AFTAPEVLQNQK(451.1889)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("biotin-maleimide", ptm.getName());
	}

	@Test
	public void test8() {
		final String fullSequence = "R.AFTAPEVLQNQK(229.162932)LTSLSDVEK.I";
		final Peptide peptide = new PeptideEx(fullSequence, fullSequence);
		Assert.assertEquals("I", peptide.getAfterSeq());
		Assert.assertEquals("R", peptide.getBeforeSeq());
		Assert.assertEquals(1, peptide.getPTMs().size());
		final PTM ptm = peptide.getPTMs().get(0);
		Assert.assertEquals(12, ptm.getPTMSites().get(0).getPosition());
		Assert.assertEquals("TMT6plex", ptm.getName());
	}

}
