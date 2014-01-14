package ch.ethz.ether.model;

import ch.ethz.ether.geom.BoundingBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by radar on 05/12/13.
 */
public abstract class AbstractModel implements IModel {
    private BoundingBox bounds;
    private List<IGeometry> geometries = new ArrayList<>();

    @Override
    public BoundingBox getBounds() {
        if (bounds == null) {
            bounds = new BoundingBox();
            for (IGeometry geometry : geometries)
                bounds.add(geometry.getBounds());
        }
        return bounds;
    }

    @Override
    public List<IGeometry> getGeometries() {
        return Collections.unmodifiableList(geometries);
    }

    protected void addGeometry(IGeometry geometry) {
        geometries.add(geometry);
    }
}
