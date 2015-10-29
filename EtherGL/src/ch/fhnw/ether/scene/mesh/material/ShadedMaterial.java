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

package ch.fhnw.ether.scene.mesh.material;

import java.util.List;

import ch.fhnw.ether.scene.attribute.IAttribute;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.util.color.RGB;

public class ShadedMaterial extends AbstractMaterial {

	private RGB emission;
	private RGB ambient;
	private RGB diffuse;
	private RGB specular;
	private float shininess;
	private float strength;
	private float alpha;

	private volatile Texture colorMap;

	public ShadedMaterial(RGB diffuse) {
		this(RGB.BLACK, RGB.BLACK, diffuse, RGB.BLACK, 0, 0, 1);
	}

	public ShadedMaterial(RGB emission, RGB ambient, RGB diffuse, RGB specular, float shininess, float strength, float alpha) {
		this(emission, ambient, diffuse, specular, shininess, strength, alpha, null);
	}

	public ShadedMaterial(RGB emission, RGB ambient, RGB diffuse, RGB specular, float shininess, float strength, float alpha, Texture colorMap) {
		this.emission = emission;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.strength = strength;
		this.alpha = alpha;
		this.colorMap = colorMap;
	}

	public final RGB getEmission() {
		return emission;
	}

	public final void setEmission(RGB emission) {
		this.emission = emission;
		updateRequest();
	}

	public final RGB getAmbient() {
		return ambient;
	}

	public final void setAmbient(RGB ambient) {
		this.ambient = ambient;
		updateRequest();
	}

	public final RGB getDiffuse() {
		return diffuse;
	}

	public final void setDiffuse(RGB diffuse) {
		this.diffuse = diffuse;
		updateRequest();
	}

	public final RGB getSpecular() {
		return specular;
	}

	public final void setSpecular(RGB specular) {
		this.specular = specular;
		updateRequest();
	}

	public final float getShininess() {
		return shininess;
	}

	public final void setShininess(float shininess) {
		this.shininess = shininess;
		updateRequest();
	}

	public final float getStrength() {
		return strength;
	}

	public final void setStrength(float strength) {
		this.strength = strength;
		updateRequest();
	}

	public final float getAlpha() {
		return alpha;
	}

	public final void setAlpha(float alpha) {
		this.alpha = alpha;
		updateRequest();
	}

	public final Texture getColorMap() {
		return colorMap;
	}

	// TODO: need to handle this correctly (with / without color map)
	// public final void setColorMap(Texture colorMap) {
	// this.colorMap = colorMap;
	// updateRequest();
	// }

	@Override
	public List<IAttribute> getProvidedAttributes() {
		List<IAttribute> attributes = super.getProvidedAttributes();
		attributes.add(IMaterial.EMISSION);
		attributes.add(IMaterial.AMBIENT);
		attributes.add(IMaterial.DIFFUSE);
		attributes.add(IMaterial.SPECULAR);
		attributes.add(IMaterial.SHININESS);
		attributes.add(IMaterial.STRENGTH);
		attributes.add(IMaterial.ALPHA);
		if (colorMap != null) {
			attributes.add(IMaterial.COLOR_MAP);
		}
		return attributes;
	}

	@Override
	public List<IAttribute> getRequiredAttributes() {		
		List<IAttribute> attributes = super.getRequiredAttributes();
		if (colorMap != null) {
			attributes.add(IGeometry.COLOR_MAP_ARRAY);
		}
		return attributes;
	}
	
	@Override
	public List<Object> getData() {
		List<Object> data = super.getData();
		data.add(emission);
		data.add(ambient);
		data.add(diffuse);
		data.add(specular);
		data.add(shininess);
		data.add(strength);
		data.add(alpha);
		if (colorMap != null) {
			data.add(colorMap);
		}
		return data;
	}
}
