package ch.ethz.ether.model;

import ch.ethz.ether.geom.Vec3;
import ch.ethz.ether.render.util.Primitives;

/**
 * Created by radar on 05/12/13.
 */
public final class CubeMesh extends GenericMesh {
    public enum Origin {
        CENTER(Vec3.ZERO),
        BOTTOM_CENTER(new Vec3(0, 0, -0.5)),
        ZERO(new Vec3(-0.5, -0.5, -0.5));

        Origin(Vec3 origin) {
            this.origin = origin;
        }

        Vec3 origin;
    }

    public CubeMesh() {
        this(Origin.CENTER);
    }

    public CubeMesh(Origin origin) {
        this(origin.origin);
    }

    public CubeMesh(Vec3 origin) {
        super(origin);
        setTriangles(Primitives.UNIT_CUBE_TRIANGLES, null, null);
        setEdges(Primitives.UNIT_CUBE_EDGES, null);
        setPoints(Primitives.UNIT_CUBE_POINTS, null);
    }
}
