package accusa2.method.statistic;

public class NoiseStatistic extends DefaultStatistic {

	public NoiseStatistic() {
		super();
	}

	@Override
	public StatisticCalculator newInstance() {
		return new NoiseStatistic();
	}

	protected double[] estimateAlpha(double[] mean, int coverage) {
		double[] alpha = new double[mean.length];

		for(int i = 0; i < mean.length; ++i) {
			alpha[i] = coverage * (mean[i] + 0.01);
		}

		return alpha;
	}

	@Override
	public String getName() {
		return "noise";
	}

}