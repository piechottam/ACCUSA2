package accusa2.filter.factory;

import accusa2.filter.process.DistanceFeaturePileupFilter;
import accusa2.pileup.features.ReadEndDistanceFeature;

// TODO CHECK
@Deprecated
public class ReadEndDistancePileupFeatureFilterFactory extends AbstractReadDistancePileupFeatureFilterFactory {

	private static int DISTANCE = 6;

	public ReadEndDistancePileupFeatureFilterFactory() {
		super('E', "Filter distance to end of read. Default: " + DISTANCE, DISTANCE);
	}

	@Override
	public DistanceFeaturePileupFilter getPileupFilterInstance() {
		return new DistanceFeaturePileupFilter(getC(), getDistance());
	}

	@Override
	public ReadEndDistanceFeature createEmptyFeature(int n) {
		return new ReadEndDistanceFeature(n, getC());
	}

}
