package accusa2.filter.process;

import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public class PolymorphismPileupFilter extends AbstractPileupFilter {

	public PolymorphismPileupFilter(char c) {
		super(c);
	}

	@Override
	public boolean filter(ParallelPileup parallelPileup) {
		this.parallelPileup = null;
		Pileup pileup1 = parallelPileup.getPileups1()[0];
		Pileup pileup2 = parallelPileup.getPileups2()[0];

		// true when all bases are A in pileup1 and C in pileup pileup2
		if(!PileupUtils.isMultiAllelic(pileup1) && !PileupUtils.isMultiAllelic(pileup2)) {
			return true;
		}

		this.parallelPileup = parallelPileup;
		return false;
	}

	@Override
	public boolean quitFiltering() {
		return parallelPileup == null;
	}

}
