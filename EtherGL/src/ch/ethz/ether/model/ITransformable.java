package ch.ethz.ether.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by radar on 05/12/13.
 */
public interface ITransformable {
    Vector3D getTranslation();
    void setTranslation(Vector3D translation);

    Vector3D getRotation();
    void setRotation(Vector3D rotation);

    Vector3D getScale();
    void setScale(Vector3D scale);
}
