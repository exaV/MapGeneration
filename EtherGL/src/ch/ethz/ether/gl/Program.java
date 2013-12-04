package ch.ethz.ether.gl;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL3;
import javax.media.opengl.GL4;

import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

// TODO we currently just wrap around JOGLs GLSL helpers. should get rid of that bloated code dependency though...
public final class Program {
    public enum ShaderType {
        VERTEX(GL3.GL_VERTEX_SHADER), TESS_CONTROL(GL4.GL_TESS_CONTROL_SHADER), TESS_EVAL(GL4.GL_TESS_EVALUATION_SHADER), GEOMETRY(GL3.GL_GEOMETRY_SHADER), FRAGMENT(GL3.GL_FRAGMENT_SHADER);

        ShaderType(int glType) {
            this.glType = glType;
        }

        int getGLType() {
            return glType;
        }

        private int glType;
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
            this.code = ShaderCode.create(gl, type.getGLType(), 1, root, new String[]{path}, false);
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

    public void dispose(GL3 gl) {
        program.release(gl);
    }

    public void enable(GL3 gl) {
        program.useProgram(gl, true);
    }

    public void disable(GL3 gl) {
        program.useProgram(gl, false);
    }

    public void setUniform(GL3 gl, String name, boolean value) {
        gl.glUniform1i(getUniformLocation(gl, name), value ? 1 : 0);
    }

    public void setUniform(GL3 gl, String name, int value) {
        gl.glUniform1i(getUniformLocation(gl, name), value);
    }

    public void setUniform(GL3 gl, String name, float value) {
        gl.glUniform1f(getUniformLocation(gl, name), value);
    }

    public void setUniformVec4(GL3 gl, String name, float[] value) {
        gl.glUniform4fv(getUniformLocation(gl, name), 1, value, 0);
    }

    public void setUniformMat4(GL3 gl, String name, float[] value) {
        gl.glUniformMatrix4fv(getUniformLocation(gl, name), 1, false, value, 0);
    }

    public void setUniformSampler(GL3 gl, String name, int unit) {
        setUniform(gl, name, unit);
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
