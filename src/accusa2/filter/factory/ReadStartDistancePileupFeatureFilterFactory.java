package accusa2.filter.factory;

import accusa2.filter.process.DistanceFeaturePileupFilter;
import accusa2.pileup.features.ReadStartDistanceFeature;

// TODO check
@Deprecated
public class ReadStartDistancePileupFeatureFilterFactory extends AbstractReadDistancePileupFeatureFilterFactory {

	private static int DISTANCE = 6;

	public ReadStartDistancePileupFeatureFilterFactory() {
		super('S', "Filter distance to start of read. Default: " + DISTANCE, DISTANCE);
	}

	@Override
	public DistanceFeaturePileupFilter getPileupFilterInstance() {
		return new DistanceFeaturePileupFilter(getC(), getDistance());
	}

	@Override
	public ReadStartDistanceFeature createEmptyFeature(int n) {
		return new ReadStartDistanceFeature(n, getC());
	}

}
