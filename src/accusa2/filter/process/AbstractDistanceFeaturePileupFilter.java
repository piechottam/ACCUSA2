package accusa2.filter.process;

import cern.colt.list.IntArrayList;
import accusa2.pileup.Pileup;

public abstract class AbstractDistanceFeaturePileupFilter extends AbstractFeaturePileupFilter {

	private int filterDistance;

	public AbstractDistanceFeaturePileupFilter(char c, int distance) {
		super(c);
		this.filterDistance = distance;
	}

	@Override
	public int[] calculateMask(char variant, Pileup pileup) {
		Integer[] distances = getDistanceFromPileup(pileup);
		if(distances.length == 0) {
			return new int[0];
		}

		IntArrayList mask = new IntArrayList(pileup.getCoverage());
		for(int i = 0; i < distances.length; ++i) {
			if(distances[i] == null || distances[i] > filterDistance) {
				continue;
			}

			if(pileup.getBases()[i] == variant) {
				mask.add(i);
			}
		}
		mask.trimToSize();
		return mask.elements();
	}

	protected abstract Integer[] getDistanceFromPileup(Pileup pileup);

	public final int getFilterDistance() {
		return filterDistance;
	}

	public final void setFilterDistance(int filterDistance) {
		this.filterDistance = filterDistance;
	}

}
