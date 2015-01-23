package ch.fhnw.ether.media;

public interface IRenderProgramListener {
	void programChanged(RenderProgram<?> program, AbstractRenderCommand<?,?>[] oldProgram, AbstractRenderCommand<?,?>[] newProgram);
}
