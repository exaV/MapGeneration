package ch.fhnw.ether.scene.light;

import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.Vec4;
import ch.fhnw.util.math.geometry.BoundingBox;

public class GenericLight implements ILight {

	public static final class LightSource {
		public enum Type {
			OFF, DIRECTIONAL_LIGHT, POINT_LIGHT, SPOT_LIGHT
		}

		private final Type type;

		private final Vec4 position;
		private final RGB ambient;
		private final RGB color;

		private final Vec3 spotDirection;
		private final float spotCosCutoff;
		private final float spotExponent;

		private final float constantAttenuation;
		private final float linearAttenuation;
		private final float quadraticAttenuation;

		public LightSource(Type type, Vec3 position, RGB ambient, RGB color, Vec3 spotDirection, float spotCosCutoff, float spotExponent,
				float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
			this.type = type;
			this.position = makePosition(type, position);
			this.ambient = ambient;
			this.color = color;
			this.spotDirection = spotDirection != null ? spotDirection : Vec3.Z_NEG;
			this.spotCosCutoff = spotCosCutoff;
			this.spotExponent = spotExponent;
			this.constantAttenuation = constantAttenuation;
			this.linearAttenuation = linearAttenuation;
			this.quadraticAttenuation = quadraticAttenuation;
		}

		public LightSource(LightSource source, Vec3 position) {
			this.type = source.type;
			this.position = makePosition(source.type, position);
			this.ambient = source.ambient;
			this.color = source.color;
			this.spotDirection = source.spotDirection;
			this.spotCosCutoff = source.spotCosCutoff;
			this.spotExponent = source.spotExponent;
			this.constantAttenuation = source.constantAttenuation;
			this.linearAttenuation = source.linearAttenuation;
			this.quadraticAttenuation = source.quadraticAttenuation;
		}

		private static Vec4 makePosition(Type type, Vec3 position) {
			if (type == Type.DIRECTIONAL_LIGHT) {
				position = position.normalize();
				return new Vec4(position.x, position.y, position.z, 0);
			} else {
				return new Vec4(position.x, position.y, position.z, 1);
			}
		}

		public static LightSource directionalSource(Vec3 direction, RGB ambient, RGB color) {
			return new LightSource(Type.DIRECTIONAL_LIGHT, direction, ambient, color, null, 0, 0, 0, 0, 0);
		}

		public static LightSource pointSource(Vec3 position, RGB ambient, RGB color, float constantAttenuation, float linearAttenuation,
				float quadraticAttenuation) {
			return new LightSource(Type.POINT_LIGHT, position, ambient, color, null, 0, 0, constantAttenuation, linearAttenuation, quadraticAttenuation);
		}

		public static LightSource spotSource(Vec3 position, RGB ambient, RGB color, Vec3 spotDirection, float spotCutoff, float spotExponent,
				float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
			return new LightSource(Type.SPOT_LIGHT, position, ambient, color, spotDirection, (float) Math.cos(Math.toRadians(spotCutoff)), spotExponent,
					constantAttenuation, linearAttenuation, quadraticAttenuation);
		}

		public Type getType() {
			return type;
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
