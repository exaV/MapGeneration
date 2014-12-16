package ch.fhnw.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;



public final class IntList extends SimpleArrayList<int[], Integer> {

	public IntList() {
	}

	public IntList(int initialCapacity) {
		super(initialCapacity);
	}

	public IntList(IntList source) {
		elementData = source._getArray().clone();
		size        = source.size;
		modCount++;
	}

	public IntList(int[] source) {
		elementData = Arrays.copyOf(source, source.length);
		size        = source.length;
		modCount++;
	}
	
	@Override
	protected int[] alloc(int count) {
		return new int[count];
	}

	@Override
	protected int[] copyOf(Integer[] array, int newSize) {
		int[] result = new int[newSize];
		int idx = 0;
		for(Integer i : array)
			result[idx++] = i.intValue();
		return result;
	}

	@Override
	protected int[] copyOf(int[] original, int newLength) {
		return Arrays.copyOf(original, newLength);
	}
	
	public final void addAllIfUnique(IntList l) {
	    for (int i=0; i<l.size(); i++)
	    {
		addIfUnique(l.get(i));
	    }
	}
	
	public void add(final int e) {
		ensureCapacity(size + 1);  // Increments modCount!!
		elementData[size++] = e;
	}
	
	public void addIfUnique(final int e) {
	    	if (contains(e))
	    	    return;
		ensureCapacity(size + 1);  // Increments modCount!!
		elementData[size++] = e;
	}

	public void add(final int idx, final int e) {
		ensureCapacity(size + 1);  // Increments modCount!!		
		System.arraycopy(elementData, idx, elementData, idx + 1, size - idx);
		size++;
		elementData[idx] = e;
	}
	
	public void addSupportingEmpty(final int idx, final int e) {
		ensureSize(size + 1);  // Increments modCount!!		
		System.arraycopy(elementData, idx, elementData, idx + 1, size - idx);
		size++;
		elementData[idx] = e;
	}

	public int get(int i) {
		if(i > size || i < 0) throw new NoSuchElementException(Integer.toString(i));
		return elementData[i];
	}
	
	@Override
	public IntList clone() {
		return (IntList)super.clone();
	}
	
	@Override
	public void clear() {
		modCount++;
		size = 0;
	}

	public void set(int i, int v) {
		elementData[i] = v;
	}
	
	public boolean contains(int v) {
		for(int i = 0; i < size; i++)
			if(elementData[i] == v)
				return true;
		return false;
	}
	
	public int indexOf( int val )
	{
		for( int i = 0; i < size; i++ )
			if( elementData[ i ] == val )
				return i;
		return -1;
	}

	public int getFirst() {
		return get(0);
	}

	public int getLast() {
		return get(size - 1);
	}


	public void addFirst(int e) {
		add(0, e);
	}
	
	public void sort() {
		Arrays.sort(elementData, 0, size);
	}

	@Override
	protected int getComponentSize() {
		return 04;
	}

	@Override
	protected void load(DataInputStream in) throws IOException {
		try {
			for(int i = 0; i < elementData.length; i++)
				elementData[i] = in.readInt();
		} catch(EOFException e) {}
	}

	@Override
	protected void store(DataOutputStream out) throws IOException {
		for(int i = 0; i < size; i++)
			out.writeInt(elementData[i]);
	}
}
