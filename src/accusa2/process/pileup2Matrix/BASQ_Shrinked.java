package accusa2.process.pileup2Matrix;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.process.phred2prob.Phred2Prob;

public class BASQ_Shrinked extends BASQ {

	private final Phred2Prob[] phred2Probs;

	public BASQ_Shrinked() {
		name = "BASQ_REDUCED";
		desc = "BASQ -> p(A) | p(A, C) | p(A, C, G) ...)";

		// pre-calculate conversions
		phred2Probs = new Phred2Prob[DefaultPileup.LENGTH - 1];
		for(int i = 0; i < phred2Probs.length; ++i) {
			phred2Probs[i] = new Phred2Prob(2 + i);
		}
	}

	@Override
	public double[][] calculate(Character[] bases, Pileup pileup) {
		phred2Prob = phred2Probs[bases.length - 2];
		return super.calculate(bases, pileup);
	}

}
