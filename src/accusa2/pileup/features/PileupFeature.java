package accusa2.pileup.features;

import accusa2.pileup.DecodedSamRecord;

public interface PileupFeature<T> extends Cloneable {

	void set(final T[] pileupFeatures);
	void set(final int read, final T pileupFeature);
	T[] get();

	String getName();
	char getC();
	int getN();

	T[] build(final int n);
	void clear(final int n);
	void resize(final int n);

	void process(final int read, final DecodedSamRecord decodedSAMRecord);
	void mask(int[] mask);

}
