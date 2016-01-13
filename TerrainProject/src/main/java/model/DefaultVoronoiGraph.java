package model;

import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.util.color.RGBA;
import controller.generation.TerrainCircle;

import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.VoronoiGraph;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by P on 04.12.2015.
 */
public class DefaultVoronoiGraph extends VoronoiGraph {
    public DefaultVoronoiGraph(Voronoi v, int numLloydRelaxations, Random r, Generation_Type generation_type) {
        super(v, numLloydRelaxations, r, Generation_Type.RANDOM, null);

    }

    public DefaultVoronoiGraph(Voronoi v, int numLloydRelaxations, Random r, Generation_Type generation_type, List<TerrainCircle> circles) {
        super(v, numLloydRelaxations, r, generation_type, circles);
    }


        @Override
    public Color getColor(Enum biome) {
        throw new UnsupportedOperationException("can only get material");
    }

    @Override
    public IMaterial getColorAsMaterial(Enum biome) {
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

        OCEAN(new ColorMaterial(new RGBA(0x44447aFF))),
        LAKE(new ColorMaterial(new RGBA(0x336699FF))),
        BEACH(new ColorMaterial(new RGBA(0xa09077FF))),
        SNOW(new ColorMaterial(new RGBA(0xffffffFF))),
        TUNDRA(new ColorMaterial(new RGBA(0xbbbbaaFF))),
        BARE(new ColorMaterial(new RGBA(0x888888FF))),
        SCORCHED(new ColorMaterial(new RGBA(0x555555FF))),
        TAIGA(new ColorMaterial(new RGBA(0x99aa77FF))),
        SHURBLAND(new ColorMaterial(new RGBA(0x889977FF))),
        TEMPERATE_DESERT(new ColorMaterial(new RGBA(0xc9d29bFF))),
        TEMPERATE_RAIN_FOREST(new ColorMaterial(new RGBA(0x448855FF))),
        TEMPERATE_DECIDUOUS_FOREST(new ColorMaterial(new RGBA(0x679459FF))),
        GRASSLAND(new ColorMaterial(new RGBA(0x88aa55FF))),
        SUBTROPICAL_DESERT(new ColorMaterial(new RGBA(0xd2b98bFF))),
        SHRUBLAND(new ColorMaterial(new RGBA(0x889977FF))),
        ICE(new ColorMaterial(new RGBA(0x99ffffFF))),
        MARSH(new ColorMaterial(new RGBA(0x2f6666FF))),
        TROPICAL_RAIN_FOREST(new ColorMaterial(new RGBA(0x337755FF))),
        TROPICAL_SEASONAL_FOREST(new ColorMaterial(new RGBA(0x559944FF))),
        COAST(new ColorMaterial(new RGBA(0x33335aFF))),
        LAKESHORE(new ColorMaterial(new RGBA(0x225588FF))),
        RIVER(new ColorMaterial(new RGBA(0x225588FF)));

        public IMaterial color;

        ColorData(IMaterial colorMaterial) {
            this.color = colorMaterial;
        }

    }

}
