/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

package ch.fhnw.ether.render;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.jogamp.opengl.GL3;

import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Mat4;

public final class Renderable {
	public static class RenderData {
		public final Object[] materialData;
		public final float[][] geometryData;
		public final Mat4 positionTransform;
		public final Mat3 normalTransform;

		public RenderData(IMesh mesh, boolean materialChanged, boolean geometryChanged) {
			if (materialChanged)
				materialData = mesh.getMaterial().getData().toArray();	
			else
				materialData = null;

			if (geometryChanged) {
				geometryData = mesh.getGeometry().getData();
				positionTransform = Mat4.multiply(Mat4.translate(mesh.getPosition()), mesh.getTransform());
				normalTransform = new Mat3(positionTransform).inverse().transpose();				
			} else {
				geometryData = null;
				positionTransform = null;
				normalTransform = null;
			}
		}		
	}

	private final IShader shader;
	private final VertexBuffer buffer;
	private final IMesh.Queue queue;
	private final Set<IMesh.Flag> flags;

	public Renderable(IMesh mesh, Map<IAttribute, Supplier<?>> globals) {
		this(null, mesh, globals);
	}

	public Renderable(IShader shader, IMesh mesh, Map<IAttribute, Supplier<?>> globals) {
		this.shader = ShaderBuilder.create(shader, mesh.getMaterial(), globals);
		this.buffer = new VertexBuffer(this.shader, mesh.getGeometry().getAttributes());
		this.queue = mesh.getQueue();
		this.flags = mesh.getFlags();
	}

	public void update(GL3 gl, RenderData data) {
		if (data == null)
			return;
		if (data.materialData != null)
			shader.update(gl, data.materialData);
		if (data.geometryData != null)
			buffer.load(gl, shader, data.geometryData, data.positionTransform, data.normalTransform);
	}

	public void render(GL3 gl) {
		shader.enable(gl);
		shader.render(gl, buffer);
		shader.disable(gl);
	}

	public IMesh.Queue getQueue() {
		return queue;
	}

	public boolean containsFlag(IMesh.Flag flag) {
		return flags.contains(flag);
	}

	public IVertexBuffer getBuffer() {
		return buffer;
	}

	@Override
	public String toString() {
		return "renderable[queue=" + getQueue() + " shader=" + shader + " buffer=" + buffer + "]";
	}
}
