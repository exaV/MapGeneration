package ch.ethz.ether.render;

import javax.media.opengl.GL3;

import ch.ethz.ether.gl.Program;

public abstract class AbstractRenderEntry implements IRenderEntry {
	private Program program;
	private IRenderGroup group;

	protected AbstractRenderEntry(Program program, IRenderGroup group) {
		this.program = program;
		this.group = group;
	}

	@Override
	public void dispose(GL3 gl) {
		// caution: we do not dispose the program
		program = null;
		group = null;
	}

	@Override
	public Program getProgram() {
		return program;
	}

	@Override
	public final IRenderGroup getGroup() {
		return group;
	}
}
