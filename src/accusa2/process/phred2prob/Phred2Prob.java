package accusa2.process.phred2prob;

import accusa2.pileup.DefaultPileup;

public final class Phred2Prob {

	private final double[] phred2errerP;
	private final double[] phred2baseP;
	private final double[] phred2baseErrorP;

	public Phred2Prob() {
		this(DefaultPileup.LENGTH);
	}

	public Phred2Prob(int n) {
		// pre-calculate probabilities
		final int min = 0;
		final int max = 256;
		phred2errerP = new double[max];
		phred2baseP = new double[max];
		phred2baseErrorP = new double[max];
		for(int i = min; i < max; i++) {
			phred2errerP[i] = Math.pow(10.0, -(double)i / 10.0);
			phred2baseP[i] = 1.0 - phred2errerP[i];
			phred2baseErrorP[i] = phred2errerP[i] / (n - 1); // ignore the called base
		}
	}

	public double convert2errorP(byte qual) {
		return phred2errerP[qual];
	}

	public double convert2P(byte qual) {
		return phred2baseP[qual];
	}
	
	public double convert2perEntityP(byte qual) {
		return phred2baseErrorP[qual];
	}
	
	public double getErrorP(byte qual) {
		return phred2errerP[qual];
	}

}
