package ch.ethz.ether.model;

import ch.ethz.util.IAddOnlyFloatList;

/**
 * Created by radar on 05/12/13.
 */
public interface ITriangleProvider {
    boolean getTriangleVertices(IAddOnlyFloatList dst);

    boolean getTriangleNormals(IAddOnlyFloatList dst);

    boolean getTriangleColors(IAddOnlyFloatList dst);
}
