package ch.fhnw.ether.view.gl;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

final class GLFWInit {
	private static GLFWErrorCallback errorCallback;

	static void init() {
		errorCallback = Callbacks.errorCallbackPrint(System.err);
		Callbacks.glfwSetCallback(errorCallback);
		if (GLFW.glfwInit() != GL11.GL_TRUE) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
	}
	
	// TODO: the error callback should be released when the program shuts down
}
