package ch.fhnw.ether.render;

import javax.media.opengl.GL3;

import ch.fhnw.ether.scene.attribute.AbstractAttribute;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.camera.CameraMatrices;
import ch.fhnw.ether.scene.light.DirectionalLight;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.GenericLight.LightSource;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;

public final class Lights {
	private static final class LightAttribute<T> extends AbstractAttribute<T> {
		public LightAttribute(String id) {
			super(id);
		}
	}

	public static LightAttribute<LightParameters> LIGHT_PARAMETERS = new LightAttribute<>("builtin.light.light_parameters");
	
	public static final class LightParameters {
		private final LightSource source;
		private final Vec3 positionEyeSpace;
		private final Vec3 spotDirectionEyeSpace;
		
		public LightParameters(LightSource source, CameraMatrices cameraMatrices) {
			this.source = source;
			this.positionEyeSpace = new Vec3(cameraMatrices.getViewMatrix().transform(source.getPosition()));
			this.spotDirectionEyeSpace = cameraMatrices.getNormalMatrix().transform(source.getSpotDirection());
		}
		
		public LightSource getSource() {
			return source;
		}
		
		public Vec3 getPositionEyeSpace() {
			return positionEyeSpace;
		}
		
		public Vec3 getSpotDirectionEyeSpace() {
			return spotDirectionEyeSpace;
		}
	};
	
	private static final GenericLight DEFAULT_LIGHT = new DirectionalLight(Vec3.Z, RGB.BLACK, RGB.WHITE);

	private GenericLight light = DEFAULT_LIGHT;
	private LightParameters lightParameters;

	public Lights(AbstractRenderer renderer) {
		renderer.addAttributeProvider(new IAttributeProvider() {
			@Override
			public void getAttributeSuppliers(ISuppliers suppliers) {
				suppliers.provide(LIGHT_PARAMETERS, () -> lightParameters);
			}
		});
	}

	public void addLight(ILight light) {
		if (light instanceof GenericLight) {
			this.light = (GenericLight)light;
		} else {
			throw new IllegalArgumentException("can only handle GenericLight");
		}
	}
	
	public void removeLight(ILight light) {
		light = DEFAULT_LIGHT;		
	}

	public void update(GL3 gl, CameraMatrices cameraMatrices) {
		lightParameters = new LightParameters(light.getLightSource(), cameraMatrices);
	}	
}
