/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.render.shader.base;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.gl.Program;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;

public abstract class AbstractShader implements IShader {
	// important: keep this in sync with PrimitiveType enum
	public static final int[] MODE = { GL.GL_POINTS, GL.GL_LINES, GL.GL_TRIANGLES };

	private Class<?> root;
	private String name;
	private String source;
	private Primitive type;
	private Program program;

	private List<IUniformAttribute<?>> uniforms = new ArrayList<>();
	private List<IArrayAttribute<?>> arrays = new ArrayList<>();

	protected AbstractShader(Class<?> root, String name, String source, Primitive type) {
		this.root = root;
		this.name = name;
		this.source = source;
		this.type = type;
	}

	@Override
	public void dispose(GL3 gl) {
		uniforms.forEach((t) -> t.dispose(gl));
		arrays.forEach((t) -> t.dispose(gl));

		name = name + " (disposed)";
		source = null;
		type = null;
		program = null;

		uniforms = null;
		arrays = null;
	}

	@Override
	public final void update(GL3 gl) {
		if (program == null) {
			String vertShader = "glsl/" + source + "_vert.glsl";
			String fragShader = "glsl/" + source + "_frag.glsl";
			String geomShader = "glsl/" + source + "_geom.glsl";
			program = Program.create(gl, root, vertShader, fragShader, geomShader, System.err);
		}
	}

	@Override
	public void enable(GL3 gl) {
		program.enable(gl);
	}

	@Override
	public void render(GL3 gl, int count) {
		int mode = MODE[type.ordinal()];
		gl.glDrawArrays(mode, 0, count);
	}

	@Override
	public void disable(GL3 gl) {
		program.disable(gl);
	}

	@Override
	public final Program getProgram() {
		return program;
	}

	@Override
	public final Primitive getPrimitiveType() {
		return type;
	}

	@Override
	public List<IUniformAttribute<?>> getUniforms() {
		return uniforms;
	}

	@Override
	public List<IArrayAttribute<?>> getArrays() {
		return arrays;
	}

	protected final void addUniform(IUniformAttribute<?> uniform) {
		uniforms.add(uniform);
	}

	protected final void addArray(IArrayAttribute<?> array) {
		arrays.add(array);
	}

	@Override
	public String toString() {
		return name + "[uniforms:" + uniforms + " array attribs:" + arrays + "]";
	}

}
