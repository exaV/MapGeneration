package ch.ethz.ether.model;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/**
 * Created by radar on 05/12/13.
 */
public class Transform implements ITransformable {
    private Vector3D translation = Vector3D.ZERO;
    private Vector3D rotation = Vector3D.ZERO;
    private Vector3D scale = new Vector3D(1, 1, 1);

    private float[] vertexTransform;
    private float[] normalTransform;

    @Override
    public Vector3D getTranslation() {
        return translation;
    }

    @Override
    public void setTranslation(Vector3D translation) {
        this.translation = translation;
        vertexTransform = null;
    }

    @Override
    public Vector3D getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
        vertexTransform = null;
        normalTransform = null;
    }

    @Override
    public Vector3D getScale() {
        return scale;
    }

    @Override
    public void setScale(Vector3D scale) {
        this.scale = scale;
        vertexTransform = null;
        normalTransform = null;
    }

    public float[] transformVertices(Vector3D origin, float[] vertices) {
        validateVertexTransform(origin);
        return vertices;
    }

    public float[] transformNormals(float[] normals) {
        validateNormalTransform();
        return normals;
    }

    private void validateVertexTransform(Vector3D origin) {
        if (vertexTransform == null) {

        }
    }

    private void validateNormalTransform() {
        if (normalTransform == null) {

        }
    }
}
