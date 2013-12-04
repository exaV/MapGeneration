package ch.ethz.ether.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import ch.ethz.ether.geom.BoundingVolume;

public interface IGeometry {
    BoundingVolume getBounds();

    Vector3D getOrigin();
}
