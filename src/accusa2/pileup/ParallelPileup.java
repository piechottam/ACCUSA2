package accusa2.pileup;

public final class ParallelPileup {

	private Pileup[] pileups1;
	private Pileup[] pileups2;

	public ParallelPileup(final int n1, final int n2) {
		pileups1 = new DefaultPileup[n1];
		pileups2 = new DefaultPileup[n2];
	}

	public ParallelPileup(final Pileup[] pileups1, final Pileup[] pileups2) {
		this.pileups1 = pileups1;
		this.pileups2 = pileups2;
	}

	public Pileup[] getPileups1() {
		return pileups1;
	}

	public Pileup[] getPileups2() {
		return pileups2;
	}

	public void setPileups1(final Pileup[] pileups1) {
		this.pileups1 = pileups1;
	}

	public void setPileups2(final Pileup[] pileups2) {
		this.pileups2 = pileups2;
	}

	public void setPileup1(final int i, final Pileup pileup) {
		pileups1[i] = pileup;
	}
	
	public void setPileup2(final int i, final Pileup pileup) {
		pileups2[i] = pileup;
	}

	public int getN1() {
		return pileups1.length;
	}

	public int getN2() {
		return pileups2.length;
	}
	
}
