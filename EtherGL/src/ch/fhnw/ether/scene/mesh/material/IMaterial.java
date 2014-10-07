package ch.fhnw.ether.scene.mesh.material;

import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;

public interface IMaterial extends IUniformAttributeProvider {
		
	/**
	 * Some empty material, does just provide zero attributes
	 */
	public final static IMaterial EmptyMaterial = new IMaterial() {
		@Override
		public void getAttributeSuppliers(ISuppliers dst) {}
	};
	
}
