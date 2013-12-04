package ch.ethz.ether.render.util;

import java.nio.FloatBuffer;

import ch.ethz.util.IAddOnlyFloatList;

import com.jogamp.common.nio.Buffers;

public final class FloatList implements IAddOnlyFloatList {
    FloatBuffer buffer;

    public FloatList() {
        this(0);
    }

    public FloatList(int initialCapacity) {
        ensureCapacity(initialCapacity);
    }

    @Override
    public void add(float value) {
        ensureCapacity(buffer.limit() + 1);
        buffer.put(value);
    }

    @Override
    public void add(float value0, float value1) {
        ensureCapacity(buffer.limit() + 2);
        buffer.put(value0);
        buffer.put(value1);
    }

    @Override
    public void add(float value0, float value1, float value2) {
        ensureCapacity(buffer.limit() + 3);
        buffer.put(value0);
        buffer.put(value1);
        buffer.put(value2);
    }

    @Override
    public void add(float value0, float value1, float value2, float value3) {
        ensureCapacity(buffer.limit() + 4);
        buffer.put(value0);
        buffer.put(value1);
        buffer.put(value2);
        buffer.put(value3);
    }

    @Override
    public void addAll(float[] values) {
        if (values != null) {
            ensureCapacity(buffer.limit() + values.length);
            buffer.put(values);
        }
    }

    @Override
    public int size() {
        return buffer.limit();
    }

    @Override
    public boolean isEmpty() {
        return buffer.limit() == 0;
    }

    public void clear() {
        buffer.limit(0);
    }

    public FloatBuffer buffer() {
        return buffer;
    }

    public float[] toArray() {
        if (buffer.limit() == 0)
            return null;
        float[] array = new float[buffer.limit()];
        buffer.rewind();
        buffer.get(array);
        return array;
    }

    @Override
    public void ensureCapacity(int capacity) {
        if (buffer == null) {
            buffer = Buffers.newDirectFloatBuffer(capacity);
        } else if (buffer.capacity() < capacity) {
            FloatBuffer b = Buffers.newDirectFloatBuffer(capacity);
            buffer.rewind();
            b.put(buffer);
            buffer = b;
        }
        buffer.limit(capacity);
    }

}
