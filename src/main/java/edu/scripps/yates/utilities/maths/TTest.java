package edu.scripps.yates.utilities.maths;

import smile.math.Math;

/*******************************************************************************
 * This class was modified by Salvador Martinez from the class TTest at the github
 *  project at https://github.com/haifengl/smile in order to be able to perform 
 *  t-test just having the mean, variance and n
 * <br>
 * Copyright (c) 2010 Haifeng Li
 *   
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import smile.math.special.Beta;

/**
 * Student's t test. A t-test is any statistical hypothesis test in which the
 * test statistic has a Student's t distribution if the null hypothesis is true.
 * It is applied when the population is assumed to be normally distributed but
 * the sample sizes are small enough that the statistic on which inference is
 * based is not normally distributed because it relies on an uncertain estimate
 * of standard deviation rather than on a precisely known value.
 * <p>
 * Among the most frequently used t tests are:
 * <ul>
 * <li>A test of whether the mean of a normally distributed population has a
 * value specified in a null hypothesis.
 * <li>A test of the null hypothesis that the means of two normally distributed
 * populations are equal. Given two data sets, each characterized by its mean,
 * standard deviation and number of data points, we can use some kind of t test
 * to determine whether the means are distinct, provided that the underlying
 * distributions can be assumed to be normal. All such tests are usually called
 * Student's t tests, though strictly speaking that name should only be used if
 * the variances of the two populations are also assumed to be equal; the form
 * of the test used when this assumption is dropped is sometimes called Welch's
 * t test. There are different versions of the t test depending on whether the
 * two samples are
 * <ul>
 * <li>unpaired, independent of each other (e.g., individuals randomly assigned
 * into two groups, measured after an intervention and compared with the other
 * group),
 * <li>or paired, so that each member of one sample has a unique relationship
 * with a particular member of the other sample (e.g., the same people measured
 * before and after an intervention).
 * </ul>
 * If the calculated p-value is below the threshold chosen for statistical
 * significance (usually 0.05 or 0.01 level), then the null hypothesis which
 * usually states that the two groups do not differ is rejected in favor of an
 * alternative hypothesis, which typically states that the groups do differ.
 * <li>A test of whether the slope of a regression line differs significantly
 * from 0.
 * </ul>
 *
 * @author Haifeng Li
 */
public class TTest {
	/**
	 * The degree of freedom of t-statistic.
	 */
	public double df;

	/**
	 * t-statistic
	 */
	public double t;

	/**
	 * p-value
	 */
	public double pvalue;

	/**
	 * Constructor.
	 */
	private TTest(double t, double df, double pvalue) {
		this.t = t;
		this.df = df;
		this.pvalue = pvalue;
	}

	/**
	 * Independent one-sample t-test whether the mean of a normally distributed
	 * population has a value specified in a null hypothesis. Small values of
	 * p-value indicate that the array has significantly different mean.
	 */
	public static TTest test(double[] x, double mean) {
		final int n = x.length;

		final double mu = Math.mean(x);
		final double var = Math.var(x);

		return test(mu, var, n, mean);
	}

	/**
	 * Independent one-sample t-test whether the mean of a normally distributed
	 * population has a value specified in a null hypothesis. Small values of
	 * p-value indicate that the array has significantly different mean.
	 */
	public static TTest test(double mu, double var, int n, double mean) {

		final int df = n - 1;

		final double t = (mu - mean) / Math.sqrt(var / n);
		final double p = Beta.regularizedIncompleteBetaFunction(0.5 * df, 0.5, df / (df + t * t));

		return new TTest(t, df, p);
	}

	/**
	 * Test if the arrays x and y have significantly different means. The data
	 * arrays are assumed to be drawn from populations with unequal variances. Small
	 * values of p-value indicate that the two arrays have significantly different
	 * means.
	 */
	public static TTest test(double[] x, double[] y) {
		return test(x, y, false);
	}

	/**
	 * Test if the arrays x and y have significantly different means. The data
	 * arrays are assumed to be drawn from populations with unequal variances. Small
	 * values of p-value indicate that the two arrays have significantly different
	 * means.
	 */
	public static TTest test(double mu1, double var1, int n1, double mu2, double var2, int n2) {
		return test(mu1, var1, n1, mu2, var2, n2, false);
	}

