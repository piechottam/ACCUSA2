package accusa2.pileup.features;

public abstract class AbstractFeature<T> implements PileupFeature<T> {

	private final char c;
	private final String name;
	private T[] pileupFeatures;

	public AbstractFeature(final int n, final char c, final String name) {
		this.c 		= c;
		this.name 	= name;
		pileupFeatures 	= build(n);
	}

	public AbstractFeature(char c, final String name) {
		this(0, c, name);
	}

	@Override
	public final T[] get() {
		return pileupFeatures;
	}

	@Override 
	public final void set(int i, final T feature) {
		pileupFeatures[i] = feature;
	}

	@Override
	public final void set(final T[] features) {
		this.pileupFeatures = features;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final char getC() {
		return c;
	}

	@Override
	public final int getN() {
		return pileupFeatures.length;
	}

	@Override
	public final void resize(int n) {
		T[] features = build(n);
		System.arraycopy(this.pileupFeatures, 0, features, 0, this.pileupFeatures.length);
		this.pileupFeatures = features;
	}

	@Override
	public final void mask(final int[] mask) {
		final T[] pileupFeatures = build(getN() - mask.length);

		int srcPos 		= 0;
		int destPos 	= 0;
		int length 		= 0;
		int masked 		= 0;
		int maskIndex 	= 0;
	
		while(srcPos < getN()) {
			if(maskIndex < mask.length) {
				masked = mask[maskIndex];
				length = masked - srcPos;
				maskIndex++;
			} else {
				length = getN() - srcPos;
			}
	
			if(length > 0) {
				System.arraycopy(this.pileupFeatures, srcPos, pileupFeatures, destPos, length);
			}
	
			srcPos += length + 1;
			destPos += length;
		}

		this.pileupFeatures = pileupFeatures;
	}
	
	@Override
	public final void clear(int n) {
		pileupFeatures = build(n);
	}

	public abstract AbstractFeature<T> newInstance();
	
}
