package accusa2.util;

import java.util.Arrays;

public final class StatisticContainer {

	private int factor; 
	private int size;

	private int[] t2count;
	private int[] count;

	private boolean processed;
	
	public StatisticContainer() {
		this(100, 40000);
	}

	public StatisticContainer(int factor, int size) {
		this.factor = factor;
		this.size = size;

		t2count = new int[size];
		Arrays.fill(t2count, 0);
		
		count = new int[size];
		Arrays.fill(count, 0);

		processed = false;
	}

	/**
	 * 
	 * @param value
	 */
	public void addValue(double value) {
		int t = transform(value);
		t2count[t]++;
		processed = false;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public int getCount(double value) {
		int t = transform(value);
		return t2count[t];
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public int getCumulativeCount(double value) {
		if(!processed) {
			process();
		}
		int t = transform(value);
		return count[t];
	}

	/**
	 * 
	 */
	private void process() {
		int sum = 0;
		for(int i = size - 1; i >= 0 ; --i) {
			sum += t2count[i];
			count[i] = sum;
		}
		processed = true;
	}

	/**
	 * 
	 * @return
	 */
	public int getFactor() {
		return factor;
	}

	/**
	 * 
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private int transform(double value) {
		if(value <= 0) {
			return 0;
		}

		int t = (int)(value * (double)factor);
		if(t >= size) {
			//System.err.println("value > " + (double)size / (double)factor + " : " + value);
			return size - 1;
		}

		return t;
	}

	/**
	 * 
	 * @param h
	 */
	public synchronized void addStatisticContainer(StatisticContainer h) {
		for(int i = 0; i < h.size; ++i) {
			t2count[i] += h.t2count[i];
		}
		processed = false;
	}

}
