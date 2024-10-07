package accusa2.filter.process;

import cern.colt.list.IntArrayList;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;

// TODO check
@Deprecated
public class ReadGroupProcessPileupFilter extends AbstractFeaturePileupFilter {

	public ReadGroupProcessPileupFilter(char c) {
		super(c);
	}

	@Override
	public int[] calculateMask(char variant, Pileup pileup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean filter(ParallelPileup parallelPileup) {
		this.parallelPileup = new ParallelPileup(1, 1);
		Pileup pileup1 = parallelPileup.getPileups1()[0];
		Pileup pileup2 = parallelPileup.getPileups2()[0];

		if(getReadGroups(pileup1).length == 0 && getReadGroups(pileup2).length == 0) {
			return false;
		}

		/*
		parallelPileup.setPileup1(0, getPileup(variant, pileup1));
		parallelPileup.setPileup2(0, getPileup(variant, pileup2));
		*/

		return true;
	}

	/*
	private Pileup getPileup(char variant, Pileup pileup) {
		int readGroup = getReadGroup(variant, pileup);
		if(readGroup < 0) {
			return new DefaultPileup(pileup);
		}

		int[] mask = calculatMask(variant, readGroup, pileup);
		return process(mask, pileup);
	}
	*/

	/*
	private int getReadGroup(char variant, Pileup pileup) {
		char[] bases = pileup.getBases();
		Integer[] readGroups = getReadGroups(pileup);
		Map<Integer, Integer> readGroup2count = new HashMap<Integer, Integer>();

		for(int i = 0; i < pileup.getCoverage(); ++i){
			if(bases[i] == variant) {
				Integer count = 1;
				int readGroup = readGroups[i];
				if(!readGroup2count.containsKey(readGroup)) {
					
					readGroup2count.put(readGroup, count);
				}
				count = readGroup2count.get(readGroup);
				count += 1;
			}
		}

		int r = -1;
		int count = 0;
		for(Integer readGroup : readGroup2count.keySet()) {
			if(readGroup2count.get(readGroup) > count) {
				r = readGroup;
			}
		}

		return r;
	}
	*/

	private Integer[] getReadGroups(Pileup pileup) {
		return (Integer[])pileup.getFeatureContainer().getFeature(getC()).get();
	}

	public int[] calculatMask(char variant, int readGroup, Pileup pileup) {
		Integer[] readGroups = getReadGroups(pileup);
		IntArrayList mask = new IntArrayList(pileup.getCoverage());
		for(int i = 0; i < pileup.getCoverage(); ++i) {
			if(readGroups[i] == readGroup && pileup.getBases()[i] == variant) {
				mask.add(i);
			}
		}
		mask.trimToSize();
		return mask.elements();
	}

}
