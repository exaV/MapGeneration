package ch.ethz.ether.render;

import java.util.EnumSet;

import ch.ethz.util.FloatList;
import ch.ethz.util.UpdateRequest;

public abstract class AbstractRenderGroup implements IRenderGroup {
	private EnumSet<Element> elements = EnumSet.noneOf(Element.class);
	private EnumSet<Appearance> appearances = EnumSet.noneOf(Appearance.class);
	private UpdateRequest updater = new UpdateRequest();
	
	protected AbstractRenderGroup() {	
	}
	
	protected AbstractRenderGroup(EnumSet<Element> elements, EnumSet<Appearance> appearances) {
		this.elements = elements;
		this.appearances = appearances;
	}
	
	@Override
	public final boolean containsElement(Element element) {
		return elements.contains(element);
	}
	
	public final void setElements(EnumSet<Element> elements) {
		this.elements = elements;
	}
	
	public final void addElement(Element element) {
		elements.add(element);
	}
	
	public final void removeElement(Element element) {
		elements.remove(element);
	}
	
	@Override
	public final boolean containsAppearance(Appearance appearance) {
		return appearances.contains(appearance);
	}

	public final void setAppearances(EnumSet<Appearance> appearances) {
		this.appearances = appearances;
	}

	public final void addAppearance(Appearance appearance) {
		appearances.add(appearance);
	}
	
	public final void removeAppearance(Appearance appearance) {
		appearances.remove(appearance);
	}
	
	@Override
	public final void requestUpdate() {
		updater.requestUpdate();
	}
	
	@Override
	public final boolean needsUpdate() {
		return updater.needsUpdate();
	}

	// all operations unsupported 
	@Override
	public void getPointVertices(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getPointColors(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getPointSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getLineVertices(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getLineColors(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getLineWidth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getTriangleVertices(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getTriangleColors(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getTriangleNormals(FloatList dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void getTriangleTexCoords(FloatList dst) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ITextureData getTriangleTexData() {
		throw new UnsupportedOperationException();
	}
}
