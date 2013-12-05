package ch.ethz.ether.model;

import ch.ethz.ether.geom.Vec3;

/**
 * Created by radar on 05/12/13.
 */
public interface ITransformable {
    Vec3 getOrigin();
    void setOrigin(Vec3 origin);

    Vec3 getTranslation();
    void setTranslation(Vec3 translation);

    Vec3 getRotation();
    void setRotation(Vec3 rotation);

    Vec3 getScale();
    void setScale(Vec3 scale);
}
