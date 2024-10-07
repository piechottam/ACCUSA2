package accusa2.process.pileup2Matrix;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.process.phred2prob.Phred2Prob;

public class BASQ extends AbstractPileup2Matrix {

	protected Phred2Prob phred2Prob;
	
	public BASQ() {
		name = "BASQ";
		desc = "BASQ -> p(A, C, G, T)";

		phred2Prob = new Phred2Prob();
	}

	// FIXME
	@Override
	public double[][] calculate(Pileup pileup) {
		return calculate(DefaultPileup.BASES3, pileup);
	}
	
	@Override
	public double[][] calculate(Character[] bases, Pileup pileup) {
		double[][] probs = new double[pileup.getCoverage()][bases.length];

		for(int i = 0; i < pileup.getCoverage(); ++i) {
			switch(Character.toUpperCase(pileup.getBases()[i])) {
			case 'A' :
			case 'C' :
			case 'G' :
			case 'T' :
				double baseP = phred2Prob.convert2P(pileup.getBASQs()[i]);
				double perBaseErrorP = phred2Prob.convert2perEntityP(pileup.getBASQs()[i]); 

				for(int j = 0; j < bases.length; ++j) {
					if(bases[j] == pileup.getBases()[i]) {
						probs[i][j] += baseP;
					} else {
						probs[i][j] += perBaseErrorP;
					}
				}
				break;

			case 'N' : 
				break;

			default : 
				throw new IllegalArgumentException("Unknown DNA base: " + pileup.getBases()[i]);
			}
		}

		return probs;
	}

}
