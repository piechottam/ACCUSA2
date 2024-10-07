package accusa2.pileup.features;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import accusa2.pileup.DecodedSamRecord;

public class PileupFeatureContainer implements Cloneable {

	private final Map<Character, Integer> c2i;

	private List<AbstractFeature<?>> features;
	public PileupFeatureContainer() {
		int n 		= 6;
		features 	= new ArrayList<AbstractFeature<?>>(n);
		c2i 		= new HashMap<Character, Integer>(n);
	}

	public void addFeature(final AbstractFeature<?> feature) throws Exception {
		if(feature == null) {
			return;
		}
		
		char c = feature.getC();
		if(c2i.containsKey(c)) {
			throw new Exception("Duplicate value: " + feature.getName());
		} else {
			c2i.put(c, features.size());
			features.add(feature);
		}
	}

	public AbstractFeature<?> getFeature(char c) {
		if(!c2i.containsKey(c)) {
			return null; 
		}

		return features.get(c2i.get(c));
	}

	public void process(int read, DecodedSamRecord decodedSAMRecord) {
		for(final AbstractFeature<?> feature : features) {
			feature.process(read, decodedSAMRecord);
		}
	}

	public void mask(int[] mask) {
		for(final AbstractFeature<?> feature : features) {
			feature.mask(mask);
		}
	}

	@Override
	public PileupFeatureContainer clone() {
		final PileupFeatureContainer features = new PileupFeatureContainer();

		for(final AbstractFeature<?> feature : this.features) {
			try {
				features.addFeature(feature.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return features;
	}

}
