package ch.ethz.fcl.ether.render;

import java.util.EnumSet;

import ch.ethz.fcl.util.UpdateRequest;

public abstract class AbstractRenderGroup implements IRenderGroup {
	private EnumSet<Element> elements = EnumSet.noneOf(Element.class);
	private EnumSet<Appearance> appearances = EnumSet.noneOf(Appearance.class);
	private UpdateRequest updater = new UpdateRequest();
	
	@Override
	public final boolean containsElement(Element element) {
		return elements.contains(element);
	}
	
	protected void addElement(Element element) {
		elements.add(element);
	}
	
	protected void removeElement(Element element) {
		elements.remove(element);
	}
	
	@Override
	public final boolean containsAppearance(Appearance appearance) {
		return appearances.contains(appearance);
	}

	protected void addAppearance(Appearance appearance) {
		appearances.add(appearance);
	}
	
	protected void removeAppearance(Appearance appearance) {
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
}
