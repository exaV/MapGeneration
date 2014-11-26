package ch.fhnw.ether.render;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.LineShader;
import ch.fhnw.ether.render.shader.builtin.PointShader;
import ch.fhnw.ether.render.shader.builtin.ShadedTriangleShader;
import ch.fhnw.ether.render.shader.builtin.UnshadedTriangleShader;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.attribute.ITypedAttribute;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.CustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;

public final class ShaderBuilder {
	private static final class Attributes implements IAttributeProvider.IAttributes {
		private final Map<String, Supplier<?>> providedAttributes = new HashMap<>();
		private final Set<String> requiredAttibutes = new HashSet<>();

		@Override
		public <T> boolean isProvided(ITypedAttribute<T> attribute) {
			return isProvided(attribute.id());
		}

		@Override
		public boolean isProvided(String id) {
			return providedAttributes.containsKey(id);
		}

		@Override
		public <T> void provide(ITypedAttribute<T> attribute, Supplier<T> supplier) {
			provide(attribute.id(), supplier);
		}

		@Override
		public void provide(String id, Supplier<?> supplier) {
			if (providedAttributes.put(id, supplier) != null)
				throw new IllegalArgumentException("duplicate attribute: " + id);
		}

		@Override
		public <T> boolean isRequired(ITypedAttribute<T> attribute) {
			return isRequired(attribute.id());
		}

		@Override
		public boolean isRequired(String id) {
			return requiredAttibutes.contains(id);
		}

		@Override
		public <T> void require(ITypedAttribute<T> attribute) {
			require(attribute.id());
		}

		@Override
		public void require(String id) {
			requiredAttibutes.add(id);
		}

		Supplier<?> getSupplier(ITypedAttribute<?> attr) {
			Supplier<?> supplier = providedAttributes.get(attr.id());
			if (supplier == null)
				throw new IllegalArgumentException("attribute not provided: " + attr.id());
			return supplier;
		}

		void clear() {
			providedAttributes.clear();
		}

		@Override
		public String toString() {
			String s = "";
			for (Entry<String, Supplier<?>> e : providedAttributes.entrySet()) {
				s += "[" + e.getKey() + ", " + e.getValue() + "] ";
			}
			return s;
		}
	}

	// FIXME: bug - only those arrays required by material should be used, but currently all arrays that a
	// geometry provides are used
	public static IShader buildShader(IMesh mesh, List<IAttributeProvider> providers) {
		Attributes attributes = new Attributes();

		// get all attributes and check if all required attributes are present
		providers.forEach((provider) -> provider.getAttributes(attributes));
		mesh.getMaterial().getAttributes(attributes);
		mesh.getGeometry().getAttributes(attributes);

		for (String id : attributes.requiredAttibutes) {
			if (!attributes.isProvided(id))
				throw new IllegalStateException("geometry does not provide required attribute " + id);
		}

		// create shader and attach all attributes this shader requires
		IShader shader = createShader(mesh, attributes);
		attachAttributes(shader, attributes, mesh);

		return shader;
	}

	public static void attachAttributes(IShader shader, List<IAttributeProvider> providers) {
		Attributes attributes = new Attributes();
		providers.forEach((provider) -> provider.getAttributes(attributes));
		attachAttributes(shader, attributes, null);
	}

	// FIXME: currently mesh only contains one geometry
	public static void attachAttributes(IShader shader, Attributes attributes, IMesh mesh) {
		// attach uniform attributes to shader
		for (IUniformAttribute<?> attr : shader.getUniforms()) {
			if (!attr.hasSupplier()) {
				attr.setSupplier(attributes.getSupplier(attr));
			}
		}

		// attach array attributes to shader
		if (mesh != null) {
			List<? extends IAttributeProvider> arrayAttributeProviders = Collections.singletonList(mesh.getGeometry());
			for (IAttributeProvider provider : arrayAttributeProviders) {
				attributes.clear();
				provider.getAttributes(attributes);
				for (IArrayAttribute<?> attr : shader.getArrays()) {
					attr.addSupplier(attributes.getSupplier(attr));
				}
			}
		}
	}

	// FIXME: make more flexible/dynamic (as soon as we have more builtin shaders): derive shader from attributes
	private static IShader createShader(IMesh mesh, IAttributeProvider.IAttributes attributes) {
		IMaterial material = mesh.getMaterial();
		if (material instanceof CustomMaterial) {
			return ((CustomMaterial) mesh.getMaterial()).getShader();
		}

		switch (mesh.getGeometry().getType()) {
		case POINTS:
			return new PointShader(attributes);
		case LINES:
			return new LineShader(attributes);
		case TRIANGLES:
			if (material instanceof ColorMaterial) {
				return new UnshadedTriangleShader(attributes);
			} else if (material instanceof ShadedMaterial) {
				return new ShadedTriangleShader(attributes);
			}
		default:
			throw new UnsupportedOperationException("material type not supported: " + material);
		}
	}
}
