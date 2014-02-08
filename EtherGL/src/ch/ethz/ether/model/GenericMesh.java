package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingBox;
import ch.ethz.ether.geom.PickUtil;
import ch.ethz.ether.geom.Vec3;
import ch.ethz.ether.view.IView;
import ch.ethz.util.IAddOnlyFloatList;

import java.util.Objects;

/**
 * Created by radar on 05/12/13.
 */
public class GenericMesh extends AbstractMesh {
    private class TransformCache {
        TransformCache() {
            triangleVertices = transform.transformVertices(GenericMesh.this.triangleVertices);
            triangleNormals = transform.transformNormals(GenericMesh.this.triangleNormals);
            edgeVertices = transform.transformVertices(GenericMesh.this.edgeVertices);
            pointVertices = transform.transformVertices(GenericMesh.this.pointVertices);
        }

        final float[] triangleVertices;
        final float[] triangleNormals;
        final float[] edgeVertices;
        final float[] pointVertices;
    }

    private BoundingBox bounds;
    private Transform transform = new Transform();
    private float[] triangleVertices;
    private float[] triangleNormals;
    private float[] triangleColors;
    private float[] edgeVertices;
    private float[] edgeColors;
    private float[] pointVertices;
    private float[] pointColors;

    private TransformCache cache;

    protected GenericMesh() {
        this(Vec3.ZERO);
    }

    protected GenericMesh(Vec3 origin) {
        transform.setOrigin(origin);
    }

    protected void setTriangles(float[] vertices, float[] normals, float[] colors) {
        triangleVertices = vertices;
        triangleNormals = normals;
        triangleColors = colors;
        invalidateCache();
    }

    protected void setEdges(float[] vertices, float[] colors) {
        edgeVertices = vertices;
        edgeColors = colors;
        invalidateCache();
    }

    protected void setPoints(float[] vertices, float[] colors) {
        pointVertices = vertices;
        pointColors = colors;
        invalidateCache();
    }

    @Override
    public BoundingBox getBounds() {
        validateCache();
        return bounds;
    }

    @Override
    public Vec3 getOrigin() {
        return transform.getOrigin();
    }

    @Override
	public void setOrigin(Vec3 origin) {
        transform.setOrigin(origin);
        invalidateCache();
    }

    @Override
    public Vec3 getTranslation() {
        return transform.getTranslation();
    }

    @Override
    public void setTranslation(Vec3 translation) {
        transform.setTranslation(translation);
        invalidateCache();
    }

    @Override
    public Vec3 getRotation() {
        return transform.getRotation();
    }

    @Override
    public void setRotation(Vec3 rotation) {
        transform.setRotation(rotation);
        invalidateCache();
    }

    @Override
    public Vec3 getScale() {
        return transform.getScale();
    }

    @Override
    public void setScale(Vec3 scale) {
        transform.setScale(scale);
        invalidateCache();
    }

    @Override
    public boolean pick(PickMode mode, int x, int y, int w, int h, IView view, IPickState state) {
        float z = PickUtil.pickBoundingBox(x, y, w, h, view, getBounds());
        if (z != Float.NaN) {
            // TODO: implement triangle / line / point picking
            state.add(z, this);
        }
        return false;
    }

    @Override
    public boolean getTriangleVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.triangleVertices);
    }

    @Override
    public boolean getTriangleNormals(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.triangleNormals);
    }

    @Override
    public boolean getTriangleColors(IAddOnlyFloatList dst) {
        return dst.add(triangleColors);
    }

    @Override
    public boolean getEdgeVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.edgeVertices);
    }

    @Override
    public boolean getEdgeColors(IAddOnlyFloatList dst) {
        Objects.requireNonNull(dst);
        return dst.add(edgeColors);
    }

    @Override
    public boolean getPointVertices(IAddOnlyFloatList dst) {
        validateCache();
        return dst.add(cache.pointVertices);
    }

    @Override
    public boolean getPointColors(IAddOnlyFloatList dst) {
        return dst.add(pointColors);
    }

    private void invalidateCache() {
        cache = null;
        bounds = null;
    }

    private void validateCache() {
        if (cache == null) {
            cache = new TransformCache();
            bounds = new BoundingBox();
            bounds.add(cache.triangleVertices);
            bounds.add(cache.edgeVertices);
            bounds.add(cache.pointVertices);
        }
    }
}
