package accusa2.filter.process;

import accusa2.pileup.DefaultPileup;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

// FIXME what should be fixed?
public abstract class AbstractFeaturePileupFilter extends AbstractPileupFilter {

	public AbstractFeaturePileupFilter(char c) {
		super(c);
	}

	// TODO make documentation
	public boolean filter(ParallelPileup parallelPileup) {
		this.parallelPileup = new ParallelPileup(1, 1);
		Pileup pileup1 = parallelPileup.getPileups1()[0];
		Pileup pileup2 = parallelPileup.getPileups2()[0];

		char variant = PileupUtils.getVariantBases(pileup1, pileup2)[0];
		int[] baseCount = PileupUtils.getBaseCount(pileup1, pileup2);

		int[] mask1 = calculateMask(variant, pileup1);
		int[] mask2 = calculateMask(variant, pileup2);
		if(mask1.length == 0 && mask2.length == 0) {
			return false;
		}

		Pileup filteredPileup1 = null;
		if(mask1.length == 0) {
			filteredPileup1 = new DefaultPileup(pileup1);
		} else {
			filteredPileup1 = PileupUtils.mask(mask1, pileup1);
		}

		Pileup filteredPileup2 = null;
		if(mask2.length == 0) {
			filteredPileup2 = new DefaultPileup(pileup2);
		} else {
			filteredPileup2 = PileupUtils.mask(mask2, pileup2);
		}

		int[] filteredBaseCount = PileupUtils.getBaseCount(filteredPileup1, filteredPileup2);
		int b2i = DefaultPileup.BASE2INT.get(variant);
		// if 50% of variants sites are filtered accept filter as valid
		if(baseCount[b2i] / 2 < filteredBaseCount[b2i]) {
			return false;
		}

		this.parallelPileup.setPileup1(0, filteredPileup1);
		this.parallelPileup.setPileup2(0, filteredPileup2);
		return true;
	}

	public abstract int[] calculateMask(char variant, Pileup pileup);

}
