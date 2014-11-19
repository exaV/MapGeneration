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

package ch.fhnw.ether.render.attribute.builtin;

import javax.media.opengl.GL3;

import ch.fhnw.ether.render.attribute.base.AbstractUniformAttribute;
import ch.fhnw.ether.render.attribute.base.BooleanUniformAttribute;
import ch.fhnw.ether.render.attribute.base.FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.base.Vec3FloatUniformAttribute;
import ch.fhnw.ether.render.attribute.base.Vec4FloatUniformAttribute;
import ch.fhnw.ether.render.gl.Program;
import ch.fhnw.ether.scene.light.GenericLight;
import ch.fhnw.ether.scene.light.GenericLight.LightSource;

public class LightUniforms extends AbstractUniformAttribute<LightSource> {

	private final BooleanUniformAttribute isLocal;
	private final BooleanUniformAttribute isSpot;
	
	private final Vec4FloatUniformAttribute position;
	private final Vec3FloatUniformAttribute ambient;
	private final Vec3FloatUniformAttribute color;

	private final Vec3FloatUniformAttribute spotDirection;
	private final FloatUniformAttribute spotCosCutoff;
	private final FloatUniformAttribute spotExponent;

	private final FloatUniformAttribute constantAttenuation;
	private final FloatUniformAttribute linearAttenuation;
	private final FloatUniformAttribute quadraticAttenuation;
	
	public LightUniforms() {
		super(GenericLight.GENERIC_LIGHT, "void");
		isLocal = new BooleanUniformAttribute("", "lightIsLocal", () -> get().isLocal());	
		isSpot = new BooleanUniformAttribute("", "lightIsSpot", () -> get().isSpot());	

		position = new Vec4FloatUniformAttribute("", "lightPosition", () -> get().getPosition());
		ambient = new Vec3FloatUniformAttribute("", "lightAmbientColor", () -> get().getAmbient());
		color = new Vec3FloatUniformAttribute("", "lightColor", () -> get().getColor());

		spotDirection = new Vec3FloatUniformAttribute("", "lightSpotDirection", () -> get().getSpotDirection());
		spotCosCutoff = new FloatUniformAttribute("", "lightSpotCosCutoff", () -> get().getSpotCosCutoff());
		spotExponent = new FloatUniformAttribute("", "lightSpotExponent", () -> get().getSpotExponent());

		constantAttenuation = new FloatUniformAttribute("", "lightConstantAttenuation", () -> get().getConstantAttenuation());
		linearAttenuation = new FloatUniformAttribute("", "lightLinearAttenuation", () -> get().getLinearAttenuation());
		quadraticAttenuation = new FloatUniformAttribute("", "lightQuadraticAttenuation", () -> get().getQuadraticAttenuation());
	}

	@Override
	public void enable(GL3 gl, Program program) {
		isLocal.enable(gl, program);
		isSpot.enable(gl, program);

		position.enable(gl, program);
		ambient.enable(gl, program);
		color.enable(gl, program);
		
		spotDirection.enable(gl, program);
		spotCosCutoff.enable(gl, program);
		spotExponent.enable(gl, program);

		constantAttenuation.enable(gl, program);
		linearAttenuation.enable(gl, program);
		quadraticAttenuation.enable(gl, program);
	}
}
