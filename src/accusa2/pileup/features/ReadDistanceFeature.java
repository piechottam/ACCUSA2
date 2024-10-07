package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
public class ReadDistanceFeature extends AbstractFeature<Integer> {

	public static final String NAME = "read start and end distance"; 

	public ReadDistanceFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public final Integer[] build(final int n) {
		return new Integer[n];
	}

	@Override
	public void process(final int read, final DecodedSamRecord decodedSAMRecord) {
		set(read, Math.min(decodedSAMRecord.getReadPostition(), decodedSAMRecord.getSAMrecord().getReadLength() - decodedSAMRecord.getReadPostition() + 1));
	}

	@Override
	public final AbstractFeature<Integer> newInstance() {
		final AbstractFeature<Integer> copy = new ReadDistanceFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}
	
}
