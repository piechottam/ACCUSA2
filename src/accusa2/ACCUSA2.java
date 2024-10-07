package accusa2;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceRecord;

import accusa2.cli.CLI;
import accusa2.cli.Parameters;
import accusa2.method.AbstractMethodFactory;
import accusa2.method.ACCUSA2Factory;
import accusa2.method.PileupFactory;
import accusa2.process.parallelpileup.dispatcher.AbstractParallelPileupWorkerDispatcher;
import accusa2.process.parallelpileup.worker.AbstractParallelPileupWorker;
import accusa2.util.AnnotatedCoordinate;
import accusa2.util.SimpleTimer;

/**
 * @author Michael Piechotta
 */
public class ACCUSA2 {

	private CLI cli;
	private static SimpleTimer timer;

	/**
	 * 
	 */
	public ACCUSA2() {
		cli = CLI.getSingleton();

		Map<String, AbstractMethodFactory> methodFactories = new TreeMap<String, AbstractMethodFactory>();

		AbstractMethodFactory methodFactory = null;

		methodFactory = new ACCUSA2Factory();
		methodFactories.put(methodFactory.getName(), methodFactory);
		
		methodFactory = new PileupFactory();
		methodFactories.put(methodFactory.getName(), methodFactory);

		cli.setMethodFactories(methodFactories);
	}

	/**
	 * 
	 * @return
	 */
	public static SimpleTimer getSimpleTimer() {
		if(timer == null) {
			timer = new SimpleTimer();
		}

		return timer;
	}

	/**
	 * 
	 * @return
	 */
	private CLI getCLI() {
		return cli;
	}

	/**
	 * 
	 * @param pathname1
	 * @param pathname2
	 * @return
	 * @throws Exception
	 */
	private List<AnnotatedCoordinate> getCoordinates(String pathname1, String pathname2) throws Exception {
		printLog("Computing overlap between sequence records.");

		SAMFileReader reader1 				= new SAMFileReader(new File(pathname1));
		List<SAMSequenceRecord> records1 	= reader1.getFileHeader().getSequenceDictionary().getSequences();
		
		SAMFileReader reader2 				= new SAMFileReader(new File(pathname2));
		List<SAMSequenceRecord> records2 	= reader2.getFileHeader().getSequenceDictionary().getSequences();

		
		String error = "Sequence Dictionary of BAM files do not match";
		if(records1.size() != records2.size()) {
			reader1.close();
			reader2.close();
			System.err.print("[ WARNING ]"  + error);
		}

		// intersect
		List<AnnotatedCoordinate> coordinates = new ArrayList<AnnotatedCoordinate>();
		Set<String> sequenceNames1 = new HashSet<String>();
		for(SAMSequenceRecord record : records1) {
			coordinates.add(new AnnotatedCoordinate(record.getSequenceName(), 1, record.getSequenceLength()));
			sequenceNames1.add(record.getSequenceName());
		}
		Set<String> sequenceNames2 = new HashSet<String>();
		for(SAMSequenceRecord record : records2) {
			sequenceNames2.add(record.getSequenceName());
		}
		if(!sequenceNames1.containsAll(sequenceNames2) || !sequenceNames2.containsAll(sequenceNames1)) {
			reader1.close();
			reader2.close();
			throw new Exception(error);
		}

		// close readers
		reader1.close();
		reader2.close();

		return coordinates;
	}

	/**
	 * 
	 * @param pathname
	 * @return
	 */
	private List<AnnotatedCoordinate> readCoordinates(String pathname) {
		List<AnnotatedCoordinate> coordinates = new ArrayList<AnnotatedCoordinate>();
		
		File file = new File(pathname);
		String line;

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			while((line = br.readLine()) != null) {
				AnnotatedCoordinate coordinate = new AnnotatedCoordinate();
				String[] cols = line.split("\t");

				coordinate.setSequenceName(cols[0]);
				coordinate.setStart(Integer.parseInt(cols[1]));
				coordinate.setEnd(Integer.parseInt(cols[2]));

				coordinates.add(coordinate);
			}
			// close and cleanup
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return coordinates;
	}

	/**
	 * 
	 * @param size
	 * @param args
	 */
	private void printProlog(int size, String[] args) {
		String lineSep = "--------------------------------------------------------------------------------";

		System.err.println(lineSep);

		StringBuilder sb = new StringBuilder();
		sb.append("ACCUSA2");
		for(String arg : args) {
			sb.append(" " + arg);
		}
		System.err.println(sb.toString());

		System.err.println(lineSep);
	}

	/**
	 * 
	 * @param line
	 */
	public static void printLog(String line) {
		String time = "[ " + getSimpleTimer().getTotalTimestring() + " ]";
		System.err.println(time + " " + line);
	}

	/**
	 * 
	 * @param comparisons
	 */
	private void printEpilog(int comparisons) {
		// print statistics to STDERR
		printLog("Screening done using " + cli.getParameters().getMaxThreads() + " thread(s)");

		System.err.println("Results can be found in: " + cli.getParameters().getOutput().getInfo());

		String lineSep = "--------------------------------------------------------------------------------";

		System.err.println(lineSep);
		System.err.println("Analyzed Parallel Pileups:\t" + comparisons);
		System.err.println("Elapsed time:\t\t\t" + getSimpleTimer().getTotalTimestring());
	}

	/**
	 * Application logic.
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ACCUSA2 accusa2 = new ACCUSA2();
		CLI cmd = accusa2.getCLI();

		if(!cmd.processArgs(args)) {
			System.exit(1);
		}
		Parameters parameters = cmd.getParameters();

		// load coordinates to search BAM files
		List<AnnotatedCoordinate> coordinates = new ArrayList<AnnotatedCoordinate>();
		if(!parameters.getBED_Pathname().isEmpty()) {
			// BED file
			coordinates = accusa2.readCoordinates(parameters.getBED_Pathname());
		} else {
			// use SAMheader to traverse the entire contigs
			coordinates = accusa2.getCoordinates(parameters.getPathname1(), parameters.getPathname2());
		}

		getSimpleTimer().getTimeString();
		// prolog
		accusa2.printProlog(coordinates.size(), args);
		// main
		AbstractParallelPileupWorkerDispatcher<? extends AbstractParallelPileupWorker> threadDispatcher = parameters.getMethodFactory().getInstance(coordinates, parameters);
		int comparisons = threadDispatcher.run();
		// epilog
		accusa2.printEpilog(comparisons);

		// cleaup
		parameters.getOutput().close();
	}

}
