package ch.fhnw.ether.examples.raytracing;

import ch.fhnw.ether.camera.Camera;
import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.AbstractController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.EventDrivenScheduler;
import ch.fhnw.ether.examples.raytracing.surface.Plane;
import ch.fhnw.ether.examples.raytracing.surface.Sphere;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.IView.ViewType;
import ch.fhnw.ether.view.gl.AbstractView;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

public class RaytracerExample1 {
	
	public static void main(String[] args) {
		new RaytracerExample1();
	}
	
	public RaytracerExample1() {
		
		// create scene objects
		ICamera cam = new Camera(2.5f, 1, 0.5f, Float.POSITIVE_INFINITY);
		ILight l = new PointLight(new Vec3(0, 0, 3), RGBA.WHITE);
		ParametricScene s = new ParametricScene(cam, l);
		RayTraceObject chugeli = new RayTraceObject(new Sphere(0.5f));
		RayTraceObject bode = new RayTraceObject(new Plane());
		
		// setup scene
		s.addMesh(chugeli);
		s.addMesh(bode);
		
		// adjust scene
		chugeli.setPosition(Vec3.Z.scale(0.5f));
		cam.move(0, 0, 1, false);
		
		// use default controller
		IController c = new AbstractController(new EventDrivenScheduler(), new RayTracingRenderer(s)) {
			@Override
			public void updateUI() {} //UI needs forward renderer
		};
		
		IView v = new AbstractView(c, 100, 100, 100, 100, ViewType.INTERACTIVE_VIEW, "Raytracing", cam);
		
		c.addView(v);
		c.setScene(s);
	}
	
}
