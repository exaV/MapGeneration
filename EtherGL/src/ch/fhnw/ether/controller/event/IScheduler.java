/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

package ch.fhnw.ether.controller.event;

import com.jogamp.opengl.GLAutoDrawable;

/*
 * XXX Some thoughts here: Idea is to have a common scheduling mechanism for 
 * timed events across different media types / render programs. Need to address many 
 * issues though:
 * - update handling after completion of an action
 * - cancellation of an action
 * - consistent execution of actions that are scheduled for the same time (i imagine
 *   some sort of "timing slots" that can be created to which actions can collectively
 *   be attached
 * - framerate info / handling
 * - etc
 */
public interface IScheduler {

	interface IAction {
		boolean run(double time, double interval);
	}

	void once(IAction action);

	void once(double delay, IAction action);

	void repeat(double interval, IAction action);

	void repeat(double delay, double interval, IAction action);

	// FIXME: stuff below doesn't belong here
	void addDrawable(GLAutoDrawable drawable);

	void removeDrawable(GLAutoDrawable drawable);

	void requestUpdate(GLAutoDrawable drawable);

	void invokeOnRenderThread(Runnable runnable);
}
