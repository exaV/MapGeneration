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

package ch.fhnw.ether.scene.mesh.material;

import ch.fhnw.util.color.RGB;

public class ShadedMaterial extends AbstractMaterial {
	private final RGB emission;
	private final RGB ambient;
	private final RGB diffuse;
	private final RGB specular;
	private final float shininess;
	private final float strength;
	private final float alpha;
	
	private Texture colorMap;

	public ShadedMaterial(RGB emission, RGB ambient, RGB diffuse, RGB specular, float shininess, float strength, float alpha) {
		this.emission = emission;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.strength = strength;
		this.alpha = alpha;
	}

	public ShadedMaterial(RGB emission, RGB ambient, RGB diffuse, RGB specular, float shininess, float strength, float alpha, Texture colorMap) {
		this(emission, ambient, diffuse, specular, shininess, strength, alpha);
		this.colorMap = colorMap;
	}
	
	@Override
	public void getAttributeSuppliers(ISuppliers suppliers) {
		suppliers.provide(IMaterial.EMISSION, () -> emission);
		suppliers.provide(IMaterial.AMBIENT, () -> ambient);
		suppliers.provide(IMaterial.DIFFUSE, () -> diffuse);
		suppliers.provide(IMaterial.SPECULAR, () -> specular);
		suppliers.provide(IMaterial.SHININESS, () -> shininess);
		suppliers.provide(IMaterial.STRENGTH, () -> strength);
		suppliers.provide(IMaterial.ALPHA, () -> alpha);

		if (colorMap != null) {
			suppliers.provide(IMaterial.COLOR_MAP, () -> colorMap);
			suppliers.require(IMaterial.COLOR_MAP_ARRAY);
		}
	}
}
