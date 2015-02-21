/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

import ch.fhnw.ether.render.gl.GLObject.Type;
import ch.fhnw.ether.render.shader.IShader;

/**
 * GLSL shader program abstraction.
 *
 * @author radar
 */
// NOTE: currently we do not plan to dispose programs
public final class Program {
	public enum ShaderType {
		//@formatter:off
		COMPUTE(GL43.GL_COMPUTE_SHADER),
		VERTEX(GL20.GL_VERTEX_SHADER),
		TESS_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
		TESS_EVAL(GL40.GL_TESS_EVALUATION_SHADER),
		GEOMETRY(GL32.GL_GEOMETRY_SHADER),
		FRAGMENT(GL20.GL_FRAGMENT_SHADER);
		//@formatter:on

		ShaderType(int glType) {
			this.glType = glType;
		}

		int getGLType() {
			return glType;
		}

		private final int glType;
	}

	// FIXME: library is currently hardcoded
	private final static URL LIBRARY = IShader.class.getResource("glsl/lib");

	private static final class Shader {
		static final Map<String, Shader> SHADERS = new HashMap<>();

		final Class<?> root;
		final String path;
		int shaderObject;

		Shader(Class<?> root, String path, ShaderType type, PrintStream out) throws IOException {
			this.root = root;
			this.path = path;

			StringBuilder code = new StringBuilder();
			URL url = root.getResource(path);
			if (url == null) {
				out.println("file not found: " + this);
				throw new FileNotFoundException("file not found: " + this);
			}
			new GLSLReader(LIBRARY, url, code, out);

			shaderObject = GL20.glCreateShader(type.glType);

			GL20.glShaderSource(shaderObject, code.toString());
			GL20.glCompileShader(shaderObject);

			if (!checkStatus(shaderObject, GL20.GL_COMPILE_STATUS, out)) {
				out.println("failed to compile shader: " + this);
				throw new IllegalArgumentException("failed to compile shader: " + this);
			}
		}

		@Override
		public String toString() {
			return root.getSimpleName() + ":" + path;
		}

		static Shader create(Class<?> root, String path, ShaderType type, PrintStream out) throws IOException {
			String key = key(root, path);
			Shader shader = SHADERS.get(key);
			if (shader == null) {
				shader = new Shader(root, path, type, out);
				SHADERS.put(key, shader);
			}
			return shader;
		}

		static String key(Class<?> root, String path) {
			return root.hashCode() + "/" + path;
		}
	}

	private static final Map<String, Program> PROGRAMS = new HashMap<>();

	private final String id;
	private final GLObject programObject;

	private Program(PrintStream out, Shader... shaders) {
		programObject = new GLObject(Type.PROGRAM);

		String id = "";
		for (Shader shader : shaders) {
			if (shader != null) {
				GL20.glAttachShader(programObject.id(), shader.shaderObject);
				id += shader.path + " ";
			}
		}
		this.id = id;

		GL20.glLinkProgram(programObject.id());
		if (!checkStatus(programObject.id(), GL20.GL_LINK_STATUS, out)) {
			out.println("failed to link program: " + this);
			throw new IllegalArgumentException("failed to link program: " + this);
		}

		GL20.glValidateProgram(programObject.id());
		if (!checkStatus(programObject.id(), GL20.GL_VALIDATE_STATUS, out)) {
			out.println("failed to validate program: " + this);
			throw new IllegalArgumentException("failed to validate program: " + this);
		}
	}

	public void enable() {
		GL20.glUseProgram(programObject.id());
	}

	public void disable() {
		GL20.glUseProgram(0);
	}

	public void setUniform(int index, boolean value) {
		GL20.glUniform1i(index, value ? 1 : 0);
	}

	public void setUniform(int index, int value) {
		GL20.glUniform1i(index, value);
	}

	public void setUniform(int index, float value) {
		GL20.glUniform1f(index, value);
	}

	public void setUniformVec2(int index, float[] value) {
		if (value != null)
			GL20.glUniform2f(index, value[0], value[1]);
	}

	public void setUniformVec3(int index, float[] value) {
		if (value != null)
			GL20.glUniform3f(index, value[0], value[1], value[2]);
	}

	public void setUniformVec4(int index, float[] value) {
		if (value != null)
			GL20.glUniform4f(index, value[0], value[1], value[2], value[3]);
	}

	private static final ThreadLocal<FloatBuffer> MAT3_BUFFER = ThreadLocal.withInitial(() -> BufferUtils.createFloatBuffer(9));

	public void setUniformMat3(int index, float[] value) {
		if (value != null) {
			FloatBuffer buffer = MAT3_BUFFER.get();
			buffer.clear();
			buffer.put(value);
			buffer.rewind();
			GL20.glUniformMatrix3(index, false, buffer);
		}
	}

	private static final ThreadLocal<FloatBuffer> MAT4_BUFFER = ThreadLocal.withInitial(() -> BufferUtils.createFloatBuffer(16));

	public void setUniformMat4(int index, float[] value) {
		if (value != null) {
			FloatBuffer buffer = MAT4_BUFFER.get();
			buffer.clear();
			buffer.put(value);
			buffer.rewind();
			GL20.glUniformMatrix4(index, false, buffer);
		}
	}

	public void setUniformSampler(int index, int unit) {
		setUniform(index, unit);
	}

	public int getAttributeLocation(String name) {
		return GL20.glGetAttribLocation(programObject.id(), name);
	}

	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programObject.id(), name);
	}

	public int getUniformBlockIndex(String name) {
		return GL31.glGetUniformBlockIndex(programObject.id(), name);
	}

	public void bindUniformBlock(int index, int bindingPoint) {
		GL31.glUniformBlockBinding(programObject.id(), index, bindingPoint);
	}

	@Override
	public String toString() {
		return id;
	}

	public static Program create(Class<?> root, String vertShader, String fragShader, String geomShader, PrintStream out) throws IOException {
		String key = key(root, vertShader, fragShader, geomShader);
		Program program = PROGRAMS.get(key);
		if (program == null) {
			Shader vert = Shader.create(root, vertShader, ShaderType.VERTEX, out);
			Shader frag = Shader.create(root, fragShader, ShaderType.FRAGMENT, out);
			Shader geom = null;
			if (geomShader != null && root.getResource(geomShader) != null) {
				geom = Shader.create(root, geomShader, ShaderType.GEOMETRY, out);
			}
			program = new Program(out, vert, frag, geom);
			PROGRAMS.put(key, program);
		}
		return program;
	}

	private static String key(Class<?> root, String... paths) {
		String key = "" + root.hashCode();
		for (String path : paths) {
			if (path != null)
				key += ":" + path;
		}
		return key;
	}

	private static boolean checkStatus(int object, int statusType, PrintStream out) {
		int status = 0;

		if (statusType == GL20.GL_COMPILE_STATUS)
			status = GL20.glGetShaderi(object, statusType);
		else if (statusType == GL20.GL_LINK_STATUS || statusType == GL20.GL_VALIDATE_STATUS)
			status = GL20.glGetProgrami(object, statusType);

		if (status == 1)
			return true;

		if (statusType == GL20.GL_COMPILE_STATUS)
			out.println(GL20.glGetShaderInfoLog(object));
		else if (statusType == GL20.GL_LINK_STATUS)
			out.println(GL20.glGetProgramInfoLog(object));
		return false;
	}
}
