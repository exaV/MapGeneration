package ch.fhnw.util;

import java.util.Arrays;

public final class SortedIntSet {
	private int[] set;
	private int   size;

	public SortedIntSet() {
		this(4);
	}

	public SortedIntSet(int size) {
		set = new int[size];
	}

	public SortedIntSet(int[] values) {
		set  = values.clone();
		size = values.length;
		Arrays.sort(set);
	}

	public boolean add(int value) {
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

	public void remove(int value) {
		int idx = Arrays.binarySearch(set, 0, size, value);
		if(idx >= 0) {
			int count = (size -  idx) - 1;
			if(count > 0)
				System.arraycopy(set, idx + 1, set, idx, count);
			size--;
		}
	}

	public boolean contains(int value) {
		return Arrays.binarySearch(set, 0, size, value) >= 0;
	}

	public int size() {
		return size;
	}

	public int[] sorted() {
		return Arrays.copyOf(set, size);
	}
	
	@Override
	public String toString() {
		return TextUtilities.toString("{", ",", "}", set, TextUtilities.NONE, 0, size);
	}

	public int[] toArray() {
		return sorted();
	}

	public void clear() {
		if (size<4)
			set  = new int[4];
		else
			set  = new int[size];
		size = 0;
	}
} 
