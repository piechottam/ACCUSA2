package accusa2.method;


import java.util.HashMap;

import java.util.List;
import java.util.TreeMap;

import java.util.Map;

import accusa2.cli.CLI;
import accusa2.cli.Parameters;
import accusa2.cli.options.PileupBuilderOption;
import accusa2.cli.options.ConsiderBasesOption;
import accusa2.cli.options.DebugOption;
import accusa2.cli.options.FalseDiscoveryRateOption;
import accusa2.cli.options.HelpOption;
import accusa2.cli.options.MaxDepthOption;
import accusa2.cli.options.MaxThreadOption;
import accusa2.cli.options.MinBASQOption;
import accusa2.cli.options.MinCoverageOption;
import accusa2.cli.options.MinMAPQOption;
import accusa2.cli.options.PathnameOption;
import accusa2.cli.options.PermutationsOption;
import accusa2.cli.options.StatisticOption;
import accusa2.cli.options.PileupFilterOption;
import accusa2.cli.options.ResultFileOption;
import accusa2.cli.options.ResultFormatOption;
import accusa2.cli.options.RetainFlagOption;
import accusa2.cli.options.BED_CoordinatesOption;
import accusa2.cli.options.VersionOption;
import accusa2.cli.options.WindowSizeOption;
import accusa2.cli.options.filter.FilterFlagOption;
import accusa2.cli.options.filter.FilterNHsamTagOption;
import accusa2.cli.options.filter.FilterNMsamTagOption;
import accusa2.filter.factory.AbstractPileupFilterFactory;
import accusa2.filter.factory.HomozygousPileupFilterFactory;
//import accusa2.filter.factory.PCRDuplicatePileupFilterFactory;
//import accusa2.filter.factory.INDEL_DistancePileupFeatureFilterFactory;
import accusa2.filter.factory.PolymorphismPileupFilterFactory;
import accusa2.filter.factory.DistancePileupFeatureFilterFactory;
//import accusa2.filter.factory.ReadEndDistancePileupFeatureFilterFactory;
//import accusa2.filter.factory.ReadGroupPileupFeatureFilterFactory;
//import accusa2.filter.factory.ReadStartDistancePileupFeatureFilterFactory;
import accusa2.io.format.DefaultResultFormat;
import accusa2.io.format.ResultFormat;
import accusa2.method.statistic.CombinedStatistic;
import accusa2.method.statistic.DefaultStatistic;
import accusa2.method.statistic.NoiseStatistic;
//import accusa2.method.statistic.PooledLRStatistic;
import accusa2.method.statistic.PooledStatistic;
import accusa2.method.statistic.PseudoCountStatistic;
import accusa2.method.statistic.ReducedStatistic;
import accusa2.method.statistic.MinimalCoverageStatistic;
import accusa2.method.statistic.StatisticCalculator;
import accusa2.method.statistic.TestStatistic;
//import accusa2.method.statistic.TestPooledStatistic;
import accusa2.process.parallelpileup.dispatcher.AbstractParallelPileupWorkerDispatcher;
import accusa2.process.parallelpileup.dispatcher.ACCUSA2_ParallelPileupWorkerDispatcher;
import accusa2.process.parallelpileup.worker.ACCUSA2_ParallelPileupWorker;
import accusa2.util.AnnotatedCoordinate;

public class ACCUSA2Factory extends AbstractMethodFactory {

	private static ACCUSA2_ParallelPileupWorkerDispatcher instance;
	
	public ACCUSA2Factory() {
		super("call", "Strand UNspecific variant calling");
	}

