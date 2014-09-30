package ch.fhnw.ether.reorg.api;

import ch.fhnw.ether.render.attribute.IUniformAttributeProvider;
import ch.fhnw.util.math.Mat4;

public interface ICamera extends I3DObject, IUniformAttributeProvider {

	Mat4 getProjectionMatrix();

	Mat4 getViewMatrix();

}
