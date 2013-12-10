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
package ch.ethz.ether.geom;

import ch.ethz.ether.gl.Viewport;
import ch.ethz.ether.view.IView;
import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public final class ProjectionUtil {
    public static Vec3 projectToDevice(IView view, Vec3 v) {
        return projectToDevice(view, new Vec4(v));
    }

    public static Vec3 projectToDevice(IView view, Vec4 v) {
        return projectToDevice(view.getCamera().getViewMatrix(), view.getCamera().getProjMatrix(), v);
    }

    public static Vec3 projectToDevice(Mat4 viewMatrix, Mat4 projMatrix, Vec3 v) {
        return projectToDevice(viewMatrix, projMatrix, new Vec4(v));
    }

    public static Vec3 projectToDevice(Mat4 viewMatrix, Mat4 projMatrix, Vec4 v) {
        Vec4 proj = projMatrix.transform(viewMatrix.transform(v));

        if (proj.w == 0)
            return null;

        // map x, y and z to range [-1, 1]
        float x = proj.x / proj.w;
        float y = proj.y / proj.w;
        float z = proj.z / proj.w;

        return new Vec3(x, y, z);
    }

    public static Vec3 projectToScreen(IView view, Vec3 v) {
        return projectToScreen(view, new Vec4(v));
    }

    public static Vec3 projectToScreen(IView view, Vec4 v) {
        return projectToScreen(view.getCamera().getViewMatrix(), view.getCamera().getProjMatrix(), view.getViewport(), v);
    }

    public static Vec3 projectToScreen(Mat4 viewMatrix, Mat4 projMatrix, Viewport viewport, Vec3 v) {
        return projectToScreen(viewMatrix, projMatrix, viewport, new Vec4(v));
    }

    public static Vec3 projectToScreen(Mat4 viewMatrix, Mat4 projMatrix, Viewport viewport, Vec4 v) {
        Vec4 proj = projMatrix.transform(viewMatrix.transform(v));

        if (proj.w == 0)
            return null;

        // map x, y and z to range [0, 1]
        float x = 0.5f * (proj.x / proj.w + 1);
        float y = 0.5f * (proj.y / proj.w + 1);
        float z = 0.5f * (proj.z / proj.w + 1);

        // map x and y to viewport
        x = x * viewport.w + viewport.x;
        y = y * viewport.h + viewport.y;
        return new Vec3(x, y, z);
    }

    public static int deviceToScreenX(IView view, float x) {
        return (int) ((1.0 + x) / 2.0 * view.getViewport().w);
    }

    public static int deviceToScreenY(IView view, float y) {
        return (int) ((1.0 + y) / 2.0 * view.getViewport().h);
    }

    public static float screenToDeviceX(IView view, int x) {
        return 2f * x / view.getViewport().w - 1f;
    }

    public static float screenToDeviceY(IView view, int y) {
        return 2f * y / view.getViewport().h - 1f;
    }

    public static Line getRay(IView view, float x, float y) {
        Mat4 vm = view.getCamera().getViewMatrix();
        Mat4 pm = view.getCamera().getProjMatrix();
        Viewport vp = view.getViewport();
        Vec3 p0 = unprojectFromScreen(vm, pm, vp, new Vec3(x, y, 0.1f));
        Vec3 p1 = unprojectFromScreen(vm, pm, vp, new Vec3(x, y, 0.9f));
        return new Line(new Vector3D(p0.x, p0.y, p0.z), new Vector3D(p1.x, p1.y, p1.z));
    }

    public static Vec3 unprojectFromScreen(IView view, Vec3 v) {
        return unprojectFromScreen(view.getCamera().getViewMatrix(), view.getCamera().getProjMatrix(), view.getViewport(), v);
    }

    public static Vec3 unprojectFromScreen(Mat4 viewMatrix, Mat4 projMatrix, Viewport viewport, Vec3 v) {
        Mat4 inverse = Mat4.product(projMatrix, viewMatrix).inverse();
        if (inverse == null)
            return null;

        // map x and y from window coordinates
        float x = (v.x - viewport.x) / viewport.w;
        float y = (v.y - viewport.y) / viewport.h;
        float z = v.z;

        // map to range -1 to 1
        x = x * 2 - 1;
        y = y * 2 - 1;
        z = z * 2 - 1;

        Vec4 result = inverse.transform(new Vec4(x, y, z, 1));

        if (result.w == 0) {
            return null;
        }

        return new Vec3(result.x / result.w, result.y / result.w, result.z / result.w);
    }
}
