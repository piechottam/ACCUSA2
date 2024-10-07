package accusa2.io.format;

public abstract class AbstractResultFormat implements ResultFormat {

	private char c;
	private String desc;
	
	public AbstractResultFormat(char c, String desc) {
		this.c = c;
		this.desc = desc;
	}

	@Override
	public final char getC() {
		return c;
	}

	@Override
	public final String getDesc() {
		return desc;
	}

}
