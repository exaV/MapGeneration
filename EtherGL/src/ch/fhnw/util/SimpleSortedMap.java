package ch.fhnw.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class SimpleSortedMap<K, V> {
	protected Object[] values;
	protected K        keys;
	protected int      size;

	protected SimpleSortedMap() {
		this(4);
	}

	protected SimpleSortedMap(int size) {
		values = new Object[size];
		keys   = allocKeys(size);
	}

	protected abstract K      allocKeys(int size);
	protected abstract K      copyKeys(K keys, int size);
	protected abstract void   clearKeys();
	protected abstract String keyValueToString(char defChar, char seperator); 

	@SuppressWarnings("unchecked")
	protected V _get(int idx) {
		return (V) (idx < 0 ? null : values[idx]);
	}

	@SuppressWarnings("unchecked")
	protected V _put(int idx, V value) {
		if(idx < 0) {
			idx = -idx - 1;
			if(size >= values.length) {
				keys   = copyKeys(keys, values.length  * 2);
				values = Arrays.copyOf(values, values.length  * 2);
			}

			int count = size -  idx;
			if(count > 0) {
				System.arraycopy(keys,   idx, keys,   idx + 1, count);
				System.arraycopy(values, idx, values, idx + 1, count);
			}
			values[idx] = value;

			size++;
			return null;
		}
		Object result = values[idx];
		values[idx] = value;
		return (V) result;
	}

	@SuppressWarnings("unchecked")
	protected V _remove(int idx) {
		if(idx >= 0) {
			Object result = values[idx];
			int count = (size -  idx) - 1;
			if(count > 0) {
				System.arraycopy(keys,   idx + 1, keys,   idx, count);
				System.arraycopy(values, idx + 1, values, idx, count);
			}
			size--;
			return (V) result;
		}
		return null;
	}

	public K keySet() {
		return copyKeys(keys, size);
	}

	// --- Map interface

	public void clear() {
		Arrays.fill(values, null);
		clearKeys();
		size = 0;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public V[] values(V[] result) {
		System.arraycopy(values, 0, result, 0, Math.min(result.length, size));
		return result;
	}

	@SuppressWarnings("unchecked")
	public V[] values(Class<V> cls) {
		return values((V[])Array.newInstance(cls, size));
	}

	@SuppressWarnings("unchecked")
	public V firstValue() {
		return size > 0 ? (V) values[0] : null;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("{");
		result.append(keyValueToString('=', ','));
		if(!isEmpty())
			result.setLength(result.length() - 1);
		result.append('}');
		return result.toString();
	}
	
	ValueIterator valueIterable;
	
	public Iterable<V> values() {
	    if(valueIterable == null)
		valueIterable = new ValueIterator();
	    return valueIterable;
	}
	
	class ValueIterator implements Iterator<V>, Iterable<V> {
		private int idx = 0;
		
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public boolean hasNext() {
			return idx < size;
		}

		@SuppressWarnings("unchecked")
		@Override
		public V next() {
			if(idx >= size) throw new NoSuchElementException(idx + " larger than " + size);
			return (V)values[idx++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
