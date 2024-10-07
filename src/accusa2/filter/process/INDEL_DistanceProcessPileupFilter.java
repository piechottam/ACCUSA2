package accusa2.filter.process;


import accusa2.pileup.Pileup;

@Deprecated
public class INDEL_DistanceProcessPileupFilter extends AbstractDistanceFeaturePileupFilter {

	public INDEL_DistanceProcessPileupFilter(char c, int distance) {
		super(c, distance);
	}
	
	@Override
	protected Integer[] getDistanceFromPileup(Pileup pileup) {
		return (Integer[])pileup.getFeatureContainer().getFeature(getC()).get();
	}

}
