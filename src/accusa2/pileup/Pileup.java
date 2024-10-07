package accusa2.pileup;

import java.util.SortedSet;

import accusa2.pileup.DefaultPileup.STRAND;
import accusa2.pileup.features.PileupFeatureContainer;

public interface Pileup {

	String getContig();
	int getPosition();
	STRAND getStrand();
	char getReferenceBase();

	char[] getBases();
	byte[] getBASQs();
	byte[] getMAPQs();

	int getCoverage();

	void setContig(String contig);
	void setPosition(int position);
	void setStrand(STRAND strand);
	void setReferenceBase(char refBase);

	void setBases(char[] bases);
	void setBASQs(byte[] basqs);
	void setMAPQs(byte[] mapqs);

	PileupFeatureContainer getFeatureContainer();
	void setFeatureContainer(PileupFeatureContainer featureContainer);

	public SortedSet<Character> getBaseSortedSet();
	
}
