package accusa2.filter.factory;

import accusa2.filter.process.PolymorphismPileupFilter;

public class PolymorphismPileupFilterFactory extends AbstractPileupFilterFactory<Object> {

	public PolymorphismPileupFilterFactory() {
		super('P', "Filter polymorphic positions");
	}

	@Override
	public PolymorphismPileupFilter getPileupFilterInstance() {
		return new PolymorphismPileupFilter(getC());
	}

}
