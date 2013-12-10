package ch.ethz.ether.model;

import ch.ethz.ether.geom.Mat4;
import ch.ethz.ether.view.IView;

import java.util.Map;

/**
 * Created by radar on 05/12/13.
 */
public interface IPickable {
    public interface IPickState {
        void add(float z, IPickable object);
    }

    boolean pick(int x, int y, int w, int h, IView view, IPickState state);
}
