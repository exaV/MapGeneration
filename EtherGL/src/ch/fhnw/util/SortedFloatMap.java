package ch.fhnw.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SortedFloatMap<T> extends SimpleSortedMap<float[], T> {

	public boolean containsKey(int key) {
		return Arrays.binarySearch(keys, 0, size, key) >= 0;
	}

	public T get(float key) {
		return _get(Arrays.binarySearch(keys, 0, size, key));
	}

	public T put(float key, T value) {
		int idx  = Arrays.binarySearch(keys, 0, size, key);
		T result = _put(idx, value);
		if(idx < 0)
			keys[-idx - 1] = key;
		return result;
	}

	public T remove(float key) {
		return _remove(Arrays.binarySearch(keys, 0, size, key));
	}

	@Override
	protected float[] allocKeys(int size) {
		return new float[size];
	}

	@Override
	protected void clearKeys() {
		Arrays.fill(keys, 0);
	}

	@Override
	protected float[] copyKeys(float[] keys, int size) {
		return Arrays.copyOf(keys, size);
	}

	@SuppressWarnings("unchecked")
	public Set<Entry<T>> entrySet() {
		Set<Entry<T>> result = new LinkedHashSet<>();
		for(int i = 0; i < size; i++)
			result.add(new Entry<>(keys[i], (T)values[i]));
		return result;
	}

	public Iterator<Entry<T>> entrySetIterator() {
		return entrySet().iterator();
	}

	@Override
	protected String keyValueToString(char defChar, char seperator) {
		StringBuilder result = new StringBuilder();
		for(float k : keySet())
			result.append(Float.toString(k)).append(defChar).append(get(k)).append(seperator);
		return result.toString();
	}

	public static class Entry<T> {
		public float key;
		public T     value;

		Entry(float key, T value) {
			this.key   = key;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return Float.floatToIntBits(key);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			return  obj instanceof Entry && key == ((Entry)obj).key;
		}
		
		@Override
		public String toString() {
			return key  + "=" + value;
		}
	}

	@SuppressWarnings("unchecked")
	public T getGreaterOrEquals(float key) {
		int idx = Arrays.binarySearch(keys, 0, size, key);
		if(idx < 0) {
			idx = -idx - 1;
			return (T) (idx >= size ? null : values[idx]);
		}
		return (T)values[idx];
	}

	public float firstKey() {
		return size > 0 ? keys[0] : Float.NaN;
	}

	@SuppressWarnings("unchecked")
	public T getNearest( float key ) {
		
		if (isEmpty())
			return null;
		
		int idx = Arrays.binarySearch(keys, 0, size, key);
		
		if(idx < 0) {
			idx = -idx - 1;
			
			if (idx >= size)
				return (T) values[idx-1];
			if (idx == 0)
				return (T) values[0];
			if (Math.abs ( keys[idx-1] - key) < Math.abs( keys[idx] -key ))
				return (T) values[idx-1];

			return (T) values[idx];
		}
		
		return (T)values[idx];
	}
}
