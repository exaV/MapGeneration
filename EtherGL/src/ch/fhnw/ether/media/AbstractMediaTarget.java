package ch.fhnw.ether.media;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ch.fhnw.util.Log;

public abstract class AbstractMediaTarget<F extends AbstractFrame, T extends IRenderTarget> implements IRenderTarget {
	private static final Log log = Log.create();

	public static final double SEC2NS = 1000 * 1000 * 1000;
	public static final double SEC2US = 1000 * 1000;
	public static final double SEC2MS = 1000;

	private   final int                                                priority;
	private   RenderProgram<T>                                         program;
	protected final AtomicBoolean                                      isRendering  = new AtomicBoolean();
	private   final Map<AbstractRenderCommand<T,?>, PerTargetState<T>> state        = new WeakHashMap<>();
	private   final long                                               startTime    = System.nanoTime();
	private   final AtomicReference<F>                                 frame        = new AtomicReference<>();
	private         F                                                  currentFrame;

	protected AbstractMediaTarget(int threadPriority) {
		this.priority = threadPriority;
	}

	@Override
	public final void start() {
		if(program.getFrameSource().getFrameCount() == 1) {
			try {
				isRendering.set(true);
				runOneCycle();
			} catch(Throwable e) {
				log.severe(e);
			}
			isRendering.set(false);
		} else {
			Thread t = new Thread(()->{
				try {
					isRendering.set(true);
					while(isRendering())
						runOneCycle();
				} catch(Throwable e) {
					isRendering.set(false);
					log.severe(e);
				}
			}, getClass().getName());
			t.setDaemon(true);
			t.setPriority(priority);
			t.start();
		}
	}

	protected void runOneCycle() throws RenderCommandException {
		program.runInternal(this);
		AbstractFrame tmp = getFrame();
		if(tmp != null) {
			render();
			currentFrame = frame.getAndSet(null);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public PerTargetState<?> getState(AbstractRenderCommand<?,?> cmd) throws RenderCommandException {
		synchronized (state) {
			PerTargetState<T> result = state.get(cmd);
			if(result == null) {
				result = cmd.createStateInternal(this);
				state.put((AbstractRenderCommand<T,?>) cmd, result);
			}
			return result;
		}
	}

	@SuppressWarnings("unused")
	public void useProgram(RenderProgram<T> program) throws RenderCommandException {
		this.program = program;	
	}

	@Override
	public void stop() {
		isRendering.set(false);
	}

	@Override
	public void render() throws RenderCommandException {}

	@Override
	public boolean isRendering() {
		return isRendering.get();
	}

	@Override
	public double getTime() {
		return (System.nanoTime() - startTime) / SEC2NS; 
	}

	@Override
	public void sleepUntil(double time) {
		time *= SEC2NS;
		long deadline = startTime + (long)time;
		long wait     = deadline - System.nanoTime();
		if(wait > 0) {
			try {
				Thread.sleep(wait / 1000000L, (int)(wait % 1000000L));
			} catch(Throwable t) {
				log.severe(t);
			}
		} else {
			//	log.warning("Missed deadline by " + -wait + "ns");
		}
	}

	public F getFrame() {
		return frame.get();
	}

	public void setFrame(F frame) {
		this.frame.set(frame);
	}

	public F getCurrentFrame() {
		return currentFrame;
	}
}
