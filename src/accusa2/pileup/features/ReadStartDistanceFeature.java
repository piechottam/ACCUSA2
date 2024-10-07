package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
@Deprecated
public class ReadStartDistanceFeature extends AbstractFeature<Integer> {

	public static String NAME = "read start distance"; 

	public ReadStartDistanceFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(int read, DecodedSamRecord decodedSAMRecord) {
		set(read, decodedSAMRecord.getReadPostition());
	}

	@Override
	public AbstractFeature<Integer> newInstance() {
		AbstractFeature<Integer> copy = new ReadStartDistanceFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}
	
}
