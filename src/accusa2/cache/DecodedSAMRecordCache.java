package accusa2.cache;

import accusa2.pileup.DecodedSamRecord;

public final class DecodedSAMRecordCache {

	private int size;
	private int capacity;
	private DecodedSamRecord[][] container;
	private int[] position2capacity;

	public DecodedSAMRecordCache(int size, int capacity) {
		this.size 			= size;
		this.capacity		= capacity;
		container 			= new DecodedSamRecord[size][capacity];
		position2capacity	= new int[size];
		clear();
	}

	/**
	 * 
	 * @param position
	 * @param decodedSAMRecord
	 */
	public void add(int position, DecodedSamRecord decodedSAMRecord) {
		if(isFull(position)) {
			return;
		}
		container[position][position2capacity[position]] = decodedSAMRecord; 
		position2capacity[position]++;
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public DecodedSamRecord[] get(int position) {
		DecodedSamRecord[] elements = new DecodedSamRecord[position2capacity[position]];
		System.arraycopy(container[position], 0, elements, 0, position2capacity[position]);
		return elements;
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public int size(int position) {
		return position2capacity[position];
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	public boolean isFull(int position) {
		return position2capacity[position] >= capacity;
	}

	/**
	 * 
	 */
	public void clear() {
		container = new DecodedSamRecord[size][capacity];
		for(int i = 0; i < size; ++i) {
			for(int j = 0; j < size; ++j) {
				container[i][j] = null;
			}
			position2capacity[i] = 0;
		}
	}

}
