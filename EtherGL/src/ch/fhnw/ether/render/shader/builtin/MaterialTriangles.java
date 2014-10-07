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

package ch.fhnw.ether.render.shader.builtin;

import java.util.ArrayList;
import java.util.List;

import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.IUniformAttribute;
import ch.fhnw.ether.render.attribute.base.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.ColorMaterialUniform;
import ch.fhnw.ether.render.attribute.builtin.NormalArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.ProjMatrixUniform;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.render.attribute.builtin.TextureUniform;
import ch.fhnw.ether.render.attribute.builtin.ViewMatrixUniform;
import ch.fhnw.ether.render.shader.base.AbstractShader;

public class MaterialTriangles extends AbstractShader {
	private List<IUniformAttribute> uniformAttributes = new ArrayList<>(5);
	private List<IArrayAttribute> arrayAttributes = new ArrayList<>(5);

	public MaterialTriangles(boolean useMaterialColor, boolean useVertexColors, boolean useTexture, boolean normals) {
		super("unshaded_vct", PrimitiveType.TRIANGLE);
		if(useMaterialColor) {
			uniformAttributes.add(new ColorMaterialUniform());
		} else {
			uniformAttributes.add(new ColorMaterialUniform(() -> new float[]{1,1,1,1}));
		}
		if(useVertexColors) {
			arrayAttributes.add(new ColorArray());
		}
		if(useTexture) {
			uniformAttributes.add(new TextureUniform());
			arrayAttributes.add(new TexCoordArray());
		}
		if(normals) {
			arrayAttributes.add(new NormalArray());
		}
		
		arrayAttributes.add(new PositionArray());
		uniformAttributes.add(new ProjMatrixUniform());
		uniformAttributes.add(new ViewMatrixUniform());
		uniformAttributes.add(new BooleanUniformAttribute("shader.vertex_colors_flag", "useVertexColors", () -> useVertexColors));
		uniformAttributes.add(new BooleanUniformAttribute("shader.texture_flag", "useTexture", () -> useTexture));
		
	}

	@Override
	public void getUniformAttributes(List<IUniformAttribute> dst) {
		dst.addAll(uniformAttributes);
	}

	@Override
	public void getArrayAttributes(List<IArrayAttribute> dst) {
		dst.addAll(arrayAttributes);
	}

	@Override
	public String toString() {
		return "triangles[uniforms:" + uniformAttributes + " array attribs:" + arrayAttributes + "]";
	}
}
