package accusa2.filter.factory;

import accusa2.filter.process.AbstractFeaturePileupFilter;
import accusa2.pileup.features.AbstractFeature;

public abstract class AbstractPileupFeatureFilterFactory<T> extends AbstractPileupFilterFactory<T> {

	public AbstractPileupFeatureFilterFactory(char c, String desc) {
		super(c, desc);
	}

	public abstract AbstractFeature<T> createEmptyFeature(int n);
	public abstract AbstractFeaturePileupFilter getPileupFilterInstance();

}
