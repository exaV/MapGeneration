package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingVolume;
import ch.ethz.util.IAddOnlyFloatList;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Map;

/**
 * Created by radar on 05/12/13.
 */
public abstract class AbstractMesh implements IMesh {
    private class TransformCache {
        TransformCache() {

        }

        float[] triangleVertices;
        float[] triangleNormals;
        float[] edgeVertices;
        float[] pointVertices;
    }

    private BoundingVolume bounds;
    private Vector3D origin;
    private Transform transform = new Transform();
    private float[] triangleVertices;
    private float[] triangleNormals;
    private float[] triangleColors;
    private float[] edgeVertices;
    private float[] edgeColors;
    private float[] pointVertices;
    private float[] pointColors;

    private TransformCache cache;

    protected AbstractMesh() {
        this.origin = Vector3D.ZERO;
    }

    protected AbstractMesh(Vector3D origin) {
        this.origin = origin;
    }

    protected void setTriangles(float[] vertices, float[] normals, float[] colors) {
        triangleVertices = vertices;
        triangleNormals = normals;
        triangleColors = colors;

    }

    protected void setEdges(float[] vertices, float[] colors) {
        edgeVertices = vertices;
        edgeColors = colors;
    }

    protected void setPoints(float[] vertices, float[] colors) {
        pointVertices = vertices;
        pointColors = colors;
    }

    @Override
    public BoundingVolume getBounds() {
        validateBounds();
        return bounds;
    }

    @Override
    public Vector3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3D origin) {
        this.origin = origin;
    }

    @Override
    public Vector3D getTranslation() {
        return null;
    }

    @Override
    public void setTranslation(Vector3D translation) {

    }

    @Override
    public Vector3D getRotation() {
        return null;
    }

    @Override
    public void setRotation(Vector3D rotation) {

    }

    @Override
    public Vector3D getScale() {
        return null;
    }

    @Override
    public void setScale(Vector3D scale) {

    }

    @Override
    public boolean pick(int x, int y, float[] viewMatrix, float[] projMatrix, Map<Float, IGeometry> geometries) {
        return false;
    }

    @Override
    public boolean getTriangleVertices(IAddOnlyFloatList dst) {
        validateTransformCache();
        return dst.addAll(cache.triangleVertices);
    }

    @Override
    public boolean getTriangleNormals(IAddOnlyFloatList dst) {
        validateTransformCache();
        return dst.addAll(cache.triangleNormals);
    }

    @Override
    public boolean getTriangleColors(IAddOnlyFloatList dst) {
        return dst.addAll(triangleColors);
    }

    @Override
    public boolean getEdgeVertices(IAddOnlyFloatList dst) {
        return dst.addAll(cache.edgeVertices);
    }

    @Override
    public boolean getEdgeColors(IAddOnlyFloatList dst) {
        return dst.addAll(edgeColors);
    }

    @Override
    public boolean getPointVertices(IAddOnlyFloatList dst) {
        return dst.addAll(cache.pointVertices);
    }

    @Override
    public boolean getPointColors(IAddOnlyFloatList dst) {
        return dst.addAll(pointColors);
    }

    private void clearTransformCache() {
        cache = null;
    }

    private void validateTransformCache() {
        if (cache == null) {
            cache = new TransformCache();
        }
    }

    private void clearBounds() {
        bounds = null;
    }

    private void validateBounds() {
        if (bounds == null) {
            bounds = new BoundingVolume();
            bounds.add(cache.triangleVertices);
            bounds.add(cache.edgeVertices);
            bounds.add(cache.pointVertices);
        }
    }
}
