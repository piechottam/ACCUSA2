package accusa2.filter.factory;

import accusa2.filter.process.AbstractFeaturePileupFilter;
import accusa2.filter.process.INDEL_DistanceProcessPileupFilter;
import accusa2.pileup.features.INDEL_DistanceFeature;


@Deprecated
public class INDEL_DistancePileupFeatureFilterFactory extends AbstractPileupFeatureFilterFactory<Integer> {

	private static int DISTANCE = 5;
	private int distance = DISTANCE;

	public INDEL_DistancePileupFeatureFilterFactory() {
		super('I', "Filter by distance to INDEL. Default: " + DISTANCE);
	}

	@Override
	public AbstractFeaturePileupFilter getPileupFilterInstance() {
		return new INDEL_DistanceProcessPileupFilter(getC(), distance);
	}

	public int getDistance() {
		return distance;
		
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		getParameters().setProcessINDELs(true);

		if(line.length() == 1) {
			return;
		}

		String[] s = line.split(Character.toString(AbstractPileupFilterFactory.SEP));
		int distance = Integer.valueOf(s[1]);
		if(distance < 0) {
			throw new IllegalArgumentException("Invalid distance+ " + line);
		}
		setDistance(distance);
	}

	@Override
	public INDEL_DistanceFeature createEmptyFeature(int n) {
		return new INDEL_DistanceFeature(n, getC());
	}

}
