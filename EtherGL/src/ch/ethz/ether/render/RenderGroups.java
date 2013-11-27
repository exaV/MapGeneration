package ch.ethz.ether.render;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import ch.ethz.ether.gl.VBO;
import ch.ethz.ether.render.IRenderGroup.Flag;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.util.FloatList;
import ch.ethz.ether.view.IView;
import ch.ethz.ether.view.IView.ViewType;
import ch.ethz.util.UpdateRequest;

final class RenderGroups implements IRenderGroups {
	class RenderEntry {
		RenderEntry(IRenderGroup group, VBO vbo) {
			this.group = group;
			this.vbo = vbo;
			switch (group.getType()) {
			case POINTS:
				mode = GL.GL_POINTS;
				break;
			case LINES:
				mode = GL.GL_LINES;
				break;
			case TRIANGLES:
				mode = GL.GL_TRIANGLES;
				break;
			default:
				throw new IllegalStateException();
			}
		}

		void render(GL2 gl) {
			gl.glColor4fv(group.getColor(), 0);
			if (mode == GL.GL_LINES)
				gl.glLineWidth(group.getLineWidth());
			else if (mode == GL.GL_POINTS)
				gl.glPointSize(group.getPointSize());
			vbo.render(gl, mode);
		}

		final IRenderGroup group;
		final VBO vbo;
		final int mode;
	}

	private final UpdateRequest updater = new UpdateRequest();

	private final List<IRenderGroup> added = new ArrayList<IRenderGroup>();
	private final List<IRenderGroup> removed = new ArrayList<IRenderGroup>();

	private final Map<IRenderGroup, RenderEntry> groups = new HashMap<IRenderGroup, RenderEntry>();
	private EnumSet<Source> sources = Source.ALL_SOURCES;

	private final FloatList vertices = new FloatList();
	private final FloatList normals = new FloatList();
	private final FloatList colors = new FloatList();
	private final FloatList texCoords = new FloatList();

	public RenderGroups() {

	}

	// TODO: Proper dispose...

	@Override
	public void add(IRenderGroup group) {
		if (!groups.containsKey(group)) {
			added.add(group);
			group.requestUpdate();
			updater.requestUpdate();
		}
	}

	@Override
	public void remove(IRenderGroup group) {
		if (groups.containsKey(group) & !removed.contains(group)) {
			removed.add(group);
			updater.requestUpdate();
		}
	}

	@Override
	public void setSource(Source source) {
		if (source != null)
			sources = EnumSet.of(source);
		else
			sources = Source.ALL_SOURCES;
	};

	void update(GL2 gl) {
		if (updater.needsUpdate()) {
			// update added / removed groups
			for (IRenderGroup group : added) {
				groups.put(group, new RenderEntry(group, new VBO(gl)));
			}
			added.clear();

			for (IRenderGroup group : removed) {
				RenderEntry entry = groups.remove(group);
				if (entry != null) {
					entry.vbo.dispose(gl);
				}
			}
			removed.clear();
		}

		for (RenderEntry entry : groups.values()) {
			if (entry.group.needsUpdate()) {
				VBO vbo = entry.vbo;

				vertices.clear();
				entry.group.getVertices(vertices);
				if (vertices.isEmpty()) {
					vbo.clear(gl);
					continue;
				}

				int numVertices = vertices.size() / 3;

				normals.clear();
				entry.group.getNormals(normals);

				colors.clear();
				entry.group.getColors(colors);

				texCoords.clear();
				entry.group.getTexCoords(texCoords);

				vbo.load(gl, numVertices, vertices.buffer(), normals.buffer(), colors.buffer(), texCoords.buffer());
			}
		}
	}

	void render(GL2 gl, IView view, Pass pass) {
		for (RenderEntry entry : groups.values()) {
			if (!sources.contains(entry.group.getSource()))
				continue;
			if (entry.group.containsFlag(Flag.INTERACTIVE_VIEW_ONLY) && view.getViewType() != ViewType.INTERACTIVE_VIEW)
				continue;
			if (entry.group.getPass() == pass)
				entry.render(gl);
		}
	}
}
