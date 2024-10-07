package accusa2.pileup.iterator;

import net.sf.samtools.SAMFileReader;

import accusa2.cli.Parameters;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.DefaultPileup.STRAND;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;
import accusa2.pileup.builder.AbstractPileupBuilder;
import accusa2.util.AnnotatedCoordinate;

/**
 * @author Michael Piechotta
 */
public class VariantParallelPileupIterator implements ParallelPileupIterator {

	protected final AnnotatedCoordinate coordinate;
	
	protected final AbstractPileupBuilder pileupBuilder1;
	protected final AbstractPileupBuilder pileupBuilder2;

	protected DefaultPileup pileup1;
	protected DefaultPileup pileup2;

	public VariantParallelPileupIterator(final AnnotatedCoordinate coordinate, final SAMFileReader reader1, final SAMFileReader reader2, final Parameters parameters) {
		this.coordinate = coordinate;

		this.pileupBuilder1 = parameters.getPileupBuilderFactory1().newInstance(coordinate, reader1, parameters);
		this.pileupBuilder2 = parameters.getPileupBuilderFactory2().newInstance(coordinate, reader2, parameters);
	}

	public boolean hasNext() {
		if(pileup1 != null && pileup2 != null) {
			return true;
		}

		// quit if there are no pileups to build
		if(!pileupBuilder1.hasNext() || !pileupBuilder2.hasNext()) {
			return false;
		}
		// init
		if(pileup1 == null) {
			pileup1 = pileupBuilder1.next();
		}
		// init
		if(pileup2 == null) {
			pileup2 = pileupBuilder2.next();
		}

		return findNext();
	}

	protected boolean isMultiAllelic(Pileup pileup1, Pileup pileup2)  {
		return PileupUtils.isMultiAllelic(pileup1, pileup2);
	}

	protected boolean findNext() {
		while (pileup1 != null && pileup2 != null) {
			final int compare = new Integer(pileup1.getPosition()).compareTo(pileup2.getPosition());

			switch (compare) {

			case -1:
				// adjust actualPosition; instead of iterating jump to specific
				// position
				pileupBuilder1.adjustCurrentGenomicPosition(pileup2.getPosition());
				if(pileupBuilder1.hasNext()) {
					pileup1 = pileupBuilder1.next();
				} else {
					pileup1 = null;
				}
				break;

			case 0:

				final STRAND strand1 = pileup1.getStrand();
				final STRAND strand2 = pileup2.getStrand();

				final boolean isMultiAllelic = isMultiAllelic(pileup1, pileup2);

				if(isMultiAllelic && strand1 == strand2) {
					return true;
				} else if((pileup1.getStrand() == STRAND.UNKNOWN || pileup2.getStrand() == STRAND.UNKNOWN) && isMultiAllelic) {
					return true;
				} else if(pileup1.getStrand() == STRAND.REVERSE) {
					if(pileupBuilder2.hasNext()) {
						pileup2 = pileupBuilder2.next();
					} else {
						pileup2 = null;
					}
				} else if(pileup2.getStrand() == STRAND.REVERSE) {
					if(pileupBuilder1.hasNext()) {
						pileup1 = pileupBuilder1.next();
					} else {
						pileup1 = null;
					}					
				} else {
					if(pileupBuilder1.hasNext()) {
						pileup1 = pileupBuilder1.next();
					} else {
						pileup1 = null;
					}
					if(pileupBuilder2.hasNext()) {
						pileup2 = pileupBuilder2.next();
					} else {
						pileup2 = null;
					}
				}
				break;

			case 1:
				// adjust actualPosition; instead of iterating jump to specific
				// position
				pileupBuilder2.adjustCurrentGenomicPosition(pileup1.getPosition());
				if(pileupBuilder2.hasNext()) {
					pileup2 = pileupBuilder2.next();
				} else {
					pileup2 = null;
				}
				break;
			}
		}

		return false;
	}

	public ParallelPileup next() {
		if (!hasNext()) {
			return null;
		}

		final ParallelPileup pileups = new ParallelPileup(1,1);
		pileups.setPileup1(0, new DefaultPileup(pileup1));
		pileups.setPileup2(0, new DefaultPileup(pileup2));

		// this is necessary!!!
		if(pileup1.getStrand() == STRAND.UNKNOWN && pileup2.getStrand() == STRAND.FORWARD) {
			pileup2 = null;
		} else if(pileup2.getStrand() == STRAND.UNKNOWN && pileup1.getStrand() == STRAND.FORWARD) {
			pileup1 = null;
		} else {
			pileup1 = null;
			pileup2 = null;
		}

		return pileups;
	}

	public final AnnotatedCoordinate getAnnotatedCoordinate() {
		return coordinate;
	}
	
	@Override
	public final void remove() {
		// not needed
	}

}
