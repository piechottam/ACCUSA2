package accusa2.cli.options;

import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

import accusa2.cli.Parameters;
import accusa2.filter.factory.AbstractPileupFeatureFilterFactory;
import accusa2.filter.factory.AbstractPileupFilterFactory;

public class PileupFilterOption extends AbstractACOption {

	private static char OR = ',';
	//private static char AND = '&'; // TODO add logic

	private Map<Character, AbstractPileupFilterFactory<?>> pileupFilterFactories;

	public PileupFilterOption(Parameters parameters, Map<Character, AbstractPileupFilterFactory<?>> pileupFilterFactories) {
		super(parameters);
		opt = 'a';
		longOpt = "pileup-filter";

		this.pileupFilterFactories = pileupFilterFactories;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		StringBuffer sb = new StringBuffer();

		for(char c : pileupFilterFactories.keySet()) {
			AbstractPileupFilterFactory<?> pileupFilterFactory = pileupFilterFactories.get(c);
			sb.append(pileupFilterFactory.getC());
			sb.append(" | ");
			sb.append(pileupFilterFactory.getDesc());
			sb.append("\n");
		}

		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase())
			.hasArg(true)
			.withDescription(
					"chain of " + longOpt.toUpperCase() + " to apply to pileups:\n" + sb.toString() + 
					"\nSeparate multiple " + longOpt.toUpperCase() + " with '" + OR + "' (e.g.: D,I)")
			.create(opt); 
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(opt)) {
			String s = line.getOptionValue(opt);
			String[] t = s.split(Character.toString(OR));

			for(String a : t) {
				char c = a.charAt(0);
				if(!pileupFilterFactories.containsKey(c)) {
					throw new IllegalArgumentException("Unknown SAM processing: " + c);
				}
				AbstractPileupFilterFactory<?> pileupFilterFactory = pileupFilterFactories.get(c);
				if(a.length() > 1) {
					pileupFilterFactory.processCLI(a);
				}
				if(pileupFilterFactory instanceof AbstractPileupFeatureFilterFactory<?>) {
					parameters.addPileupFeatureFilterFactory((AbstractPileupFeatureFilterFactory<?>)pileupFilterFactory);
				}
				parameters.addPileupFilterFactory(pileupFilterFactory);
			}
		}
	}

}
