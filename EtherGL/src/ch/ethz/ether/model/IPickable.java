package ch.ethz.ether.model;

import ch.ethz.ether.view.IView;

/**
 * Created by radar on 05/12/13.
 */
public interface IPickable {
    public enum PickMode {
        POINT,
        INSIDE,
        INTERSECT
    }

    public interface IPickState {
        void add(float z, IPickable object);
    }

    boolean pick(PickMode mode, int x, int y, int w, int h, IView view, IPickState state);
}
