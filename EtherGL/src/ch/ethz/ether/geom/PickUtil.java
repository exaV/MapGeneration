/*
 * Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona)
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

package ch.ethz.ether.geom;

import ch.ethz.ether.view.IView;

/**
 * File created by radar on 10/12/13.
 */
public final class PickUtil {
    public static float pickBoundingVolume(int x, int y, int w, int h, IView view, BoundingVolume bounds) {
        y = view.getViewport().h - y;
        BoundingVolume b = new BoundingVolume();
        float xmin = bounds.getMinX();
        float xmax = bounds.getMaxX();
        float ymin = bounds.getMinY();
        float ymax = bounds.getMaxY();
        float zmin = bounds.getMinZ();
        float zmax = bounds.getMaxZ();

        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmin, ymin, zmin)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmin, ymin, zmax)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmin, ymax, zmin)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmin, ymax, zmax)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmax, ymin, zmin)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmax, ymin, zmax)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmax, ymax, zmin)));
        b.add(ProjectionUtil.projectToScreen(view, new Vec4(xmax, ymax, zmax)));
        if (b.getMinZ() > 0 && x > b.getMinX() && x < b.getMaxX() && y > b.getMinY() && y < b.getMaxY())
            return b.getMinZ();
        else
            return Float.NaN;
    }
}