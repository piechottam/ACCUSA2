package accusa2.pileup.features;

import accusa2.cache.Coordinate;
import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
public class INDEL_DistanceFeature extends AbstractFeature<Integer> {

	public static String NAME = "INDEL distance"; 

	public INDEL_DistanceFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(int read, DecodedSamRecord decodedSAMrecord) {
		// TODO
		int distance = Integer.MAX_VALUE;

		for(Coordinate indel : decodedSAMrecord.getIndels()) {
			distance = Math.min(distance, Math.abs(decodedSAMrecord.getGenomicPostition() - indel.getGenomicPosition()));
		}

		set(read, distance);
	}

	@Override
	public INDEL_DistanceFeature newInstance() {
		INDEL_DistanceFeature copy = new INDEL_DistanceFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}

}
