package ch.fhnw.ether.view;

import java.io.File;

public interface IWindow {
	/**
	 * Shows / hides this window.
	 */
	void setVisible(boolean visible);
	
	/**
	 * Set this window's position.
	 */
	void setPosition(int x, int y);
	
	/**
	 * Set this window's size.
	 */
	void setSize(int width, int height);
	
	/**
	 * Enable or disable this window as a fullscreen window.
	 */
	void setFullscreen(boolean enabled);
	
	/**
	 * Enable or disable mouse pointer for this window.
	 */
	void setPointerVisible(boolean visible);
	
	/**
	 * Confine or unconfine pointer for this window.
	 */
	void setPointerConfined(boolean confined);
	
	/**
	 * Set pointer icon for this window.
	 */
	void setPointerIcon(File pngImage, int hotspotX, int hotspotY);
	
	/**
	 * Warp pointer to x y.
	 */
	void warpPointer(int x, int y);
	
	/**
	 * Display (i.e. render) this view. Must be run from render thread.
	 */
	void display();
}
