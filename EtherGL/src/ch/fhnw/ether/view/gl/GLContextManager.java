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

package ch.fhnw.ether.view.gl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class GLContextManager {
	public interface IGLContext {
	}

	private static final class ExistingContext implements IGLContext {
	}

	private static final class TemporaryContext implements IGLContext {
		private final GLFWWindow window;

		TemporaryContext() {
			window = GLFWWindow.createDummyWindow(getSharedWindow());
		}

		void makeCurrent() {
			window.makeContextCurrent(true);
		}

		void release() {
			window.makeContextCurrent(false);
		}
	}

	private static class ContextPool {
		private static final int MAX_CONTEXTS = 10;
		private final AtomicInteger numContexts = new AtomicInteger();
		private final BlockingQueue<TemporaryContext> contexts = new LinkedBlockingQueue<>();
		
		TemporaryContext acquireContext(boolean wait) {
			TemporaryContext context = null;
			context = contexts.poll();
			if (context == null) {
				if (numContexts.incrementAndGet() < MAX_CONTEXTS) {
					context = new TemporaryContext();
				} else if (wait) {
					try {
						context = contexts.take();
					} catch (InterruptedException e) {
						// XXX what to do in this case?
						e.printStackTrace();
					}
				}
			}
			if (context != null)
				context.makeCurrent();
			return context;
		}
		
		void releaseContext(TemporaryContext context) {
			context.release();
			contexts.add(context);			
		}
		
	}

	private static final IGLContext VOID_CONTEXT = new ExistingContext();

	private static ContextPool contexts = new ContextPool();
	
	private static GLFWWindow theSharedWindow;

	public static IGLContext acquireContext() {
		return acquireContext(true);
	}

	public static IGLContext acquireContext(boolean wait) {
		if (GLFWWindow.hasContextCurrent())
			return VOID_CONTEXT;

		return contexts.acquireContext(wait);
	}

	public static void releaseContext(IGLContext context) {
		if (context instanceof TemporaryContext)
			contexts.releaseContext((TemporaryContext)context);
	}

	public synchronized static GLFWWindow getSharedWindow() {
		if (theSharedWindow == null)
			theSharedWindow = GLFWWindow.createDummyWindow(null);
		return theSharedWindow;
	}
}
