package ch.fhnw.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class BooleanList extends SimpleArrayList<BitSet, Boolean> {

	public BooleanList() {
	}

	public BooleanList(int initialCapacity) {
		super(initialCapacity);
	}

	public BooleanList(BooleanList source) {
		elementData = (BitSet) source.elementData.clone();
		size        = source.size;
		modCount++;
	}
	
	@Override
	protected BitSet alloc(int count) {
		return new BitSet(count);
	}

	@Override
	protected BitSet copyOf(Boolean[] array, int newSize) {
		BitSet result = new BitSet(newSize);
		int idx = 0;
		for(Boolean i : array)
			result.set(idx++, i.booleanValue());
		return result;
	}

	@Override
	protected BitSet copyOf(BitSet original, int newLength) {
		return (BitSet) original.clone();
	}

	public void add(final boolean e) {
		modCount++;
		elementData.set(size++, e);
	}

	public void addIfUnique(final boolean e) {
		if (contains(e))
			return;
		modCount++;
		elementData.set(size++, e);
	}

	public void add(final int idx, final boolean e) {
		modCount++;
		for(int i = size; --i >= idx;)
			elementData.set(i + 1, elementData.get(i));
		size++;
		elementData.set(idx, e);
	}

	public boolean get(int i) {
		if(i > size || i < 0) throw new NoSuchElementException(Integer.toString(i));
		return elementData.get(i);
	}

	@Override
	public BooleanList clone() {
		return (BooleanList)super.clone();
	}

	@Override
	public void clear() {
		modCount++;
		size = 0;
		elementData.clear();
	}

	public void set(int i, boolean v) {
		elementData.set(i, v);
	}

	public boolean contains(boolean v) {
		for(int i = 0; i < size; i++)
			if(elementData.get(i) == v)
				return true;
		return false;
	}

	public int indexOf(boolean val ) {
		for(int i = 0; i < size; i++)
			if(elementData.get(i) == val)
				return i;
		return -1;
	}

	public boolean getFirst() {
		return get(0);
	}

	public boolean getLast() {
		return get(size - 1);
	}

	public byte[] toPackedByteArray() {
		byte[] result = new byte[(size + 7) / 8];
		for(int i = 0; i < size; i++)
			result[i >> 3] |= (elementData.get(i) ? 0x80 : 0) >> (i % 8);
		return result;
	}	
	
	@Override
	public void ensureCapacity(int minCapacity) {}
	
	@Override
	public Iterator<Boolean> iterator() {
		return new Iterator<Boolean>() {
			private int pos;
			
			@Override
			public boolean hasNext() {
				return pos < size;
			}

			@Override
			public Boolean next() {
				return get(pos++) ? Boolean.TRUE : Boolean.FALSE;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public boolean[] toBooleanArray() {
		boolean[] result = new boolean[size];
		for(int i = 0; i < size; i++)
			result[i] = elementData.get(i);
		return result;
	}
	
	@Override
	protected int getComponentSize() {
		return 0;
	}

	@Override
	protected void load(DataInputStream in) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void store(DataOutputStream out) {
		throw new UnsupportedOperationException();
	}
}
