package ch.ethz.ether.render;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.util.UpdateRequest;

public final class GeometryGroups {
	private final UpdateRequest updater = new UpdateRequest();
	private final List<IGeometryGroup> groups = new ArrayList<IGeometryGroup>();
	
	public GeometryGroups() {
		
	}
	
	public void add(IGeometryGroup group) {
		if (!groups.contains(group)) {
			groups.add(group);
			updater.requestUpdate();
		}
	}
	
	public void remove(IGeometryGroup group) {
		if (groups.remove(group)) {
			updater.requestUpdate();
		}
	}
	
	List<IGeometryGroup> getGroups() {
		return groups;
	}
	
	boolean needsUpdate() {
		return updater.needsUpdate();
	}
}
