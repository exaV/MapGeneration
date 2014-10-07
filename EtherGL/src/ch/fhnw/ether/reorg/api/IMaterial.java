package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;

public interface IMaterial extends IUniformAttributeProvider {
	
	IUniformAttribute[] getUniformAttributes();
	
	/**
	 * Some empty material, does just provide zero attributes
	 */
	public final static IMaterial EmptyMaterial = new IMaterial() {
		@Override
		public void getAttributeSuppliers(ISuppliers dst) {}

		@Override
		public IUniformAttribute[] getUniformAttributes() {
			return new IUniformAttribute[0];
		}
	};
	
}
