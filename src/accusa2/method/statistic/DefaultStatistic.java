package accusa2.method.statistic;

import umontreal.iro.lecuyer.probdistmulti.DirichletDist;

import accusa2.pileup.Pileup;
import accusa2.process.pileup2Matrix.AbstractPileup2Matrix;
import accusa2.process.pileup2Matrix.BASQ;

public class DefaultStatistic implements StatisticCalculator {

	protected AbstractPileup2Matrix pileup2Matrix;
	
	public DefaultStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new DefaultStatistic();
	}

	public double getStatistic(final Pileup pileup1, final Pileup pileup2) {
	assert(pileup1.getContig().equals(pileup2.getContig()) && pileup1.getPosition() == pileup2.getPosition());

		final double[][] matrix1 = pileup2Matrix.calculate(pileup1);
		final double[][] matrix2 = pileup2Matrix.calculate(pileup2);

		final double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		final double[] alpha1 = estimateAlpha(prob1, pileup1.getCoverage());

		final DirichletDist dirichlet1 = new DirichletDist(alpha1);
		final double density11 = Math.log10(Math.max(Double.MIN_VALUE, Math.log10(dirichlet1.density(prob1))));
		
		final double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		final double[] alpha2 = estimateAlpha(prob2, pileup2.getCoverage());
		
		final DirichletDist dirichlet2 = new DirichletDist(alpha2);
		final double density22 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));

		final double density12 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob2)));
		final double density21 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob1)));

		final double z = (density11 + density22) - (density12 + density21);
		return z;
	}

	protected double[] estimateAlpha(final double[] mean, final int coverage) {
		final double[] alpha = new double[mean.length];

		for(int i = 0; i < mean.length; ++i) {
			alpha[i] = coverage * mean[i];
		}

		return alpha;
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "default";
	}

}