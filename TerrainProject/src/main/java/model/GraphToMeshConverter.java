package model;

import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.MeshUtilities;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.color.RGBA;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.VoronoiGraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by P on 04.12.2015.
 */
public class GraphToMeshConverter {
    final static int HEIGHTFACTOR = 150;


    public static List<IMesh> createMapAsMesh(VoronoiGraph v, boolean drawBiomes, boolean drawRivers, boolean drawSites, boolean drawCorners, boolean drawDelaunay, boolean drawVoronoi) {

        ColorMaterial[] colors = null;
        if (!drawBiomes) {
            colors = new ColorMaterial[100];
            for (int i = 0; i < colors.length; i++) {
                colors[i] = new ColorMaterial(new RGBA((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.f));
            }
        }
        List<IMesh> meshes = new ArrayList<>(35000); //currently there are 30'000 meshes

        //draw via triangles
        for (Center c : v.centers) {
            meshes.addAll(drawPolygonAsMesh(v, c, drawBiomes ? v.getColorAsMaterial(c.biome) : colors[c.index % 100], HEIGHTFACTOR));
            //drawPolygon(g, c, drawBiomes ? getColor(c.biome) : defaultColors[c.index]);
            //drawPolygon(pixelCenterGraphics, c, new Color(c.index)); no equivalent implemented
        }
        List<IMesh> merged = MeshUtilities.mergeMeshes(meshes);

        System.out.println("#meshes after merge: " + merged.size());

        return merged;
        //return meshes;
    }


    private static List<IMesh> drawPolygonAsMesh(VoronoiGraph v, Center c, ColorMaterial color, int HEIGHTFACTOR) {

        List<IMesh> polygon = new ArrayList<>();

        //only used if Center c is on the edge of the graph. allows for completely filling in the outer polygons
        Corner edgeCorner1 = null;
        Corner edgeCorner2 = null;
        c.area = 0;
        for (Center n : c.neighbors) {
            Edge e = v.edgeWithCenters(c, n);

            if (e.v0 == null) {
                //outermost voronoi edges aren't stored in the graph
                continue;
            }

            //find a corner on the exterior of the graph
            //if this Edge e has one, then it must have two,
            //finding these two corners will give us the missing
            //triangle to render. this special triangle is handled
            //outside this for loop
            Corner cornerWithOneAdjacent = e.v0.border ? e.v0 : e.v1;
            if (cornerWithOneAdjacent.border) {
                if (edgeCorner1 == null) {
                    edgeCorner1 = cornerWithOneAdjacent;
                } else {
                    edgeCorner2 = cornerWithOneAdjacent;
                }
            }

            polygon.add(createTriangle(e.v0, e.v1, c, color, HEIGHTFACTOR));
            c.area += Math.abs(c.loc.x * (e.v0.loc.y - e.v1.loc.y)
                    + e.v0.loc.x * (e.v1.loc.y - c.loc.y)
                    + e.v1.loc.x * (c.loc.y - e.v0.loc.y)) / 2;
        }

        //handle the missing triangle
        if (edgeCorner2 != null) {
            //if these two outer corners are NOT on the same exterior edge of the graph,
            //then we actually must render a polygon (w/ 4 points) and take into consideration
            //one of the four corners (either 0,0 or 0,height or width,0 or width,height)
            //note: the 'missing polygon' may have more than just 4 points. this
            //is common when the number of sites are quite low (less than 5), but not a problem
            //with a more useful number of sites.
            //TODO: find a way to fix this

            if (v.closeEnough(edgeCorner1.loc.x, edgeCorner2.loc.x, 1)) {
                polygon.add(createTriangle(edgeCorner1, edgeCorner2, c, color, HEIGHTFACTOR));
            } else {
                float[] tr0 = {
                        (float) c.loc.x, (float) c.loc.y, (float) c.elevation * HEIGHTFACTOR,
                        (float) edgeCorner2.loc.x, (float) edgeCorner2.loc.y, (float) edgeCorner2.elevation * HEIGHTFACTOR,
                        (float) edgeCorner1.loc.x, (float) edgeCorner1.loc.y, (float) edgeCorner1.elevation * HEIGHTFACTOR
                };

                float[] tr1 = {
                        (float) edgeCorner2.loc.x, (float) edgeCorner2.loc.y, (float) edgeCorner2.elevation * HEIGHTFACTOR,
                        (float) ((v.closeEnough(edgeCorner1.loc.x, v.bounds.x, 1) || v.closeEnough(edgeCorner2.loc.x, v.bounds.x, .5)) ? v.bounds.x : v.bounds.right),
                        (float) ((v.closeEnough(edgeCorner1.loc.y, v.bounds.y, 1) || v.closeEnough(edgeCorner2.loc.y, v.bounds.y, .5)) ? v.bounds.y : v.bounds.bottom),
                        (float) edgeCorner2.elevation * HEIGHTFACTOR, //TODO: almost certainly wrong :/
                        (float) edgeCorner1.loc.x, (float) edgeCorner1.loc.y, (float) edgeCorner1.elevation * HEIGHTFACTOR
                };
                float[] normals = {
                        0, 1, 0,
                        0, 1, 0,
                        0, 1, 0
                };

                float[] colors = {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1};

                DefaultGeometry g = DefaultGeometry.createVNC(IGeometry.Primitive.TRIANGLES, tr0, normals, colors);
                DefaultGeometry g1 = DefaultGeometry.createVNC(IGeometry.Primitive.TRIANGLES, tr1, normals, colors);

                polygon.add(new DefaultMesh(color, g));
                polygon.add(new DefaultMesh(color, g1));
                c.area += 0; //TODO: area of polygon given vertices
            }

        }

        //return polygon;
        return MeshUtilities.mergeMeshes(polygon);
    }

    private static IMesh createTriangle(Corner c1, Corner c2, Center center, ColorMaterial color, int HEIGHTFACTOR) {
        float[] vertices = {
                (float) center.loc.x, (float) center.loc.y, (float) center.elevation * HEIGHTFACTOR,
                (float) c1.loc.x, (float) c1.loc.y, (float) c1.elevation * HEIGHTFACTOR,
                (float) c2.loc.x, (float) c2.loc.y, (float) c2.elevation * HEIGHTFACTOR
        };
        float[] normals = {
                0, 1, 0,
                0, 1, 0,
                0, 1, 0
        };
        float[] colors = {1, 0, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1};


        DefaultGeometry g = DefaultGeometry.createVNC(IGeometry.Primitive.TRIANGLES, vertices, normals, colors);
        return new DefaultMesh(color, g);
    }
}
