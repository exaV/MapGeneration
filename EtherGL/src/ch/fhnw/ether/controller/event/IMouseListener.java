package ch.fhnw.ether.controller.event;

public interface IMouseListener {

	void mouseEntered(MouseEvent event);

	void mouseExited(MouseEvent event);
	
	void mouseMoved(MouseEvent event);
	
	void mouseDragged(MouseEvent event);
	
	void mouseButtonPressed(MouseEvent event);
	
	void mouseButtonReleased(MouseEvent event);
}
