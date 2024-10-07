package accusa2.process.parallelpileup.worker;

import java.io.IOException;

import accusa2.ACCUSA2;
import accusa2.cli.Parameters;
import accusa2.filter.factory.AbstractPileupFilterFactory;
import accusa2.filter.process.AbstractPileupFilter;
import accusa2.method.statistic.StatisticCalculator;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.PileupUtils;
import accusa2.pileup.iterator.ParallelPileupIterator;
import accusa2.pileup.iterator.VariantParallelPileupIterator;
import accusa2.pileup.sample.SampleBasesWithoutReplacement;
import accusa2.pileup.sample.SampleBasqsWithoutReplacement;
import accusa2.pileup.sample.SamplePileupWithoutReplacement;
import accusa2.process.parallelpileup.dispatcher.ACCUSA2_ParallelPileupWorkerDispatcher;
import accusa2.util.AnnotatedCoordinate;
import accusa2.util.DiscriminantStatisticContainer;

public class ACCUSA2_ParallelPileupWorker extends AbstractParallelPileupWorker {

	protected final StatisticCalculator statisticCalculator;

	//protected SamplePileupWithoutReplacement samplePileups;
	protected SamplePileupWithoutReplacement samplePileups;

	protected SampleBasesWithoutReplacement sampleBases;
	protected SampleBasqsWithoutReplacement sampleBasqs;

	protected final int permutations;

	protected DiscriminantStatisticContainer discriminantStatisticContainer;

	public ACCUSA2_ParallelPileupWorker(
			final ACCUSA2_ParallelPileupWorkerDispatcher threadDispatcher, 
			final AnnotatedCoordinate coordinate, final Parameters parameters) {
		super(threadDispatcher, coordinate, parameters);

		statisticCalculator = parameters.getStatisticCalculator().newInstance();

		samplePileups 		= new SamplePileupWithoutReplacement();
		sampleBases 		= new SampleBasesWithoutReplacement();
		sampleBasqs 		= new SampleBasqsWithoutReplacement();

		permutations 		= parameters.getPermutations();

		discriminantStatisticContainer = new DiscriminantStatisticContainer();
	}

