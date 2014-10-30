/*
 * Copyright (c) 2013 - 2014 Stefan Muller Arisona, Simon Schubiger, Samuel von Stachelski
 * Copyright (c) 2013 - 2014 FHNW & ETH Zurich
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *  Neither the name of FHNW / ETH Zurich nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.fhnw.ether.mapping.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.controller.tool.AbstractTool;
import ch.fhnw.ether.mapping.BimberRaskarCalibrator;
import ch.fhnw.ether.mapping.ICalibrationModel;
import ch.fhnw.ether.mapping.ICalibrator;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.scene.mesh.DefaultMesh;
import ch.fhnw.ether.scene.mesh.MeshLibrary;
import ch.fhnw.ether.scene.mesh.IMesh.Pass;
import ch.fhnw.ether.scene.mesh.geometry.DefaultGeometry;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.IAttributesVisitor;
import ch.fhnw.ether.scene.mesh.geometry.IGeometry.PrimitiveType;
import ch.fhnw.ether.scene.mesh.material.ColorMaterial;
import ch.fhnw.ether.scene.mesh.material.PointMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.ProjectionUtil;
import ch.fhnw.util.PreferencesStore;
import ch.fhnw.util.Viewport;
import ch.fhnw.util.color.RGBA;
import ch.fhnw.util.math.Vec3;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;

// FIXME: need possibility to disable current 3d scene when tool is active
public final class CalibrationTool extends AbstractTool {
	//@formatter:off	
	private static final String[] HELP = { 
		"Calibration Tool for 3D Mapping", 
		"", 
		"[0] Return", 
		"", 
		"[C] Clear Calibration", 
		"[L] Load Calibration",
		"[S] Save Calibration", 
		"[DEL] Clear Current Calibration Point"
	};
	//@formatter:on

	public static final double MAX_CALIBRATION_ERROR = 0.5;

	public static final RGBA MODEL_COLOR = RGBA.WHITE;
	public static final RGBA UNCALIBRATED_COLOR = RGBA.YELLOW;
	public static final RGBA CALIBRATED_COLOR = RGBA.GREEN;

	private static final float CROSSHAIR_SIZE = 20;

	private final ICalibrator calibrator = new BimberRaskarCalibrator();
	private final ICalibrationModel model;

	private Map<IView, CalibrationContext> contexts = new HashMap<>();

	private DefaultMesh lines;
	private DefaultMesh points;
	private DefaultMesh calibratedLines;
	private DefaultMesh calibratedPoints;

	public CalibrationTool(IController controller, ICalibrationModel model) {
		super(controller);
		this.model = model;
		lines = new DefaultMesh(new ColorMaterial(MODEL_COLOR), DefaultGeometry.createV(PrimitiveType.LINES, model.getCalibrationLines()), Pass.OVERLAY);
		points = new DefaultMesh(new PointMaterial(10, MODEL_COLOR), DefaultGeometry.createV(PrimitiveType.POINTS, model.getCalibrationPoints()), Pass.OVERLAY);
		calibratedLines = new DefaultMesh(new ColorMaterial(UNCALIBRATED_COLOR), DefaultGeometry.createV(PrimitiveType.LINES, new float[0]),
				Pass.DEVICE_SPACE_OVERLAY);
		calibratedPoints = new DefaultMesh(new PointMaterial(10, UNCALIBRATED_COLOR), DefaultGeometry.createV(PrimitiveType.POINTS, new float[0]),
				Pass.DEVICE_SPACE_OVERLAY);
	}

	@Override
	public void activate() {
		getController().enableViews(Collections.singleton(getController().getCurrentView()));
		IRenderer render = getController().getRenderer();
		render.addMesh(lines);
		render.addMesh(points);
		render.addMesh(calibratedLines);
		render.addMesh(calibratedPoints);
	}

	@Override
	public void deactivate() {
		getController().enableViews(null);
		IRenderer render = getController().getRenderer();
		render.removeMesh(lines);
		render.removeMesh(points);
		render.removeMesh(calibratedLines);
		render.removeMesh(calibratedPoints);
	}

	@Override
	public void refresh(IView view) {
		getController().enableViews(Collections.singleton(view));
		updateCalibratedGeometry(view);
	}

	@Override
	public void keyPressed(KeyEvent e, IView view) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_L:
			loadCalibration(view);
			break;
		case KeyEvent.VK_S:
			saveCalibration(view);
			break;
		case KeyEvent.VK_UP:
			cursorAdjust(view, 0, 1);
			break;
		case KeyEvent.VK_DOWN:
			cursorAdjust(view, 0, -1);
			break;
		case KeyEvent.VK_LEFT:
			cursorAdjust(view, -1, 0);
			break;
		case KeyEvent.VK_RIGHT:
			cursorAdjust(view, 1, 0);
			break;
		case KeyEvent.VK_C:
			clearCalibration(view);
			break;
		case KeyEvent.VK_H:
			DefaultController.printHelp(HELP);
			break;
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
			deleteCurrent(view);
			break;
		}
		view.getController().repaintViews();
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		int mx = e.getX();
		int my = view.getViewport().h - e.getY();
		CalibrationContext context = getContext(view);

		// reset first
		context.currentSelection = -1;

		// first, try to hit calibration point
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			int x = ProjectionUtil.deviceToScreenX(view, context.projectedVertices.get(i).x);
			int y = ProjectionUtil.deviceToScreenY(view, context.projectedVertices.get(i).y);
			if (snap2D(mx, my, x, y)) {
				// we got a point to move!
				context.currentSelection = i;
				calibrate(view);
				return;
			}
		}

		// second, try to hit model point
		float[] mv = model.getCalibrationPoints();
		for (int i = 0; i < mv.length; i += 3) {
			Vec3 vv = ProjectionUtil.projectToScreen(view, new Vec3(mv[i], mv[i + 1], mv[i + 2]));
			if (vv == null)
				continue;
			if (snap2D(mx, my, (int) vv.x, (int) vv.y)) {
				Vec3 m = new Vec3(mv[i], mv[i + 1], mv[i + 2]);
				int index = context.modelVertices.indexOf(m);
				if (index != -1) {
					context.currentSelection = index;
				} else {
					Vec3 p = new Vec3(ProjectionUtil.screenToDeviceX(view, (int) vv.x), ProjectionUtil.screenToDeviceY(view, (int) vv.y), 0);
					context.currentSelection = context.modelVertices.size();
					context.modelVertices.add(m);
					context.projectedVertices.add(p);
				}
				calibrate(view);
				return;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		int mx = e.getX();
		int my = view.getViewport().h - e.getY();
		CalibrationContext context = getContext(view);

		if (context.currentSelection != -1) {
			Vec3 a = new Vec3(ProjectionUtil.screenToDeviceX(view, mx), ProjectionUtil.screenToDeviceY(view, my), 0);
			context.projectedVertices.set(context.currentSelection, a);
			calibrate(view);
		}
	}

	private CalibrationContext getContext(IView view) {
		CalibrationContext context = contexts.get(view);
		if (context == null) {
			context = new CalibrationContext();
			contexts.put(view, context);
		}
		return context;
	}

	private void cursorAdjust(IView view, float dx, float dy) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			Vec3 p = context.projectedVertices.get(context.currentSelection);
			Vec3 a = new Vec3(p.x + dx / view.getViewport().w, p.y + dy / view.getViewport().h, 0);
			context.projectedVertices.set(context.currentSelection, a);
			calibrate(view);
		}
	}

	private void deleteCurrent(IView view) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			context.modelVertices.remove(context.currentSelection);
			context.projectedVertices.remove(context.currentSelection);
			context.currentSelection = -1;
			calibrate(view);
		}
	}

	private void loadCalibration(IView view) {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (IView v : view.getController().getViews()) {
			getContext(v).load(p, iv);
			calibrate(v);
			iv++;
		}
	}

	private void saveCalibration(IView view) {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (IView v : view.getController().getViews()) {
			getContext(v).save(p, iv);
			iv++;
		}
	}

	private void clearCalibration(IView view) {
		contexts.put(view, new CalibrationContext());
		view.setCameraMatrices(null, null);
		calibrate(view);
	}

	private void calibrate(IView view) {
		ICamera camera = view.getCamera();
		CalibrationContext context = getContext(view);
		context.calibrated = false;
		try {
			double error = calibrator.calibrate(context.modelVertices, context.projectedVertices, camera.getNear(), camera.getFar());
			if (error < MAX_CALIBRATION_ERROR)
				context.calibrated = true;
			// System.out.println("error: " + error);
		} catch (Throwable ignored) {
		}
		if (context.calibrated) {
			view.setCameraMatrices(calibrator.getViewMatrix(), calibrator.getProjMatrix());
		}

		// need to update VBOs
		refresh(view);

		// lazily repaint all views
		view.getController().repaintViews();
	}

	private void updateCalibratedGeometry(IView view) {
		CalibrationContext context = getContext(view);

		// prepare points
		calibratedPoints.getGeometry().accept(new IAttributesVisitor() {
			@Override
			public boolean visit(PrimitiveType type, String[] attributes, float[][] data) {
				data[0] = Vec3.toArray(context.projectedVertices);
				return true;
			}
		});
		((ColorMaterial) calibratedPoints.getMaterial()).setColor(context.calibrated ? CALIBRATED_COLOR : UNCALIBRATED_COLOR);

		// prepare lines
		List<Vec3> v = new ArrayList<>();
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			Vec3 a = context.modelVertices.get(i);
			Vec3 aa = ProjectionUtil.projectToDevice(view, a);
			if (aa == null)
				continue;
			a = context.projectedVertices.get(i);
			MeshLibrary.addLine(v, a.x, a.y, a.z, aa.x, aa.y, aa.z);

			if (i == context.currentSelection) {
				Viewport viewport = view.getViewport();
				MeshLibrary.addLine(v, a.x - CROSSHAIR_SIZE / viewport.w, a.y, a.z, a.x + CROSSHAIR_SIZE / viewport.w, a.y, a.z);
				MeshLibrary.addLine(v, a.x, a.y - CROSSHAIR_SIZE / viewport.h, a.z, a.x, a.y + CROSSHAIR_SIZE / viewport.h, a.z);
			}
		}

		calibratedLines.getGeometry().accept(new IAttributesVisitor() {
			@Override
			public boolean visit(PrimitiveType type, String[] attributes, float[][] data) {
				data[0] = Vec3.toArray(v);
				return true;
			}
		});
		((ColorMaterial) calibratedLines.getMaterial()).setColor(context.calibrated ? CALIBRATED_COLOR : UNCALIBRATED_COLOR);
	}

}
