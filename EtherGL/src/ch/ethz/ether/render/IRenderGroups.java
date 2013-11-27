package ch.ethz.ether.render;

import ch.ethz.ether.render.IRenderGroup.Source;

public interface IRenderGroups {
	public static final class Factory {
		public static IRenderGroups create() {
			return new RenderGroups();
		}
	}
	
	void add(IRenderGroup group);
	void remove(IRenderGroup group);
	
	void setSource(Source source);
}
