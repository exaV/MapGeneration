package ch.fhnw.ether.video;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.media.RenderCommandException;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.video.fx.AbstractVideoFX;

public class ColorMapMaterialTarget extends AbstractVideoTarget {
	private final ColorMapMaterial material;
	private final IController      controller;

	public ColorMapMaterialTarget(ColorMapMaterial material, IController controller, boolean realTime) {
		super(Thread.MIN_PRIORITY, AbstractVideoFX.GLFX, realTime);
		this.material   = material;
		this.controller = controller;
	}

	@Override
	public void render() throws RenderCommandException {
		Texture texture = getFrame().getTexture();
		controller.run(time->{
			material.setColorMap(texture);
		});
		sleepUntil(getFrame().playOutTime);
	}

	public ColorMapMaterial getMaterial() {
		return material;
	}
}
