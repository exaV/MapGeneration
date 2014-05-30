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

package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingBox;
import ch.ethz.ether.render.AbstractRenderGroup;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.util.IAddOnlyFloatList;

// TODO: this class is doomed, mainly for testing right now
public class GenericMeshModel extends AbstractModel {
    private final IRenderGroup triangles = new AbstractRenderGroup(Source.MODEL, Type.TRIANGLES) {
        @Override
        public void getVertices(IAddOnlyFloatList dst) {
            for (IGeometry mesh : getGeometries()) {
                if (mesh instanceof ITriangleProvider) {
                    ((ITriangleProvider) mesh).getTriangleVertices(dst);
                }
            }
        }
    };

    private final IRenderGroup edges = new AbstractRenderGroup(Source.MODEL, Type.LINES) {
        @Override
        public void getVertices(IAddOnlyFloatList dst) {
            for (IGeometry mesh : getGeometries()) {
                if (mesh instanceof IEdgeProvider) {
                    ((IEdgeProvider) mesh).getEdgeVertices(dst);
                }
            }
        }
    };

    private final IRenderGroup points = new AbstractRenderGroup(Source.MODEL, Type.POINTS) {
        @Override
        public void getVertices(IAddOnlyFloatList dst) {
            for (IGeometry mesh : getGeometries()) {
                if (mesh instanceof IPointProvider) {
                    ((IPointProvider) mesh).getPointVertices(dst);
                }
            }
        }
    };

    private final IRenderGroup bounds = new AbstractRenderGroup(Source.MODEL, Type.LINES) {
        @Override
        public void getVertices(IAddOnlyFloatList dst) {
            for (IGeometry mesh : getGeometries()) {
                BoundingBox bounds = mesh.getBounds();
                float xmin = bounds.getMinX();
                float xmax = bounds.getMaxX();
                float ymin = bounds.getMinY();
                float ymax = bounds.getMaxY();
                float zmin = bounds.getMinZ();
                float zmax = bounds.getMaxZ();

                dst.add(xmin, ymin, zmin);
                dst.add(xmax, ymin, zmin);

                dst.add(xmin, ymax, zmin);
                dst.add(xmax, ymax, zmin);

                dst.add(xmin, ymin, zmin);
                dst.add(xmin, ymax, zmin);

                dst.add(xmax, ymin, zmin);
                dst.add(xmax, ymax, zmin);

                dst.add(xmin, ymin, zmax);
                dst.add(xmax, ymin, zmax);

                dst.add(xmin, ymax, zmax);
                dst.add(xmax, ymax, zmax);

                dst.add(xmin, ymin, zmax);
                dst.add(xmin, ymax, zmax);

                dst.add(xmax, ymin, zmax);
                dst.add(xmax, ymax, zmax);

                dst.add(xmin, ymin, zmin);
                dst.add(xmin, ymin, zmax);

                dst.add(xmax, ymin, zmin);
                dst.add(xmax, ymin, zmax);

                dst.add(xmax, ymax, zmin);
                dst.add(xmax, ymax, zmax);

                dst.add(xmin, ymax, zmin);
                dst.add(xmin, ymax, zmax);
            }
        }

        @Override
        public float[] getColor() {
            return new float[] { 1, 1, 0, 0 };
        }
    };

    public GenericMeshModel() {
        IRenderer.GROUPS.add(triangles);
        IRenderer.GROUPS.add(edges);
        IRenderer.GROUPS.add(points);
        IRenderer.GROUPS.add(bounds);
    }
}
