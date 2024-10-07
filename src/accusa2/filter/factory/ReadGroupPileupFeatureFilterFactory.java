package accusa2.filter.factory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;

import accusa2.filter.process.AbstractFeaturePileupFilter;
import accusa2.filter.process.ReadGroupProcessPileupFilter;
import accusa2.pileup.features.ReadGroupFeature;

// TODO check
@Deprecated
public class ReadGroupPileupFeatureFilterFactory extends AbstractPileupFeatureFilterFactory<Integer> {

	private static ReadGroupPileupFeatureFilterFactory singleton;
	private Map<String, Integer> readGroupId2int;

	private ReadGroupPileupFeatureFilterFactory() {
		super('R', "Filter by read groupd ID");
	}

	@Override
	public AbstractFeaturePileupFilter getPileupFilterInstance() {
		return new ReadGroupProcessPileupFilter(getC());
	}

	public static ReadGroupPileupFeatureFilterFactory getSingleton() {
		if(singleton == null) {
			singleton = new ReadGroupPileupFeatureFilterFactory();
		}

		return singleton;
	}
	
	private void initReadGroupId2int() {
		String pathname1 = getParameters().getPathname1();
		SAMFileReader reader1 = new SAMFileReader(new File(pathname1));
		
		String pathname2 = getParameters().getPathname2();
		SAMFileReader reader2 = new SAMFileReader(new File(pathname2));

		readGroupId2int = getReadGroups(reader1, reader2);		
	}

	public int processReadGroupId(String id) {
		if(readGroupId2int == null) {
			initReadGroupId2int();
		}

		return readGroupId2int.get(id);
	}

	private List<SAMReadGroupRecord> getReadGroups(SAMFileReader reader) {
		return reader.getFileHeader().getReadGroups();
	}

	private Map<String,Integer> getReadGroups(SAMFileReader reader1, SAMFileReader reader2) {
		Map<String, Integer> readGroupId2int = new HashMap<String, Integer>();

		for(SAMReadGroupRecord readGroupRecord : getReadGroups(reader1)) {
			readGroupId2int.put(readGroupRecord.getId(), readGroupId2int.size());
		}

		for(SAMReadGroupRecord readGroupRecord : getReadGroups(reader2)) {
			if(!readGroupId2int.containsKey(readGroupRecord.getId())) {
				readGroupId2int.put(readGroupRecord.getId(), readGroupId2int.size());
			}
		}

		return readGroupId2int;
	}

	@Override
	public ReadGroupFeature createEmptyFeature(int n) {
		return new ReadGroupFeature(n, getC());
	}

}
