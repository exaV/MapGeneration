/*
 * Copyright (c) 2014, FHNW (Simon Schubiger)
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
 *  Neither the name of FHNW nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
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

public class FloatList implements IAddOnlyFloatList {
	private float[] data = new float[16];
	private int     size;
	
	@Override
	public void add(float value) {
		ensureCapacity(size + 1);
		data[size++] = value;
	}

	@Override
	public void add(float value0, float value1) {
		ensureCapacity(size + 2);
		data[size++] = value0;
		data[size++] = value1;
	}

	@Override
	public void add(float value0, float value1, float value2) {
		ensureCapacity(size + 3);
		data[size++] = value0;
		data[size++] = value1;
		data[size++] = value2;
	}

	@Override
	public void add(float value0, float value1, float value2, float value3) {
		ensureCapacity(size + 4);
		data[size++] = value0;
		data[size++] = value1;
		data[size++] = value2;
		data[size++] = value3;
	}

	@Override
	public boolean add(float[] values) {
		if ((values != null) && (values.length > 0)) {
			ensureCapacity(size + values.length);
			System.arraycopy(values, 0, data, size, values.length);
			size += values.length;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public int capacity() {
		return data.length;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void ensureCapacity(int capacity) {
		if(data.length > capacity) return;
		
		int newCap = capacity;
		while(newCap < capacity)
			newCap *= 2;
		float[] tmp = data;
		data = new float[newCap];
		System.arraycopy(tmp, 0, data, 0, size);
	}

	public float[] toArray() {
		return Arrays.copyOf(data, size);
	}

	public void clear() {
		size = 0;
	}
}
