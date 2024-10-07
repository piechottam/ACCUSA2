package accusa2.process.pileup2Matrix;

import accusa2.pileup.Pileup;

public abstract class AbstractPileup2Matrix {

	protected String name;
	protected String desc;

	/**
	 * 
	 * @param pileup
	 * @return
	 */
	public abstract double[][] calculate(Pileup pileup);
	public abstract double[][] calculate(Character[] bases, Pileup pileup);

	/**
	 * 
	 * @return name of conversion.
	 */
	public final String getDescription() {
		return desc;
	}

	/**
	 * 
	 * @return name of conversion.
	 */
	public final String getName() {
		return name;
	}

}
