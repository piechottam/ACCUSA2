package accusa2.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import accusa2.filter.factory.AbstractPileupFeatureFilterFactory;
import accusa2.filter.factory.AbstractPileupFilterFactory;
import accusa2.filter.samtag.SamTagFilter;
import accusa2.io.format.DefaultResultFormat;
import accusa2.io.format.ResultFormat;
import accusa2.io.output.Output;
import accusa2.io.output.OutputPrinter;
import accusa2.method.AbstractMethodFactory;
import accusa2.method.statistic.DefaultStatistic;
import accusa2.method.statistic.StatisticCalculator;
import accusa2.pileup.DefaultPileup;
import accusa2.pileup.builder.DirectedPileupBuilderFactory;
import accusa2.pileup.builder.PileupBuilderFactory;
import accusa2.pileup.builder.UndirectedPileupBuilderFactory;

public class Parameters {

	
	// cache related
	private int windowSize;
	private int maxDepth;

	// filter: read, base specific
	private byte minBASQ;
	private byte minMAPQ;
	private int minCoverage;
	private Set<Character> bases;

	// filter: flags
	private int filterFlags;
	private int retainFlags;

	// filter: statistic
	private double fdr;
	
	private int permutations;

	private int maxThreads;
	
	// output format related
	private Output output;
	private ResultFormat resultFormat;

	// version
	public final String VERSION = "$Revision: 1916 $";
	public final String DATE = "$Date: 2012-05-15 11:49:00 +0200 (Di, 15. Mai 2012) $";

	// bed file to scan for variants
	private String bedPathname;

	private boolean processINDELs;

	// chosen instances
	private AbstractMethodFactory methodFactory;
	private StatisticCalculator statisticCalculator;
	private List<AbstractPileupFilterFactory<?>> abstractPileupFilterFactories;
	private List<AbstractPileupFeatureFilterFactory<?>> abstractPileupFeatureFilterFactories;
	
	// path to BAM files
	private List<String> pathnames1;
	private List<String> pathnames2;

	// properties for BAM files
	private boolean isDirected1;
	private boolean isDirected2;
	private PileupBuilderFactory pileupBuilderFactory1;
	private PileupBuilderFactory pileupBuilderFactory2;

	private List<SamTagFilter> samTagFilters;

	// debug flag
	private boolean debug; 

