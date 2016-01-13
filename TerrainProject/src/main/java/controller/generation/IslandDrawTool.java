package controller.generation;

import java.util.LinkedList;
import java.util.List;

import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.event.IKeyEvent;
import ch.fhnw.ether.controller.event.IPointerEvent;
import ch.fhnw.ether.controller.tool.AbstractTool;
import ch.fhnw.ether.examples.raytracing.surface.Plane;
import ch.fhnw.ether.scene.camera.IViewCameraState;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.LineMaterial;
import ch.fhnw.ether.view.ProjectionUtilities;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Mat4;
import ch.fhnw.util.math.Vec3;
import ch.fhnw.util.math.geometry.Line;


/**
 * Created by P on 13.01.2016.
 */
public class IslandDrawTool extends AbstractTool {

  private final float CIRCLEGROWRATE = 0.3f;


  private IMesh mapBoundaries;
  private final RGBA TOOL_COLOR = new RGBA(1.1f, 0.1f, 0.1f, 0f);
  private final RGBA MAPBOUNDARIES_COLOR = new RGBA(0.1f, 0.6f, 0.2f, 0);

  private boolean moving = false;

  private float xOffset = 0;
  private float yOffset = 0;
  Plane groundPlane = new Plane();
  LinkedList<TerrainCircle> terrainCircles = new LinkedList<>();

  boolean isPressed = false;
  Vec3 m;
  float radius = 1;
  IMesh circleMesh;
  float time = 0;


  public IslandDrawTool(IController controller, int width, int height) {
    super(controller);
    final float w = width / 2;
    final float h = height / 2;
    float[] v = {-w, h, 0, //topleft
        w, h, 0, //topright
        w, -h, 0, //bottomright

        w, -h, 0, //bottomright
        -w, -h, 0,  //bottomleft
        -w, h, 0}; //topleft


    mapBoundaries = new DefaultMesh(new ColorMaterial(MAPBOUNDARIES_COLOR), DefaultGeometry.createV(IGeometry.Primitive.TRIANGLES, v));
    //mesh = new DefaultMesh(new ColorMaterial(TOOL_COLOR), geometry);
    //mesh.setTransform(Mat4.scale(1f, 1f, 0.001f));
  }

  @Override
  public void activate() {
    getController().getRenderManager().addMesh(mapBoundaries);
    if(circleMesh != null) getController().getRenderManager().addMesh(circleMesh);
    for (TerrainCircle terrainCircle : terrainCircles) {
      getController().getRenderManager().addMesh(terrainCircle.mesh);
    }

  }

  @Override
  public void deactivate() {
    getController().getRenderManager().removeMesh(mapBoundaries);
    if(circleMesh != null) getController().getRenderManager().removeMesh(circleMesh);
    for (TerrainCircle terrainCircle : terrainCircles) {
      getController().getRenderManager().removeMesh(terrainCircle.mesh);
    }
  }

  public void update(float dt) {

    if (isPressed) {
      if (circleMesh != null) getController().getRenderManager().removeMesh(circleMesh);

      //increase radius
      radius += CIRCLEGROWRATE * dt + time;
      time += dt;

      circleMesh = createCircleMesh(radius);
      getController().getRenderManager().addMesh(circleMesh);
      circleMesh.setTransform(Mat4.translate(m.x-500, m.y-500, 0));
    }

  }

  @Override
  public void pointerPressed(IPointerEvent e) {
    super.pointerPressed(e);

    IViewCameraState vcs = getController().getRenderManager().getViewCameraState(getController().getCurrentView());
    Vec3 close = ProjectionUtilities.unprojectFromScreen(vcs, new Vec3(e.getX(), e.getY(), 0));
    Vec3 far = ProjectionUtilities.unprojectFromScreen(vcs, new Vec3(e.getX(), e.getY(), 1));

    this.m = groundPlane.intersect(new Line(close, far));
    isPressed = true;
    this.m = new Vec3(this.m.x+500,this.m.y+500,0);
  }

  @Override
  public void pointerReleased(IPointerEvent e) {
    super.pointerReleased(e);

    IMesh circleMesh = createCircleMesh(radius);
    getController().getRenderManager().addMesh(circleMesh);
    circleMesh.setTransform(Mat4.translate(m.x-500, m.y-500, 0));
    terrainCircles.addLast(new TerrainCircle(m, radius, circleMesh));
    isPressed = false;
    radius = 1;
    time = 0;
  }

  @Override
  public void keyPressed(IKeyEvent e) {
    super.keyPressed(e);
    System.out.println("key pressed");
    System.out.println(e.getKeyCode());
    switch (e.getKeySym()) {
      case IKeyEvent.VK_Z:
        System.out.println("z");
        undoCircle();
        break;

    }
  }

  /**
   * @param radius of the circle
   * @return cicle centered at (0,0,1) with specified radius
   */
  private IMesh createCircleMesh(float radius) {
    int i = 0;
    final int triangleAmount = 256; //# of triangles used to draw circle


    //create vertex Buffer
    float[] vertexBuffer = new float[(triangleAmount + 2) * 3];
    final double twicePi = 2.0 * Math.PI;
    vertexBuffer[i++] = 0;
    vertexBuffer[i++] = 0;
    vertexBuffer[i++] = 0;

    for (; i <= triangleAmount; i += 3) {

      vertexBuffer[i] = (radius * (float) Math.cos(i * twicePi / triangleAmount));
      vertexBuffer[i + 1] = (radius * (float) Math.sin(i * twicePi / triangleAmount));
      vertexBuffer[i + 2] = 0;

    }
    vertexBuffer[i++] = 0;
    vertexBuffer[i++] = 0;
    vertexBuffer[i] = 0;


    //create Mesh
    return new DefaultMesh(new LineMaterial(TOOL_COLOR), DefaultGeometry.createV(IGeometry.Primitive.LINES, vertexBuffer));


  }

  private void undoCircle() {
    if (!terrainCircles.isEmpty()) {
      getController().getRenderManager().removeMesh(terrainCircles.getLast().mesh);
      terrainCircles.removeLast();
    }
  }

  public List<TerrainCircle> getCircles() {return terrainCircles;}
}
