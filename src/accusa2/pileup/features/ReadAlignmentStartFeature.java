package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
@Deprecated
public class ReadAlignmentStartFeature extends AbstractFeature<Integer> {

	public static String NAME = "read alignment position"; 

	public ReadAlignmentStartFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(int read, DecodedSamRecord decodedSAMRecord) {
		set(read, decodedSAMRecord.getSAMrecord().getAlignmentStart());
	}

	@Override
	public AbstractFeature<Integer> newInstance() {
		AbstractFeature<Integer> copy = new ReadAlignmentStartFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}
	
}
