package accusa2.pileup.iterator;


import net.sf.samtools.SAMFileReader;
import accusa2.cli.Parameters;
import accusa2.pileup.Pileup;
import accusa2.util.AnnotatedCoordinate;

public class DefaultParallelPileupIterator extends VariantParallelPileupIterator {

	public DefaultParallelPileupIterator(AnnotatedCoordinate coordinate, SAMFileReader reader1, SAMFileReader reader2, Parameters parameters) {
		super(coordinate, reader1, reader2, parameters);
	}

	protected boolean isMultiAllelic(Pileup pileup1, Pileup pileup2)  {
		return true;
	}

}
