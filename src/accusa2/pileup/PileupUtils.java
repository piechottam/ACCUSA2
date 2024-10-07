package accusa2.pileup;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import accusa2.pileup.features.PileupFeatureContainer;

import cern.colt.list.CharArrayList;

public abstract class PileupUtils {

	/**
	 * 
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public boolean isMultiAllelic(final Pileup pileup1, final Pileup pileup2) {
		final Set<Character> c = new HashSet<Character>(DefaultPileup.LENGTH);
		for(final char b : pileup1.getBases()) {
			c.add(b);
		}
		for(final char b : pileup2.getBases()) {
			c.add(b);
		}
	
		return c.size() > 1;
	}

	/**
	 * 
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public Set<Character> getUniqueBases(final Pileup pileup1, final Pileup pileup2) {
		final Set<Character> c = new HashSet<Character>(DefaultPileup.LENGTH);
		for(final char b : pileup1.getBases()) {
			c.add(b);
		}
		for(final char b : pileup2.getBases()) {
			c.add(b);
		}
		return c;
	}

	/**
	 * 
	 * @param pileup
	 * @return
	 */
	static public boolean isMultiAllelic(final Pileup pileup) {
		if(pileup.getBases().length <= 1) {
			return false;
		}
	
		return PileupUtils.getUniqueBases(pileup).size() > 1;
	}

	/**
	 * 
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public int getAlleles(final Pileup pileup1, final Pileup pileup2) {
		final Set<Character> c = new HashSet<Character>(DefaultPileup.LENGTH);
		for(final char b : pileup1.getBases()) {
			c.add(b);
		}
		for(final char b : pileup2.getBases()) {
			c.add(b);
		}
	
		return c.size();
	}

	/**
	 * 
	 * @param pileup
	 * @return
	 */
	static public SortedSet<Character> getUniqueBases(final Pileup pileup) {
		SortedSet<Character> c = new TreeSet<Character>();
		for(char b : pileup.getBases()) {
			c.add(b);
		}
		return c;
	}

	
	static public int[] getBase2Count(final Pileup pileup1) {
		final int[] count = new int[DefaultPileup.LENGTH]; 
		for(final char b : pileup1.getBases()) {
			count[DefaultPileup.BASE2INT.get(b)]++;
		}
		return count;
	}