	protected void processParallelPileupIterator(final ParallelPileupIterator parallelPileupIterator) {
		ACCUSA2.printLog("Started screening contig " + 
				parallelPileupIterator.getAnnotatedCoordinate().getSequenceName() + 
				":" + 
				parallelPileupIterator.getAnnotatedCoordinate().getStart() + 
				"-" + 
				parallelPileupIterator.getAnnotatedCoordinate().getEnd());

		while (parallelPileupIterator.hasNext()) {
			final ParallelPileup pileups = parallelPileupIterator.next();
			final Pileup pileup1 = pileups.getPileups1()[0];
			final Pileup pileup2 = pileups.getPileups2()[0];

			// TODO make a filter out of this how to deal with pileups where > 3 different bases can be observed?
			if(PileupUtils.getAlleles(pileup1, pileup2) > 2) {
				continue;
			}

			// calculate unfiltered value
			final double unfilteredValue = statisticCalculator.getStatistic(pileup1, pileup2);
			if(!isValidValue(unfilteredValue)) {
				continue;
			}

			final StringBuilder sb = new StringBuilder();
			sb.append(resultFormat.convert2String(pileup1, pileup2, unfilteredValue));

			final int pileupFilterCount = parameters.getPileupFilterFactories().size();
			int pileupFilterIndex = 0;
			if(pileupFilterCount == 0 ) { // no filters
				processPileups(pileups, unfilteredValue);
			} else { // calculate filters or quit
				// container for pileups
				ParallelPileup filteredPileups = new ParallelPileup(1, 1);
				filteredPileups.setPileup1(0, new DefaultPileup(pileup1));
				filteredPileups.setPileup2(0, new DefaultPileup(pileup2));

				// container for value(s)
				double filteredValue = unfilteredValue;

				// apply each filter
				for(; pileupFilterIndex < pileupFilterCount; ++pileupFilterIndex) {
					AbstractPileupFilterFactory<?> abstractPileupFilterFactory = parameters.getPileupFilterFactories().get(pileupFilterIndex);

					AbstractPileupFilter abstractProcessPileupFilter = abstractPileupFilterFactory.getPileupFilterInstance();

					// apply filter
					if(abstractProcessPileupFilter.filter(filteredPileups)) {

						// quit filtering
						if(abstractProcessPileupFilter.quitFiltering()) {
							// reset
							filteredPileups.setPileup1(0, new DefaultPileup());
							filteredPileups.setPileup2(0, new DefaultPileup());
							filteredValue = -1;
							break;
						}

						// change parallel pileup
						filteredPileups = abstractProcessPileupFilter.getFilteredParallelPileup();

						// check coverage restrains
						if(!isValidParallelPileup(filteredPileups)) {
							filteredValue = -1;
							break;
						}

						// calculate value for filterePileups
						filteredValue = statisticCalculator.getStatistic(filteredPileups.getPileups1()[0], filteredPileups.getPileups2()[0]);
						if(!isValidValue(filteredValue)) {
							filteredValue = -1;
							break;
						}

						// append calculated result
						sb.append(resultFormat.getSEP());
						sb.append(filteredValue);
					} else {
						// append dummy result
						sb.append(resultFormat.getSEP());
						sb.append("*");
					}
				}

				// don't process filtered results when one filter said to quit
				boolean correct = true;
				if(pileupFilterIndex != pileupFilterCount) {
					correct = false;
				}

				// append empty result
				for(;pileupFilterIndex < pileupFilterCount; ++pileupFilterIndex) {
					sb.append(resultFormat.getSEP());
					sb.append("-1");
				}

				// append filtered result
				sb.append(resultFormat.getSEP());
				sb.append(filteredValue);

				if(correct) {
					processPileups(filteredPileups, filteredValue);
				}
			}

			// considered comparisons
			comparisons++;
			try {
				// write output 
				tmpOutput.write(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * construct true positive set on filtered values
	 */
	private final void processPileups(final ParallelPileup pileups, final double value) {
		if(isValidParallelPileup(pileups) && isValidValue(value)) {
			discriminantStatisticContainer.addR_Value(value, pileups.getPileups1()[0], pileups.getPileups2()[0]);
			resample(value, pileups);
		}
	}

	protected final boolean isValidParallelPileup(final ParallelPileup parallelPileup) {
		return parallelPileup.getPileups1()[0].getCoverage() >= parameters.getMinCoverage() && parallelPileup.getPileups2()[0].getCoverage() >= parameters.getMinCoverage();
	}

	protected final boolean isValidValue(final double value) {
		return value > 0.0;
	}

	@Override
	protected ParallelPileupIterator buildParallelPileupIterator_Helper(final AnnotatedCoordinate coordinate,
			final Parameters parameters) {
		return new VariantParallelPileupIterator(coordinate, reader1, reader2, parameters);
	}

	/*
	 * FIXME samples bas+basq and bases, sample as long as the values bigger zero.
	 */
	private void resample(final double observedValue, final ParallelPileup parallelPileup) {
		final Pileup pileup1 = parallelPileup.getPileups1()[0];
		final Pileup pileup2 = parallelPileup.getPileups2()[0];

		// container
		Pileup[] sampledPileups = new Pileup[2];

		// permutate
		double sumValue = 0.0;
		for(int i = 0; i < permutations; ++i) {
			// sample from pooled pileup
			sampledPileups = samplePileups.sample(pileup1, pileup2);
			double sampledValue = statisticCalculator.getStatistic(sampledPileups[0], sampledPileups[1]);
			
			sampledPileups = sampleBases.sample(pileup1, pileup2);
			sampledValue = Math.max(sampledValue, statisticCalculator.getStatistic(sampledPileups[0], sampledPileups[1]));
			
			if(isValidValue(sampledValue)) {
				sumValue += sampledValue;
			}
			else {
				--i;
			}
		}
		double val = sumValue / (double) permutations;
		discriminantStatisticContainer.addNULL_Value(val, pileup1, pileup2);
	}

	public DiscriminantStatisticContainer getDiscriminantStatisticContainer() {
		return discriminantStatisticContainer;
	}

}


