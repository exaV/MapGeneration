/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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

package ch.fhnw.ether.ui;

import ch.fhnw.ether.view.IView;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

public abstract class AbstractWidget implements IWidget {
	private UI ui;
	private int x;
	private int y;
	private String label;
	private String help;
	private IWidgetAction<? extends IWidget> action;

	protected AbstractWidget(int x, int y, String label, String help, IWidgetAction<? extends IWidget> action) {
		this.x = x;
		this.y = y;
		this.label = label;
		this.help = help;
		this.action = action;
	}

	@Override
	public final UI getUI() {
		return ui;
	}

	@Override
	public final void setUI(UI ui) {
		this.ui = ui;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getHelp() {
		return help;
	}

	@Override
	public boolean hit(int x, int y, IView view) {
		return false;
	}

	@Override
	public IWidgetAction<? extends IWidget> getAction() {
		return action;
	}

	@Override
	public void setAction(IWidgetAction<? extends IWidget> action) {
		this.action = action;
	}

	protected void requestUpdate() {
		if (ui != null)
			ui.requestUpdate();
	}

	@Override
	public boolean keyPressed(KeyEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mousePressed(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseReleased(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseMoved(MouseEvent e, IView view) {
		return false;
	}

	@Override
	public boolean mouseDragged(MouseEvent e, IView view) {
		return false;
	}
}