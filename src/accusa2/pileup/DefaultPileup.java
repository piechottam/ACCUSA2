package accusa2.pileup;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import accusa2.pileup.features.PileupFeatureContainer;

/**
 * Encapsulates a pileup column.
 * 
 * @author Sebastian Fröhler
 * @author Michael Piechotta
 * 
 * Michael Piechotta: refactored
 */
public final class DefaultPileup implements Pileup {

	public static final char STRAND_FORWARD_CHAR = '+';
	public static final char STRAND_REVERSE_CHAR = '-';
	public static final char STRAND_UNKNOWN_CHAR = '.';
	
	public static final char[] BASES = {'A' ,'C', 'G', 'T', 'N'};
	public static final int[] COMPLEMENT = {3, 2, 1, 0, 4};

	public static final char[] BASES2 = {'A' ,'C', 'G', 'T'};
	public static final Character[] BASES3 = {'A' ,'C', 'G', 'T'};

	public static final Map<Character, Integer> BASE2INT = new HashMap<Character, Integer>(BASES.length);
	// init base to integer
	static {
		for(int i = 0; i < BASES.length; ++i) {
			BASE2INT.put(BASES[i], i);
			BASE2INT.put(Character.toLowerCase(BASES[i]), i);
		}
	}
	public static final int LENGTH = BASES.length - 1; // ignore N

	public static final Map<Character, Character> BASES2_COMPLEMENT = new HashMap<Character, Character>();
	static {
		for(char c : BASES2) {
			BASES2_COMPLEMENT.put(c, BASES2[COMPLEMENT[BASE2INT.get(c)]]);
		}
	}

	private PileupFeatureContainer featureContainer;

	// container
	private String contig;
	private int position;
	private STRAND strand;
	private char refBase;
	private char[] bases;
	private byte[] basqs;
	private byte[] mapqs;

	private SortedSet<Character> baseSortedSet;

	public DefaultPileup() {
		featureContainer= new PileupFeatureContainer();

		contig 		= new String();
		position 	= -1;
		strand		= STRAND.UNKNOWN;
		bases 		= new char[0];
		basqs 		= new byte[0];
		mapqs 		= new byte[0];

		baseSortedSet		= new TreeSet<Character>();
	}

	public DefaultPileup(final Pileup pileup) {
		featureContainer= pileup.getFeatureContainer().clone();

		contig 		= new String(pileup.getContig());
		position 	= pileup.getPosition();
		strand		= pileup.getStrand();
		refBase 	= pileup.getReferenceBase();
		bases 		= pileup.getBases().clone();
		basqs 		= pileup.getBASQs().clone();
		mapqs 		= pileup.getMAPQs().clone();

		baseSortedSet = new TreeSet<Character>(pileup.getBaseSortedSet());
	}

	public PileupFeatureContainer getFeatureContainer() {
		return featureContainer;
	}

	public void setFeatureContainer(final PileupFeatureContainer featureContainer) {
		this.featureContainer = featureContainer;
	}
	
	public String getContig() {
		return contig;
	}

	public int getPosition() {
		return position;
	}

	public STRAND getStrand() {
		return strand;
	}
	
	public char getReferenceBase() {
		return refBase;
	}

	public SortedSet<Character> getBaseSortedSet() {
		return baseSortedSet;
	}
	
	public char[] getBases() {
		return bases;
	}

	public byte[] getBASQs() {
		return basqs;
	}

	public byte[] getMAPQs() {
		return mapqs;
	}

	public int getCoverage() {
		return bases.length;
	}
	
	public void set(final String contig, final int position, final char refBase, final char[] bases, final byte[] basqs, final byte[] mapqs) {
		this.contig 	= contig;
		this.position 	= position;
		this.refBase 	= refBase;

		this.bases 		= bases;
		baseSortedSet = PileupUtils.getUniqueBases(this);
		this.basqs 		= basqs;
		this.mapqs 		= mapqs;
	}

	public void set(final String contig, final int position, final char refBase, final char[] bases, final byte[] basqs) {
		this.contig 	= contig;
		this.position 	= position;
		this.refBase 	= refBase;

		this.bases 		= bases;
		baseSortedSet 	= PileupUtils.getUniqueBases(this);
		this.basqs 		= basqs;
	}

	public void setContig(final String contig) {
		this.contig = contig;
	}

	public void setReferenceBase(final char referenceBase) {
		this.refBase = referenceBase;
	}

	@Override
	public void setPosition(final int position) {
		this.position = position;
	}

	@Override
	public void setStrand(final STRAND strand) {
		this.strand = strand;
	}
	
	@Override
	public void setBases(final char[] bases) {
		this.bases = bases;
		baseSortedSet = PileupUtils.getUniqueBases(this);
	}

	@Override
	public void setBASQs(final byte[] basqs) {
		this.basqs = basqs;
	}

	@Override
	public void setMAPQs(final byte[] mapqs) {
		this.mapqs = mapqs;
	}

	public enum STRAND {
		FORWARD(STRAND_FORWARD_CHAR),REVERSE(STRAND_REVERSE_CHAR),UNKNOWN(STRAND_UNKNOWN_CHAR);
		final char c;

		private STRAND(char c) {
			this.c = c;
		}

		public char character() {
	        return c;
	    }

		public static STRAND getEnum(String s) {
			switch(s.charAt(0)) {

			case STRAND_FORWARD_CHAR:
				return STRAND.FORWARD;

			case STRAND_REVERSE_CHAR:
				return STRAND.REVERSE;				

			default:
				return STRAND.UNKNOWN;
			}
		}
		
	}

}
