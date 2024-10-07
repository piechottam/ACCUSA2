package accusa2.method.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import umontreal.iro.lecuyer.probdistmulti.DirichletDist;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;
import accusa2.process.pileup2Matrix.AbstractPileup2Matrix;
import accusa2.process.pileup2Matrix.BASQ;

import accusa2.util.MathUtil;

public class TestStatistic implements StatisticCalculator {

	protected AbstractPileup2Matrix pileup2Matrix;

	public TestStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new TestStatistic();
	}

	private Pileup addPseudoCount(Set<Character> basesSet, Pileup pileup) {
		List<Character> missing = new ArrayList<Character>(DefaultPileup.BASES2.length); 

		for(char b : basesSet) {
			if(!pileup.getBaseSortedSet().contains(b)) {
				missing.add(b);
			}
		}
		if(missing.size() == 0) {
			return null;
		}

		Pileup ret = new DefaultPileup();
		char[] bases = new char[missing.size()];
		byte[] basqs = new byte[missing.size()];

		for(int i = 0; i < missing.size(); ++i) {
			bases[i] = missing.get(i);
			basqs[i] = MathUtil.mean(pileup.getBASQs());
		}
		ret.setBases(bases);
		ret.setBASQs(basqs);

		return ret;
	}

	public double getStatistic(Pileup pileup1, Pileup pileup2) {
		Set<Character> observedBases = PileupUtils.getUniqueBasesSet(pileup1, pileup2);

		// add p
		Pileup missingPileup1 = addPseudoCount(observedBases, pileup1);
		Pileup missingPileup2 = addPseudoCount(observedBases, pileup2);
		
		if(missingPileup1 != null) {
			pileup1 = PileupUtils.mergePileups(pileup1, missingPileup1);
			pileup2 = PileupUtils.mergePileups(pileup2, missingPileup1);
		}
		
		if(missingPileup2 != null) {
			pileup2 = PileupUtils.mergePileups(pileup2, missingPileup2);
			pileup1 = PileupUtils.mergePileups(pileup1, missingPileup2);
		}
		
		double[][] matrix1 = pileup2Matrix.calculate(pileup1);
		double[][] matrix2 = pileup2Matrix.calculate(pileup2);
	
		Pileup pileupP = PileupUtils.mergePileups(pileup1, pileup2);
		// TEST int coverage = (pileup1.getCoverage() + pileup2.getCoverage()) / 2;
		int minCoverage = Math.min(pileup1.getCoverage(), pileup2.getCoverage());
	
		double[][] matrixP = pileup2Matrix.calculate(pileupP);
	
		double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		double[] alpha1 = estimateAlpha(prob1, pileup1.getCoverage());
		// TEST double[] alpha1 = estimateAlpha(prob1, coverage);
	
		DirichletDist dirichlet1 = new DirichletDist(alpha1);
		double density11 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob1)));
	
		double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		double[] alpha2 = estimateAlpha(prob2, pileup2.getCoverage());
		// TEST double[] alpha2 = estimateAlpha(prob2, coverage);
	
		DirichletDist dirichlet2 = new DirichletDist(alpha2);
		double density22 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));
	
		double[] probP = accusa2.util.MathUtil.mean(matrixP);
		// TEST double[] alphaP = estimateAlpha(probP, pileupP.getCoverage());
		double[] alphaP = estimateAlpha(probP, minCoverage);
		DirichletDist dirichletP = new DirichletDist(alphaP);
	
		double density1P = Math.log10(Math.max(Double.MIN_VALUE, dirichletP.density(prob1)));
		double density2P = Math.log10(Math.max(Double.MIN_VALUE, dirichletP.density(prob2)));

		double z = (density11 + density22) - (density1P + density2P);
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
		return "test";
	}

}