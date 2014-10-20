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

package ch.fhnw.ether.render.gl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL3;
import javax.media.opengl.GL4;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * GLSL shader program abstraction.
 *
 * @author radar
 */
// TODO we currently just wrap around JOGLs GLSL helpers. should get rid non-GL code dependencies though...
public final class Program {
	public enum ShaderType {
		//@formatter:off
		VERTEX(GL3.GL_VERTEX_SHADER),
		TESS_CONTROL(GL4.GL_TESS_CONTROL_SHADER),
		TESS_EVAL(GL4.GL_TESS_EVALUATION_SHADER),
		GEOMETRY(GL3.GL_GEOMETRY_SHADER),
		FRAGMENT(GL3.GL_FRAGMENT_SHADER);
		//@formatter:on

		ShaderType(int glType) {
			this.glType = glType;
		}

		int getGLType() {
			return glType;
		}

		private final int glType;
	}

	public static final class Shader {
		private static final Map<String, Shader> SHADERS = new HashMap<>();

		private final Class<?> root;
		private final String path;
		private final ShaderType type;
		private final ShaderCode code;

		private Shader(GL3 gl, Class<?> root, String path, ShaderType type, PrintStream out) {
			this.root = root;
			this.path = path;
			this.type = type;
			this.code = ShaderCode.create(gl, type.getGLType(), 1, root, new String[] { path }, false);
			if (code == null)
				throw new IllegalArgumentException("could not create shader " + path);
			code.compile(gl, out);
		}

		public void dispose(GL3 gl) {
			code.destroy(gl);
			SHADERS.remove(key(root, path));
		}

		public Class<?> getRoot() {
			return root;
		}

		public String getPath() {
			return path;
		}

		public ShaderType getType() {
			return type;
		}

		public static Shader create(GL3 gl, Class<?> root, String path, ShaderType type, PrintStream out) {
			String key = key(root, path);
			Shader shader = SHADERS.get(key);
			if (shader == null) {
				shader = new Shader(gl, root, path, type, out);
				SHADERS.put(key, shader);
			}
			return shader;
		}

		private static String key(Class<?> root, String path) {
			return root.getName() + "/" + path;
		}
	}

	private static final Map<String, Program> PROGRAMS = new HashMap<>();

	private final ShaderProgram program = new ShaderProgram();
	private String id;

	public Program(GL3 gl, PrintStream out, Shader... shaders) {
		for (Shader shader : shaders) {
			program.add(gl, shader.code, out);
			id += shader.path + " ";
		}
		program.link(gl, out);
		program.validateProgram(gl, out);
	}

	/*
	 * NOTE: currently we do not plan to destroy / release programs public void dispose(GL3 gl) { program.release(gl); }
	 */

	public void enable(GL3 gl) {
		program.useProgram(gl, true);
	}

	public void disable(GL3 gl) {
		program.useProgram(gl, false);
	}

	public void setUniform(GL3 gl, int index, boolean value) {
		gl.glUniform1i(index, value ? 1 : 0);
	}

	public void setUniform(GL3 gl, int index, int value) {
		gl.glUniform1i(index, value);
	}

	public void setUniform(GL3 gl, int index, float value) {
		gl.glUniform1f(index, value);
	}

	public void setUniformVec4(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniform4fv(index, 1, value, 0);
	}

	public void setUniformMat4(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniformMatrix4fv(index, 1, false, value, 0);
	}

	public void setUniformSampler(GL3 gl, int index, int unit) {
		setUniform(gl, index, unit);
	}

	public int getAttributeLocation(GL3 gl, String name) {
		return gl.glGetAttribLocation(program.program(), name);
	}

	public int getUniformLocation(GL3 gl, String name) {
		return gl.glGetUniformLocation(program.program(), name);
	}

	@Override
	public String toString() {
		return id;
	}

	public static Program create(GL3 gl, Class<?> root, String vertexShader, String fragmentShader, PrintStream out) {
		String key = key(root, vertexShader, fragmentShader);
		Program program = PROGRAMS.get(key);
		if (program == null) {
			Shader vert = Shader.create(gl, root, vertexShader, ShaderType.VERTEX, out);
			Shader frag = Shader.create(gl, root, fragmentShader, ShaderType.FRAGMENT, out);
			program = new Program(gl, out, vert, frag);
			PROGRAMS.put(key, program);
		}
		return program;
	}

	private static String key(Class<?> root, String path0, String path1) {
		return root.getName() + "/" + path0 + ":" + path1;
	}
}
