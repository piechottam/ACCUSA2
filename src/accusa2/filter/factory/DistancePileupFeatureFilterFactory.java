package accusa2.filter.factory;

import accusa2.filter.process.DistanceFeaturePileupFilter;
import accusa2.pileup.features.DistanceFeature;

public class DistancePileupFeatureFilterFactory extends AbstractReadDistancePileupFeatureFilterFactory {

	private static int FILTER_DISTANCE = 6;

	public DistancePileupFeatureFilterFactory() {
		super('D', "Filter distance to start/end of read, intron and INDEL position. Default: " + FILTER_DISTANCE, FILTER_DISTANCE);
	}

	@Override
	public DistanceFeaturePileupFilter getPileupFilterInstance() {
		return new DistanceFeaturePileupFilter(getC(), getDistance());
	}

	@Override
	public DistanceFeature createEmptyFeature(int n) {
		return new DistanceFeature(n, getC());
	}

}