	/**
	 * 
	 */
	public Parameters() {
		windowSize 		= 200;

		minBASQ 		= Byte.parseByte(new String("20"));
		minMAPQ 		= Byte.parseByte(new String("20"));
		minCoverage 	= 3;
		maxDepth 		= 500;
		maxThreads 		= 1;
		bases			= new TreeSet<Character>();
		for(char b : DefaultPileup.BASES2) {
			bases.add(b);
		}

		fdr 			= 0.3;

		permutations	= 10;
		filterFlags		= 0;
		retainFlags		= 0;

		output 			= new OutputPrinter();
		resultFormat	= new DefaultResultFormat();

		bedPathname 	= new String();

		processINDELs	= false;

		statisticCalculator	= new DefaultStatistic();

		abstractPileupFeatureFilterFactories = new ArrayList<AbstractPileupFeatureFilterFactory<?>>();
		abstractPileupFilterFactories = new ArrayList<AbstractPileupFilterFactory<?>>();

		pathnames1		= new ArrayList<String>(1);
		isDirected1 	= false;
		pileupBuilderFactory1	= new UndirectedPileupBuilderFactory(bases);
		
		pathnames2		= new ArrayList<String>(1);
		isDirected2 	= false;
		pileupBuilderFactory2	= new UndirectedPileupBuilderFactory(bases);

		samTagFilters 	= new ArrayList<SamTagFilter>(3);

		debug			= false;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * 
	 * @param maxDepth
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPathname1() {
		return pathnames1.get(0);
	}

	/**
	 * 
	 * @param pathname1
	 */
	public void setPathname1(String pathname1) {
		pathnames1.add(pathname1);
	}

	/**
	 * 
	 * @return
	 */
	public String getPathname2() {
		return pathnames2.get(0);
	}

	/**
	 * 
	 * @param pathname2
	 */
	public void setPathname2(String pathname2) {
		pathnames2.add(pathname2);
	}

	/**
	 * 
	 * @return
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * 
	 * @param windowSize
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * 
	 * @return
	 */
	public byte getMinMAPQ() {
		return minMAPQ;
	}

	/**
	 * 
	 * @param minMAPQ
	 */
	public void setMinMAPQ(byte minMAPQ) {
		this.minMAPQ = minMAPQ;
	}

	/**
	 * 
	 * @return
	 */
	public byte getMinBASQ() {
		return minBASQ;
	}

	/**
	 * 
	 * @param minBASQ
	 */
	public void setMinBASQ(byte minBASQ) {
		this.minBASQ = minBASQ;
	}

	/**
	 * 
	 * @return
	 */
	public int getMinCoverage() {
		return minCoverage;
	}

	/**
	 * 
	 * @param minCoverage
	 */
	public void setMinCoverage(int minCoverage) {
		this.minCoverage = minCoverage;
	}

	/**
	 * 
	 * @return
	 */
	public int getMaxThreads() {
		return maxThreads;
	}

	/**
	 * 
	 * @param maxThreads
	 */
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	/**
	 * 
	 * @param output
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

	/**
	 * 
	 * @return
	 */
	public Output getOutput() {
		return output;
	}

	/**
	 * 
	 * @param fdr
	 */
	public void setFDR(double fdr) {
		this.fdr = fdr;
	}

	/**
	 * 
	 * @return
	 */
	public double getFDR() {
		return fdr;
	}

	/**
	 * 
	 * @param flags
	 */
	public void setFilterFlags(int flags) {
		this.filterFlags = flags;
	}

	/**
	 * 
	 * @return
	 */
	public int getFilterFlags() {
		return filterFlags;
	}

	/**
	 * 
	 * @param retainFlags
	 */
	public void setRetainFlags(int retainFlags) {
		this.retainFlags = retainFlags;
	}

	/**
	 * 
	 * @return
	 */
	public int getRetainFlags() {
		return retainFlags;
	}

	/**
	 * 
	 * @return
	 */
	public int getPermutations() {
		return permutations;
	}

	/**
	 * 
	 * @param permutations
	 */
	public void setPermutations(int permutations) {
		this.permutations = permutations;
	}

	/**
	 * 
	 * @return
	 */
	public ResultFormat getResultFormat() {
		return resultFormat;
	}

	/**
	 * 
	 * @param resultFormat
	 */
	public void setResultFormat(ResultFormat resultFormat) {
		this.resultFormat = resultFormat;
	}
	
	/**
	 * 
	 * @param workerFactory
	 */
	public void setWorkerFactory(AbstractMethodFactory workerFactory) {
		this.methodFactory = workerFactory;
	}

	/**
	 * 
	 * @return
	 */
	public AbstractMethodFactory getMethodFactory() {
		return methodFactory;
	}

	/**
	 * 
	 * @param statistic
	 */
	public void setStatistic(StatisticCalculator statistic) {
		this.statisticCalculator = statistic;
	}

	public StatisticCalculator getStatisticCalculator() {
		return statisticCalculator;
	}

	public void setMethodFactory(AbstractMethodFactory factory) {
		this.methodFactory = factory;
	}

	public void setBED_Pathname(String bedPathname) {
		this.bedPathname = bedPathname;
	}

	public String getBED_Pathname() {
		return bedPathname;
	}

	public void setBases(Set<Character> bases) {
		this.bases = bases;
		pileupBuilderFactory1 = null;
		pileupBuilderFactory2 = null;
	}

	public Set<Character> getBases() {
		return bases;
	}

	public List<AbstractPileupFilterFactory<?>> getPileupFilterFactories() {
		return abstractPileupFilterFactories;
	}
	
	public List<AbstractPileupFeatureFilterFactory<?>> getPileupFeatureFilterFactories() {
		return abstractPileupFeatureFilterFactories;
	}

	public void addPileupFilterFactory(AbstractPileupFilterFactory<?> abstractPileupFilter) {
		abstractPileupFilter.setParameters(this);
		abstractPileupFilterFactories.add(abstractPileupFilter);
	}

	public void addPileupFeatureFilterFactory(AbstractPileupFeatureFilterFactory<?> abstractPileupFeatureFilter) {
		abstractPileupFeatureFilter.setParameters(this);
		abstractPileupFeatureFilterFactories.add(abstractPileupFeatureFilter);
	}

	public void setProcessINDELs(boolean processINDELs) {
		this.processINDELs = processINDELs;
	}

	public boolean getProcessINDELs() {
		return processINDELs;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean getDebug() {
		return debug;
	}

	private PileupBuilderFactory buildPileupBuilderFactory(boolean isDirected) {
		if(isDirected) {
			return new DirectedPileupBuilderFactory(bases);
		} else {
			return new UndirectedPileupBuilderFactory(bases);
		}
	}

	public PileupBuilderFactory getPileupBuilderFactory1(){
		if(pileupBuilderFactory1 == null) {
			pileupBuilderFactory1 = buildPileupBuilderFactory(isDirected1);
		}

		return pileupBuilderFactory1;
	}

	public PileupBuilderFactory getPileupBuilderFactory2(){
		if(pileupBuilderFactory2 == null) {
			pileupBuilderFactory2 = buildPileupBuilderFactory(isDirected2);
		}

		return pileupBuilderFactory2;
	}
	
	public List<SamTagFilter> getSamTagFilter() {
		return samTagFilters;
	}

	public void setIsDirected1(boolean isDirected) {
		isDirected1 = isDirected;
		pileupBuilderFactory1 = null;
	}
	
	public void setIsDirected2(boolean isDirected) {
		isDirected2 = isDirected;
		pileupBuilderFactory2 = null;
	}
	
}
