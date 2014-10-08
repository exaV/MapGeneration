/*
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich (Stefan Muller Arisona & Simon Schubiger)
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona & Simon Schubiger
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

package ch.fhnw.ether.examples.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.light.ILight;
import ch.fhnw.ether.scene.mesh.GenericMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.I3DObject;
import ch.fhnw.util.math.geometry.Primitives;

public class MappingTriangleScene implements IScene {
	
	private List<IMesh> objects = new ArrayList<>(10);
	private IMaterial material = new ColorMaterial(RGBA.WHITE);
	private ICamera camera;
	
    public MappingTriangleScene(ICamera camera) {
        for (int i = 0; i < 10; ++i) {
            GenericMesh cube = new GenericMesh(PrimitiveType.TRIANGLE, material);
            cube.setGeometry(Primitives.UNIT_CUBE_TRIANGLES);
            double s = 0.1 + 0.1 * Math.random();
            double tx = -1 + 2 * Math.random();
            double ty = -1 + 2 * Math.random();
            cube.getGeometry().setScale(new Vec3(s, s, s));
            cube.getGeometry().setRotation(new Vec3(0, 0, 360 * Math.random()));
            cube.getGeometry().setTranslation(new Vec3(tx, ty, 0));
            objects.add(cube);
        }
        this.camera = camera;
    }

	@Override
	public List<? extends I3DObject> getObjects() {
		return objects;
	}

	@Override
	public List<? extends IMesh> getMeshes() {
		return objects;
	}

	@Override
	public void setRenderer(
			IRenderer renderer) {
		
		final List<IGeometry> geo = Collections.synchronizedList(objects.stream().map((x) -> {return x.getGeometry();}).collect(Collectors.toList()));
		IShader s = new MaterialShader(EnumSet.of(ShaderInput.MATERIAL_COLOR));
		IRenderable ret = renderer.createRenderable(Pass.DEPTH, s, material, geo);
		
		renderer.addRenderables(ret);
	}

	@Override
	public List<? extends ICamera> getCameras() {
		return Collections.singletonList(camera);
	}

	@Override
	public List<? extends ILight> getLights() {
		return Collections.emptyList();
	}

	@Override
	public void renderUpdate() {
		//updates not needed
	}


}
