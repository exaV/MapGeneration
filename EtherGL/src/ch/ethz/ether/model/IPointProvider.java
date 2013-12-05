package ch.ethz.ether.model;

import ch.ethz.util.IAddOnlyFloatList;

/**
 * Created by radar on 05/12/13.
 */
public interface IPointProvider {
    boolean getPointVertices(IAddOnlyFloatList dst);

    boolean getPointColors(IAddOnlyFloatList dst);
}
