package accusa2.method.statistic;

import accusa2.process.pileup2Matrix.BASQ_Shrinked;

public class ReducedStatistic extends DefaultStatistic {

	public ReducedStatistic() {
		pileup2Matrix = new BASQ_Shrinked();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new ReducedStatistic();
	}

	public String getDescription() {
		return "";
	}
	
	public String getName() {
		return "reduced";
	}

}