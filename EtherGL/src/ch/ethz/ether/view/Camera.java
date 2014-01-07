/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.view;

import ch.ethz.ether.geom.BoundingVolume;
import ch.ethz.ether.geom.Mat4;
import ch.ethz.util.MathUtil;

/**
 * OpenGL-aligned camera model
 *
 * @author radar
 */
public class Camera {
    private static final boolean KEEP_ROT_X_POSITIVE = true;

    private final IView view;

    private boolean locked = false;

    private float near = 0.1f;
    // public float far = 1000.0f;
    private float far = Float.POSITIVE_INFINITY;

    private float fov = 45.0f;
    private float distance = 2.0f;
    private float rotateZ = 0.0f;
    private float rotateX = 45.0f;
    private float translateX = 0.0f;
    private float translateY = 0.0f;

    private Mat4 projMatrix;
    private Mat4 viewMatrix;
    private Mat4 viewProjMatrix;
    private Mat4 viewProjInvMatrix;
    private Mat4 viewProjInvTpMatrix;

    public Camera(IView view) {
        this.view = view;
    }

    public boolean isLocked() {
        return locked;
    }

    public float getNearClippingPlane() {
        return near;
    }

    public void setNearClippingPlane(float near) {
        if (locked)
            return;
        this.near = near;
        update();
    }

    public float getFarClippingPlane() {
        return far;
    }

    public void setFarClippingPlane(float far) {
        if (locked)
            return;
        this.far = far;
        update();
    }

    public float getFOV() {
        return fov;
    }

    public void setFOV(float fov) {
        if (locked)
            return;
        this.fov = fov;
        update();
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        if (locked)
            return;
        this.distance = MathUtil.clamp(distance, near, far);
        update();
    }

    public void addToDistance(float delta) {
        if (locked)
            return;
        this.distance = MathUtil.clamp(distance + distance / 10.0f * delta, near, far);
        update();
    }

    public float getRotateZ() {
        return rotateZ;
    }

    public void setRotateZ(float rotateZ) {
        if (locked)
            return;
        this.rotateZ = rotateZ;
        update();
    }

    public void addToRotateZ(float delta) {
        if (locked)
            return;
        rotateZ += delta;
        update();
    }

    public float getRotateX() {
        return rotateX;
    }

    public void setRotateX(float rotateX) {
        if (locked)
            return;
        this.rotateX = MathUtil.clamp(rotateX, KEEP_ROT_X_POSITIVE ? 0 : -90, 90);
        update();
    }

    public void addToRotateX(float delta) {
        if (locked)
            return;
        this.rotateX = MathUtil.clamp(rotateX + delta, KEEP_ROT_X_POSITIVE ? 0 : -90, 90);
        update();
    }

    public float getTranslateX() {
        return translateX;
    }

    public void setTranslateX(float translateX) {
        if (locked)
            return;
        this.translateX = translateX;
        update();
    }

    public void addToTranslateX(float delta) {
        if (locked)
            return;
        translateX += distance * delta / view.getViewport().w;
        update();
    }

    public float getTranslateY() {
        return translateY;
    }

    public void setTranslateY(float translateY) {
        if (locked)
            return;
        this.translateY = translateY;
        update();
    }

    public void addToTranslateY(float delta) {
        if (locked)
            return;
        translateY += distance * delta / view.getViewport().h;
        update();
    }

    public void frame(BoundingVolume bounds) {
        if (locked)
            return;
        // FIXME hack, assume centered model for now
        float extent = 1.5f * Math.max(Math.max(bounds.getExtentX(), bounds.getExtentY()), bounds.getExtentZ());
        float d = 0.5f * extent / (float) Math.tan(Math.toRadians(fov / 2));
        distance = MathUtil.clamp(d, near, far);
        translateX = 0;
        translateY = 0;
        update();
    }

    public Mat4 getProjMatrix() {
        if (projMatrix == null) {
            projMatrix = new Mat4();
            projMatrix.perspective(fov, (float) view.getViewport().w / (float) view.getViewport().h, near, far);
        }
        return projMatrix;
    }

    public Mat4 getViewMatrix() {
        if (viewMatrix == null) {
            viewMatrix = Mat4.identityMatrix();
            viewMatrix.rotate(rotateZ, 0, 0, 1);
            viewMatrix.rotate(rotateX - 90, 1, 0, 0);
            viewMatrix.translate(translateX, translateY, -distance);
        }
        return viewMatrix;
    }

    public Mat4 getViewProjMatrix() {
        if (viewProjMatrix == null) {
            viewProjMatrix = Mat4.product(projMatrix, viewMatrix);
        }
        return viewProjMatrix;
    }

    public Mat4 getViewProjInvMatrix() {
        if (viewProjInvMatrix == null) {
            viewProjInvMatrix = getViewProjMatrix().inverse();
        }
        return viewProjInvMatrix;
    }

    public Mat4 getViewProjInvTpMatrix() {
        if (viewProjInvTpMatrix == null) {
            viewProjInvTpMatrix = getViewProjInvMatrix().transposed();
        }
        return viewProjInvTpMatrix;
    }

    public void setMatrices(Mat4 projMatrix, Mat4 viewMatrix) {
        if (projMatrix == null) {
            this.locked = false;
        } else {
            this.locked = true;
            this.projMatrix = projMatrix;
            this.viewMatrix = viewMatrix;
        }
        update();
    }

    public void update() {
        if (!locked) {
            viewMatrix = null;
            projMatrix = null;
        }
        viewProjMatrix = null;
        viewProjInvMatrix = null;
        viewProjInvTpMatrix = null;
        view.update();
    }
}