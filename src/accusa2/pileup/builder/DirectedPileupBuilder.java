package accusa2.pileup.builder;

import java.util.List;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import accusa2.cache.DecodedSAMRecordCache;
import accusa2.cache.Coordinate;
import accusa2.cli.Parameters;
import accusa2.filter.factory.AbstractPileupFeatureFilterFactory;
import accusa2.pileup.DecodedSamRecord;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.DefaultPileup.STRAND;
import accusa2.util.AnnotatedCoordinate;

/**
 * @author michael
 *
 */
public class DirectedPileupBuilder extends UndirectedPileupBuilder {

	private DecodedSAMRecordCache forwardDecodedSAMrecordCache;
	private DecodedSAMRecordCache reverseDecodedSAMrecordCache;

	private List<Coordinate> forwardIndels;
	private List<Coordinate> reverseIndels;

	private STRAND strand;

	public DirectedPileupBuilder(final AnnotatedCoordinate coordinate, final SAMFileReader reader, final Parameters parameters) {
		super(coordinate, reader, parameters);
	}

	@Override
	public void adjustCurrentGenomicPosition(final int genomicPosition) {
		super.adjustCurrentGenomicPosition(genomicPosition);
		switch2FORWARD();
	}

	@Override
	public void initCache() {
		final int maxDepth = windowRecordCache.getParameters().getMaxDepth() / 2; 
		forwardDecodedSAMrecordCache= new DecodedSAMRecordCache(windowRecordCache.getParameters().getWindowSize(), maxDepth);
		reverseDecodedSAMrecordCache= new DecodedSAMRecordCache(windowRecordCache.getParameters().getWindowSize(), maxDepth);

		switch2FORWARD();
	}

	@Override
	protected void clearCache() {
		forwardDecodedSAMrecordCache.clear();
		reverseDecodedSAMrecordCache.clear();

		switch2FORWARD();
	}

	protected void switch2FORWARD() {
		decodedSAMrecordCache	= forwardDecodedSAMrecordCache;
		indels		= forwardIndels;
		strand 		= STRAND.FORWARD;
	}
	
	protected void switch2REVERSE() {
		decodedSAMrecordCache	= reverseDecodedSAMrecordCache;
		indels		= reverseIndels;
		strand 		= STRAND.REVERSE;		
	}
	
	@Override
	public boolean hasNext() {
		final int maxGenomicPosition = windowRecordCache.getMaxGenomicPosition();

		// ensure that we stay in the range
		while(genomicPosition >= 0 && genomicPosition < maxGenomicPosition) {

			// ret 0, 1, 2 indicates if RecordCaches is empty, not empty, needs to be updated...
			final int ret = windowRecordCache.adjustCurrentGenomicPosition(genomicPosition);

			switch(ret) {

			case 2:
				genomicPosition = windowRecordCache.getWindowStart();
				clearCache();
				processWindowRecordCache();
				break;

			case 0:
				return false;

			}

			if(isValid(genomicPosition)) {
				return true;
			}

			switch(strand) {

			case FORWARD:
				switch2REVERSE();
				break;

			case REVERSE:
				switch2FORWARD();

				++genomicPosition;
				break;

			case UNKNOWN:
				// nothing to be done
				break;
			}
		}

		return false;		
		
	}

	// FIXME - make this smaller
	protected DefaultPileup getPileup() {
		if(strand == STRAND.FORWARD) {
			return super.getPileup();
		}
		
		// copied from UndirectedPileupBuilder
		final int windowPosition = windowRecordCache.convertGenomicPosition2WindowPosition(genomicPosition);

		final DefaultPileup pileup = new DefaultPileup();
		pileup.setContig(windowRecordCache.getContig());
		pileup.setPosition(genomicPosition);

		final int length = decodedSAMrecordCache.size(windowPosition);
		final char[] bases = new char[length];
		final byte[] basqs = new byte[length];
		final byte[] mapqs = new byte[length];

		for(final AbstractPileupFeatureFilterFactory<?> factory : windowRecordCache.getParameters().getPileupFeatureFilterFactories()) {
			try {
				pileup.getFeatureContainer().addFeature(factory.createEmptyFeature(length));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for(int read = 0; read < length; ++read) {
			final DecodedSamRecord decodedSAMRecord = decodedSAMrecordCache.get(windowPosition)[read];
			final SAMRecord SAMrecord = decodedSAMRecord.getSAMrecord();
			final int readPosition = decodedSAMRecord.getReadPostition();

			bases[read] = DefaultPileup.BASES2_COMPLEMENT.get((char)SAMrecord.getReadBases()[readPosition]);
			basqs[read] = (byte)SAMrecord.getBaseQualities()[readPosition];
			mapqs[read] = (byte)SAMrecord.getMappingQuality();

			pileup.getFeatureContainer().process(read, decodedSAMRecord);
		}
		pileup.setBases(bases);
		pileup.setBASQs(basqs);
		pileup.setMAPQs(mapqs);

		return pileup;
	}

	@Override
	public DefaultPileup next() {
		if(!hasNext()) {
			return null;
		}
		final DefaultPileup pileup = getPileup();
		pileup.setStrand(strand);

		switch(strand) {

		case FORWARD:
			switch2REVERSE();
			break;

		case REVERSE:
			switch2FORWARD();

			++genomicPosition;
			break;

		case UNKNOWN:
			break;

		}

		return pileup;
	}

	/**
	 * 
	 * @param readPosition
	 * @param genomicPosition
	 * @param cigarElement
	 * @param record
	 * @return
	 */
	@Override
	public void processWindowRecordCache() {
		clearCache();
		for(final SAMRecord record : windowRecordCache.getRecords()) {
			try {
				if(record.getReadNegativeStrandFlag()) {
					switch2REVERSE();
				} else {
					switch2FORWARD();
				}

				processRecord(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		switch2FORWARD();
	}
	
}
