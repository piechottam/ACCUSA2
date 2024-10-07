package accusa2.method.statistic;

import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public final class CombinedStatistic implements StatisticCalculator {

	protected final StatisticCalculator ho_he;
	protected final StatisticCalculator he_he;

	public CombinedStatistic() {
		ho_he = new DefaultStatistic();
		he_he = new PooledStatistic();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new CombinedStatistic();
	}

	public double getStatistic(final Pileup pileup1, final Pileup pileup2) {
		if(PileupUtils.isMultiAllelic(pileup1) && PileupUtils.isMultiAllelic(pileup2) || 
				pileup1.getBaseSortedSet().size() != 1 && pileup2.getBaseSortedSet().size() != 1) {
			return he_he.getStatistic(pileup1, pileup2);
		} else {
			return ho_he.getStatistic(pileup1, pileup2);
		}
	}

	@Override
	public String getDescription() {
		return "default(ho:he) + pooled(he:he)";
	}

	@Override
	public String getName() {
		return "combined";
	}

}