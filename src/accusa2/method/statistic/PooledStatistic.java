package accusa2.method.statistic;


import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;
import accusa2.process.pileup2Matrix.AbstractPileup2Matrix;
import accusa2.process.pileup2Matrix.BASQ;

public final class PooledStatistic implements StatisticCalculator {

	protected final AbstractPileup2Matrix pileup2Matrix;

	public PooledStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new PooledStatistic();
	}
	
	public double getStatistic(final Pileup pileup1, final Pileup pileup2) {
	assert(pileup1.getContig().equals(pileup2.getContig()) && pileup1.getPosition() == pileup2.getPosition());

		final double[][] matrix1 = pileup2Matrix.calculate(pileup1);
		final double[][] matrix2 = pileup2Matrix.calculate(pileup2);
	
		final Pileup pileupP = PileupUtils.mergePileups(pileup1, pileup2);
		final int coverage1 = pileup1.getCoverage();
		final int coverage2 = pileup2.getCoverage();
		//final int coverage = Math.min(pileup1.getCoverage(), pileup2.getCoverage());

		final double[][] matrixP = pileup2Matrix.calculate(pileupP);
	
		final double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		final double[] alpha1 = estimateAlpha(prob1, coverage1);

		final DirichletDist dirichlet1 = new DirichletDist(alpha1);
		final double density11 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob1)));

		final double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		final double[] alpha2 = estimateAlpha(prob2, coverage2);

		final DirichletDist dirichlet2 = new DirichletDist(alpha2);
		final double density22 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));

		final double[] probP = accusa2.util.MathUtil.mean(matrixP);
		final double[] alphaP = estimateAlpha(probP, coverage1 + coverage2);
		final DirichletDist dirichletP = new DirichletDist(alphaP);

		final double density1P = Math.log10(Math.max(Double.MIN_VALUE, dirichletP.density(prob1)));
		final double density2P = Math.log10(Math.max(Double.MIN_VALUE, dirichletP.density(prob2)));
	
		final double z = (density11 + density22) - (density1P + density2P);
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
		return "pooled";
	}

}