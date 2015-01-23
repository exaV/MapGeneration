package ch.fhnw.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.Set;

public class AutoDisposer<T> extends Thread {
	private static final Log log = Log.create();

	private static final boolean DBG = false;
	
	protected ReferenceQueue<T>                   refQ   = new ReferenceQueue<>();
	protected IdentityHashSet<Reference<T>>       refSet = new IdentityHashSet<>();
	private   Class<? extends Reference<T>>       refCls;
	private   Constructor<? extends Reference<T>> ctor;

	public AutoDisposer(Class<? extends Reference<T>> refCls) {
		super("AutoDisposer for " + refCls.getName());
		this.refCls = refCls;
		setDaemon(true);
		setPriority(Thread.MIN_PRIORITY);
		start();
	}

	@SuppressWarnings("unchecked")
	public synchronized void add(T object) {
		try {
			if(ctor == null) {
				for(Constructor<?> ctor : refCls.getDeclaredConstructors()) {
					Class<?>[] argTypes = ctor.getParameterTypes();
					if(argTypes.length == 2 
							&& argTypes[0].isAssignableFrom(object.getClass()) 
							&& argTypes[1].isAssignableFrom(refQ.getClass())) {
						this.ctor = (Constructor<? extends Reference<T>>) ctor;
						break;
					}
				}
				if(ctor == null)
					throw new IllegalArgumentException("No reference constructor found in " + refCls.getName());
			}
			
			Reference<T> ref = ctor.newInstance(object, refQ);
			refSet.add(ref);

			if(DBG) System.out.println("add:" + ref);
		} catch(Throwable t) {
			log.severe(t);
		}
	}

	@Override
	public void run() {
		for(;;) {
			try {
				doDispose((Reference<?>)refQ.remove());
			} catch (Throwable t) {
				log.severe(t);
			}
		}
	}
	
	public synchronized void doDispose() {
		if(DBG) System.out.println("*** " + refCls.getName() + ": doDispose(" + refSet.size() + ")");
		for(;;) {
			Reference<?> ref = (Reference<?>) refQ.poll();
			if(ref == null)
				break;

			doDispose(ref);
		}
		if(DBG) System.out.println("+++ " + refCls.getName() + ": doDispose");
	}

	private void doDispose(Reference<?> ref) {
		if(DBG) System.out.println("dispose:" + ref);
		ref.dispose();
		refSet.remove(ref);
	}

	public abstract static class Reference<T> extends WeakReference<T> {
		String label;
		
		public Reference(T referent, ReferenceQueue<? super T> q) {
			super(referent, q);
			try {
				label = referent.toString();
			} catch(Throwable t) {}
		}

		public abstract void dispose();

		@Override
		public String toString() {
			return getClass().getName() + ":" + ClassUtilities.identityHashCode(this) + ":" + label;
		}
	}

	public Set<Reference<T>> getRefSet() {
		return refSet;
	}

	public static void runGC() {
		for(int i = 0; i < 5; i++) {
			long before = Runtime.getRuntime().freeMemory();
			System.gc();
			Runtime.getRuntime().runFinalization();
			System.gc();
			long after  = Runtime.getRuntime().freeMemory();
			if(after >= before)
				break;
		}		
	}	
}
