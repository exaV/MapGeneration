package ch.fhnw.ether.examples.raytracing;

import java.util.Timer;
import java.util.TimerTask;

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

public class RaytracerExample2 {
	
	public static void main(String[] args) {
		new RaytracerExample2();
	}
	
	public RaytracerExample2() {
		
		ICamera cam = new Camera(2.5f, 1, 0.5f, Float.POSITIVE_INFINITY);
		cam.move(0, 0, 1, false);
		ILight l = new PointLight(new Vec3(-1, -1, 3), RGBA.WHITE);
		ParametricScene s = new ParametricScene(cam, l);
		
		Sphere sphere = new Sphere(0.5f);
		sphere.setPosition(new Vec3(0,0,0.5f));
		RayTraceObject chugeli = new RayTraceObject(sphere);
		RayTraceObject bode = new RayTraceObject(new Plane());
		RayTraceObject waendli = new RayTraceObject(new Plane(Vec3.X_NEG, 4), RGBA.YELLOW);
		RayTraceObject anders_waendli = new RayTraceObject(new Plane(Vec3.X, 4), RGBA.RED);
		RayTraceObject wand = new RayTraceObject(new Plane(Vec3.Y_NEG, 4), RGBA.GREEN);
		RayTraceObject henderi_wand = new RayTraceObject(new Plane(Vec3.Y, 4), RGBA.CYAN);
		RayTraceObject dach = new RayTraceObject(new Plane(Vec3.Z_NEG, 4), RGBA.BLUE);
		s.addMesh(chugeli);
		s.addMesh(bode);
		s.addMesh(waendli);
		s.addMesh(anders_waendli);
		s.addMesh(dach);
		s.addMesh(wand);
		s.addMesh(henderi_wand);

		IController c = new AbstractController(new EventDrivenScheduler(), new RayTracingRenderer(s)) {
			@Override
			public void updateUI() {} //UI needs forward renderer
		};
		
		IView v = new AbstractView(c, 100, 100, 100, 100, ViewType.INTERACTIVE_VIEW, "Raytracing", cam);
		
		c.addView(v);
		c.setScene(s);
		
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			private float n = 0;
			@Override
			public void run() {
				chugeli.setPosition(Vec3.Z.scale((float) Math.sin(n)+0.5f));
				n += 0.1;
				if(n >= Math.PI) n = 0;
				v.repaint();
			}
		}, 1000, 50);
	}
	
}
