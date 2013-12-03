package ch.ethz.ether.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL3;

import ch.ethz.ether.render.IRenderGroup.Flag;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderer.IRenderGroups;
import ch.ethz.ether.render.util.FloatList;
import ch.ethz.ether.view.IView;
import ch.ethz.ether.view.IView.ViewType;
import ch.ethz.util.UpdateRequest;

final class RenderGroups implements IRenderGroups {
	private final UpdateRequest updater = new UpdateRequest();

	private final List<IRenderGroup> added = new ArrayList<>();
	private final List<IRenderGroup> removed = new ArrayList<>();

	private final Map<IRenderGroup, IRenderEntry> groups = new HashMap<>();
	private EnumSet<Source> sources = Source.ALL_SOURCES;

	private final FloatList data = new FloatList();

	public RenderGroups() {

	}

	@Override
	public void add(IRenderGroup group) {
		removed.remove(group);
		if (!groups.containsKey(group)) {
			added.add(group);
			group.requestUpdate();
			group.requestTextureUpdate();
			updater.requestUpdate();
		}
	}

	@Override
	public void add(IRenderGroup group, IRenderGroup... groups) {
		add(group);
		for (IRenderGroup g : groups)
			add(g);
	}

	@Override
	public void remove(IRenderGroup group) {
		added.remove(group);
		if (groups.containsKey(group) & !removed.contains(group)) {
			removed.add(group);
			updater.requestUpdate();
		}
	}

	@Override
	public void remove(IRenderGroup group, IRenderGroup... groups) {
		remove(group);
		for (IRenderGroup g : groups)
			remove(g);
	}

	@Override
	public void setSource(Source source) {
		if (source != null)
			sources = EnumSet.of(source);
		else
			sources = Source.ALL_SOURCES;
	};

	void update(GL3 gl, IRenderer renderer) {
		if (updater.needsUpdate()) {
			// update added / removed groups
			for (IRenderGroup group : added) {
				groups.put(group, renderer.getEntry(gl, group));
			}
			added.clear();

			for (IRenderGroup group : removed) {
				IRenderEntry entry = groups.remove(group);
				entry.dispose(gl);
			}
			removed.clear();
		}
	}

	void render(GL3 gl, IRenderer renderer, IView view, float[] projMatrix, float[] viewMatrix, Pass pass) {
		for (IRenderEntry entry : groups.values()) {
			if (!sources.contains(entry.getGroup().getSource()))
				continue;
			if (entry.getGroup().containsFlag(Flag.INTERACTIVE_VIEW_ONLY) && view.getViewType() != ViewType.INTERACTIVE_VIEW)
				continue;
			if (entry.getGroup().getPass() == pass) {
				entry.update(gl, renderer, view, data);
				entry.render(gl, renderer, view, projMatrix, viewMatrix);
			}
		}
	}
}
