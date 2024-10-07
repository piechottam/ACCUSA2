package accusa2.pileup.sample;

import accusa2.pileup.Pileup;

public interface SamplePileup {

	public abstract void setPileup(Pileup pileup);

	public abstract Pileup sample(int n);

}