package ch.ethz.ether.model;

import java.util.Map;

/**
 * Created by radar on 05/12/13.
 */
public interface IPickable {
    boolean pick(int x, int y, float[] viewMatrix, float[] projMatrix, Map<Float, IGeometry> geometries);
}
