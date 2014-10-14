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

package ch.fhnw.ether.camera;

import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.I3DObject;

public interface ICamera extends I3DObject {

	public interface IOrbitControl {
		float getAzimut();

		void setAzimut(float azimut);

		void addToAzimut(float amount);

		float getElevation();

		void setElevation(float elevation);

		void addToElevation(float amount);

		float getZoom();

		void setZoom(float zoom);

		void addToZoom(float amount);

		Vec3 getPivot();

		void setPivot(float x, float y, float z);

		void movePivot(float x, float y, float z);
	}

	// view parameters

	@Override
	Vec3 getPosition();

	Vec3 getForwardDirection();

	Vec3 getUpDirection();

	// projection parameters

	float getFov();

	void setFov(float fov);

	float getAspect();

	void setAspect(float aspect);

	float getNear();

	void setNear(float near);

	float getFar();

	void setFar(float far);

	// view / projection matrix access

	Mat4 getViewMatrix();

	void setViewMatrix(Mat4 viewMatrix);

	Mat4 getProjectionMatrix();

	void setProjectionMatrix(Mat4 projectionMatrix);

	Mat4 getViewProjMatrix();

	Mat4 getViewProjInvMatrix();

	// camera control

	IOrbitControl getOrbitControl();

	// camera matrix

	void turn(float amount, Vec3 axis, boolean localTransformation);

	void move(float x, float y, float z, boolean localTransformation);

	void setRotation(float xAxis, float yAxis, float zAxis);

	void setPosition(float x, float y, float z);

}
