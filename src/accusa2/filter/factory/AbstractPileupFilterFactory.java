package accusa2.filter.factory;

import accusa2.cli.Parameters;
import accusa2.filter.process.AbstractPileupFilter;

public abstract class AbstractPileupFilterFactory<T> {

	public static char SEP = ':';

	private char c;
	private String desc;

	private Parameters parameters;

	public AbstractPileupFilterFactory(char c, String desc) {
		this.c = c;
		this.desc = desc;
	}

	public abstract AbstractPileupFilter getPileupFilterInstance();

	public final char getC() {
		return c;
	}

	public final String getDesc() {
		return desc;
	}

	public final Parameters getParameters() {
		return parameters;
	}

	public final void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	public void processCLI(String line) throws IllegalArgumentException {
		// implement to change behavior via CLI
	}
	
}
