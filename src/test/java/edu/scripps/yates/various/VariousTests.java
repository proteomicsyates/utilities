package edu.scripps.yates.various;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.Assert;

public class VariousTests {
	private final Pattern aktMotif = Pattern.compile("(R.R..[ST])");
	private final Pattern erkMotif = Pattern.compile("(P.[ST]P)");
	private final Pattern[] motifPatterns = { aktMotif, erkMotif };
	private final DecimalFormat df = new DecimalFormat("#.##");

	@Test
	public void testingMotifPatterns() {
		final String[] positiveAKTMotifs = { "RHRAST", "RHRASS", "RHRASSASDF" };
		for (final String string : positiveAKTMotifs) {
			Assert.assertTrue(aktMotif.matcher(string).find());
		}

		final String[] negativeAKTMotifs = { "RHRA", "RHRAS", "RRAST", "RHRALKS", "RHHRASSASDF" };
		for (final String string : negativeAKTMotifs) {
			Assert.assertFalse(aktMotif.matcher(string).find());
		}

		final String[] positiveERKMotifs = { "PGSP", "ASDFPSTPASDF", "WERPASDFPATPAASDF" };
		for (final String string : positiveERKMotifs) {
			Assert.assertTrue(erkMotif.matcher(string).find());
		}

		final String[] negativeERKMotifs = { "PGS", "PSP", "AASDFSTP", "ASDFPSTKPASDF", "WERPASDFPATKPAASDF" };
		for (final String string : negativeERKMotifs) {
			Assert.assertFalse(erkMotif.matcher(string).find());
		}
	}

}
