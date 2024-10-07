package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
@Deprecated
public class ReadEndDistanceFeature extends AbstractFeature<Integer> {

	public static String NAME = "read end distance";

	public ReadEndDistanceFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(int read, DecodedSamRecord decodedSAMRecord) {
		// TODO check what the real read length is
		set(read, decodedSAMRecord.getSAMrecord().getReadLength() - decodedSAMRecord.getReadPostition() + 1);
	}

	@Override
	public AbstractFeature<Integer> newInstance() {
		AbstractFeature<Integer> copy = new ReadEndDistanceFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}
	
}
