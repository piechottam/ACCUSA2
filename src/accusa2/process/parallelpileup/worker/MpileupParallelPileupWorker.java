package accusa2.process.parallelpileup.worker;

import java.io.IOException;

import accusa2.ACCUSA2;
import accusa2.cli.Parameters;
import accusa2.pileup.ParallelPileup;
import accusa2.pileup.Pileup;
import accusa2.pileup.iterator.DefaultParallelPileupIterator;
import accusa2.pileup.iterator.ParallelPileupIterator;
import accusa2.process.parallelpileup.dispatcher.MpileupParallelPileupWorkerDispatcher;
import accusa2.util.AnnotatedCoordinate;

public class MpileupParallelPileupWorker extends AbstractParallelPileupWorker {

	public MpileupParallelPileupWorker(MpileupParallelPileupWorkerDispatcher workerDispatcher, AnnotatedCoordinate coordinate, Parameters parameters) {
		super(workerDispatcher, coordinate, parameters);
	}

	@Override
	protected void processParallelPileupIterator(ParallelPileupIterator parallelPileup) {
		ACCUSA2.printLog("Started screening contig " + 
				parallelPileupIterator.getAnnotatedCoordinate().getSequenceName() + 
				":" + 
				parallelPileupIterator.getAnnotatedCoordinate().getStart() + 
				"-" + 
				parallelPileupIterator.getAnnotatedCoordinate().getEnd());

		while (parallelPileup.hasNext()) {
			// considered comparisons
			comparisons++;

			StringBuilder sb = new StringBuilder();

			ParallelPileup pileups = parallelPileup.next();
			Pileup pileup1 = pileups.getPileups1()[0];
			Pileup pileup2 = pileups.getPileups2()[0];

			sb.append(resultFormat.convert2String(pileup1, pileup2, 0));
			try {
				tmpOutput.write(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected ParallelPileupIterator buildParallelPileupIterator_Helper(AnnotatedCoordinate coordinate, Parameters parameters) {
		return new DefaultParallelPileupIterator(coordinate, reader1, reader2, parameters);
	}

}