	/**
	 * 
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public int[] getBaseCount(final Pileup pileup1, final Pileup pileup2) {
		final int[] count = new int[DefaultPileup.LENGTH]; 
		for(final char b : pileup1.getBases()) {
			count[DefaultPileup.BASE2INT.get(b)]++;
		}
		for(final char b : pileup2.getBases()) {
			count[DefaultPileup.BASE2INT.get(b)]++;
		}
	
		return count; 
	}

	static public Character[] getVariantBases(Pileup pileup1, Pileup pileup2) {
		Set<Character> bases1 = getUniqueBases(pileup1);
		Set<Character> bases2 = getUniqueBases(pileup2);

		Set<Character> diff = new HashSet<Character>(bases1);
		diff.addAll(bases2);
		Set<Character> tmp = new HashSet<Character>(bases1);
		tmp.retainAll(bases2);
		diff.removeAll(tmp);
		if(diff.size() > 0) {
			return diff.toArray(new Character[diff.size()]);
		}

		return getSortedVariantBases(pileup1, pileup2);
	}
	
	/**
	 * 
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public Character[] getSortedVariantBases(Pileup pileup1, Pileup pileup2) {
		int[] count = new int[DefaultPileup.LENGTH]; 
		for(char b : pileup1.getBases()) {
			count[DefaultPileup.BASE2INT.get(b)]++;
		}
		for(char b : pileup2.getBases()) {
			count[DefaultPileup.BASE2INT.get(b)]++;
		}
		SortedMap<Integer, Character> map = new TreeMap<Integer,Character>();
		for(int i = 0; i < count.length; ++i) {
			if(count[i] > 0) {
				map.put(count[i], DefaultPileup.BASES[i]);
			}
		}
		return map.values().toArray(new Character[map.size()]);
	}

	// FIXME
	static public Pileup mask(int[] mask, Pileup input) {
		Pileup output = new DefaultPileup(input);
		
		char[] bases = new char[input.getCoverage() - mask.length];
		byte[] basqs = new byte[input.getCoverage() - mask.length];
		byte[] mapqs = new byte[input.getCoverage() - mask.length];
	
		int srcPos 		= 0;
		int destPos 	= 0;
		int length 		= 0;
		int masked 		= 0;
		int maskIndex 	= 0;
	
		while(srcPos < input.getCoverage()) {
			if(maskIndex < mask.length) {
				masked = mask[maskIndex];
				length = masked - srcPos;
				maskIndex++;
			} else {
				length = input.getCoverage() - srcPos;
			}
	
			if(length > 0) {
				System.arraycopy(input.getBases(), srcPos, bases, destPos, length);
				System.arraycopy(input.getBASQs(), srcPos, basqs, destPos, length);
				System.arraycopy(input.getMAPQs(), srcPos, mapqs, destPos, length);
			}
	
			srcPos += length + 1;
			destPos += length;
		}

		output.setBases(bases);
		output.setBASQs(basqs);
		output.setMAPQs(mapqs);
		
		PileupFeatureContainer maskedFeatureContainer = input.getFeatureContainer().clone();
		maskedFeatureContainer.mask(mask);
		output.setFeatureContainer(maskedFeatureContainer);
		return output;
	}

	/**
	 * Merge pileup1 and pileup2
	 * @param pileup1
	 * @param pileup2
	 * @return
	 */
	static public DefaultPileup mergePileups(Pileup pileup1, Pileup pileup2) {
		assert(pileup1.getContig().equals(pileup2.getContig()));
		assert(pileup1.getPosition() == pileup2.getPosition());
		assert(pileup1.getReferenceBase() == pileup2.getReferenceBase());

		DefaultPileup pileup3 = new DefaultPileup();

		int combined = pileup1.getCoverage() + pileup2.getCoverage();
		char[] bases = new char[combined];
		//System.arraycopy(src, srcPos, dest, destPos, length)
		System.arraycopy(pileup1.getBases(), 0, bases, 0, pileup1.getBases().length);
		System.arraycopy(pileup2.getBases(), 0, bases, pileup1.getBases().length, pileup2.getBases().length);
		
		byte[] basqs = new byte[combined];
		System.arraycopy(pileup1.getBASQs(), 0, basqs, 0, pileup1.getBASQs().length);
		System.arraycopy(pileup2.getBASQs(), 0, basqs, pileup1.getBASQs().length, pileup2.getBASQs().length);
		
		byte[] mapqs = new byte[combined];
		System.arraycopy(pileup1.getMAPQs(), 0, mapqs, 0, pileup1.getMAPQs().length);
		System.arraycopy(pileup2.getMAPQs(), 0, mapqs, pileup1.getMAPQs().length, pileup2.getMAPQs().length);
	
		String contig = pileup1.getContig();
		int pos = pileup1.getPosition();
		char ref = pileup1.getReferenceBase();

		pileup3.set(contig, pos, ref, bases, basqs, mapqs);
		return pileup3;
	}

	public static char[] getUniqueBasesArray(Pileup pileup1, Pileup pileup2) {
		SortedSet<Character> bases = new TreeSet<Character>();
		bases.addAll(pileup1.getBaseSortedSet());
		bases.addAll(pileup2.getBaseSortedSet());

		CharArrayList list = new CharArrayList();
		list.addAllOf(bases);
		list.trimToSize();

		return list.elements(); 
	}

	public static Set<Character> getUniqueBasesSet(Pileup pileup1, Pileup pileup2) {
		SortedSet<Character> bases = new TreeSet<Character>();
		bases.addAll(pileup1.getBaseSortedSet());
		bases.addAll(pileup2.getBaseSortedSet());

		return bases;
	}

}
