package accusa2.pileup.features;

import accusa2.cache.Coordinate;
import accusa2.pileup.DecodedSamRecord;

/**
 * @author mpiechotta
 *
 */
public class DistanceFeature extends AbstractFeature<Integer> {

	public static final String NAME = "minimal read start, end, INDEL distance"; 

	public DistanceFeature(int n, char c) {
		super(n, c, NAME);
	}

	@Override
	public final Integer[] build(int n) {
		return new Integer[n];
	}

	@Override
	public void process(final int read, final DecodedSamRecord decodedSamRecord) {
		int distance = Math.min(decodedSamRecord.getReadPostition(), decodedSamRecord.getSAMrecord().getReadLength() - decodedSamRecord.getReadPostition());

		if(decodedSamRecord.getIndels().size() > 0) {
			distance = getIndelDistance(distance, decodedSamRecord);
		}

		if(decodedSamRecord.getSkipped().size() > 0) {
			distance = getSkippedDistance(distance, decodedSamRecord);
		}

		set(read, distance);
	}

	public int getIndelDistance(int distance, final DecodedSamRecord decodedSAMRecord) {
		for(final Coordinate indel : decodedSAMRecord.getIndels()) {
			distance = Math.min(distance, Math.abs(decodedSAMRecord.getGenomicPostition() - indel.getGenomicPosition()));
			distance = Math.min(distance, Math.abs(decodedSAMRecord.getGenomicPostition() - (indel.getGenomicPosition() + indel.getCigarElement().getLength())));
		}

		return distance;
	}

	public int getSkippedDistance(int distance, final DecodedSamRecord decodedSamRecord) {
		for(final Coordinate skipped : decodedSamRecord.getSkipped()) {
			distance = Math.min(distance, Math.abs(decodedSamRecord.getGenomicPostition() - skipped.getGenomicPosition()));
			distance = Math.min(distance, Math.abs(decodedSamRecord.getGenomicPostition() - (skipped.getGenomicPosition() + skipped.getCigarElement().getLength())));
		}

		return distance;
	}
	
	@Override
	public final DistanceFeature newInstance() {
		final DistanceFeature copy = new DistanceFeature(getN(), getC());
		copy.set(get().clone());
		return copy;
	}
	
}