	/**
	 * Test if the arrays x and y have significantly different means. Small values
	 * of p-value indicate that the two arrays have significantly different means.
	 * 
	 * @param equalVariance true if the data arrays are assumed to be drawn from
	 *                      populations with the same true variance. Otherwise, The
	 *                      data arrays are allowed to be drawn from populations
	 *                      with unequal variances.
	 */
	public static TTest test(double[] x, double[] y, boolean equalVariance) {

		final int n1 = x.length;
		final int n2 = y.length;

		final double mu1 = Math.mean(x);
		final double var1 = Math.var(x);

		final double mu2 = Math.mean(y);
		final double var2 = Math.var(y);

		return test(mu1, var1, n1, mu2, var2, n2, equalVariance);

	}

	/**
	 * Test if the arrays x and y have significantly different means. Small values
	 * of p-value indicate that the two arrays have significantly different means.
	 * 
	 * @param equalVariance true if the data arrays are assumed to be drawn from
	 *                      populations with the same true variance. Otherwise, The
	 *                      data arrays are allowed to be drawn from populations
	 *                      with unequal variances.
	 */
	public static TTest test(double mu1, double var1, int n1, double mu2, double var2, int n2, boolean equalVariance) {
		final double df = calculateDF(mu1, var1, n1, mu2, var2, n2, equalVariance);
		if (equalVariance) {

			final double svar = ((n1 - 1) * var1 + (n2 - 1) * var2) / df;

			final double t = (mu1 - mu2) / Math.sqrt(svar * (1.0 / n1 + 1.0 / n2));
			final double p = Beta.regularizedIncompleteBetaFunction(0.5 * df, 0.5, df / (df + t * t));

			return new TTest(t, df, p);
		} else {

			final double t = (mu1 - mu2) / Math.sqrt(var1 / n1 + var2 / n2);
			final double p = Beta.regularizedIncompleteBetaFunction(0.5 * df, 0.5, df / (df + t * t));

			return new TTest(t, df, p);
		}
	}

	public static double calculateDF(double mu1, double var1, int n1, double mu2, double var2, int n2,
			boolean equalVariance) {
		if (equalVariance) {
			final double df = n1 + n2 - 2;
			return df;
		} else {
			final double df = Math.sqr(var1 / n1 + var2 / n2)
					/ (Math.sqr(var1 / n1) / (n1 - 1) + Math.sqr(var2 / n2) / (n2 - 1));
			return df;
		}
	}

	/**
	 * Given the paired arrays x and y, test if they have significantly different
	 * means. Small values of p-value indicate that the two arrays have
	 * significantly different means.
	 */
	public static TTest pairedTest(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("Input vectors have different size");
		}

		final double mu1 = Math.mean(x);
		final double var1 = Math.var(x);

		final double mu2 = Math.mean(y);
		final double var2 = Math.var(y);

		final int n = x.length;
		final int df = n - 1;

		double cov = 0.0;
		for (int j = 0; j < n; j++) {
			cov += (x[j] - mu1) * (y[j] - mu2);
		}
		cov /= df;

		final double sd = Math.sqrt((var1 + var2 - 2.0 * cov) / n);
		final double t = (mu1 - mu2) / sd;
		final double p = Beta.regularizedIncompleteBetaFunction(0.5 * df, 0.5, df / (df + t * t));

		return new TTest(t, df, p);
	}

	/**
	 * Test whether the Pearson correlation coefficient, the slope of a regression
	 * line, differs significantly from 0. Small values of p-value indicate a
	 * significant correlation.
	 * 
	 * @param r  the Pearson correlation coefficient.
	 * @param df the degree of freedom. df = n - 2, where n is the number of samples
	 *           used in the calculation of r.
	 */
	public static TTest test(double r, int df) {
		final double TINY = 1.0e-20;

		final double t = r * Math.sqrt(df / ((1.0 - r + TINY) * (1.0 + r + TINY)));
		final double p = Beta.regularizedIncompleteBetaFunction(0.5 * df, 0.5, df / (df + t * t));

		return new TTest(t, df, p);
	}

	/**
	 * depending on pvalue, prints '*' if <0.05, '**' if <0.01 and '***' if <0.001
	 * 
	 * @return
	 */
	public String printSignificanceAsterisks() {
		final StringBuilder sb = new StringBuilder();
		if (pvalue < 0.05) {
			sb.append("*");
		}
		if (pvalue < 0.01) {
			sb.append("*");
		}
		if (pvalue < 0.001) {
			sb.append("*");
		}
		return sb.toString();
	}

}
