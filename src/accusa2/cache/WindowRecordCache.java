package accusa2.cache;

import java.util.ArrayList;
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
public class WindowRecordCache {

	// window coordinates
	private String contig;
	private int windowStart;
	private int windowSize;

	// SAMrecords in current window
	private List<SAMRecord> records;
	private SAMFileReader reader;
	
	// current window position
	private int maxGenomicPosition;

	private Parameters parameters;

	private int filtered;

	public WindowRecordCache(AnnotatedCoordinate coordinate, SAMFileReader reader, Parameters parameters) {
		// window parameters
		this.contig 	= coordinate.getSequenceName();
		this.windowStart= coordinate.getStart();
		this.windowSize = parameters.getWindowSize();
		
		this.parameters	= parameters;

		records 		= new ArrayList<SAMRecord>(windowSize * 50);

		// SAMrecord provider 
		this.reader		= reader;

		// define max genomic position
		maxGenomicPosition 	= Math.min(coordinate.getEnd(), reader.getFileHeader().getSequence(contig).getSequenceLength());

		filtered 		= 0;

		// init
		adjustWindow(windowStart);
	}

	public List<SAMRecord> getRecords() {
		if(isContained(windowStart) && records.size() == 0) {
			fillRecords();
		}

		return records;
	}

	/*
	 * Read Records from bam file and store them internally
	 */
	private void fillRecords() {
		if(windowStart < -1 || windowStart > maxGenomicPosition) {
			return;
		}
		// cleanup
		records.clear();

		// get iterator for reads mapping into specified window
		SAMRecordIterator iterator = reader.query(contig, windowStart, Math.min(windowStart + windowSize - 1, maxGenomicPosition), false);

		while(iterator.hasNext()) {
			SAMRecord record = iterator.next();

			if(isValid(record)) {
				records.add(record);
			} else {
				filtered++;
			}
		}

		iterator.close();
		iterator = null;
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

		if(errors != null) {
			for(SAMValidationError error : errors) {
				 System.err.println(error.toString());
			}
		}

		return false;
	}

	private int getNextPosition(int targetPosition) {
		SAMRecordIterator iterator = reader.query(contig, targetPosition, maxGenomicPosition, true);
		while(iterator.hasNext() ) {
			SAMRecord record = iterator.next();

			if(isValid(record)) {
				iterator.close();
				iterator = null;
				return record.getAlignmentStart();
			}
		}
		iterator.close();
		iterator = null;

		// if no more reads are found 
		return -1;
	}

	public int adjustCurrentGenomicPosition(int targetPosition) {
		if(targetPosition >= 0 && targetPosition < maxGenomicPosition) {
			if(isContained(targetPosition)) {
				return 1;
			} else {
				return adjustWindow(targetPosition) ? 2 : 0;
			}
		} else {
			return 0;
		}
	}

	private boolean adjustWindow(int genomicPosition) {
		this.windowStart = genomicPosition;
		fillRecords();

		if(records.size() == 0) {
			int nextPosition = getNextPosition(genomicPosition + windowSize);
			if(nextPosition > 0) {
				return adjustWindow(nextPosition);
			}
		} else {
			return true;
		}

		return false;
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
