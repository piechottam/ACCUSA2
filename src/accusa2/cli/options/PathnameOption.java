package accusa2.cli.options;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.samtools.SAMFileReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

import accusa2.cli.Parameters;

public class PathnameOption extends AbstractACOption {

	private char c;
	
	public PathnameOption(Parameters paramters, char c) {
		super(paramters);
		this.c = c;
		opt = c;
		longOpt = "bam" + c;
	}

	@SuppressWarnings("static-access")
	@Override
	public Option getOption() {
		return OptionBuilder.withLongOpt(longOpt)
			.withArgName(longOpt.toUpperCase()) 
			.hasArg(true)
			.withDescription("Path to file " + longOpt.toUpperCase())
			.create(opt);
	}

	@Override
	public void process(CommandLine line) throws Exception {
		if(line.hasOption(c)) {
	    	String pathname = line.getOptionValue(c);
	    	File file = new File(pathname);
	    	if(!file.exists()) {
	    		throw new FileNotFoundException("File " + longOpt.toUpperCase() + " (" + pathname + ") in not accessible!");
	    	}
	    	SAMFileReader reader = new SAMFileReader(file);
	    	if(!reader.hasIndex()) {
	    		reader.close();
	    		throw new FileNotFoundException("Index for BAM file" + c + " is not accessible!");
	    	}
	    	// beware of ugly code
	    	if(c == '1') {
	    		parameters.setPathname1(pathname);
	    	} else if(c == '2') {
	    		parameters.setPathname2(pathname);
	    	} else {
	    		reader.close();
	    		throw new IllegalArgumentException(c + " is not supported!");
	    	}
	    	reader.close();
	    } else {
	    	throw new ParseException("BAM File" + c + " is not provided!");
	    }
	}

}
