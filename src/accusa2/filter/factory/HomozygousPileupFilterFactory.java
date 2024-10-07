package accusa2.filter.factory;

import accusa2.filter.process.HomozygousProcessPileupFilter;

public class HomozygousPileupFilterFactory extends AbstractPileupFilterFactory<Object> {

	private int sample;

	public HomozygousPileupFilterFactory() {
		super('H', "Filter non-homozygous pileup/BAM (1 or 2). Default: none");
		sample = 0;
	}

	@Override
	public HomozygousProcessPileupFilter getPileupFilterInstance() {
		return new HomozygousProcessPileupFilter(getC(), sample);
	}

	@Override
	public void processCLI(String line) throws IllegalArgumentException {
		if(line.length() == 1) {
			throw new IllegalArgumentException("Invalid argument " + line);
		}

		String[] s = line.split(Character.toString(AbstractPileupFilterFactory.SEP));
		int sample = Integer.valueOf(s[1]);
		if(sample == 1 || sample == 2) {
			setSample(sample);
			return;
		}
		throw new IllegalArgumentException("Invalid argument " + sample);
	}

	public final void setSample(int sample) {
		this.sample = sample;
	}

	public final int getSample() {
		return sample;
	}

}
