package ch.fhnw.util;

import java.util.Arrays;

public final class SortedLongMap<T> extends SimpleSortedMap<long[], T> {

	public boolean containsKey(int key) {
		return Arrays.binarySearch(keys, 0, size, key) >= 0;
	}

	public T get(long key) {
		return _get(Arrays.binarySearch(keys, 0, size, key));
	}

	public T put(long key, T value) {
		int idx = Arrays.binarySearch(keys, 0, size, key);
		T result = _put(idx, value);
		if(idx < 0)
			keys[-idx - 1] = key;
		return result;
	}

	public T remove(long key) {
		return _remove(Arrays.binarySearch(keys, 0, size, key));
	}

	@Override
	protected long[] allocKeys(int size) {
		return new long[size];
	}

	@Override
	protected void clearKeys() {
		Arrays.fill(keys, 0);
	}

	@Override
	protected long[] copyKeys(long[] keys, int size) {
		return Arrays.copyOf(keys, size);
	}
	
	@Override
	protected String keyValueToString(char defChar, char seperator) {
		StringBuilder result = new StringBuilder();
		for(long k : keySet())
			result.append(Long.toString(k)).append(defChar).append(get(k)).append(seperator);
		return result.toString();
	}
}
