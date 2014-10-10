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

package ch.fhnw.util.color;

public final class ColorUtil {
	public static final float EPS = 216.f / 24389.f;
	public static final float K = 24389.f / 27.f;

	public static final float XR = 0.964221f; // reference white D50
	public static final float YR = 1.0f;
	public static final float ZR = 0.825211f;

	private static final float[] LTABLE = new float[1024 * 1024 * 2];
	private static final float[] UTABLE = new float[1024 * 1024 * 2];
	private static final float[] VTABLE = new float[1024 * 1024 * 2];

	static {
		float[] luv = new float[3];
		int idx = 0;
		for (int r = 0; r < 255; r += 2)
			for (int g = 0; g < 255; g += 2)
				for (int b = 0; b < 255; b += 2) {
					getLUVfromRGBInternal(r, g, b, luv);
					LTABLE[idx] = luv[0];
					UTABLE[idx] = luv[1];
					VTABLE[idx] = luv[2];
					idx++;
				}
	}

	public static void getLUVfromRGB(int rgb, final float[] luv) {
		int idx = ((rgb >> 1 & 0x7F) | (rgb >> 2 & 0x3F80) | (rgb >> 3 & 0x1FC000));
		luv[0] = LTABLE[idx];
		luv[1] = UTABLE[idx];
		luv[2] = VTABLE[idx];
	}

	public static void getLUVfromRGBInternal(final int red, final int green, final int blue, final float[] luv) {
		// http://www.brucelindbloom.com

		if (red == 0 && green == 0 && blue == 0) {
			luv[0] = 0.0f;
			luv[1] = 0.0f;
			luv[2] = 0.0f;
			return;
		}

		float r, g, b, X, Y, Z, yr;
		float L;

		// RGB to XYZ

		r = red / 255.f; // R 0..1
		g = green / 255.f; // G 0..1
		b = blue / 255.f; // B 0..1

		// System.out.println("r = "+r+" g = "+g+" b = "+b);

		// assuming sRGB (D65)
		if (r <= 0.04045f)
			r = r / 12.92f;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045f)
			g = g / 12.92f;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045f)
			b = b / 12.92f;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		/*
		 * X = 0.436052025f*r + 0.385081593f*g + 0.143087414f *b; Y = 0.222491598f*r + 0.71688606f *g + 0.060621486f *b;
		 * Z = 0.013929122f*r + 0.097097002f*g + 0.71418547f *b;
		 */

		X = 0.4360747f * r + 0.3850649f * g + 0.1430804f * b;
		Y = 0.2225045f * r + 0.7168786f * g + 0.0606169f * b;
		Z = 0.0139322f * r + 0.0971045f * g + 0.7141733f * b;

		// XYZ to Luv

		float u, v, u_, v_, ur_, vr_;

		u_ = 4.f * X / (X + 15.f * Y + 3.f * Z);
		v_ = 9.f * Y / (X + 15.f * Y + 3.f * Z);

		ur_ = 4.f * XR / (XR + 15.f * YR + 3.f * ZR);
		vr_ = 9.f * YR / (XR + 15.f * YR + 3.f * ZR);

		yr = Y / YR;

		if (yr > EPS)
			L = (116.f * (float) Math.pow(yr, 1 / 3.f) - 16.f);
		else
			L = K * yr;

		u = 13.f * L * (u_ - ur_);
		v = 13.f * L * (v_ - vr_);

		luv[0] = L;
		luv[1] = u;
		luv[2] = v;
		// System.out.println("L="+L+" u="+u+" v="+v);
		/*
		 * luv[0] = (int) (2.55*L + .5); luv[1] = (int) (u + .5); luv[2] = (int) (v + .5);
		 */
	}

	public static float getL(int index) {
		return LTABLE[index];
	}

	public static float getU(int index) {
		return UTABLE[index];
	}

	public static float getV(int index) {
		return VTABLE[index];
	}
}
