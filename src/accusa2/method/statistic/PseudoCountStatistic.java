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

public class PseudoCountStatistic implements StatisticCalculator {

	protected AbstractPileup2Matrix pileup2Matrix;

	public PseudoCountStatistic() {
		pileup2Matrix = new BASQ();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new PseudoCountStatistic();
	}

	private Pileup getMissingPileup(Set<Character> basesSet, Pileup pileup) {
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

	public double getStatistic(final Pileup pileup1, final Pileup pileup2) {
		Set<Character> observedBases = PileupUtils.getUniqueBasesSet(pileup1, pileup2);

		// add p
		Pileup missingPileup1 = getMissingPileup(observedBases, pileup1);
		Pileup missingPileup2 = getMissingPileup(observedBases, pileup2);

		Pileup modififed1 = new DefaultPileup(pileup1);
		Pileup modififed2 = new DefaultPileup(pileup2);

		if(missingPileup1 != null) {
			modififed1 = PileupUtils.mergePileups(modififed1, missingPileup1);
			modififed2 = PileupUtils.mergePileups(modififed2, missingPileup1);
		}

		if(missingPileup2 != null) {
			modififed2 = PileupUtils.mergePileups(modififed2, missingPileup2);
			modififed1 = PileupUtils.mergePileups(modififed1, missingPileup2);
		}

		double[][] matrix1 = pileup2Matrix.calculate(modififed1);
		double[][] matrix2 = pileup2Matrix.calculate(modififed2);
	
		Pileup pileupP = PileupUtils.mergePileups(pileup1, pileup2);
		int minCoverage = Math.min(modififed1.getCoverage(), modififed2.getCoverage());
	
		double[][] matrixP = pileup2Matrix.calculate(pileupP);
	
		double[] prob1 = accusa2.util.MathUtil.mean(matrix1);
		double[] alpha1 = estimateAlpha(prob1, modififed1.getCoverage());
	
		DirichletDist dirichlet1 = new DirichletDist(alpha1);
		double density11 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet1.density(prob1)));
	
		double[] prob2 = accusa2.util.MathUtil.mean(matrix2);
		double[] alpha2 = estimateAlpha(prob2, modififed2.getCoverage());
	
		DirichletDist dirichlet2 = new DirichletDist(alpha2);
		double density22 = Math.log10(Math.max(Double.MIN_VALUE, dirichlet2.density(prob2)));
	
		double[] probP = accusa2.util.MathUtil.mean(matrixP);
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
		return "pseudo";
	}

}