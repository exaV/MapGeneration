package ch.fhnw.ether.scene.mesh.material;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ether.render.attribute.IAttribute.ISuppliers;

public class CombinedMaterial implements IMaterial {
	
	List<IMaterial> materials = new ArrayList<>(5);
	
	public CombinedMaterial(IMaterial material) {
		materials.add(material);
	}
	
	public CombinedMaterial(IMaterial material0, IMaterial material1) {
		materials.add(material0);
		materials.add(material1);
	}
	
	public CombinedMaterial(IMaterial material0, IMaterial material1, IMaterial material2) {
		materials.add(material0);
		materials.add(material1);
		materials.add(material2);
	}

	@Override
	public void getAttributeSuppliers(ISuppliers dst) {
		for(IMaterial m : materials) {
			m.getAttributeSuppliers(dst);
		}
	}

}
