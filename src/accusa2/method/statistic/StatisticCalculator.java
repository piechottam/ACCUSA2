package accusa2.method.statistic;

import accusa2.pileup.Pileup;

public interface StatisticCalculator {

	public double getStatistic(Pileup pileup1, Pileup pileup2);
	
	public StatisticCalculator newInstance();
	
	public String getName();
	public String getDescription();

}
