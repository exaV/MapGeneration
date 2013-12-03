/*
Copyright (c) 2013, ETH Zurich (Stefan Mueller Arisona, Eva Friedrich)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
  this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.
 * Neither the name of ETH Zurich nor the names of its contributors may be 
  used to endorse or promote products derived from this software without
  specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.ether.mapping;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import ch.ethz.ether.gl.ProjectionUtilities;
import ch.ethz.ether.render.AbstractRenderGroup;
import ch.ethz.ether.render.IRenderGroup;
import ch.ethz.ether.render.IRenderGroup.Pass;
import ch.ethz.ether.render.IRenderGroup.Source;
import ch.ethz.ether.render.IRenderGroup.Type;
import ch.ethz.ether.render.IRenderer;
import ch.ethz.ether.render.util.IAddOnlyFloatList;
import ch.ethz.ether.render.util.Primitives;
import ch.ethz.ether.scene.AbstractTool;
import ch.ethz.ether.scene.IScene;
import ch.ethz.ether.view.IView;
import ch.ethz.util.PreferencesStore;

public final class CalibrationTool extends AbstractTool {
	// @formatter:off
	private static final String[] CALIBRATION_HELP = {
		"Calibration Tool for 3D Mapping",
		"",
		"[0] Return",
		"",
		"[C] Clear Calibration",
		"[L] Load Calibration",
		"[S] Save Calibration",
		"[DEL] Clear Current Calibration Point",
	};
	// @formatter:on

	public static final double MAX_CALIBRATION_ERROR = 0.5;

	public static final float[] MODEL_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_UNCALIBRATED = { 1.0f, 1.0f, 0.0f, 1.0f };
	public static final float[] CALIBRATION_COLOR_CALIBRATED = { 0.0f, 1.0f, 0.0f, 1.0f };

	private static final float POINT_SIZE = 10;
	private static final float CROSSHAIR_SIZE = 20;

	private final ICalibrator calibrator = new BimberRaskarCalibrator();
	private final ICalibrationModel model;

	private Map<IView, CalibrationContext> contexts = new HashMap<>();

	private IRenderGroup modelPoints = new AbstractRenderGroup(Source.TOOL, Type.POINTS, Pass.OVERLAY) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			dst.addAll(model.getCalibrationVertices());
		};

		@Override
		public float[] getColor() {
			return MODEL_COLOR;
		};

		@Override
		public float getPointSize() {
			return POINT_SIZE;
		};
	};

	private IRenderGroup modelLines = new AbstractRenderGroup(Source.TOOL, Type.LINES, Pass.OVERLAY) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			dst.addAll(model.getCalibrationLines());
		};

		@Override
		public float[] getColor() {
			return MODEL_COLOR;
		};
	};

	private IRenderGroup calibrationPoints = new AbstractRenderGroup(Source.TOOL, Type.POINTS, Pass.DEVICE_SPACE_OVERLAY) {
		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			IView view = getScene().getCurrentView();
			if (view == null)
				return;
			for (float[] v : getContext(view).projectedVertices) {
				dst.addAll(v);
			}
		};

		@Override
		public float[] getColor() {
			IView view = getScene().getCurrentView();
			if (view == null)
				return MODEL_COLOR;
			return getContext(view).calibrated ? CALIBRATION_COLOR_CALIBRATED : CALIBRATION_COLOR_UNCALIBRATED;
		};

		@Override
		public float getPointSize() {
			return 10;
		};
	};

	private IRenderGroup calibrationLines = new AbstractRenderGroup(Source.TOOL, Type.LINES, Pass.DEVICE_SPACE_OVERLAY) {
		private float[] v = new float[3];

		@Override
		public void getVertices(IAddOnlyFloatList dst) {
			IView view = getScene().getCurrentView();
			if (view == null)
				return;

			CalibrationContext context = getContext(view);
			for (int i = 0; i < context.projectedVertices.size(); ++i) {
				float[] a = context.modelVertices.get(i);
				if (!ProjectionUtilities.projectToDevice(view, a[0], a[1], a[2], v))
					continue;
				a = context.projectedVertices.get(i);
				Primitives.addLine(dst, v[0], v[1], v[2], a[0], a[1], a[2]);

				if (i == context.currentSelection) {
					Primitives.addLine(dst, a[0] - CROSSHAIR_SIZE / view.getWidth(), a[1], a[2], a[0] + CROSSHAIR_SIZE / view.getWidth(), a[1], a[2]);
					Primitives.addLine(dst, a[0], a[1] - CROSSHAIR_SIZE / view.getHeight(), a[2], a[0], a[1] + CROSSHAIR_SIZE / view.getHeight(), a[2]);
				}
			}
		};

		@Override
		public float[] getColor() {
			IView view = getScene().getCurrentView();
			if (view == null)
				return MODEL_COLOR;
			return getContext(view).calibrated ? CALIBRATION_COLOR_CALIBRATED : CALIBRATION_COLOR_UNCALIBRATED;
		};
	};

	public CalibrationTool(IScene scene, ICalibrationModel model) {
		super(scene);
		this.model = model;
	}

	@Override
	public void activate() {
		IRenderer.GROUPS.add(modelLines, modelPoints, calibrationPoints, calibrationLines);
		IRenderer.GROUPS.setSource(Source.TOOL);
	}
	
	@Override
	public void deactivate() {
		IRenderer.GROUPS.remove(modelLines, modelPoints, calibrationPoints, calibrationLines);
		IRenderer.GROUPS.setSource(null);
		getScene().enableViews(null);
	}
	
	@Override
	public void viewChanged(IView view) {
		getScene().enableViews(Collections.singleton(view));
		calibrationPoints.requestUpdate();
		calibrationLines.requestUpdate();
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
		case KeyEvent.VK_BACK_SPACE:
		case KeyEvent.VK_DELETE:
			deleteCurrent(view);
			break;
		}
		view.getScene().repaintViews();
	}

	@Override
	public void mousePressed(MouseEvent e, IView view) {
		CalibrationContext context = getContext(view);

		// reset first
		context.currentSelection = -1;

		// first, try to hit calibration point
		for (int i = 0; i < context.projectedVertices.size(); ++i) {
			int x = ProjectionUtilities.deviceToScreenX(view, context.projectedVertices.get(i)[0]);
			int y = ProjectionUtilities.deviceToScreenY(view, context.projectedVertices.get(i)[1]);
			if (snap2D(e.getX(), view.getHeight() - e.getY(), x, y)) {
				// we got a point to move!
				context.currentSelection = i;
				calibrate(view);
				return;
			}
		}

		// second, try to hit model point
		float[] mv = model.getCalibrationVertices();
		float[] vv = new float[3];
		for (int i = 0; i < mv.length; i += 3) {
			if (!ProjectionUtilities.projectToScreen(view, mv[i], mv[i + 1], mv[i + 2], vv))
				continue;
			if (snap2D(e.getX(), view.getHeight() - e.getY(), (int) vv[0], (int) vv[1])) {
				float[] a = new float[] { mv[i], mv[i + 1], mv[i + 2] };
				int index = context.modelVertices.indexOf(a);
				if (index != -1) {
					context.currentSelection = index;
				} else {
					context.currentSelection = context.modelVertices.size();
					context.modelVertices.add(a);
					context.projectedVertices.add(new float[] { ProjectionUtilities.screenToDeviceX(view, (int) vv[0]), ProjectionUtilities.screenToDeviceY(view, (int) vv[1]), 0 });
				}
				calibrate(view);
				return;
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e, IView view) {
		CalibrationContext context = getContext(view);
		if (context.currentSelection != -1) {
			float[] a = new float[] { ProjectionUtilities.screenToDeviceX(view, e.getX()), ProjectionUtilities.screenToDeviceY(view, view.getHeight() - e.getY()), 0 };
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
			float[] p = context.projectedVertices.get(context.currentSelection);
			float[] a = new float[] { p[0] + dx / view.getWidth(), p[1] + dy / view.getHeight(), 0 };
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
		for (IView v : view.getScene().getViews()) {
			getContext(v).load(p, iv);
			calibrate(v);
			iv++;
		}
	}

	private void saveCalibration(IView view) {
		Preferences p = PreferencesStore.get();
		int iv = 0;
		for (IView v : view.getScene().getViews()) {
			getContext(v).save(p, iv);
			iv++;
		}
	}

	private void clearCalibration(IView view) {
		contexts.put(view, new CalibrationContext());
		view.getCamera().setMatrices(null, null);
		calibrate(view);
	}

	private void calibrate(IView view) {
		CalibrationContext context = getContext(view);
		context.calibrated = false;
		try {
			double error = calibrator.calibrate(context.modelVertices, context.projectedVertices, view.getCamera().getNearClippingPlane(), view.getCamera().getFarClippingPlane());
			if (error < MAX_CALIBRATION_ERROR)
				context.calibrated = true;
			// System.out.println("error: " + error);
		} catch (Throwable t) {
		}
		if (context.calibrated)
			view.getCamera().setMatrices(calibrator.getProjectionMatrix(), calibrator.getModelMatrix());
		else
			view.getCamera().setMatrices(null, null);

		// need to update VBOs
		calibrationPoints.requestUpdate();
		calibrationLines.requestUpdate();
		
		// lazily repaint all views
		view.getScene().repaintViews();
	}
}
