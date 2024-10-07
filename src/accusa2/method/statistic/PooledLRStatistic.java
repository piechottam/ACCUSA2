package accusa2.method.statistic;


import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;
import accusa2.process.pileup2Matrix.AbstractPileup2Matrix;
import accusa2.process.pileup2Matrix.BASQ;

@Deprecated
public class PooledLRStatistic implements StatisticCalculator {

	protected AbstractPileup2Matrix pileup2Matrix;

	public PooledLRStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new PooledLRStatistic();
	}

	public double getStatistic(Pileup pileup1, Pileup pileup2) {
	assert(pileup1.getContig().equals(pileup2.getContig()) && pileup1.getPosition() == pileup2.getPosition());

		int coverage = Math.min(pileup1.getCoverage(), pileup2.getCoverage());

		double[][] matrix1 = pileup2Matrix.calculate(pileup1);
		Pileup pileupP = PileupUtils.mergePileups(pileup1, pileup2);
		double[][] matrixP = pileup2Matrix.calculate(pileupP);

		double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		//double[] alpha1 = estimateAlpha(prob1, pileup1.getCoverage());
		double[] alpha1 = estimateAlpha(prob1, coverage);

		DirichletDist dirichlet1 = new DirichletDist(alpha1);
		double density11 = Math.log(Math.max(Double.MIN_VALUE, dirichlet1.density(prob1)));

		double[][] matrix2 = pileup2Matrix.calculate(pileup2);
		double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		//double[] alpha2 = estimateAlpha(prob2, pileup2.getCoverage());
		double[] alpha2 = estimateAlpha(prob2, coverage);

		DirichletDist dirichlet2 = new DirichletDist(alpha2);
		double density22 = Math.log(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));

		double[] probP = accusa2.util.MathUtil.mean(matrixP);
		//double[] alphaP = estimateAlpha(probP, pileupP.getCoverage());
		double[] alphaP = estimateAlpha(probP, coverage);
		DirichletDist dirichletP = new DirichletDist(alphaP);

		double density1P = Math.log(Math.max(Double.MIN_VALUE, dirichletP.density(prob1)));
		double density2P = Math.log(Math.max(Double.MIN_VALUE, dirichletP.density(prob2)));

		double z = -2 * ( ( density1P + density2P ) - ( density11 + density22 ) );
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
		return "";
	}

	@Override
	public String getName() {
		return "pooledLR";
	}

}