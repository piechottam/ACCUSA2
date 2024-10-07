package accusa2.process.pileup2Matrix;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.process.phred2prob.Phred2Prob;

@Deprecated
public class BASQ_MAPQ extends AbstractPileup2Matrix {

	private Phred2Prob phred2Prob;
	
	public BASQ_MAPQ() {
		name = "BASQ+MAPQ";
		desc = "BASQ+MAPQ -> p(A, C, G, T)";
		
		phred2Prob = new Phred2Prob();
	}

	@Override
	public double[][] calculate(Pileup pileup) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public double[][] calculate(Character[] bases, Pileup pileup) {
		double[][] probs = new double[pileup.getCoverage()][DefaultPileup.LENGTH];

		for(int i = 0; i < pileup.getCoverage(); ++i) {
			switch(pileup.getBases()[i]) {
			case 'A' :
			case 'C' :
			case 'G' :
			case 'T' :
				double MAPQbaseP = phred2Prob.convert2P(pileup.getMAPQs()[i]);
				double MAPQperBaseErrorP = phred2Prob.convert2perEntityP(pileup.getMAPQs()[i]);

				double BASQbaseP = phred2Prob.convert2P(pileup.getBASQs()[i]);
				double BASQperBaseErrorP = phred2Prob.convert2perEntityP(pileup.getBASQs()[i]);

				double sum = 0.0;
				for(int j = 0; j < DefaultPileup.LENGTH; ++j) {
					if(j == DefaultPileup.BASE2INT.get(pileup.getBases()[i])) {
						probs[i][j] += MAPQbaseP * BASQbaseP;
						sum += MAPQbaseP * BASQbaseP;
					} else {
						probs[i][j] += MAPQperBaseErrorP * BASQperBaseErrorP;
						sum += MAPQperBaseErrorP * BASQperBaseErrorP;
					}
				}

				for(int j = 0; j < DefaultPileup.LENGTH; ++j) {
					probs[i][j] /= sum;
				}

			case 'N' : 
				break;

			default : 
				throw new IllegalArgumentException("Unknown DNA base: " + pileup.getBases()[i]);
			}
		}

		return probs;
	}

}