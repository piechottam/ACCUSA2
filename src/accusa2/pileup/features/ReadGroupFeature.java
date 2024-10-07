package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
@Deprecated
public class ReadGroupFeature extends AbstractFeature<Integer> {

	public static String NAME = "read group";

	public ReadGroupFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(int read, DecodedSamRecord decodedSAMRecord) {
		/*
		int readGroup = ReadGroupPileupFilterFactory.getSingleton().processReadGroupId(recordCache.get(windowPosition)[read].getReadGroup().getId());
		set(read, readGroup);
		*/
	}
	
	@Override
	public AbstractFeature<Integer> newInstance() {
		AbstractFeature<Integer> copy = new ReadGroupFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}

}
