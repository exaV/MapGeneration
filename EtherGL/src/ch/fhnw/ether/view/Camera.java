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
 */

package ch.fhnw.ether.view;

import ch.fhnw.ether.reorg.base.BaseCamera;
import ch.fhnw.util.math.Mat4;

/**
 * OpenGL-aligned camera model
 *
 * @author radar
 */
public class Camera extends BaseCamera {
	private static final boolean KEEP_ROT_X_POSITIVE = true;

	private final Object lock = new Object();

	private boolean locked = false;

	private float distance = 2.0f;

	private boolean ortho = false;

	public Camera() {
		super();
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	public void setNear(float near) {
		if (locked)
			return;
		super.setNear(near);
	}

	@Override
	public void setFar(float far) {
		if (locked)
			return;
		super.setFar(far);
	}

	@Override
	public void setFov(float fov) {
		if (locked)
			return;
		super.setFov(fov);
	}

	public float getDistance() {
		return distance;
	}

	public boolean isOrtho() {
		return ortho;
	}

	public void setOrtho(boolean ortho) {
		this.ortho = ortho;
	}

	public void setMatrices(Mat4 projMatrix, Mat4 viewMatrix) {
		synchronized (lock) {
			if (projMatrix == null) {
				this.locked = false;
			} else {
				this.locked = true;
				setProjectionMatrix(projMatrix);
				setViewMatrix(viewMatrix);
			}
		}
	}

	@Override
	public Mat4 getProjectionMatrix() {
		synchronized (lock) {
			return super.getProjectionMatrix();
		}
	}

	@Override
	public Mat4 getViewMatrix() {
		synchronized (lock) {
			return super.getViewMatrix();
		}
	}

	@Override
	public Mat4 getViewProjMatrix() {
		synchronized (lock) {
			return Mat4.product(getProjectionMatrix(), getViewMatrix());
		}
	}

	@Override
	public Mat4 getViewProjInvMatrix() {
		synchronized (lock) {
			return getViewProjMatrix().inverse();
		}
	}

	public Mat4 getViewProjInvTpMatrix() {
		synchronized (lock) {
			return getViewProjInvMatrix().transposed();
		}
	}

}
