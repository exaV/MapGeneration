package ch.ethz.util;

public interface IAddOnlyFloatList {
    void add(float value);

    void add(float value0, float value1);

    void add(float value0, float value1, float value2);

    void add(float value0, float value1, float value2, float value3);

    void addAll(float[] values);

    int size();

    boolean isEmpty();

    void ensureCapacity(int capacity);
}
