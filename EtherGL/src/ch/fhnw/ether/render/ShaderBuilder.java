package ch.fhnw.ether.render;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.LineShader;
import ch.fhnw.ether.render.shader.builtin.PointShader;
import ch.fhnw.ether.render.shader.builtin.ShadedTriangleShader;
import ch.fhnw.ether.render.shader.builtin.UnshadedTriangleShader;
import ch.fhnw.ether.render.variable.IShaderArray;
import ch.fhnw.ether.render.variable.IShaderUniform;
import ch.fhnw.ether.render.variable.IShaderVariable;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.attribute.IAttributeProvider;
import ch.fhnw.ether.scene.attribute.ITypedAttribute;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.CustomMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.ShadedMaterial;

public final class ShaderBuilder {
	private static final class Attributes implements IAttributeProvider.IAttributes {
		private final Map<IAttribute, Supplier<?>> attributes = new HashMap<>();

		@Override
		public <T> void provide(ITypedAttribute<T> attribute, Supplier<T> supplier) {
			if (attributes.put(attribute, supplier) != null)
				throw new IllegalArgumentException("duplicate attribute: " + attribute);
		}

		@Override
		public void require(IAttribute attribute) {
			attributes.put(attribute, null);
		}

		Supplier<?> getSupplier(IShader shader, IShaderVariable<?> variable) {
			for (Entry<IAttribute, Supplier<?>> entry : attributes.entrySet()) {
				if (entry.getKey().id().equals(variable.id()))
					return entry.getValue();
			}
			throw new IllegalArgumentException("shader " + shader + " requires attribute " + variable.id());
		}

		@Override
		public String toString() {
			String s = "";
			for (Entry<IAttribute, Supplier<?>> e : attributes.entrySet()) {
				s += "[" + e.getKey() + ", " + e.getValue() + "] ";
			}
			return s;
		}
	}

	@SuppressWarnings("unchecked")
	public static <S extends IShader> S create(S shader, IMesh mesh, List<IAttributeProvider> providers) {
		Attributes attributes = new Attributes();

		// get attributes from mesh and from renderer)
		if (mesh != null)
			mesh.getMaterial().getAttributes(attributes);
		if (providers != null)
			providers.forEach((provider) -> provider.getAttributes(attributes));

		// create shader and attach all attributes this shader requires
		if (shader == null)
			shader = (S) createShader(mesh, Collections.unmodifiableSet(attributes.attributes.keySet()));

		attachUniforms(shader, attributes);

		if (mesh != null)
			attachArrays(shader, mesh);

		return shader;
	}

	// as soon as we have more builtin shaders we should move to a more flexible scheme, e.g. derive shader from
	// provided attributes
	private static IShader createShader(IMesh mesh, Collection<IAttribute> attributes) {
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

	private static void attachUniforms(IShader shader, Attributes attributes) {
		for (IShaderUniform<?> uniform : shader.getUniforms()) {
			if (!uniform.hasSupplier()) {
				uniform.setSupplier(attributes.getSupplier(shader, uniform));
			}
		}
	}

	// FIXME: currently mesh only contains one geometry
	// XXX i think this part can be reduced to something cleaner
	private static void attachArrays(IShader shader, IMesh mesh) {
		List<? extends IAttributeProvider> arrayAttributeProviders = Collections.singletonList(mesh.getGeometry());
		for (IAttributeProvider provider : arrayAttributeProviders) {
			Attributes attributes = new Attributes();
			provider.getAttributes(attributes);
			for (IShaderArray<?> array : shader.getArrays()) {
				array.addSupplier(attributes.getSupplier(shader, array));
			}
		}
	}
}
