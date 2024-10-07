package accusa2.io.format;

import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;

public interface ResultFormat {

	char getC();
	String getDesc(); 

	String convert2String(Pileup pileup1, Pileup pileup2, double value);
	String convert2String(Pileup pileup1, Pileup pileup2);

	ParallelPileup extractPileups(String line);
	double extractValue(String line);

	char getCOMMENT();
	char getSEP();
	char getSEP2();
	char getEMPTY();

}
