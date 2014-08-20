package ch.fhnw.util;

import java.util.Arrays;

public final class IntList {
	private int[] data;;
	private int   size;
	
	public IntList() {
		this(16);
	}
	
	public IntList(int size) {
		this.data = new int[size];
	}

	public IntList(int ... ints) {
		this.data = Arrays.copyOf(ints, ints.length);
		this.size = ints.length;
	}

	public void add(int value) {
		ensureCapacity(size + 1);
		data[size++] = value;
	}

	private void ensureCapacity(int capacity) {
		if(data.length > capacity) return;
		
		int newCap = capacity;
		while(newCap < capacity)
			newCap *= 2;
		int[] tmp = data;
		data = new int[newCap];
		System.arraycopy(tmp, 0, data, 0, size);
	}

	public int size() {
		return size;
	}

	public void clear() {
		size = 0;
	}

	public int get(int i) {
		return data[i];
	}

	public boolean isEmpty() {
		return size == 0;
	}
}
