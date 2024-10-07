package accusa2.filter.process;

import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public class HomozygousProcessPileupFilter extends AbstractPileupFilter {

	private int sample;

	public HomozygousProcessPileupFilter(char c, int sample) {
		super(c);
		this.sample = sample;
	}

	@Override
	public boolean filter(ParallelPileup parallelPileup) {
		this.parallelPileup = null;
		Pileup pileup = null;

		switch(sample) {

		case 1:
			pileup = parallelPileup.getPileups1()[0];
			break;

		case 2:
			pileup = parallelPileup.getPileups2()[0];
			break;

		default:
			throw new IllegalArgumentException("Unsupported sample!");
		}

		if(PileupUtils.isMultiAllelic(pileup)) {
			return true;
		}

		this.parallelPileup = parallelPileup;
		return false;
	}
	
	@Override
	public boolean quitFiltering() {
		return parallelPileup == null;
	}

	public final int getSample() {
		return sample;
	}

}
