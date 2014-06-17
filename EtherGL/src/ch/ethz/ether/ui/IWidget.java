package ch.ethz.ether.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import ch.ethz.ether.render.TextRenderGroup;
import ch.ethz.ether.view.IView;

public interface IWidget {
    static final Color TEXT_COLOR = Color.WHITE;

    public interface IWidgetAction<T extends IWidget> {
        void execute(T widget, IView view);
    }

    UI getUI();
    
    void setUI(UI ui);
	
	int getX();
	
	int getY();
	
	String getLabel();
	
	String getHelp();
	
	boolean hit(int x, int y, IView view);
	
	void draw(TextRenderGroup group);
	
	IWidgetAction<? extends IWidget> getAction();
	
	void setAction(IWidgetAction<? extends IWidget> action);
	
	void fire(IView view);
	
	boolean keyPressed(KeyEvent e, IView view);
	
	boolean mousePressed(MouseEvent e, IView view);
	
	boolean mouseReleased(MouseEvent e, IView view);
	
	boolean mouseMoved(MouseEvent e, IView view);
	
	boolean mouseDragged(MouseEvent e, IView view);
}
