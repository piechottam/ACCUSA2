package accusa2.filter.factory;

public abstract class AbstractReadDistancePileupFeatureFilterFactory extends AbstractPileupFeatureFilterFactory<Integer> {

	private int filterDistance;

	public AbstractReadDistancePileupFeatureFilterFactory(char c, String desc, int distance) {
		super(c, desc);
		this.filterDistance = distance;
	}

	public final int getDistance() {
		return filterDistance;
	}

	public final void setDistance(int distance) {
		this.filterDistance = distance;
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			return;
		}

		String[] s = line.split(Character.toString(AbstractPileupFilterFactory.SEP));
		int distance = Integer.valueOf(s[1]);
		if(distance < 0) {
			throw new IllegalArgumentException("Invalid distance " + line);
		}
		setDistance(distance);
	}
	
}
