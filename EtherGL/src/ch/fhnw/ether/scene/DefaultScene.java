/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.IMesh;

public class DefaultScene implements IScene {

	private final IController controller;

	private final List<IMesh> meshes = new ArrayList<>();
	private final List<ICamera> cameras = new ArrayList<>();
	private final List<ILight> lights = new ArrayList<>();
	private final List<I3DObject> objects = new ArrayList<>();
	
	public DefaultScene(IController controller) {
		this.controller = controller;
	}
	
	public DefaultScene(IController controller, ICamera camera, List<IMesh> meshes) {
		this(controller);
		meshes.addAll(meshes);
		objects.addAll(meshes);
	}
	
	@Override
	public final void add3DObject(I3DObject object) {
		if (object instanceof IMesh) {
			meshes.add((IMesh)object);
			controller.getRenderer().addMesh((IMesh)object);
		}
		if (object instanceof ICamera)
			cameras.add((ICamera)object);
		if (object instanceof ILight)
			lights.add((ILight)object);		
		objects.add(object);
	}
	
	@Override
	public final void remove3DObject(I3DObject object) {
		if (object instanceof IMesh) {
			meshes.remove(object);
			controller.getRenderer().removeMesh((IMesh)object);
		}
		if (object instanceof ICamera)
			cameras.remove(object);
		if (object instanceof ILight)
			lights.remove(object);		
		objects.remove(object);
	}

	@Override
	public final List<? extends I3DObject> get3DObjects() {
		return Collections.unmodifiableList(objects);
	}

	@Override
	public final List<? extends IMesh> getMeshes() {
		return Collections.unmodifiableList(meshes);
	}

	@Override
	public final List<? extends ICamera> getCameras() {
		return Collections.unmodifiableList(cameras);
	}

	@Override
	public final List<ILight> getLights() {
		return Collections.unmodifiableList(lights);
	}

	protected final IController getController() {
		return controller;
	}
}
