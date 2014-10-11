/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GLAutoDrawable;

abstract class AbstractScheduler implements IScheduler {

	private final List<GLAutoDrawable> drawables = new ArrayList<>();

	protected final BlockingQueue<Runnable> renderQueue = new LinkedBlockingQueue<>();
	private final BlockingQueue<Runnable> modelQueue = new LinkedBlockingQueue<>();

	protected AbstractScheduler() {
		new Thread(this::runRenderThread).start();
		new Thread(this::runModelThread).start();
	}

	@Override
	public void addDrawable(GLAutoDrawable drawable) {
		invokeOnRenderThread(() -> drawables.add(drawable));
	}

	@Override
	public void removeDrawable(GLAutoDrawable drawable) {
		invokeOnRenderThread(() -> drawables.remove(drawable));
	}

	@Override
	public void invokeOnRenderThread(Runnable runnable) {
		renderQueue.add(runnable);
	}

	@Override
	public void invokeOnModelThread(Runnable runnable) {
		modelQueue.add(runnable);
	}

	protected abstract void runRenderThread();

	protected void displayDrawables() {
		for (GLAutoDrawable drawable : drawables) {
			try {
				drawable.display();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void runModelThread() {
		try {
			while (true) {
				try {
					modelQueue.take().run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
