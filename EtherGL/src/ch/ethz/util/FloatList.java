package ch.ethz.util;


// quick and dirty
// XXX we probably better use an existing library (trove, or similar)
public final class FloatList {
	float[] array;
	int size;

	public FloatList() {
		this(0);
	}
	
	public FloatList(int initialCapacity) {
		array = new float[initialCapacity];
	}
	
	public void add(float value) {
		ensureCapacity(size + 1);
		array[size++] = value;
	}
	
	public void addAll(float[] values) {
		ensureCapacity(size + values.length);
		System.arraycopy(values, 0, array, size, values.length);
		size += values.length;
	}
	
	public int size() {
		return size;
	}
	
	public void clear() {
		size = 0;
	}
	
	public float[] toArray() {
		return array;
	}
	
	public void ensureCapacity(int capacity) {
		if (capacity > array.length) {
			float[] a = new float[capacity];
			System.arraycopy(array, 0, a, 0, size);
			array = a;
		}
	}
}
