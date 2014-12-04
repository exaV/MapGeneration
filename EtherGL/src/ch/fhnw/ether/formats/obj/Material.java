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

package ch.fhnw.ether.formats.obj;

import ch.fhnw.ether.image.Frame;
import ch.fhnw.util.color.RGB;

public class Material {
	private Frame texture;
	private RGB Ka;
	private RGB Kd;
	private RGB Ks;
	private float shininess;
	private String name;
	public String texName;

	public Material(String name) {
		Ka = new RGB(1, 1, 1);
		Kd = new RGB(1, 1, 1);
		Ks = new RGB(0.5f, 0.5f, 0.5f);
		texture = null;
		name = null;
		texName = null;
		shininess = 0;
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Frame getTexture() {
		return texture;
	}

	public void setTexture(Frame texture) {
		this.texture = texture;
	}

	public RGB getKa() {
		return Ka;
	}

	public RGB getKd() {
		return Kd;
	}

	public RGB getKs() {
		return Ks;
	}

	public float getShininess() {
		return shininess;
	}

	public void setKa(RGB ka) {
		Ka = ka;
	}

	public void setKd(RGB kd) {
		Kd = kd;
	}

	public void setKs(RGB ks) {
		Ks = ks;
	}

	public void setShininess(float s) {
		shininess = s;
	}

}
