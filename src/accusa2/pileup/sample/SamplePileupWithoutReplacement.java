package accusa2.pileup.sample;

import java.util.Random;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public class SamplePileupWithoutReplacement {

	private Random random = new Random();

	public Pileup[] sample(final Pileup pileup1, final Pileup pileup2) {
		Pileup pileup = new DefaultPileup(PileupUtils.mergePileups(pileup1, pileup2));

		Pileup[] pileups = new Pileup[2];
		pileups[0] = new DefaultPileup();
		pileups[1] = new DefaultPileup();

		char[] bases1 = new char[pileup1.getCoverage()];
		byte[] basqs1 = new byte[pileup1.getCoverage()];

		char[] bases2 = new char[pileup2.getCoverage()];
		byte[] basqs2 = new byte[pileup2.getCoverage()];

		for(int i = 0; i < pileup.getCoverage(); ++i) {
			int r1 = random.nextInt(pileup.getCoverage());
			int r2 = random.nextInt(pileup.getCoverage());

			char base = pileup.getBases()[r1];
			byte basq = pileup.getBASQs()[r1];

			pileup.getBases()[r1] = pileup.getBases()[r2];
			pileup.getBases()[r2] = base;

			pileup.getBASQs()[r1] = pileup.getBASQs()[r2];
			pileup.getBASQs()[r2] = basq;
		}

		System.arraycopy(pileup.getBases(), 0, bases1, 0, bases1.length);
		pileups[0].setBases(bases1);
		System.arraycopy(pileup.getBases(), bases1.length, bases2, 0, bases2.length);
		pileups[1].setBases(bases2);

		System.arraycopy(pileup.getBASQs(), 0, basqs1, 0, basqs1.length);
		pileups[0].setBASQs(basqs1);
		System.arraycopy(pileup.getBASQs(), basqs1.length, basqs2, 0, basqs2.length);
		pileups[1].setBASQs(basqs2);

		return pileups;
	}

}
