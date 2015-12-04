package com.hoten.delaunay.voronoi;

import com.hoten.delaunay.geom.Point;

import java.util.ArrayList;

/**
 * Corner.java
 *
 * @author Connor
 */
public class Corner {

    public ArrayList<Center> touches = new ArrayList<>();
    public ArrayList<Corner> adjacent = new ArrayList<>();
    public ArrayList<Edge> protrudes = new ArrayList<>();
    public Point loc;
    public int index;
    public boolean border;
    public double elevation;
    public boolean water, ocean, coast;
    public Corner downslope;
    public int river;
    public double moisture;
}
