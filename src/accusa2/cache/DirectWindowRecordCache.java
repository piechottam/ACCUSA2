package accusa2.cache;

import java.io.File;

import java.io.IOException;
import java.util.List;

import accusa2.cli.Parameters;
import accusa2.filter.samtag.SamTagFilter;
import accusa2.util.AnnotatedCoordinate;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMValidationError;
//import net.sf.samtools.SAMValidationError;

/**
 * @author Michael Piechotta
 */

@Deprecated
public class DirectWindowRecordCache {

	// window coordinates
	private String contig;
	private int windowStart;
	private int windowSize;

	// SAMrecords in current window
	private SAMFileReader reader;
	private SAMRecordIterator iterator;
	private SAMRecord record;
	
	// current window position
	private int maxGenomicPosition;

	private Parameters parameters;

	private int filtered;

	public DirectWindowRecordCache(AnnotatedCoordinate coordinate, String pathname, Parameters parameters) {
		// window parameters
		this.contig 	= coordinate.getSequenceName();
		this.windowStart= coordinate.getStart();
		this.windowSize = parameters.getWindowSize();
		
		this.parameters	= parameters;

		// SAMrecord provider 
		reader 			= new SAMFileReader(new File(pathname));
		// be silent
		reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
		
		// define max genomic position
		maxGenomicPosition 	= Math.min(coordinate.getEnd(), reader.getFileHeader().getSequence(contig).getSequenceLength());

		filtered 		= 0;
		
		// init
		adjustWindow(windowStart);
	}

	public boolean hasNext() {
		while(record == null && iterator.hasNext()) {
			record = iterator.next();

			if(isValid(record)) {
				return true;
			} else {
				filtered++;
			}
		}

		return false;
	}

	public SAMRecord next() {
		if(!hasNext()) {
			return null;
		}

		// swap, delete and remove
		SAMRecord ret = record;
		record = null;
		return ret;
	}

	private boolean isValid(SAMRecord samRecord) {
		byte mapq = (byte)samRecord.getMappingQuality();
		List<SAMValidationError> errors = samRecord.isValid();
		if(!samRecord.getReadUnmappedFlag()
				&& !samRecord.getNotPrimaryAlignmentFlag()
				&& (mapq < 0 || mapq >= parameters.getMinMAPQ())
				&& (parameters.getFilterFlags() == 0 || (parameters.getFilterFlags() > 0 && ((samRecord.getFlags() & parameters.getFilterFlags()) == 0)))
				&& (parameters.getRetainFlags() == 0 || (parameters.getRetainFlags() > 0 && ((samRecord.getFlags() & parameters.getRetainFlags()) > 0)))
				&& errors == null // isValid is expensive
				) { // only store valid records that contain mapped reads
			for(SamTagFilter samTagFilter : parameters.getSamTagFilter()) {
				if(samTagFilter.filter(samRecord)) {
					return false;
				}
			}

			return true;
		}
		/*
		if(errors != null) {
			for(SAMValidationError error : errors) {
				 System.err.println(error.toString());
			}
		}
		*/

		return false;
	}

	private boolean findNext(int targetPosition) {
		iterator.close();
		iterator = reader.query(contig, targetPosition, maxGenomicPosition, true);
		while(iterator.hasNext() ) {
			record = iterator.next();

			if(isValid(record)) {
				this.windowStart = record.getAlignmentStart();
				return true;
			}
		}

		// if no more reads are found 
		return false;
	}

	public int adjustCurrentGenomicPosition(int targetPosition) {
		if(targetPosition > 0 && targetPosition < maxGenomicPosition) {
			if(isContained(targetPosition)) {
				return 1;
			} else {
				return adjustWindow(targetPosition) ? 2 : 0;
			}
		} else {
			return 0;
		}
	}

	private boolean adjustWindow(int targetPosition) {
		this.windowStart = targetPosition;

		iterator.close();
		iterator = reader.query(contig, targetPosition, maxGenomicPosition, true);
		record = null;

		if(hasNext()) {
			return true;
		} else {
			return findNext(targetPosition + windowSize);
		}
	}

	public void close() throws IOException {
		if(reader != null) {
			reader.close();
			reader = null;
		}
		if(iterator != null) {
			iterator.close();
			iterator = null;
		}
	}

	public int convertGenomicPosition2WindowPosition(int genomicPosition) {
		if(genomicPosition < windowStart || genomicPosition > windowStart + windowSize) {
			return -1;
		}

		return genomicPosition - windowStart;
	}

	public Parameters getParameters() {
		return parameters;
	}

	public boolean isContained(int genomicPosition) {
		return genomicPosition < maxGenomicPosition && genomicPosition >= windowStart && genomicPosition < getWindowEnd() - 1;
	}

	public int getWindowStart() {
		return windowStart;
	}

	public int getWindowEnd() {
		return windowStart + windowSize;
	}

	public String getContig() {
		return contig;
	}

	public int getMaxGenomicPosition() {
		return maxGenomicPosition;
	}

	public int getFiltered() {
		return filtered;
	}

}
