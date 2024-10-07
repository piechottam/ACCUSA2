package accusa2.io.format;

import net.sf.samtools.SAMUtils;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;

public class DefaultResultFormat extends AbstractResultFormat {

	public static final char COMMENT 	= '#';
	public static final char EMPTY 	= '*';
	public static final char SEP 		= '\t';
	public static final char SEP2 	= ',';

	public DefaultResultFormat() {
		super('D', "ACCUSA2 default output");
	}
	
	@Override
	public String convert2String(Pileup pileup1, Pileup pileup2, double value) {
		StringBuilder sb = new StringBuilder();

		sb.append(pileup1.getContig());
		sb.append(SEP);
		sb.append(pileup1.getPosition());
		addPileup(sb, pileup1);
		addPileup(sb, pileup2);

		sb.append(SEP);
		sb.append(value);

		return sb.toString();
	}

	@Override
	public String convert2String(Pileup pileup1, Pileup pileup2) {
		StringBuilder sb = new StringBuilder();

		sb.append(pileup1.getContig());
		sb.append(SEP);
		sb.append(pileup1.getPosition());
		addPileup(sb, pileup1);
		addPileup(sb, pileup2);

		return sb.toString();		
	}
	
	private void addPileup(StringBuilder sb, Pileup pileup) {
		sb.append(SEP);
		sb.append(pileup.getStrand().character());

		sb.append(SEP);
		sb.append(new String(pileup.getBases()));
			
		sb.append(SEP);
		sb.append(SAMUtils.phredToFastq(pileup.getBASQs()));
	}

	@Override
	public ParallelPileup extractPileups(String line) {
		if(line.charAt(0) == getCOMMENT()) {
			return null;
		}

		String[] cols = line.split(Character.toString(SEP));
		ParallelPileup pileups = new ParallelPileup(1, 1);

		pileups.setPileup1(0, extractPileupsFull(2, cols, getDefaultPileup(cols)));
		pileups.setPileup2(0, extractPileupsFull(5, cols, getDefaultPileup(cols)));

		return pileups;
	}
	
	private Pileup getDefaultPileup(String[] cols) {
		Pileup pileup = new DefaultPileup();
		pileup.setContig(cols[0]);
		pileup.setPosition(Integer.parseInt(cols[1]));
		return pileup;
	}
	
	private Pileup extractPileupsFull(int i, String[] cols, Pileup pileup) {
		for(int k = 0;k < 3; ++k) {
			switch(k) {
			case 0:
				pileup.setStrand(DefaultPileup.STRAND.getEnum(cols[i + k]));
				break;
	
			case 1:
				char[] bases = new char[cols[i + k].length()];
				for(int j = 0; j < bases.length; ++j) {
					bases[j] = cols[i + k].charAt(j);
				}
				pileup.setBases(bases);
				break;
	
			case 2:
				byte[] basqs = new byte[cols[i + k].length()];
				for(int j = 0; j < basqs.length; ++j) {
					basqs[j] = (byte)cols[i + k].charAt(j);
				}
				pileup.setBASQs(basqs);
				break;
			}
		}

		return pileup;
	}


	@Override
	public double extractValue(String line) {
		String[] cols = line.split(Character.toString(SEP));
		return Double.parseDouble(cols[cols.length - 1]);
	}

	@Override
	public char getCOMMENT() {
		return COMMENT;
	}

	@Override
	public char getEMPTY() {
		return EMPTY;
	}

	@Override
	public char getSEP() {
		return SEP;
	}
	
	@Override
	public char getSEP2() {
		return SEP2;
	}
	
}
