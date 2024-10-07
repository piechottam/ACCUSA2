package accusa2.filter.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cern.colt.list.IntArrayList;

import accusa2.pileup.Pileup;

// TODO check
@Deprecated
public class PCRDuplicateProcessPileupFilter extends AbstractFeaturePileupFilter {

	public PCRDuplicateProcessPileupFilter(char c) {
		super(c);
	}

	// TODO make more efficient
	@Override
	public int[] calculateMask(char variant, Pileup pileup) {
		Integer[] alignmentStarts = (Integer[])pileup.getFeatureContainer().getFeature(getC()).get();

		int count = 1;
		int start = -1;

		Map<Integer, List<Integer>> start2index = new HashMap<Integer, List<Integer>>(alignmentStarts.length);
		for(int i = 0; i < pileup.getCoverage(); ++i) {
			if(pileup.getBases()[i] != variant) {
				continue;
			}

			Integer alignmentStart = alignmentStarts[i];
			List<Integer> index = null;
			if(!start2index.containsKey(alignmentStart)) {
				index = new ArrayList<Integer>(4);
				start2index.put(alignmentStart, index);
			}
			index = start2index.get(alignmentStart);
			index.add(i);
			if(index.size() > count) {
				count = index.size();
				start = alignmentStart;
			}
		}

		IntArrayList mask = new IntArrayList(pileup.getCoverage());
		if(start > 0) {
			mask.addAllOf(start2index.get(start));
		}
		mask.trimToSize();

		return mask.elements();
	}

}
