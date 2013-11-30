/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.ui;

import java.awt.Color;

import ch.ethz.ether.view.IView;

public class Button {
	public static final int BUTTON_WIDTH = 48;
	public static final int BUTTON_HEIGHT = 48;

	public static final int BUTTON_GAP = 8;

	public enum State {
		DEFAULT(0.6f, 0, 0, 0.75f), PRESSED(1, 0.2f, 0.2f, 0.75f), DISABLED(0.5f, 0.5f, 0.5f, 0.75f);

		State(float r, float g, float b, float a) {
			this.color = new Color(r, g, b, a);
		}

		public Color getColor() {
			return color;
		}

		private final Color color;
	}

	public interface IButtonAction {
		void execute(Button button, IView view);
	}


	private UI ui;
	private int x;
	private int y;
	private String label;
	private String help;
	private int key;
	private State state = State.DEFAULT;
	private IButtonAction action;

	public Button(int x, int y, String label, String help, int key) {
		this(x, y, label, help, key, null);
	}

	public Button(int x, int y, String label, String help, int key, IButtonAction action) {
		this.x = x;
		this.y = y;
		this.label = label;
		this.help = help;
		this.key = key;
		this.action = action;
	}

	public Button(int x, int y, String label, String help, int key, State state, IButtonAction action) {
		this(x, y, label, help, key, action);
		setState(state);
	}

	public Button(int x, int y, String label, String help, int key, boolean pressed, IButtonAction action) {
		this(x, y, label, help, key, action);
		setState(pressed);
	}
	
	void setUI(UI ui) {
		this.ui = ui;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getLabel() {
		return label;
	}

	public String getHelp() {
		return help;
	}

	public int getKey() {
		return key;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
		if (ui != null) ui.requestUpdate();
	}

	public void setState(boolean pressed) {
		setState(pressed ? State.PRESSED : State.DEFAULT);
		if (ui != null) ui.requestUpdate();
	}

	public IButtonAction getAction() {
		return action;
	}

	public void setAction(IButtonAction action) {
		this.action = action;
	}

	public boolean hit(int x, int y, IView view) {
		float bx = ui.getX() + this.x * (BUTTON_GAP + BUTTON_WIDTH);
		float by = ui.getY() + this.y * (BUTTON_GAP + BUTTON_HEIGHT);
		return x >= bx && x <= bx + BUTTON_WIDTH && y >= by && y <= by + BUTTON_HEIGHT;
	}

	public void fire(IView view) {
		if (state == State.DISABLED)
			return;
		if (action == null)
			throw new UnsupportedOperationException("button '" + label + "' has no action defined");
		action.execute(this, view);
	}
}
