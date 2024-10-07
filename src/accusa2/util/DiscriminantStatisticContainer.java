package accusa2.util;

import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;

public class DiscriminantStatisticContainer {

	private int factor			= 100;
	private int size			= 40000;
	
	/*
	 * V = FP
	 * R = TP + FP
	 */

	private StatisticContainer ho_he_V;
	private StatisticContainer ho_he_R;

	private StatisticContainer he_he_V;
	private StatisticContainer he_he_R;

	public DiscriminantStatisticContainer() {
		ho_he_V 	= new StatisticContainer(factor, size);
		ho_he_R		= new StatisticContainer(factor, size);

		he_he_V 	= new StatisticContainer(factor, size);
		he_he_R		= new StatisticContainer(factor, size);
	}

	public void addNULL_Value(double value, Pileup pileup1, Pileup pileup2) {
		if(PileupUtils.isMultiAllelic(pileup1) && PileupUtils.isMultiAllelic(pileup2) || 
				pileup1.getBaseSortedSet().size() != 1 && pileup2.getBaseSortedSet().size() != 1) {
			he_he_V.addValue(value); 
		} else {
			ho_he_V.addValue(value);
		}
	}

	public void addR_Value(double value, Pileup pileup1, Pileup pileup2) {
		if(PileupUtils.isMultiAllelic(pileup1) && PileupUtils.isMultiAllelic(pileup2) || 
				pileup1.getBaseSortedSet().size() != 1 && pileup2.getBaseSortedSet().size() != 1) {
			he_he_R.addValue(value); 
		} else {
			ho_he_R.addValue(value);
		}
	}

	public synchronized void addContainer(DiscriminantStatisticContainer c) {
		ho_he_V.addStatisticContainer(c.ho_he_V);
		ho_he_R.addStatisticContainer(c.ho_he_R);

		he_he_V.addStatisticContainer(c.he_he_V);
		he_he_R.addStatisticContainer(c.he_he_R);
	}

	public final int getFactor() {
		return factor;
	}

	public final int getSize() {
		return size;
	}

	public StatisticContainer getHe_he_R() {
		return he_he_R;
	}

	public StatisticContainer getHe_he_V() {
		return he_he_V;
	}

	public StatisticContainer getHo_he_R() {
		return ho_he_R;
	}

	public StatisticContainer getHo_he_V() {
		return ho_he_V;
	}

	public double getFDR(double value, Pileup pileup1, Pileup pileup2) {
		double V = 0.0;
		double R = 0.0;

		if(PileupUtils.isMultiAllelic(pileup1) && PileupUtils.isMultiAllelic(pileup2) || 
				pileup1.getBaseSortedSet().size() != 1 && pileup2.getBaseSortedSet().size() != 1) {
			V	= (double)he_he_V.getCumulativeCount(value);
			R 	= (double)he_he_R.getCumulativeCount(value);
		} else {
			V	= (double)ho_he_V.getCumulativeCount(value);
			R 	= (double)ho_he_R.getCumulativeCount(value);						
		}

		return V / R;
	}

}
