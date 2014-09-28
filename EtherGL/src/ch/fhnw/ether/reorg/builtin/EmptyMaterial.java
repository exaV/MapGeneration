package ch.fhnw.ether.reorg.builtin;

import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;
import ch.fhnw.ether.reorg.api.IMaterial;

public class EmptyMaterial implements IMaterial {

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
	}

}
