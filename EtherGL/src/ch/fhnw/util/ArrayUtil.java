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
 */package ch.fhnw.util;

import java.util.function.Predicate;

public final class ArrayUtil {
	public static <T> T find(T[] array, Predicate<T> predicate) {
		for (T element : array) {
			if (predicate.test(element))
				return element;
		}
		return null;
	}

	public static void interleave(IAddOnlyFloatList dst, float[] v1, int s1, float[] v2, int s2) {
		if (v1 == null)
			return;
		int i1 = 0;
		int i2 = 0;
		for (; i1 < v1.length; i1 += s1, i2 += s2) {
			for (int i = i1; i < i1 + s1; ++i)
				dst.add(v1[i]);
			if (v2 != null) {
				for (int i = i2; i < i2 + s2; ++i)
					dst.add(v2[i]);
			}
		}
	}

	public static void interleave(IAddOnlyFloatList dst, float[] v1, int s1, float[] v2, int s2, float[] v3, int s3) {
		if (v1 == null)
			return;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		for (; i1 < v1.length; i1 += s1, i2 += s2, i3 += s3) {
			for (int i = i1; i < i1 + s1; ++i)
				dst.add(v1[i]);
			if (v2 != null) {
				for (int i = i2; i < i2 + s2; ++i)
					dst.add(v2[i]);
			}
			if (v3 != null) {
				for (int i = i3; i < i3 + s3; ++i)
					dst.add(v3[i]);
			}
		}
	}

	public static void interleave(IAddOnlyFloatList dst, float[] v1, int s1, float[] v2, int s2, float[] v3, int s3, float[] v4, int s4) {
		if (v1 == null)
			return;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		for (; i1 < v1.length; i1 += s1, i2 += s2, i3 += s3) {
			for (int i = i1; i < i1 + s1; ++i)
				dst.add(v1[i]);
			if (v2 != null) {
				for (int i = i2; i < i2 + s2; ++i)
					dst.add(v2[i]);
			}
			if (v3 != null) {
				for (int i = i3; i < i3 + s3; ++i)
					dst.add(v3[i]);
			}
			if (v4 != null) {
				for (int i = i4; i < i4 + s4; ++i)
					dst.add(v4[i]);
			}
		}
	}
}
