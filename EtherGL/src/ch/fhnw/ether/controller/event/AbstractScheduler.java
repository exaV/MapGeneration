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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.jogamp.opengl.GLAutoDrawable;

abstract class AbstractScheduler implements IScheduler {

	private static final class DelayedAction implements Delayed {
		private long delay;
		private final long interval;
		private final IAction action;

		public DelayedAction(long delay, long interval, IAction action) {
			this.delay = delay;
			this.interval = interval;
			this.action = action;
		}

		boolean run(double time) {
			if (action.run(time, interval / 1000000000.0) && interval > 0) {
				delay += interval;
				return true;
			}
			return false;
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return unit.convert(START_TIME - System.nanoTime() + this.delay, TimeUnit.NANOSECONDS);
		}

		@Override
		public int compareTo(Delayed o) {
			long d0 = getDelay(TimeUnit.NANOSECONDS);
			long d1 = o.getDelay(TimeUnit.NANOSECONDS);
			// System.out.println("compare delay: " + d0 + " " + d1);
			return (d0 < d1) ? -1 : ((d0 == d1) ? 0 : 1);
		}
	}

	private static final long START_TIME = System.nanoTime();

	private final DelayQueue<DelayedAction> modelQueue = new DelayQueue<>();

	private final List<GLAutoDrawable> drawables = new ArrayList<>();

	protected final BlockingQueue<Runnable> renderQueue = new LinkedBlockingQueue<>();

	protected AbstractScheduler() {
		new Thread(this::runModelThread).start();
		new Thread(this::runRenderThread).start();
	}

	@Override
	public void once(IAction action) {
		once(0, action);
	}

	@Override
	public void once(double delay, IAction action) {
		modelQueue.add(new DelayedAction(s2ns(delay), 0, action));
	}

	@Override
	public void repeat(double interval, IAction action) {
		repeat(0, interval, action);
	}

	@Override
	public void repeat(double delay, double interval, IAction action) {
		modelQueue.add(new DelayedAction(s2ns(delay), s2ns(interval), action));
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
		while (true) {
			try {
				DelayedAction action = modelQueue.take();
				if (action.run(getTime()))
					modelQueue.add(action);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private double getTime() {
		long elapsed = System.nanoTime() - START_TIME;
		return elapsed / 1000000000.0;
	}

	private long s2ns(double time) {
		return (long) (time * 1000000000);
	}
}
