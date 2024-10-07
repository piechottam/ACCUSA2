/**
 * 
 */
package accusa2.pileup.builder;

import java.util.List;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import accusa2.cache.DecodedSAMRecordCache;
import accusa2.cache.Coordinate;
import accusa2.cache.WindowRecordCache;
import accusa2.cli.Parameters;
import accusa2.filter.factory.AbstractPileupFeatureFilterFactory;
import accusa2.pileup.DecodedSamRecord;
import accusa2.pileup.DefaultPileup;
import accusa2.util.AnnotatedCoordinate;

/**
 * @author michael
 *
 */
public class UndirectedPileupBuilder extends AbstractPileupBuilder {

	protected WindowRecordCache windowRecordCache;
	protected DecodedSAMRecordCache decodedSAMrecordCache;

	public UndirectedPileupBuilder(final AnnotatedCoordinate coordinate, final SAMFileReader reader, final Parameters parameters) {
		windowRecordCache 	= new WindowRecordCache(coordinate, reader, parameters);
		genomicPosition 	= coordinate.getStart();
	}

	/**
	 * 
	 */
	public void initCache() {
		decodedSAMrecordCache = new DecodedSAMRecordCache(windowRecordCache.getParameters().getWindowSize(), windowRecordCache.getParameters().getMaxDepth());
	}

	/**
	 * 
	 */
	protected void clearCache() {
		decodedSAMrecordCache.clear();
	}

	/**
	 * 
	 * @return
	 */
	public void processWindowRecordCache() {
		clearCache();
		for(final SAMRecord record : windowRecordCache.getRecords()) {
			try {
				processRecord(record);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		final int maxGenomicPosition = windowRecordCache.getMaxGenomicPosition();
		// ensure that we stay in the range
		if(genomicPosition >= maxGenomicPosition || genomicPosition < -1) {
			return false;
		}

		while(genomicPosition >= 0 && genomicPosition < maxGenomicPosition) {
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

			++genomicPosition;
		}

		return false;
	}

	@Override
	public DefaultPileup next() {
		if(!hasNext()) {
			return null;
		}

		DefaultPileup pileup = getPileup();
		++genomicPosition;
		return pileup;
	}

	protected boolean isValid(int genomicPosition) {
		int windowPosition = windowRecordCache.convertGenomicPosition2WindowPosition(genomicPosition);
		return decodedSAMrecordCache.size(windowPosition) > 0 && decodedSAMrecordCache.size(windowPosition) >= windowRecordCache.getParameters().getMinCoverage();
	}

	protected DefaultPileup getPileup() {
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

			bases[read] = (char)SAMrecord.getReadBases()[readPosition];
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
	protected void processInsertion(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		indels.add(new Coordinate(genomicPosition, readPosition, cigarElement));
	}

	@Override
	protected void processAlignmetMatch(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		for(int i = 0; i < cigarElement.getLength(); ++i) {
			if(record.getBaseQualities()[readPosition] < windowRecordCache.getParameters().getMinBASQ()) {
				// iterate
				++readPosition;
				++genomicPosition;
			} else {
				cachePosition(readPosition, genomicPosition, cigarElement, indels, skipped, record);

				// iterate
				++readPosition;
				++genomicPosition;
			}
		}
	}

	@Override
	protected void processHardClipping(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		System.err.println("Hard Clipping not handled yet!");
	}

	@Override
	protected void processDeletion(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		processInsertion(readPosition, genomicPosition, cigarElement, record);
	}

	@Override
	protected void processSkipped(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		skipped.add(new Coordinate(genomicPosition, readPosition, cigarElement));
	}

	@Override
	protected void processSoftClipping(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		// ignore
	}

	@Override
	protected void processPadding(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record) {
		System.err.println("Padding not handled yet!");
	}

	/**
	 * 
	 * @param readPosition
	 * @param genomicPosition
	 * @param cigarElement
	 * @param record
	 * @return
	 */
	protected int cachePosition(int readPosition, int genomicPosition, final CigarElement cigarElement, final List<Coordinate> indels, final List<Coordinate> skipped, final SAMRecord record) {
		if(!windowRecordCache.isContained(genomicPosition)) {
			return -1;
		}

		final int windowPosition = windowRecordCache.convertGenomicPosition2WindowPosition(genomicPosition);

		if(decodedSAMrecordCache.isFull(windowPosition)) {
			return -1;
		}

		// data set specific rat data - don't process masked data
		if(record.getReadBases()[readPosition] == 'N') {
			return -1;
		}

		final int read = decodedSAMrecordCache.size(windowPosition);

		final DecodedSamRecord decodedSAMRecord = new DecodedSamRecord(readPosition, genomicPosition, cigarElement, indels, skipped, record);
		decodedSAMrecordCache.add(windowPosition, decodedSAMRecord);

		return read;
	}

	protected final int getGenomicPosition() {
		return genomicPosition;
	}

}
