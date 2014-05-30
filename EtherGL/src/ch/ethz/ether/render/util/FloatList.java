/*
 * Copyright (c) 2013 - 2014, ETH Zurich & FHNW (Stefan Muller Arisona)
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
 *  Neither the name of ETH Zurich nor the names of its contributors may be
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

package ch.ethz.ether.render.util;

import java.nio.FloatBuffer;
import ch.ethz.util.IAddOnlyFloatList;

import com.jogamp.common.nio.Buffers;

/**
 * Implementation of an "add only" float list. Used for collecting data from
 * different sources (e.g. VBOs).
 * 
 * XXX Note: this is a rather primitive implementation, in particular in terms
 * of reallocation. The basic idea is to use only as little instances of float
 * buffer as necessary (i.e., 1).
 * 
 * @author radar
 * 
 */
public final class FloatList implements IAddOnlyFloatList {
	private static final int OVER_ALLOCATE = 10 * 1024 * 1024;

	FloatBuffer buffer;

	public FloatList() {
		this(0);
	}

	public FloatList(int initialCapacity) {
		ensureCapacity(initialCapacity);
	}

	@Override
	public void add(float value) {
		ensureCapacity(buffer.limit() + 1);
		buffer.put(value);
	}

	@Override
	public void add(float value0, float value1) {
		ensureCapacity(buffer.limit() + 2);
		buffer.put(value0);
		buffer.put(value1);
	}

	@Override
	public void add(float value0, float value1, float value2) {
		ensureCapacity(buffer.limit() + 3);
		buffer.put(value0);
		buffer.put(value1);
		buffer.put(value2);
	}

	@Override
	public void add(float value0, float value1, float value2, float value3) {
		ensureCapacity(buffer.limit() + 4);
		buffer.put(value0);
		buffer.put(value1);
		buffer.put(value2);
		buffer.put(value3);
	}

	@Override
	public boolean add(float[] values) {
		if ((values != null) && (values.length > 0)) {
			ensureCapacity(buffer.limit() + values.length);
			buffer.put(values);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int size() {
		return buffer.limit();
	}

	@Override
	public int capacity() {
		return buffer.capacity();
	}

	@Override
	public boolean isEmpty() {
		return buffer.limit() == 0;
	}

	public void clear() {
		buffer.limit(0);
	}

	public FloatBuffer buffer() {
		return buffer;
	}

	public float[] toArray() {
		if (buffer.limit() == 0)
			return null;
		float[] array = new float[buffer.limit()];
		buffer.rewind();
		buffer.get(array);
		return array;
	}

	@Override
	public void ensureCapacity(int capacity) {
		if (buffer == null) {
			buffer = Buffers.newDirectFloatBuffer(capacity);
		} else if (buffer.capacity() < capacity) {
			FloatBuffer b = Buffers.newDirectFloatBuffer(capacity + OVER_ALLOCATE);
			buffer.rewind();
			b.put(buffer);
			buffer = b;
		}
		buffer.limit(capacity);
	}
}
