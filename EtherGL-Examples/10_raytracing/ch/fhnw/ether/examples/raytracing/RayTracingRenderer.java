package ch.fhnw.ether.examples.raytracing;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.forward.ForwardRenderer;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.Primitive;
import ch.fhnw.ether.scene.mesh.material.ColorMapMaterial;
import ch.fhnw.ether.scene.mesh.material.Texture;
import ch.fhnw.ether.view.IView;
import ch.fhnw.util.Viewport;

public class RayTracingRenderer implements IRenderer {
	private final RayTracer       rayTracer;
	private final ForwardRenderer renderer      = new ForwardRenderer();
	private final Texture         screenTexture;
	private final IMesh           plane;
	private long                  n = 0;

	public RayTracingRenderer(RayTracer rayTracer) {
		this.rayTracer = rayTracer;
		this.screenTexture = new Texture(rayTracer);
		this.plane         = createScreenPlane(-1, -1, 2, 2, screenTexture);
		this.renderer.addMesh(plane);
	}
	
	@Override
	public void render(GL3 gl, IView view) {
		long t = System.currentTimeMillis();
		Viewport viewport = view.getViewport();
		if (viewport.w != rayTracer.getWidth() || viewport.h != rayTracer.getHeight())
			rayTracer.setSize(viewport.w, viewport.h);
		rayTracer.setCamera(view.getCamera());
		rayTracer.setLights(view.getController().getScene().getLights());
		
		screenTexture.update();

		renderer.render(gl, view);
		System.out.println((System.currentTimeMillis() - t) + "ms for " + ++n + "th frame");
	}

	private static IMesh createScreenPlane(float x, float y, float w, float h, Texture texture) {
		float[] vertices = { x, y, 0, x + w, y, 0, x + w, y + h, 0, x, y, 0, x + w, y + h, 0, x, y + h, 0 };
		IGeometry geometry = DefaultGeometry.createVM(Primitive.TRIANGLES, vertices, MeshLibrary.DEFAULT_QUAD_TEX_COORDS);

		return new DefaultMesh(new ColorMapMaterial(texture), geometry, Pass.DEVICE_SPACE_OVERLAY);
	}

	@Override
	public void addMesh(IMesh mesh) {
		rayTracer.addMesh(mesh);
	}

	@Override
	public void removeMesh(IMesh mesh) {
		rayTracer.removeMesh(mesh);
	}

	@Override
	public void addLight(ILight light) {
		rayTracer.addLigth(light);
	}

	@Override
	public void removeLight(ILight light) {
		rayTracer.removeLight(light);
	}
}
