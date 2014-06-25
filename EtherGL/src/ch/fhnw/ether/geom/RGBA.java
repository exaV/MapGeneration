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
 */package ch.fhnw.ether.geom;

public class RGBA {
	public static final RGBA BLACK = new RGBA();
	public static final RGBA WHITE = new RGBA(1, 1, 1, 1);

	public static final RGBA RED = new RGBA(1, 0, 0, 1);
	public static final RGBA GREEN = new RGBA(0, 1, 0, 1);
	public static final RGBA BLUE = new RGBA(0, 0, 1, 1);

	public static final RGBA YELLOW = new RGBA(1, 1, 0, 1);
	public static final RGBA MAGENTA = new RGBA(1, 0, 1, 1);
	public static final RGBA CYAN = new RGBA(0, 1, 1, 1);

	public static final RGBA GRAY = new RGBA(0.5f, 0.5f, 0.5f, 1);
	public static final RGBA LIGHT_GRAY = new RGBA(0.75f, 0.75f, 0.75f, 1);
	public static final RGBA DARK_GRAY = new RGBA(0.25f, 0.25f, 0.25f, 1);

	public float[] v;

	public RGBA() {
		v = new float[4];
	}

	public RGBA(RGBA rgba) {
		this(rgba.v);
	}

	public RGBA(float[] rgba) {
		this(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public RGBA(float r, float g, float b, float a) {
		v = new float[4];
		v[0] = r;
		v[1] = g;
		v[2] = b;
		v[3] = a;
	}

	@Override
	public String toString() {
		return "rgba[" + v[0] + " " + v[1] + " " + v[2] + " " + v[3] + "]";
	}
}
