package ch.ethz.util;

import ch.ethz.ether.geom.Vec3;
import ch.ethz.ether.geom.Vec4;

import java.util.Collection;

public interface IAddOnlyFloatList {
    void add(float value);

    void add(float value0, float value1);

    void add(float value0, float value1, float value2);

    void add(float value0, float value1, float value2, float value3);

    boolean add(float[] values);

    int size();

    boolean isEmpty();

    void ensureCapacity(int capacity);
}
