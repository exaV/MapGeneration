package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingVolume;
import ch.ethz.ether.geom.Vec3;

public interface IGeometry {
    BoundingVolume getBounds();

    Vec3 getOrigin();
}
