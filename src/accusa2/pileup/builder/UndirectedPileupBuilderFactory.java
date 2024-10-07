package accusa2.pileup.builder;

import java.util.Set;

import net.sf.samtools.SAMFileReader;
import accusa2.cli.Parameters;
import accusa2.pileup.DefaultPileup;
import accusa2.util.AnnotatedCoordinate;

public class UndirectedPileupBuilderFactory implements PileupBuilderFactory {

	private final Set<Character> bases;

	public UndirectedPileupBuilderFactory(final Set<Character> bases) {
		this.bases = bases;
	}

	@Override
	public AbstractPileupBuilder newInstance(final AnnotatedCoordinate coordinate, final SAMFileReader reader, final Parameters parameters) {
		AbstractPileupBuilder pileupBuilder;

		if(bases.size() < DefaultPileup.BASES2.length) {
			pileupBuilder = new RestrictedUndirectedPileupBuilder(coordinate, reader, parameters);
		} else {
			pileupBuilder = new UndirectedPileupBuilder(coordinate, reader, parameters);
		}

		// init
		pileupBuilder.initCache();
		pileupBuilder.processWindowRecordCache();

		return pileupBuilder;
	}

	public boolean isDirected() {
		return false;
	}

}
