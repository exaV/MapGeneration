package ch.fhnw.util;

import java.util.Arrays;

public final class SortedIntMap<T> extends SimpleSortedMap<int[], T> {

	public SortedIntMap() {
	}

	public SortedIntMap(int size) {
		super(size);
	}

	@SuppressWarnings("unchecked")
	public SortedIntMap(SortedIntMap<T> map) {
		super(map.size());
		for(int i = 0; i < map.size; i++)
			put(map.keys[i], (T)map.values[i]);
	}

	public boolean containsKey(int key) {
		return Arrays.binarySearch(keys, 0, size, key) >= 0;
	}

	public T get(int key) {
		return _get(Arrays.binarySearch(keys, 0, size, key));
	}

	public T put(int key, T value) {
		int idx = Arrays.binarySearch(keys, 0, size, key);
		T result = _put(idx, value);
		if(idx < 0)
			keys[-idx - 1] = key;
		return result;
	}

	public T remove(int key) {
		return _remove(Arrays.binarySearch(keys, 0, size, key));
	}

	@Override
	protected int[] allocKeys(int size) {
		return new int[size];
	}

	@Override
	protected void clearKeys() {
		Arrays.fill(keys, 0);
	}

	@Override
	protected int[] copyKeys(int[] keys, int size) {
		return Arrays.copyOf(keys, size);
	}

	@Override
	protected String keyValueToString(char defChar, char seperator) {
		StringBuilder result = new StringBuilder();
		for(int k : keySet())
			result.append(Integer.toString(k)).append(defChar).append(get(k)).append(seperator);
		return result.toString();
	}

	public int lastKey() {
		return keys[size - 1];
	}

	@SuppressWarnings("unchecked")
	public void addAll( SortedIntMap<T> neu ) {
		for ( int i = 0; i < neu.size(); i++ )
			put( neu.keys[i], (T) neu.values[i] );
	}
}
