package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingBox;
import ch.ethz.ether.geom.Vec3;

public interface IGeometry {
    Object getContainer();

    BoundingBox getBounds();

    Vec3 getOrigin();
}
