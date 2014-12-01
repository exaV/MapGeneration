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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL3;
import javax.media.opengl.GL4;

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

	private static final class Shader {
		static final Map<String, Shader> SHADERS = new HashMap<>();

		final Class<?> root;
		final String path;
		final ShaderType type;
		int shaderObject;

		Shader(GL3 gl, Class<?> root, String path, ShaderType type, PrintStream out) {
			this.root = root;
			this.path = path;
			this.type = type;

			String content = "";
			try (BufferedReader in = new BufferedReader(new InputStreamReader(root.getResourceAsStream(path)))) {
				String line;
				while ((line = in.readLine()) != null)
					content += line + "\n";
			} catch (Exception e) {
				e.printStackTrace(out);
				out.println("Failed to read shader: " + this);
				throw new IllegalArgumentException(toString());
			}

			shaderObject = gl.glCreateShader(type.glType);

			gl.glShaderSource(shaderObject, 1, new String[] { content }, new int[] { content.length() }, 0);			
			gl.glCompileShader(shaderObject);

			if (!checkStatus(gl, shaderObject, GL3.GL_COMPILE_STATUS, out)) {
				out.println("Failed to compile shader: " + this);
				throw new IllegalArgumentException(toString());
			}
		}
		
		@Override
		public String toString() {
			return root + "/" + path + " " + type;
		}

		static Shader create(GL3 gl, Class<?> root, String path, ShaderType type, PrintStream out) {
			String key = key(root, path);
			Shader shader = SHADERS.get(key);
			if (shader == null) {
				shader = new Shader(gl, root, path, type, out);
				SHADERS.put(key, shader);
			}
			return shader;
		}

		static String key(Class<?> root, String path) {
			return root.getName() + "/" + path;
		}
	}

	private static final Map<String, Program> PROGRAMS = new HashMap<>();

	private final String id;
	private final int programObject;

	private Program(GL3 gl, PrintStream out, Shader... shaders) {
		programObject = gl.glCreateProgram();

		String id = "";
		for (Shader shader : shaders) {
			if (shader != null) {
				gl.glAttachShader(programObject, shader.shaderObject);
				id += shader.path + " ";
			}
		}
		this.id = id;

		gl.glLinkProgram(programObject);
		if (!checkStatus(gl, programObject, GL3.GL_LINK_STATUS, out)) {
			out.println("Failed to link program: " + this);
			throw new IllegalArgumentException(toString());
		}

		gl.glValidateProgram(programObject);
		if (!checkStatus(gl, programObject,  GL3.GL_VALIDATE_STATUS, out)) {
			out.println("Failed to validate program: " + this);
			throw new IllegalArgumentException(toString());		}
	}

	// NOTE: currently we do not plan to dispose programs
	// public void dispose(GL3 gl) {
	//   dispose each shader & delete program
	// }

	public void enable(GL3 gl) {
		gl.glUseProgram(programObject);
	}

	public void disable(GL3 gl) {
		gl.glUseProgram(0);
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

	public void setUniformVec2(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniform2fv(index, 1, value, 0);
	}

	public void setUniformVec3(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniform3fv(index, 1, value, 0);
	}

	public void setUniformVec4(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniform4fv(index, 1, value, 0);
	}

	public void setUniformMat3(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniformMatrix3fv(index, 1, false, value, 0);
	}

	public void setUniformMat4(GL3 gl, int index, float[] value) {
		if (value != null)
			gl.glUniformMatrix4fv(index, 1, false, value, 0);
	}

	public void setUniformSampler(GL3 gl, int index, int unit) {
		setUniform(gl, index, unit);
	}

	public int getAttributeLocation(GL3 gl, String name) {
		return gl.glGetAttribLocation(programObject, name);
	}

	public int getUniformLocation(GL3 gl, String name) {
		return gl.glGetUniformLocation(programObject, name);
	}

	public int getUniformBlockIndex(GL3 gl, String name) {
		return gl.glGetUniformBlockIndex(programObject, name);
	}

	public void bindUniformBlock(GL3 gl, int index, int bindingPoint) {
		gl.glUniformBlockBinding(programObject, index, bindingPoint);
	}

	@Override
	public String toString() {
		return id;
	}

	public static Program create(GL3 gl, Class<?> root, String vertShader, String fragShader, String geomShader, PrintStream out) {
		String key = key(root, vertShader, fragShader, geomShader);
		Program program = PROGRAMS.get(key);
		if (program == null) {
			Shader vert = Shader.create(gl, root, vertShader, ShaderType.VERTEX, out);
			Shader frag = Shader.create(gl, root, fragShader, ShaderType.FRAGMENT, out);
			Shader geom = null;
			if (geomShader != null && root.getResource(geomShader) != null) {
				geom = Shader.create(gl, root, geomShader, ShaderType.GEOMETRY, out);
			}
			program = new Program(gl, out, vert, frag, geom);
			PROGRAMS.put(key, program);
		}
		return program;
	}

	private static String key(Class<?> root, String... paths) {
		String key = root.getName();
		for (String path : paths) {
			if (path != null)
				key += ":" + path;
		}
		return key;
	}

	private static boolean checkStatus(GL3 gl3, int object, int statusType, PrintStream out) {
		int[] status = { 0 };

		if (statusType == GL3.GL_COMPILE_STATUS)
			gl3.glGetShaderiv(object, statusType, status, 0);
		else if (statusType == GL3.GL_LINK_STATUS || statusType == GL3.GL_VALIDATE_STATUS)
			gl3.glGetProgramiv(object, statusType, status, 0);

		if (status[0] != 1) {
			out.println("status: " + status[0]);
			gl3.glGetShaderiv(object, GL3.GL_INFO_LOG_LENGTH, status, 0);
			byte[] infoLog = new byte[status[0]];
			if (statusType == GL3.GL_COMPILE_STATUS)
				gl3.glGetShaderInfoLog(object, status[0], status, 0, infoLog, 0);
			else if (statusType == GL3.GL_LINK_STATUS)
				gl3.glGetProgramInfoLog(object, status[0], status, 0, infoLog, 0);
			out.println(new String(infoLog));
			return false;
		}
		return true;
	}
}
