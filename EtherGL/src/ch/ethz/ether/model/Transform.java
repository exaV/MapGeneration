package ch.ethz.ether.model;

import ch.ethz.ether.geom.Vec3;

/**
 * Created by radar on 05/12/13.
 */
public class Transform implements ITransformable {
    private Vec3 translation = Vec3.ZERO;
    private Vec3 rotation = Vec3.ZERO;
    private Vec3 scale = Vec3.ONE;

    private float[] vertexTransform;
    private float[] normalTransform;

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

    public float[] transformVertices(Vec3 origin, float[] vertices) {
        validateVertexTransform(origin);
        return vertices;
    }

    public float[] transformNormals(float[] normals) {
        validateNormalTransform();
        return normals;
    }

    private void validateVertexTransform(Vec3 origin) {
        if (vertexTransform == null) {

        }
    }

    private void validateNormalTransform() {
        if (normalTransform == null) {

        }
    }
}
