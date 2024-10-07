package accusa2.pileup.builder;

import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMRecord;
import accusa2.cache.Coordinate;
import accusa2.pileup.DefaultPileup;

public abstract class AbstractPileupBuilder {

	protected int genomicPosition;
	protected List<Coordinate> indels;
	protected List<Coordinate> skipped;

	public abstract boolean hasNext();
	public abstract DefaultPileup next();

	public void adjustCurrentGenomicPosition(final int genomicPosition) {
		this.genomicPosition = genomicPosition;
	}

	public abstract void initCache();
	public abstract void processWindowRecordCache();

	/**
	 * 
	 * @param record
	 * @throws Exception
	 */
	protected void processRecord(final SAMRecord record) throws Exception {
		int readPosition 	= 0;
		int genomicPosition = record.getAlignmentStart();
		indels				= new ArrayList<Coordinate>(3);
		skipped				= new ArrayList<Coordinate>(3);

		for(final CigarElement cigarElement : record.getCigar().getCigarElements()) {

			switch(cigarElement.getOperator()) {

			/*
			 * handle insertion
			 */
			case I:
				processInsertion(readPosition, genomicPosition, cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * handle alignment/sequence match and mismatch
			 */
			case M:
			case EQ:
			case X:
				processAlignmetMatch(readPosition, genomicPosition, cigarElement, record);
				readPosition += cigarElement.getLength();
				genomicPosition += cigarElement.getLength();
				break;

			/*
			 * handle hard clipping 
			 */
			case H:
				processHardClipping(readPosition, genomicPosition, cigarElement, record);
				// FIXME
				break;

			/*
			 * handle deletion from the reference and introns
			 */
			case D:
				processDeletion(readPosition, genomicPosition, cigarElement, record);
				genomicPosition += cigarElement.getLength();
				break;

			case N:
				processSkipped(readPosition, genomicPosition, cigarElement, record);
				genomicPosition += cigarElement.getLength();
				break;

			/*
			 * soft clipping
			 */
			case S:
				processSoftClipping(readPosition, genomicPosition, cigarElement, record);
				readPosition += cigarElement.getLength();
				break;

			/*
			 * silent deletion from padded sequence
			 */
			case P:
				processPadding(readPosition, genomicPosition, cigarElement, record);
				// FIXME
				break;

			default:
				throw new RuntimeException("Unsupported Cigar Operator: " + cigarElement.getOperator().toString());
			}
		}
	}
	
	abstract protected void processInsertion(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);
	abstract protected void processAlignmetMatch(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);	
	abstract protected void processHardClipping(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);
	abstract protected void processDeletion(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);
	abstract protected void processSkipped(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);
	abstract protected void processSoftClipping(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);
	abstract protected void processPadding(int readPosition, int genomicPosition, final CigarElement cigarElement, final SAMRecord record);

}
