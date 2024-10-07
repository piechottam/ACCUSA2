package accusa2.filter.factory;

import accusa2.filter.process.PCRDuplicateProcessPileupFilter;
import accusa2.pileup.features.AbstractFeature;
import accusa2.pileup.features.ReadAlignmentStartFeature;

@Deprecated
public class PCRDuplicatePileupFilterFactory extends AbstractPileupFeatureFilterFactory<Integer> {

	public PCRDuplicatePileupFilterFactory() {
		super('C', "Filter PCR duplicates");
	}

	@Override
	public PCRDuplicateProcessPileupFilter getPileupFilterInstance() {
		return new PCRDuplicateProcessPileupFilter(getC());
	}

	@Override
	public AbstractFeature<Integer> createEmptyFeature(int n) {
		return new ReadAlignmentStartFeature(n, getC());
	}

}
