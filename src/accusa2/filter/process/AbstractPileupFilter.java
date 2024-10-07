package accusa2.filter.process;

import accusa2.pileup.ParallelPileup;

public abstract class AbstractPileupFilter {
	
	private char c;

	protected ParallelPileup parallelPileup;
	
	public AbstractPileupFilter(char c) {
		this.c = c;
	}

	public abstract boolean filter(ParallelPileup parallelPileup);

	public boolean quitFiltering() {
		return false;
	}

	public ParallelPileup getFilteredParallelPileup() {
		return parallelPileup;
	}

	public final char getC() {
		return c;
	}

}