	public void initACOptions() {
		acOptions.add(new PathnameOption(parameters, '1'));
		acOptions.add(new PathnameOption(parameters, '2'));

		acOptions.add(new BED_CoordinatesOption(parameters));
		acOptions.add(new ResultFileOption(parameters));
		if(getResultFormats().size() == 1 ) {
			Character[] a = getResultFormats().keySet().toArray(new Character[1]);
			parameters.setResultFormat(getResultFormats().get(a[0]));
		} else {
			acOptions.add(new ResultFormatOption(parameters, getResultFormats()));
		}

		acOptions.add(new MaxThreadOption(parameters));
		acOptions.add(new WindowSizeOption(parameters));

		acOptions.add(new MinCoverageOption(parameters));
		acOptions.add(new MinBASQOption(parameters));
		acOptions.add(new MinMAPQOption(parameters));
		acOptions.add(new MaxDepthOption(parameters));
		
		acOptions.add(new FilterFlagOption(parameters));
		acOptions.add(new RetainFlagOption(parameters));

		if(getStatistics().size() == 1 ) {
			String[] a = getStatistics().keySet().toArray(new String[1]);
			parameters.setStatistic(getStatistics().get(a[0]));
		} else {
			acOptions.add(new StatisticOption(parameters, getStatistics()));
		}

		if(getPileupFilters().size() == 1 ) {
			Character[] a = getPileupFilters().keySet().toArray(new Character[1]);
			parameters.addPileupFilterFactory(getPileupFilters().get(a[0]));
		} else {
			acOptions.add(new PileupFilterOption(parameters, getPileupFilters()));
		}

		acOptions.add(new ConsiderBasesOption(parameters));
		acOptions.add(new FalseDiscoveryRateOption(parameters));
		acOptions.add(new PermutationsOption(parameters));
		acOptions.add(new PileupBuilderOption(parameters));
		
		acOptions.add(new DebugOption(parameters));
		acOptions.add(new HelpOption(parameters, CLI.getSingleton()));
		acOptions.add(new VersionOption(parameters, CLI.getSingleton()));
		
		acOptions.add(new FilterNHsamTagOption(parameters));
		acOptions.add(new FilterNMsamTagOption(parameters));
		
	}

	@Override
	public AbstractParallelPileupWorkerDispatcher<ACCUSA2_ParallelPileupWorker> getInstance(List<AnnotatedCoordinate> coordinates, Parameters parameters) {
		if(instance == null) {
			instance = new ACCUSA2_ParallelPileupWorkerDispatcher(coordinates, parameters);
		}
		return instance;
	}

	public Map<String, StatisticCalculator> getStatistics() {
		Map<String, StatisticCalculator> statistics = new TreeMap<String, StatisticCalculator>();

		StatisticCalculator statistic = new DefaultStatistic();
		statistics.put(statistic.getName(), statistic);

		statistic = new ReducedStatistic();
		statistics.put(statistic.getName(), statistic);

		statistic = new PseudoCountStatistic();
		statistics.put(statistic.getName(), statistic);
		
		statistic = new PooledStatistic();
		statistics.put(statistic.getName(), statistic);

		statistic = new MinimalCoverageStatistic();
		statistics.put(statistic.getName(), statistic);
		
		statistic = new NoiseStatistic();
		statistics.put(statistic.getName(), statistic);
		
		statistic = new TestStatistic();
		statistics.put(statistic.getName(), statistic);
/*
		statistic = new PooledLRStatistic();
		statistics.put(statistic.getName(), statistic);
*/
		statistic = new CombinedStatistic();
		statistics.put(statistic.getName(), statistic);

		return statistics;
	}

	public Map<Character, AbstractPileupFilterFactory<?>> getPileupFilters() {
		Map<Character, AbstractPileupFilterFactory<?>> abstractPileupFilters = new HashMap<Character, AbstractPileupFilterFactory<?>>();

		
		AbstractPileupFilterFactory<?> abstractPileupFilterFactory = null;

/*
		AbstractPileupFilterFactory<?> abstractPileupFilterFactory = new ReadStartDistancePileupFeatureFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);


		abstractPileupFilterFactory = new ReadEndDistancePileupFeatureFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);
		
		abstractPileupFilterFactory = new INDEL_DistancePileupFeatureFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);

		abstractPileupFilterFactory = ReadGroupPileupFeatureFilterFactory.getSingleton();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);


		abstractPileupFilterFactory = new PCRDuplicatePileupFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);
*/

		abstractPileupFilterFactory = new DistancePileupFeatureFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);

		abstractPileupFilterFactory = new HomozygousPileupFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);

		abstractPileupFilterFactory = new PolymorphismPileupFilterFactory();
		abstractPileupFilterFactory.setParameters(parameters);
		abstractPileupFilters.put(abstractPileupFilterFactory.getC(), abstractPileupFilterFactory);

		return abstractPileupFilters;
	}

	public Map<Character, ResultFormat> getResultFormats() {
		Map<Character, ResultFormat> resultFormats = new HashMap<Character, ResultFormat>();

		ResultFormat resultFormat = new DefaultResultFormat();
		resultFormats.put(resultFormat.getC(), resultFormat);

		/*
		resultFormat = new MpileupResultFormat();
		resultFormats.put(resultFormat.getC(), resultFormat);
		*/

		return resultFormats;
	}

}
