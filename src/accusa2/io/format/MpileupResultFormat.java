package accusa2.io.format;

import net.sf.samtools.SAMUtils;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;

public class MpileupResultFormat extends AbstractResultFormat {

	public static char EMPTY 	= '*';
	public static char COMMENT = '#';
	public static char SEP 	= '\t';
	public static char SEP2 	= ',';

	public MpileupResultFormat() {
		super('M', "samtools mpileup like format (base columns without: $ ^ < > *)");
	}

	@Override
	public String convert2String(Pileup pileup1, Pileup pileup2, double value) {
		return convert2String(pileup1, pileup2);
	}

	@Override
	public String convert2String(Pileup pileup1, Pileup pileup2) {
		StringBuilder sb = new StringBuilder();

		sb.append(pileup1.getContig());
		sb.append(SEP);
		sb.append(pileup1.getPosition());
		sb.append(SEP);
		sb.append('N');
		addPileup(sb, pileup1);
		addPileup(sb, pileup2);

		return sb.toString();		
	}
	
	private void addPileup(StringBuilder sb, Pileup pileup) {
		sb.append(SEP);
		sb.append(pileup.getStrand().character());

		sb.append(SEP);
		sb.append(pileup.getCoverage());

		sb.append(SEP);
		sb.append(new String(pileup.getBases()));
		sb.append(SEP);
		sb.append(SAMUtils.phredToFastq(pileup.getBASQs()));

		sb.append(SEP);
		if(pileup.getMAPQs().length > 0) {
			sb.append(SAMUtils.phredToFastq(pileup.getMAPQs()));
		} else {
			sb.append(EMPTY);
		}
	}

	private Pileup getDefaultPileup(String[] cols) {
		Pileup pileup = new DefaultPileup();
		pileup.setContig(cols[0]);
		pileup.setPosition(Integer.parseInt(cols[1]));
		return pileup;
	}

	private void extractPileupsFull(int i, String s, Pileup pileup) {
		switch(i) {
		case 0:
			char[] bases = new char[s.length()];
			for(int j = 0; j < bases.length; ++j) {
				bases[j] = s.charAt(j);
			}
			pileup.setBases(bases);
			break;

		case 1:
			byte[] basqs = new byte[s.length()];
			for(int j = 0; j < basqs.length; ++j) {
				basqs[j] = (byte)s.charAt(j);
			}
			pileup.setBASQs(basqs);
			break;

		case 2:
			byte[] mapqs = new byte[s.length()];
			for(int j = 0; j < mapqs.length; ++j) {
				mapqs[j] = (byte)s.charAt(j);
			}
			pileup.setMAPQs(mapqs);
			break;
		}
	}
	
	@Override
	public ParallelPileup extractPileups(String line) {
		if(line.charAt(0) == getCOMMENT()) {
			return null;
		}

		String[] cols = line.split(Character.toString(SEP));
		ParallelPileup pileups = new ParallelPileup(1, 1);

		Pileup pileup = null;
		int j = 0;
		for(int i = 2; i < cols.length - 1; ++i) {
			if( i % 3 == 0 ) {
				if(pileup != null) {
					if(j == 0) {
						pileups.setPileup1(0, pileup);
						++j;
					} else {
						pileups.setPileup2(0, pileup);
					}
				}
				pileup = getDefaultPileup(cols);
			}
			extractPileupsFull(i % 3, cols[i], pileup);
		}

		return pileups;
	}

	@Override
	public double extractValue(String line) {
		return 0;
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
