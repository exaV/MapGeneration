package ch.fhnw.ether.controller.event;

public interface IWindowListener {

	void windowClosed(WindowEvent event);

	void windowGainedFocus(WindowEvent event);

	void windowLostFocus(WindowEvent event);

	void windowResized(WindowEvent event);
	
	void windowScrolled(WindowEvent event);
}
