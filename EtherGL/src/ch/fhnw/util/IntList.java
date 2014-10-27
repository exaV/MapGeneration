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

package ch.fhnw.util;

import java.util.Arrays;

public final class IntList {
	private int[] data;
	private int size;

	public IntList() {
		this(16);
	}

	public IntList(int size) {
		this.data = new int[size];
	}

	public IntList(int... values) {
		this.data = Arrays.copyOf(values, values.length);
		this.size = values.length;
	}

	public int get(int i) {
		return data[i];
	}

	public void add(int value) {
		ensureCapacity(size + 1);
		data[size++] = value;
	}

	public boolean add(int... values) {
		if ((values != null) && (values.length > 0)) {
			ensureCapacity(size + values.length);
			System.arraycopy(values, 0, data, size, values.length);
			size += values.length;
			return true;
		} else {
			return false;
		}
	}

	public void clear() {
		size = 0;
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int capacity() {
		return data.length;
	}

	private void ensureCapacity(int capacity) {
		if (data.length > capacity)
			return;

		int newCap = capacity;
		while (newCap < capacity)
			newCap *= 2;
		int[] tmp = data;
		data = new int[newCap];
		System.arraycopy(tmp, 0, data, 0, size);
	}
}
