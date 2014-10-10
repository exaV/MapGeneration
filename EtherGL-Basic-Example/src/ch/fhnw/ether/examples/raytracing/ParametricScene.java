package ch.fhnw.ether.examples.raytracing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.examples.raytracing.util.IntersectResult;
import ch.fhnw.ether.examples.raytracing.util.Ray;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.I3DObject;

public class ParametricScene implements IScene {
	
	private List<RayTraceObject> meshes = new ArrayList<>();
	private ICamera camera;
	private ILight light;
	private RGBA background = RGBA.WHITE;
	
	public ParametricScene(ICamera camera, ILight light) {
		this.camera = camera;
		this.light = light;
	}

	@Override
	public List<? extends I3DObject> getObjects() {
		return meshes;
	}

	@Override
	public List<? extends IMesh> getMeshes() {
		return meshes;
	}

	@Override
	public List<ICamera> getCameras() {
		return Collections.singletonList(camera);
	}

	@Override
	public List<ILight> getLights() {
		return Collections.singletonList(light);
	}

	@Override
	public void setRenderer(IRenderer renderer) {
	}

	@Override
	public void renderUpdate() {
	}
	
	/**
	 * @param origin ray origin
	 * @param direction ray direction
	 * @return The color which results for the given ray in the scene. As float array with rgba-components
	 */
	public RGBA intersection(Ray ray) {
		
		//find nearest intersection point in scene
		IntersectResult nearest = new IntersectResult(null, null, background, Float.POSITIVE_INFINITY);
		for(RayTraceObject r : meshes) {
			IntersectResult intersect = r.intersect(ray);
			if(intersect.isValid() && intersect.dist < nearest.dist) {
				nearest = intersect;
			}
		}
		
		//no object found
		if(!nearest.isValid()) return background; 
		
		//create vector which is sure over surface
		Vec3 position_ontop_surface = nearest.position.subtract(ray.direction.scale(0.01f));
		position_ontop_surface = position_ontop_surface.add(nearest.surface.getNormalAt(nearest.position).scale(0.0001f));

float dist_to_light = light.getPosition().subtract(nearest.position).length();
		
		//check if path to light is clear
		Ray light_ray = new Ray(position_ontop_surface, light.getPosition().subtract(position_ontop_surface));
		for(RayTraceObject r : meshes) {
			IntersectResult intersect = r.intersect(light_ray);
			if(intersect.isValid() && intersect.dist < dist_to_light) {
				return RGBA.BLACK;
			}
		}
		
		// diffuse color
		float dot = light_ray.direction.dot(nearest.surface.getNormalAt(nearest.position));
		return nearest.color.scaleRGB(Math.max(dot, 0));
	}
	
	public void addMesh(RayTraceObject mesh) {
		meshes.add(mesh);
	}

}
