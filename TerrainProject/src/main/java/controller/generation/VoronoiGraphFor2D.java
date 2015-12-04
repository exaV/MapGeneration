package controller.generation;

import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.util.color.RGBA;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.VoronoiGraph;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;

import java.awt.*;
import java.util.Random;

/**
 * Created by P on 04.12.2015.
 */
public class VoronoiGraphFor2D extends VoronoiGraph {
    public VoronoiGraphFor2D(Voronoi v, int numLloydRelaxations, Random r) {
        super(v, numLloydRelaxations, r);

    }

    @Override
    protected Color getColor(Enum biome) {
        throw new UnsupportedOperationException("can only get material");
    }

    @Override
    protected ColorMaterial getColorAsMaterial(Enum biome) {
        System.out.printf("biome: %s material: %s \n", biome, ((ColorData) biome).color);
                return ((ColorData) biome).color;
            }

            @Override
            protected Enum getBiome(Center p) {
                if (p.ocean) {
                    return ColorData.OCEAN;
                } else if (p.water) {
                    if (p.elevation < 0.1) {
                return ColorData.MARSH;
            }
            if (p.elevation > 0.8) {
                return ColorData.ICE;
            }
            return ColorData.LAKE;
        } else if (p.coast) {
            return ColorData.BEACH;
        } else if (p.elevation > 0.8) {
            if (p.moisture > 0.50) {
                return ColorData.SNOW;
            } else if (p.moisture > 0.33) {
                return ColorData.TUNDRA;
            } else if (p.moisture > 0.16) {
                return ColorData.BARE;
            } else {
                return ColorData.SCORCHED;
            }
        } else if (p.elevation > 0.6) {
            if (p.moisture > 0.66) {
                return ColorData.TAIGA;
            } else if (p.moisture > 0.33) {
                return ColorData.SHRUBLAND;
            } else {
                return ColorData.TEMPERATE_DESERT;
            }
        } else if (p.elevation > 0.3) {
            if (p.moisture > 0.83) {
                return ColorData.TEMPERATE_RAIN_FOREST;
            } else if (p.moisture > 0.50) {
                return ColorData.TEMPERATE_DECIDUOUS_FOREST;
            } else if (p.moisture > 0.16) {
                return ColorData.GRASSLAND;
            } else {
                return ColorData.TEMPERATE_DESERT;
            }
        } else {
            if (p.moisture > 0.66) {
                return ColorData.TROPICAL_RAIN_FOREST;
            } else if (p.moisture > 0.33) {
                return ColorData.TROPICAL_SEASONAL_FOREST;
            } else if (p.moisture > 0.16) {
                return ColorData.GRASSLAND;
            } else {
                return ColorData.SUBTROPICAL_DESERT;
            }
        }
    }

    public enum ColorData {

        OCEAN(new ColorMaterial(new RGBA(68, 68, 122, 1))),
        LAKE(new ColorMaterial(new RGBA(51, 102, 153, 1))),
        BEACH(new ColorMaterial(new RGBA(160, 144, 119, 1))),
        SNOW(new ColorMaterial(new RGBA(255, 255, 255, 1))),
        TUNDRA(new ColorMaterial(new RGBA(187, 187, 170, 1))),
        BARE(new ColorMaterial(new RGBA(136, 136, 136, 1))),
        SCORCHED(new ColorMaterial(new RGBA(85, 85, 85, 1))),
        TAIGA(new ColorMaterial(new RGBA(153, 170, 119, 1))),
        SHURBLAND(new ColorMaterial(new RGBA(136, 153, 119, 1))),
        TEMPERATE_DESERT(new ColorMaterial(new RGBA(201, 210, 155, 1))),
        TEMPERATE_RAIN_FOREST(new ColorMaterial(new RGBA(68, 136, 85, 1))),
        TEMPERATE_DECIDUOUS_FOREST(new ColorMaterial(new RGBA(103, 148, 89, 1))),
        GRASSLAND(new ColorMaterial(new RGBA(136, 170, 85, 1))),
        SUBTROPICAL_DESERT(new ColorMaterial(new RGBA(210, 185, 139, 1))),
        SHRUBLAND(new ColorMaterial(new RGBA(136, 153, 119, 1))),
        ICE(new ColorMaterial(new RGBA(153, 255, 255, 1))),
        MARSH(new ColorMaterial(new RGBA(47, 102, 102, 1))),
        TROPICAL_RAIN_FOREST(new ColorMaterial(new RGBA(51, 119, 85, 1))),
        TROPICAL_SEASONAL_FOREST(new ColorMaterial(new RGBA(85, 153, 68, 1))),
        COAST(new ColorMaterial(new RGBA(51, 51, 90, 1))),
        LAKESHORE(new ColorMaterial(new RGBA(34, 85, 136, 1))),
        RIVER(new ColorMaterial(new RGBA(34, 85, 136, 1)));

        public ColorMaterial color;

        ColorData(ColorMaterial colorMaterial) {
            this.color = colorMaterial;
        }

    }

}
