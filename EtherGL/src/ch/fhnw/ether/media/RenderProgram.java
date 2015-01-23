package ch.fhnw.ether.media;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import ch.fhnw.util.ArrayUtilities;
import ch.fhnw.util.CollectionUtilities;

public class RenderProgram<T extends IRenderTarget> extends AbstractRenderCommand<T,RenderProgram.State<T>> {	
	static class Update {
		final AbstractRenderCommand<?,?> oldCmd;
		final AbstractRenderCommand<?,?> newCmd;
		final boolean                    first;

		Update(AbstractRenderCommand<?,?> oldCmd, AbstractRenderCommand<?,?> newCmd, boolean first) {
			this.oldCmd = oldCmd;
			this.newCmd = newCmd;
			this.first  = first;
		}

		boolean isAdd() {
			return newCmd != null && oldCmd == null;
		}

		boolean isReplace() {
			return newCmd != null && oldCmd != null;
		}

		boolean isRemove() {
			return newCmd == null && oldCmd != null;
		}
		
		void add(List<AbstractRenderCommand<?,?>> program) {
			if(first)
				program.add(0, newCmd);
			else
				program.add(newCmd);
		}
		
		void replace(List<AbstractRenderCommand<?,?>> program) {
			program.set(program.indexOf(oldCmd), newCmd);
		}
		
		void remove(List<AbstractRenderCommand<?,?>> program) {
			program.remove(oldCmd);
		}
	}

	private final AtomicReference<AbstractRenderCommand<T,?>[]> program   = new AtomicReference<>();
	private final List<IRenderProgramListener>                  listeners = new ArrayList<>();
	
	@SafeVarargs
	public RenderProgram(AbstractFrameSource<T,?> source, AbstractRenderCommand<T,?> ... commands) {
		program.set(ArrayUtilities.prepend(source, commands));
	}

	public Update createAddFirst(AbstractRenderCommand<T,?> cmd) {
		return new Update(null, cmd, true);
	}

	public Update createAddLast(AbstractRenderCommand<T,?> cmd) {
		return new Update(null, cmd, false);
	}

	public Update createRemove(AbstractRenderCommand<T,?> cmd) {
		return new Update(cmd, null, false);
	}

	public Update createReplace(AbstractRenderCommand<T,?> oldCmd, AbstractRenderCommand<T,?> newCmd) {
		return new Update(oldCmd, newCmd, false);
	}

	public void addFirst(AbstractRenderCommand<T,?> cmd) {
		update(createAddFirst(cmd));
	}

	public void addLast(AbstractRenderCommand<T,?> cmd) {
		update(createAddLast(cmd));
	}

	public void remove(AbstractRenderCommand<T,?> cmd) {
		update(createRemove(cmd));
	}

	public void replace(AbstractRenderCommand<T,?> oldCmd, AbstractRenderCommand<T,?> newCmd) {
		update(createReplace(oldCmd, newCmd));
	}

	public void update(Update ... updates) {
		List<AbstractRenderCommand<?,?>> tmp = new ArrayList<>(program.get().length + updates.length);
		CollectionUtilities.addAll(tmp, program.get());
		
		for(Update update : updates) {
			if(update.isAdd())
				update.add(tmp);
			if(update.isReplace())
				update.replace(tmp);
			if(update.isRemove())
				update.remove(tmp);
		}

		setProgram(tmp);
	}

	@SuppressWarnings("unchecked")
	private synchronized void setProgram(List<AbstractRenderCommand<?,?>> program) {
		AbstractRenderCommand<T,?>[] oldProgram = this.program.get(); 
		AbstractRenderCommand<T,?>[] newProgram = program.toArray(new AbstractRenderCommand[program.size()]);
		for(IRenderProgramListener l : listeners)
			l.programChanged(this, oldProgram, newProgram);
		this.program.set(newProgram);
	}

	@Override
	protected void run(State<T> state) throws RenderCommandException {
		final T target = state.getTarget();
		AbstractRenderCommand<T,?>[] commands = program.get(); 
		for(AbstractRenderCommand<T,?> command : commands)
			command.runInternal(target);
	}

	@Override
	protected State<T> createState(T target) {
		return new State<>(target);
	}
	
	static class State<T extends IRenderTarget> extends PerTargetState<T> {
		public State(T target) {
			super(target);
		}
	}

	public AbstractRenderCommand<T, ?>[] getProgram() {
		return program.get();
	}
	
	public synchronized void addListener(IRenderProgramListener listener) {
		CollectionUtilities.addIfUnique(listeners, listener);
	}
	
	public synchronized void removeListener(IRenderProgramListener listener) {
		CollectionUtilities.removeAll(listeners, listener);
	}

	public AbstractFrameSource<T, ?> getFrameSource() {
		AbstractRenderCommand<T,?>[] commands = program.get(); 
		for(AbstractRenderCommand<T,?> command : commands)
			if(command instanceof AbstractFrameSource)
				return (AbstractFrameSource<T, ?>) command;
		return null;
	}
}
