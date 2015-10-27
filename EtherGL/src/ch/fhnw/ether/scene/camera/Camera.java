/*
 * Copyright (c) 2013 - 2015 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2015 FHNW & ETH Zurich
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

import ch.fhnw.util.UpdateRequest;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.BoundingBox;

public final class Camera implements ICamera {
	
	private final UpdateRequest update = new UpdateRequest();

	private Vec3 position = new Vec3(0, -5, 0);
	private Vec3 target = Vec3.ZERO;
	private Vec3 up = Vec3.Z;

	private float fov = 45;
	private float near = 0.01f;
	private float far = 100000f;
	
	private String name = "camera";

	public Camera() {
	}

	public Camera(Vec3 position, Vec3 target, Vec3 up, float fov, float near, float far) {
		this.position = position;
		this.target = target;
		this.up = up;
		this.fov = fov;
		this.near = near;
		this.far = far;
	}

	@Override
	public BoundingBox getBounds() {
		BoundingBox b = new BoundingBox();
		b.add(getPosition());
		return b;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public void setPosition(Vec3 position) {
		this.position = position;
		requestUpdate();
	}

	@Override
	public Vec3 getTarget() {
		return target;
	}

	@Override
	public void setTarget(Vec3 target) {
		this.target = target;
		requestUpdate();
	}

	@Override
	public Vec3 getUp() {
		return up;
	}

	@Override
	public void setUp(Vec3 up) {
		this.up = up;
		requestUpdate();
	}

	@Override
	public float getFov() {
		return fov;
	}

	@Override
	public void setFov(float fov) {
		this.fov = fov;
		requestUpdate();
	}

	@Override
	public float getNear() {
		return near;
	}

	@Override
	public void setNear(float near) {
		this.near = near;
		requestUpdate();
	}

	@Override
	public float getFar() {
		return far;
	}

	@Override
	public void setFar(float far) {
		this.far = far;
		requestUpdate();
	}
	
	@Override
	public Vec3 getCameraXAxis() {
		return up.cross(getCameraZAxis()).normalize();
	}
	
	@Override
	public Vec3 getCameraYAxis() {
		return getCameraZAxis().cross(getCameraXAxis()).normalize();
	}

	@Override
	public Vec3 getCameraZAxis() {
		return position.subtract(target).normalize();
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
		requestUpdate();
	}
	
	@Override
	public void requestUpdate() {
		update.requestUpdate();
	}

	@Override
	public boolean needsUpdate() {
		return update.needsUpdate();
	}
	
	@Override
	public String toString() {
		return "camera '" + getName() + "' [" + position + target + up + "][" + getCameraXAxis() + getCameraYAxis() + getCameraZAxis() + "]";
	}
}
