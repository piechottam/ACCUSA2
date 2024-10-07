package accusa2.filter.process;

import accusa2.pileup.Pileup;

public class DistanceFeaturePileupFilter extends AbstractDistanceFeaturePileupFilter {

	public DistanceFeaturePileupFilter(char c, int distance) {
		super(c, distance);
	}

	@Override
	protected Integer[] getDistanceFromPileup(Pileup pileup) {
		return (Integer[])pileup.getFeatureContainer().getFeature(getC()).get();
	}

}
