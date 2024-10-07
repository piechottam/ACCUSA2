package accusa2.method.statistic;


import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import accusa2.pileup.Pileup;
import accusa2.process.pileup2Matrix.AbstractPileup2Matrix;
import accusa2.process.pileup2Matrix.BASQ;

public class MinimalCoverageStatistic implements StatisticCalculator {

	protected AbstractPileup2Matrix pileup2Matrix;
	protected char[] observedBases;
	
	public MinimalCoverageStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new MinimalCoverageStatistic();
	}

	public double getStatistic(Pileup pileup1, Pileup pileup2) {
	assert(pileup1.getContig().equals(pileup2.getContig()) && pileup1.getPosition() == pileup2.getPosition());

		double[][] matrix1 = pileup2Matrix.calculate(pileup1);
		double[][] matrix2 = pileup2Matrix.calculate(pileup2);

		int minCoverage = Math.min(pileup1.getCoverage(), pileup2.getCoverage());
		
		double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		double[] alpha1 = estimateAlpha(prob1, minCoverage);

		DirichletDist dirichlet1 = new DirichletDist(alpha1);
		double density11 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob1)));

		double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		double[] alpha2 = estimateAlpha(prob2, minCoverage);

		DirichletDist dirichlet2 = new DirichletDist(alpha2);
		double density22 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));

		double density12 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob2)));
		double density21 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob1)));

		double z = (density11 + density22) - (density12 + density21);
		return z;
	}

	protected double[] estimateAlpha(double[] mean, int coverage) {
		double[] alpha = new double[mean.length];

		for(int i = 0; i < mean.length; ++i) {
			alpha[i] = coverage * mean[i];
		}

		return alpha;
	}

	@Override
	public String getDescription() {
		return "Use the minimal coverage to estimate alpha(s)";
	}

	@Override
	public String getName() {
		return "coverage";
	}

}