package ch.ethz.ether.model;

import ch.ethz.ether.geom.Mat4;
import ch.ethz.ether.geom.Vec3;

/**
 * Created by radar on 05/12/13.
 */
// MV = T * R * S * T-Origin
// MN = transposed((R * S)^-1)
public class Transform implements ITransformable {
    private Vec3 origin = Vec3.ZERO;
    private Vec3 translation = Vec3.ZERO;
    private Vec3 rotation = Vec3.ZERO;
    private Vec3 scale = Vec3.ONE;

    private Mat4 vertexTransform;
    private Mat4 normalTransform;

    @Override
    public Vec3 getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(Vec3 origin) {
        this.origin = origin;
    }

    @Override
    public Vec3 getTranslation() {
        return translation;
    }

    @Override
    public void setTranslation(Vec3 translation) {
        this.translation = translation;
        vertexTransform = null;
    }

    @Override
    public Vec3 getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vec3 rotation) {
        this.rotation = rotation;
        vertexTransform = null;
        normalTransform = null;
    }

    @Override
    public Vec3 getScale() {
        return scale;
    }

    @Override
    public void setScale(Vec3 scale) {
        this.scale = scale;
        vertexTransform = null;
        normalTransform = null;
    }

    public float[] transformVertices(float[] vertices) {
        validateVertexTransform(origin);
        return vertexTransform.transform(vertices);
    }

    public float[] transformNormals(float[] normals) {
        validateNormalTransform();
        return normalTransform.transform(normals);
    }

    private void validateVertexTransform(Vec3 origin) {
        if (vertexTransform == null) {
            vertexTransform = Mat4.identityMatrix();
            vertexTransform.translate(-origin.x, -origin.y, -origin.z);
            vertexTransform.scale(scale);
            vertexTransform.rotate(rotation.z, Vec3.Z);
            vertexTransform.rotate(rotation.y, Vec3.Y);
            vertexTransform.rotate(rotation.x, Vec3.X);
            vertexTransform.translate(translation);
        }
    }

    private void validateNormalTransform() {
        if (normalTransform == null) {
            normalTransform = Mat4.identityMatrix();
            normalTransform.scale(scale);
            normalTransform.rotate(rotation.z, Vec3.Z);
            normalTransform.rotate(rotation.y, Vec3.Y);
            normalTransform.rotate(rotation.x, Vec3.X);
            normalTransform = normalTransform.inverse();
            normalTransform = normalTransform.transposed();
        }
    }
}
