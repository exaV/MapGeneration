package ch.fhnw.ether.view.gl;

import java.io.File;
import java.io.IOException;

import com.jogamp.common.util.IOUtil;
import com.jogamp.newt.Display;
import com.jogamp.newt.opengl.GLWindow;

public class PointerConfig {
	
	private GLWindow window;	
	private Display display;
	
	public PointerConfig(GLWindow w, Display d){
		window = w;
		display = d;
	}
	
	public void warpPointer(int x, int y){
		window.warpPointer(x, y);
	}
	
	public void setPointerVisible(boolean mouseVisible){
		window.setPointerVisible(mouseVisible);
	}
	
	public void confinePointer(boolean confine){
		window.confinePointer(confine);
	}
	
	public void setPointerIcon(File CursorLocation, int hotSpotX, int hotSpotY){
				
		String[] resources = {CursorLocation.toString()};
		
		try {
			window.setPointerIcon(display.createPointerIcon(new IOUtil.ClassResources(resources, null, null), hotSpotX, hotSpotY));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
