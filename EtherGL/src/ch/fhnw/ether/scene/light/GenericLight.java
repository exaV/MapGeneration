package ch.fhnw.ether.scene.light;

import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.color.RGB;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public class GenericLight implements ILight {
	public static LightAttribute GENERIC_LIGHT = new LightAttribute("builtin.light.generic_light");

	public static class LightParameters {
		private final boolean isLocal;
		private final boolean isSpot;

		private final float[] position;
		private final float[] ambient;
		private final float[] color;

		private final float[] spotDirection;
		private final float spotCosCutoff;
		private final float spotExponent;

		private final float constantAttenuation;
		private final float linearAttenuation;
		private final float quadraticAttenuation;

		public LightParameters(boolean isLocal, boolean isSpot, Vec3 position, RGB ambient, RGB color, Vec3 spotDirection, float spotCosCutoff,
				float spotExponent, float constantAttenuation, float linearAttenuation, float quadraticAttenuation) {
			this.isLocal = isLocal;
			this.isSpot = isSpot;
			this.position = position.toArray();
			this.ambient = ambient.toArray();
			this.color = color.toArray();
			this.spotDirection = spotDirection != null ? spotDirection.toArray() : null;
			this.spotCosCutoff = spotCosCutoff;
			this.spotExponent = spotExponent;
			this.constantAttenuation = constantAttenuation;
			this.linearAttenuation = linearAttenuation;
			this.quadraticAttenuation = quadraticAttenuation;
		}

		public LightParameters(LightParameters parameters, Vec3 position) {
			this.isLocal = parameters.isLocal;
			this.isSpot = parameters.isSpot;
			this.position = position.toArray();
			this.ambient = parameters.ambient;
			this.color = parameters.color;
			this.spotDirection = parameters.spotDirection;
			this.spotCosCutoff = parameters.spotCosCutoff;
			this.spotExponent = parameters.spotExponent;
			this.constantAttenuation = parameters.constantAttenuation;
			this.linearAttenuation = parameters.linearAttenuation;
			this.quadraticAttenuation = parameters.quadraticAttenuation;
		}

		public boolean isLocal() {
			return isLocal;
		}

		public boolean isSpot() {
			return isSpot;
		}

		public float[] getPosition() {
			return position;
		}

		public float[] getAmbient() {
			return ambient;
		}

		public float[] getColor() {
			return color;
		}

		public float[] getSpotDirection() {
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

	private LightParameters lightParameters;

	protected GenericLight(LightParameters lightParameters) {
		this.lightParameters = lightParameters;
	}

	@Override
	public BoundingBox getBounds() {
		// FIXME: correct...
		BoundingBox b = new BoundingBox();
		b.add(lightParameters.getPosition());
		return b;
	}

	@Override
	public Vec3 getPosition() {
		return new Vec3(lightParameters.getPosition());
	}

	@Override
	public void setPosition(Vec3 position) {
		lightParameters = new LightParameters(lightParameters, position);
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

	public LightParameters getLightParameters() {
		return lightParameters;
	}

	public void setLightParameters(LightParameters lightParameters) {
		this.lightParameters = lightParameters;
		requestUpdate();
	}

	private void requestUpdate() {
		updater.requestUpdate();
	}
}
