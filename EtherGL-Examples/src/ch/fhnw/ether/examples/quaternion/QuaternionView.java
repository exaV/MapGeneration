/*
 * Copyright (c) 2014, FHNW (Simon Schubiger)
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
 *  Neither the name of FHNW nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
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
package ch.fhnw.ether.examples.quaternion;

import ch.fhnw.ether.view.AbstractView;
import ch.fhnw.ether.view.Camera;

import com.jogamp.newt.event.KeyEvent;

public class QuaternionView extends AbstractView {
	public QuaternionView(QuaternionController controller, int x, int y, int w, int h, String title) {
		super(controller, x, y, w, h,ViewType.INTERACTIVE_VIEW, title);
	}

	private static final float[][] CAM_PARAMS = {
		{ 0, 0, 5,   0,   0, 0},
		{ 0, 5, 0,   0, -90, 0},
		{ 0, 0,-5,   0, 180, 0},
		{ 0,-5, 0,   0,  90, 0},
		{-5, 0, 0,  90,   0, 0},
		{ 5, 0, 0, -90,   0, 0},
	};
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_0:
		case KeyEvent.VK_1:
		case KeyEvent.VK_2:
		case KeyEvent.VK_3:
		case KeyEvent.VK_4:
		case KeyEvent.VK_5:
			float[] params = CAM_PARAMS[e.getKeyCode() - KeyEvent.VK_0];
			Camera  cam    = getCamera();
			cam.setPosition(params[0], params[1], params[2]);
			cam.setOrientation(params[3], params[4], params[5]);
			break;
		default:
			super.keyPressed(e);
			break;
		}

	}
}
