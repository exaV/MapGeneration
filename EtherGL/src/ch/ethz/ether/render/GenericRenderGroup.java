package ch.ethz.ether.render;

import java.util.EnumSet;

import ch.ethz.util.IAddOnlyFloatList;

public class GenericRenderGroup extends AbstractRenderGroup {
    private float[] vertices;
    private float[] normals;
    private float[] colors;
    private float[] texCoords;
    private float pointSize;
    private float lineWidth;
    private ITextureData texData;

    public GenericRenderGroup(Source source, Type type) {
        super(source, type);
    }

    public GenericRenderGroup(Source source, Type type, Pass pass) {
        super(source, type, pass);
    }

    public GenericRenderGroup(Source source, Type type, Pass pass, EnumSet<Flag> flags) {
        super(source, type, pass, flags);
    }

    @Override
    public void getVertices(IAddOnlyFloatList dst) {
        dst.addAll(vertices);
    }

    @Override
    public void getNormals(IAddOnlyFloatList dst) {
        dst.addAll(normals);
    }

    @Override
    public void getColors(IAddOnlyFloatList dst) {
        dst.addAll(colors);
    }

    @Override
    public void getTexCoords(IAddOnlyFloatList dst) {
        dst.addAll(texCoords);
    }

    @Override
    public float getPointSize() {
        return pointSize;
    }

    @Override
    public float getLineWidth() {
        return lineWidth;
    }

    @Override
    public ITextureData getTextureData() {
        return texData;
    }

    public final void set(float[] vertices, float[] normals, float[] colors, float[] textCoords, float pointSize, float lineWidth, ITextureData texData) {
        this.vertices = vertices;
        this.normals = normals;
        this.colors = colors;
        this.texCoords = textCoords;
        this.pointSize = pointSize;
        this.lineWidth = lineWidth;
        this.texData = texData;
        requestUpdate();
    }
}
