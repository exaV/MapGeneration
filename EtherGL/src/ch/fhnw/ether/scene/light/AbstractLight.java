package ch.fhnw.ether.scene.light;

import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public abstract class AbstractLight implements ILight {
	private final UpdateRequest updater = new UpdateRequest(true);
	
	private Vec3 position;
	private String name = "unnamed_light";
	private RGB color;

	protected AbstractLight(Vec3 position, RGB color) {
		this.position = position;
		this.color = color;
	}

	@Override
	public BoundingBox getBounds() {
		BoundingBox b = new BoundingBox();
		b.add(position);
		return b;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public RGB getColor() {
		return color;
	}

	@Override
	public void setColor(RGB color) {
		this.color = color;
	}
	
	@Override
	public boolean needsUpdate() {
		return updater.needsUpdate();
	}
	
	protected void requestUpdate() {
		updater.requestUpdate();
	}
}
