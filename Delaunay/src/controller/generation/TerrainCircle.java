package controller.generation;

import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.util.math.Vec3;

/**
 * Created by P on 14.01.2016.
 */
public class TerrainCircle {
    public final Vec3 m;
    public final float r;
    public final IMesh mesh;

    public TerrainCircle(Vec3 middlePoint, float radius, IMesh mesh){
        m = middlePoint;
        r = radius;
        this.mesh = mesh;
    }
}
