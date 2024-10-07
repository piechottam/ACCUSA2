package accusa2.pileup.sample;

import java.util.Random;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public final class RobustSamplePileupWithoutReplacement {

	
	private int cov1;
	private int cov2;
	private int cov;

	private DefaultPileup pileup;
	private DefaultPileup baseBasqPileup1;
	private DefaultPileup baseBasqPileup2;

	private DefaultPileup basePileup1;
	private DefaultPileup basePileup2;

	private Random random = new Random();

	public void setPileups(final Pileup pileup1, final Pileup pileup2) {
		cov1 = pileup1.getCoverage();
		cov2 = pileup2.getCoverage();
		cov = cov1 + cov2;

		baseBasqPileup1 = new DefaultPileup();
		baseBasqPileup1.setBases(new char[cov1]);
		baseBasqPileup1.setBASQs(new byte[cov1]);
		basePileup1 = new DefaultPileup(pileup1);

		baseBasqPileup2 = new DefaultPileup();
		baseBasqPileup2.setBases(new char[cov2]);
		baseBasqPileup2.setBASQs(new byte[cov2]);
		basePileup2 = new DefaultPileup(pileup2);

		pileup = PileupUtils.mergePileups(pileup1, pileup2);
	}

	public Pileup[] sample() {
		final char[] bases = pileup.getBases();
		final byte[] basqs = pileup.getBASQs();

		for(int i = 0; i < cov; ++i) {
			final int r1 = random.nextInt(cov);
			final int r2 = random.nextInt(cov);

			final char base = pileup.getBases()[r1];
			bases[r1] = pileup.getBases()[r2];
			pileup.getBases()[r2] = base;

			final byte basq = pileup.getBASQs()[r1];
			basqs[r1] = pileup.getBASQs()[r2];
			pileup.getBASQs()[r2] = basq;
		}

		System.arraycopy(pileup.getBases(), 0, baseBasqPileup1.getBases(), 0, cov1);
		System.arraycopy(pileup.getBases(), cov1, baseBasqPileup2.getBases(), 0, cov2);
		basePileup1.setBases(baseBasqPileup1.getBases());
		
		System.arraycopy(pileup.getBASQs(), 0, baseBasqPileup1.getBASQs(), 0, cov1);
		System.arraycopy(pileup.getBASQs(), cov1, baseBasqPileup2.getBASQs(), 0, cov2);
		basePileup2.setBases(baseBasqPileup2.getBases());
		
		
		final Pileup[] pileups = new Pileup[4];
		pileups[0] = baseBasqPileup1;
		pileups[1] = baseBasqPileup2;

		pileups[2] = basePileup1;
		pileups[3] = basePileup2;

		return pileups;
	}

}
