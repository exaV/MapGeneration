package ch.fhnw.util;

import java.util.Arrays;

public final class LongSet {
	private long[] set = new long[4];
	private int    size;

	public boolean add(long value) {
		int idx = Arrays.binarySearch(set, 0, size, value);
		if(idx < 0) {
			idx = -idx - 1;
			if(size >= set.length)
				set = Arrays.copyOf(set, set.length  * 2);

			int count = size -  idx;
			if(count > 0)
				System.arraycopy(set, idx, set, idx + 1, count);
			set[idx] = value;
			size++;
			return true;
		}
		return false;
	}

	public void remove(long value) {
		int idx = Arrays.binarySearch(set, 0, size, value);
		if(idx >= 0) {
			int count = (size -  idx) - 1;
			if(count > 0)
				System.arraycopy(set, idx + 1, set, idx, count);
			size--;
		}
	}

	public boolean contains(long value) {
		return Arrays.binarySearch(set, 0, size, value) >= 0;
	}

	public int size() {
		return size;
	}

	public long[] sorted() {
		return Arrays.copyOf(set, size);
	}
} 
