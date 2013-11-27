package ch.ethz.ether.render;

import java.util.EnumSet;

import ch.ethz.ether.render.util.IAddOnlyFloatList;
import ch.ethz.util.UpdateRequest;

public abstract class AbstractRenderGroup implements IRenderGroup {
	private final UpdateRequest updater = new UpdateRequest();
	private final Source source;
	private final Type type;
	private Pass pass;
	private EnumSet<Flag> flags;
	
	protected AbstractRenderGroup(Source source, Type type) {
		this(source, type, Pass.DEPTH);
	}
	
	protected AbstractRenderGroup(Source source, Type type, Pass pass) {
		this(source, type, pass, EnumSet.noneOf(Flag.class));
	}

	protected AbstractRenderGroup(Source source, Type type, Pass pass, EnumSet<Flag> flags) {
		this.source = source;
		this.type = type;
		this.pass = pass;
		this.flags = flags;
	}
	
	@Override
	public final void requestUpdate() {
		updater.requestUpdate();
	}
	
	@Override
	public final boolean needsUpdate() {
		return updater.needsUpdate();
	}

	@Override
	public Source getSource() {
		return source;
	}
	
	@Override
	public final Type getType() {
		return type;
	}
	
	@Override
	public Pass getPass() {
		return pass;
	}
	
	@Override
	public void setPass(Pass pass) {
		this.pass = pass;
		requestUpdate();
	}
	
	protected final EnumSet<Flag> getFlags() {
		return flags.clone();
	}

	protected final void setFlags(EnumSet<Flag> flags) {
		this.flags = flags.clone();
		requestUpdate();
	}
	
	@Override
	public final boolean containsFlag(Flag flag) {
		return flags.contains(flag);
	}
	
	// all operations are no-op
	@Override
	public void getVertices(IAddOnlyFloatList dst) {
	}

	@Override
	public void getNormals(IAddOnlyFloatList dst) {
	}

	@Override
	public void getColors(IAddOnlyFloatList dst) {
	}
	
	@Override
	public void getTexCoords(IAddOnlyFloatList dst) {
	}
	
	@Override
	public float[] getColor() {
		return DEFAULT_COLOR;
	}
	
	@Override
	public float getPointSize() {
		return 1;
	}

	@Override
	public float getLineWidth() {
		return 1;
	}

	@Override
	public ITextureData getTexData() {
		return null;
	}
}
