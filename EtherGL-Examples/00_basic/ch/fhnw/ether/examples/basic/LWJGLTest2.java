package ch.fhnw.ether.examples.basic;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFW;

import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.GLFWWindow;

public class LWJGLTest2 {

	private GLFWWindow window;

	public void run() {
		System.out.println("Hello LWJGL " + Sys.getVersion() + "!");

		try {
			window = new GLFWWindow(null, 100, 100, "Hello", IView.INTERACTIVE_VIEW);
			while (true) {
				//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

				//GLFW.glfwSwapBuffers(window); // swap the color buffers

				// Poll for window events. The key callback above will only be
				// invoked during this call.
				GLFW.glfwWaitEvents();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.util.Debug", "true");
		new LWJGLTest2().run();
	}
}
