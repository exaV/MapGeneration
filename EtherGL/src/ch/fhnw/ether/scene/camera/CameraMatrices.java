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

package ch.fhnw.ether.scene.camera;

import ch.fhnw.util.math.Mat3;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;

public final class CameraMatrices {
	private final Vec3 position;
	private final Vec3 target;
	private final Vec3 up;

	private final float fov;
	private final float near;
	private final float far;

	private final float aspect;

	private Mat4 viewMatrix;
	private Mat4 projMatrix;
	private Mat4 viewProjMatrix;
	private Mat4 viewProjInvMatrix;
	private Mat3 normalMatrix;
	private Vec3 eyeDirection;

	public CameraMatrices(Vec3 position, Vec3 target, Vec3 up, float fov, float near, float far, float aspect) {
		this.position = position;
		this.target = target;
		this.up = up;
		this.fov = fov;
		this.near = near;
		this.far = far;
		this.aspect = aspect;
	}
	
	public CameraMatrices(Mat4 viewMatrix, Mat4 projMatrix) {
		position = target = up = null;
		fov = near = far = aspect = 0;
		this.viewMatrix = viewMatrix;
		this.projMatrix = projMatrix;
	}

	public Mat4 getViewMatrix() {
		if (viewMatrix == null) {
			viewMatrix = Mat4.lookAt(position, target, up);
		}
		return viewMatrix;
	}

	public Mat4 getProjMatrix() {
		if (projMatrix == null) {
			projMatrix = Mat4.perspective(fov, aspect, near, far);
		}
		return projMatrix;
	}

	public Mat4 getViewProjMatrix() {
		if (viewProjMatrix == null) {
			viewProjMatrix = Mat4.product(getProjMatrix(), getViewMatrix());
		}
		return viewProjMatrix;
	}

	public Mat4 getViewProjInvMatrix() {
		if (viewProjInvMatrix == null) {
			viewProjInvMatrix = getViewProjMatrix().inverse();
		}
		return viewProjInvMatrix;
	}

	public Mat3 getNormalMatrix() {
		if (normalMatrix == null) {
			normalMatrix = new Mat3(getViewMatrix()).inverse().transpose();
		}
		return normalMatrix;
	}
	
	public Vec3 getEyeDirection() {
		if (eyeDirection == null) {
			eyeDirection = Vec3.Z;
		}
		return eyeDirection;
	}
}
