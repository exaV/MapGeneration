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
import ch.fhnw.ether.render.shader.IShader.Attributes;
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
	private static class Suppliers implements IAttributeProvider.ISuppliers {
		private final Map<String, Supplier<?>> providedAttributes = new HashMap<>();
		private final Set<String> requiredAttibutes = new HashSet<>();

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
		public <T> void require(ITypedAttribute<T> attribute) {
			require(attribute.id());
		}

		@Override
		public void require(String id) {
			requiredAttibutes.add(id);
		}

		Supplier<?> get(ITypedAttribute<?> attr) {
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

	public static IShader buildShader(IMesh mesh, AttributeProviders providers) {
		Suppliers suppliers = new Suppliers();

		// FIXME: here's a bug - only those arrays required by material should be used, but currently all arrays that a
		// geometry provides are used

		// 0. get all attributes, and check if all required attributes are present

		providers.getAttributeSuppliers(suppliers);
		mesh.getMaterial().getAttributeSuppliers(suppliers);
		mesh.getGeometry().getAttributeSuppliers(suppliers);

		for (String id : suppliers.requiredAttibutes) {
			if (!suppliers.providedAttributes.containsKey(id))
				throw new IllegalStateException("geometry does not provide required attribute " + id);
		}

		// 1. create shader and get all attributes this shader requires

		IShader shader = createShader(mesh, new Attributes(suppliers.providedAttributes.keySet()));

		List<IUniformAttribute<?>> uniforms = shader.getUniforms();
		List<IArrayAttribute<?>> arrays = shader.getArrays();


		// 2. bind shader attributes to provided global attributes (matrices, lights), material and geometry

		// 2.1. handle uniform attributes

		for (IUniformAttribute<?> attr : uniforms) {
			if (!attr.hasSupplier()) {
				attr.setSupplier(suppliers.get(attr));
			}
		}

		// 2.2. handle array attributes

		// FIXME: currently mesh only contains one geometry
		List<? extends IAttributeProvider> arrayAttributeProviders = Collections.singletonList(mesh.getGeometry());
		for (IAttributeProvider provider : arrayAttributeProviders) {
			suppliers.clear();
			provider.getAttributeSuppliers(suppliers);
			for (IArrayAttribute<?> attr : arrays) {
				attr.addSupplier(suppliers.get(attr));
			}
		}
		
		return shader;
	}

	// FIXME: make more flexible/dynamic (as soon as we have more builtin shaders): derive shader from attributes
	private static IShader createShader(IMesh mesh, Attributes attributes) {
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


