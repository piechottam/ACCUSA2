package accusa2.pileup.sample;

import java.util.Random;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public final class SamplePileupWithReplacement {

	private final Random random = new Random();

	
	public Pileup samplePileup(final Pileup pooled, final Pileup pileup) {
		final DefaultPileup bootstrapped = new DefaultPileup(pileup);
		final int n = pileup.getCoverage();
		final char[] bases = new char[n];
		final byte[] basqs = new byte[n];

		int r = 0;
		for(int i = 0; i < n; ++i) {
			r = random.nextInt(pooled.getCoverage());
			bases[i] = pooled.getBases()[r];
			basqs[i] = pooled.getBASQs()[r];
		}
		bootstrapped.setBases(bases);
		bootstrapped.setBASQs(basqs);
		return bootstrapped;
	}

	public Pileup[] sample(final Pileup pileup1, final Pileup pileup2) {
		final Pileup[] pileups = new Pileup[2];
		
		final Pileup pooled = PileupUtils.mergePileups(pileup1, pileup2);
		pileups[0] = samplePileup(pooled, pileup1);
		pileups[1] = samplePileup(pooled, pileup2);

		return pileups;
	}
	
}