package ch.ethz.ether.model;

import ch.ethz.util.IAddOnlyFloatList;

/**
 * Created by radar on 05/12/13.
 */
public interface IEdgeProvider {
    boolean getEdgeVertices(IAddOnlyFloatList dst);

    boolean getEdgeColors(IAddOnlyFloatList dst);
}
