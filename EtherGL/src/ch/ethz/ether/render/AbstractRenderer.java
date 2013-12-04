package ch.ethz.ether.render;

import javax.media.opengl.GL3;

import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.view.IView;

public abstract class AbstractRenderer implements IRenderer {

    protected void updateGroups(GL3 gl, IRenderer renderer) {
        ((RenderGroups) GROUPS).update(gl, renderer);
    }

    protected void renderGroups(GL3 gl, IRenderer renderer, IView view, float[] projMatrix, float[] viewMatrix, Pass pass) {
        ((RenderGroups) GROUPS).render(gl, renderer, view, projMatrix, viewMatrix, pass);
    }
}
