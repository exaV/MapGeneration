package ch.fhnw.ether.scene.light;

import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;
import ch.fhnw.util.math.geometry.BoundingBox;

public class GenericLight implements ILight {
	public static LightAttribute<LightSource> GENERIC_LIGHT = new LightAttribute<>("builtin.light.generic_light");

	public static class LightSource {
		private final boolean isLocal;
		private final boolean isSpot;

		private final Vec4 position;
		private final RGB ambient;
		private final RGB color;

		private final Vec3 spotDirection;
		private final float spotCosCutoff;
		private final float spotExponent;

		private final float constantAttenuation;
		private final float linearAttenuation;
		private final float quadraticAttenuation;

		public LightSource(boolean isLocal, boolean isSpot, Vec3 position, RGB ambient, RGB color, Vec3 spotDirection, float spotCosCutoff, float spotExponent,
				float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
			this.isLocal = isLocal;
			this.isSpot = isSpot;
			this.position = makePosition(isLocal, position);
			this.ambient = ambient;
			this.color = color;
			this.spotDirection = spotDirection != null ? spotDirection : Vec3.Z_NEG;
			this.spotCosCutoff = spotCosCutoff;
			this.spotExponent = spotExponent;
			this.constantAttenuation = constantAttenuation;
			this.linearAttenuation = linearAttenuation;
			this.quadraticAttenuation = quadraticAttenuation;
		}

		public LightSource(LightSource parameters, Vec3 position) {
			this.isLocal = parameters.isLocal;
			this.isSpot = parameters.isSpot;
			this.position = makePosition(parameters.isLocal, position);
			this.ambient = parameters.ambient;
			this.color = parameters.color;
			this.spotDirection = parameters.spotDirection;
			this.spotCosCutoff = parameters.spotCosCutoff;
			this.spotExponent = parameters.spotExponent;
			this.constantAttenuation = parameters.constantAttenuation;
			this.linearAttenuation = parameters.linearAttenuation;
			this.quadraticAttenuation = parameters.quadraticAttenuation;
		}
		
		private static Vec4 makePosition(boolean isLocal, Vec3 position) {
			if (isLocal)
				return new Vec4(position.x, position.y, position.z, 1);
			else {
				position = position.normalize();
				return new Vec4(position.x, position.y, position.z, 0);
			}
		}

		public static LightSource directionalSource(Vec3 direction, RGB ambient, RGB color) {
			return new LightSource(false, false, direction, ambient, color, null, 0, 0, 0, 0, 0);
		}

		public static LightSource pointSource(Vec3 position, RGB ambient, RGB color, float constantAttenuation, float linearAttenuation,
				float quadraticAttenuation) {
			return new LightSource(true, false, position, ambient, color, null, 0, 0, constantAttenuation, linearAttenuation, quadraticAttenuation);
		}

		public static LightSource spotSource(Vec3 position, RGB ambient, RGB color, Vec3 spotDirection, float spotCutoff, float spotExponent,
				float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
			return new LightSource(true, true, position, ambient, color, spotDirection, (float)Math.cos(Math.toRadians(spotCutoff)), spotExponent, constantAttenuation, linearAttenuation,
					quadraticAttenuation);
		}

		public boolean isLocal() {
			return isLocal;
		}

		public boolean isSpot() {
			return isSpot;
		}

		public Vec4 getPosition() {
			return position;
		}

		public RGB getAmbient() {
			return ambient;
		}

		public RGB getColor() {
			return color;
		}

		public Vec3 getSpotDirection() {
			return spotDirection;
		}

		public float getSpotCosCutoff() {
			return spotCosCutoff;
		}

		public float getSpotExponent() {
			return spotExponent;
		}

		public float getConstantAttenuation() {
			return constantAttenuation;
		}

		public float getLinearAttenuation() {
			return linearAttenuation;
		}

		public float getQuadraticAttenuation() {
			return quadraticAttenuation;
		}
	}

	private final UpdateRequest updater = new UpdateRequest(true);

	private String name = "unnamed_light";

	private LightSource lightParameters;

	protected GenericLight(LightSource lightParameters) {
		this.lightParameters = lightParameters;
	}

	@Override
	public BoundingBox getBounds() {
		// TODO
		return null;
	}

	@Override
	public Vec3 getPosition() {
		return new Vec3(lightParameters.getPosition());
	}

	@Override
	public void setPosition(Vec3 position) {
		lightParameters = new LightSource(lightParameters, position);
		requestUpdate();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		requestUpdate();
	}

	@Override
	public void getAttributeSuppliers(ISuppliers suppliers) {
		suppliers.provide(GENERIC_LIGHT, () -> lightParameters);
	}

	@Override
	public boolean needsUpdate() {
		return updater.needsUpdate();
	}

	public LightSource getLightSource() {
		return lightParameters;
	}

	public void setLightParameters(LightSource lightParameters) {
		this.lightParameters = lightParameters;
		requestUpdate();
	}

	private void requestUpdate() {
		updater.requestUpdate();
	}
}
