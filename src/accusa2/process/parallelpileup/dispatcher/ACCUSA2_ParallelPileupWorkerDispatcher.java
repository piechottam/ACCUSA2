package accusa2.process.parallelpileup.dispatcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

//import umontreal.iro.lecuyer.probdist.ChiSquareDist;

import accusa2.cli.Parameters;
import accusa2.filter.factory.AbstractPileupFilterFactory;
import accusa2.io.format.ResultFormat;
import accusa2.io.format.TmpResultFormat;
import accusa2.io.output.Output;
import accusa2.io.output.TmpOutputReader;
import accusa2.io.output.TmpOutputWriter;
//import accusa2.method.statistic.PooledLRStatistic;
import accusa2.pileup.ParallelPileup;
import accusa2.process.parallelpileup.worker.ACCUSA2_ParallelPileupWorker;
import accusa2.util.AnnotatedCoordinate;
import accusa2.util.DiscriminantStatisticContainer;

public class ACCUSA2_ParallelPileupWorkerDispatcher extends AbstractParallelPileupWorkerDispatcher<ACCUSA2_ParallelPileupWorker> {

	private final DiscriminantStatisticContainer discriminantStatisticContainer; 
	//private ChiSquareDist chiSquare;

	public ACCUSA2_ParallelPileupWorkerDispatcher(List<AnnotatedCoordinate> coordinates, Parameters parameters) {
		super(coordinates, parameters);

		// FIXME how many degrees of freedom
		//chiSquare = new ChiSquareDist(12);

		discriminantStatisticContainer = new DiscriminantStatisticContainer();
	}

	@Override
	protected void processFinishedWorker(ACCUSA2_ParallelPileupWorker parallelPileupWorker) {
		synchronized (comparisons) {
			comparisons += parallelPileupWorker.getComparisons();
		}

		synchronized (discriminantStatisticContainer) {
			discriminantStatisticContainer.addContainer(parallelPileupWorker.getDiscriminantStatisticContainer());
		}

		synchronized (tmpOutputs) {
			for(int i : parallelPileupWorker.getTmpOutputWriters().keySet()) {
				tmpOutputs[i] = parallelPileupWorker.getTmpOutputWriters().get(i);
			}
		}
	}

	@Override
	protected ACCUSA2_ParallelPileupWorker buildParallelPileupWorker() {
		return new ACCUSA2_ParallelPileupWorker(this, next(), parameters);
	}

	@Override
	protected void writeOuptut() {
		final Output output = parameters.getOutput();
		final ResultFormat resultFormat = new TmpResultFormat();

		// TODO move somewhere else
		// write Header
		try {
			// write header
			final StringBuilder sb = new StringBuilder();
			sb.append("#");
			sb.append("contig");
			sb.append(resultFormat.getSEP());
			sb.append("position");
			sb.append(resultFormat.getSEP());
			sb.append("strand1");
			sb.append(resultFormat.getSEP());
			sb.append("bases1");
			sb.append(resultFormat.getSEP());
			sb.append("basqs1");
			sb.append(resultFormat.getSEP());
			sb.append("strand2");
			sb.append(resultFormat.getSEP());
			sb.append("bases2");
			sb.append(resultFormat.getSEP());
			sb.append("basqs2");
			sb.append(resultFormat.getSEP());
			sb.append("unfiltered");

			String filtered = new String();
			for(final AbstractPileupFilterFactory<?> abstractPileupFilterFactory : parameters.getPileupFilterFactories()) {
				filtered += abstractPileupFilterFactory.getC();
				sb.append(resultFormat.getSEP());
				sb.append("filtered_");
				sb.append(filtered);
			}
			if(parameters.getPileupFilterFactories().size() > 0) {
				sb.append(resultFormat.getSEP());
				sb.append("filtered");
			}

			// FIXME bad style
			//if(parameters.getStatisticCalculator() instanceof PooledLRStatistic) {
			//	sb.append(resultFormat.getSEP());
			//	sb.append("p_value");
			//}

			sb.append(resultFormat.getSEP());
			sb.append("fdr");

			output.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//int i = 0;
		for(final TmpOutputWriter tmpOutputWriter : tmpOutputs) {
			try {
				tmpOutputWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				final TmpOutputReader tmpOutputReader = new TmpOutputReader(tmpOutputWriter.getInfo());

				String line = null;
				while((line = tmpOutputReader.readLine()) != null) {
					final double value = resultFormat.extractValue(line);

					final ParallelPileup parallelPileup = resultFormat.extractPileups(line);

					final double fdr = discriminantStatisticContainer.getFDR(value, parallelPileup.getPileups1()[0], parallelPileup.getPileups2()[0]);

					if( fdr > 1  || fdr < 0 ) {
						//throw new Exception("FDR: " + fdr + " Value: " + value);
						System.err.println("FDR: " + fdr + " Value: " + value);
						System.err.println(line);
						System.err.println("he_he_R: " + discriminantStatisticContainer.getHe_he_R().getCumulativeCount(value));
						System.err.println("he_he_V: " + discriminantStatisticContainer.getHe_he_V().getCumulativeCount(value));
						System.err.println("ho_he_R: " + discriminantStatisticContainer.getHo_he_R().getCumulativeCount(value));
						System.err.println("ho_he_V: " + discriminantStatisticContainer.getHo_he_V().getCumulativeCount(value));
					}

					if(fdr <= parameters.getFDR()) {
						//if(parameters.getStatisticCalculator() instanceof PooledLRStatistic) {
						//	output.write(line + resultFormat.getSEP() + (1 - chiSquare.cdf(value)) + resultFormat.getSEP() + fdr);
						//} else {
							output.write(line + resultFormat.getSEP() + fdr);
						//}
					}
				}
				tmpOutputReader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(!parameters.getDebug()){
				new File(tmpOutputWriter.getInfo()).delete();
			}
		}

		final File file = new File("/tmp/FDR");
		try {
			final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			StringBuilder sb = new StringBuilder();
			sb.append("value");
			sb.append('\t');
			sb.append("he_he_R");
			sb.append('\t');
			sb.append("he_he_V");
			sb.append('\t');
			sb.append("ho_he_R");
			sb.append('\t');
			sb.append("ho_he_V");
			sb.append('\n');
			bw.write(sb.toString());

			for(int i = 0; i < discriminantStatisticContainer.getSize(); ++i) {
				sb = new StringBuilder();
				final double value = (double)i / (double)discriminantStatisticContainer.getFactor();

				sb.append(value);
				sb.append('\t');
				sb.append(discriminantStatisticContainer.getHe_he_R().getCount(value));
				sb.append('\t');
				sb.append(discriminantStatisticContainer.getHe_he_V().getCount(value));
				sb.append('\t');
				sb.append(discriminantStatisticContainer.getHo_he_R().getCount(value));
				sb.append('\t');
				sb.append(discriminantStatisticContainer.getHo_he_V().getCount(value));
				sb.append('\n');
				bw.write(sb.toString());
			}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
