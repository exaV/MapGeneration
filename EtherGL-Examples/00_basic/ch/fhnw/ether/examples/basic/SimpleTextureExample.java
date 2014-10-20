package ch.fhnw.ether.examples.basic;

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

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;

import ch.fhnw.ether.camera.Camera;
import ch.fhnw.ether.camera.ICamera;
import ch.fhnw.ether.controller.DefaultController;
import ch.fhnw.ether.controller.IController;
import ch.fhnw.ether.render.IRenderable;
import ch.fhnw.ether.render.IRenderer;
import ch.fhnw.ether.render.IRenderer.Pass;
import ch.fhnw.ether.render.attribute.IArrayAttribute;
import ch.fhnw.ether.render.attribute.IAttribute.PrimitiveType;
import ch.fhnw.ether.render.attribute.builtin.ColorArray;
import ch.fhnw.ether.render.attribute.builtin.PositionArray;
import ch.fhnw.ether.render.attribute.builtin.TexCoordArray;
import ch.fhnw.ether.render.gl.Texture;
import ch.fhnw.ether.render.shader.IShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader;
import ch.fhnw.ether.render.shader.builtin.MaterialShader.ShaderInput;
import ch.fhnw.ether.scene.AbstractScene;
import ch.fhnw.ether.scene.IScene;
import ch.fhnw.ether.scene.mesh.GenericMesh;
import ch.fhnw.ether.scene.mesh.IMesh;
import ch.fhnw.ether.scene.mesh.geometry.VertexGeometry;
import ch.fhnw.ether.scene.mesh.material.IMaterial;
import ch.fhnw.ether.scene.mesh.material.TextureMaterial;
import ch.fhnw.ether.view.IView;
import ch.fhnw.ether.view.gl.DefaultView;
import ch.fhnw.util.math.Vec3;

public final class SimpleTextureExample {

	private static IView view = null;

	// This is our own scene. Has its own Shader and own mesh.
	// And now also its own timer to animate things
	private static class CoolScene extends AbstractScene {

		private IShader s = new MaterialShader(EnumSet.of(ShaderInput.VERTEX_COLOR, ShaderInput.TEXTURE));
		private IMesh mesh = makeTexturedTriangle();
		private Timer timer = new Timer();
		private IRenderable r = null;

		public CoolScene(ICamera camera) {
			super(camera);
			mesh.getGeometry().setOrigin(new Vec3(0, 0, 0.25));
			mesh.getGeometry().setTranslation(new Vec3(0, 0, 0.5f));
			// setup an event timer
			timer.scheduleAtFixedRate(new TimerTask() {
				private int c = 0;

				@Override
				public void run() {

					// make some heavy animation calculation
					c += 4;
					if (c >= 360)
						c = 0;
					float f = 0.4f + 0.6f * (float) (Math.sin(Math.toRadians(c)) * 0.5 + 1);

					// apply changes to geometry
					mesh.getGeometry().setScale(new Vec3(f, f, f));
					VertexGeometry g = (VertexGeometry) mesh.getGeometry();
					float[] colors = g.getVertexData(1);
					for (int i = 0; i < colors.length; ++i) {
						if (i % 4 == 3)
							continue;
						colors[i] -= 0.2f * (1 - f);
						if (colors[i + 0] <= 0)
							colors[i + 0] = 1;
					}

					// update renderable
					r.requestUpdate();

					// update view, because we have no fix rendering loop but event-based rendering
					if (view != null)
						view.repaint();
				}
			}, 1000, 50);
		}

		@Override
		public void setRenderer(IRenderer renderer) {
			r = renderer.createRenderable(Pass.DEPTH, s, mesh.getMaterial(), Collections.singletonList(mesh.getGeometry()));
			renderer.addRenderables(r);
		}

	}

	// does anybody know why we need a "main"-procedure even though we use OOP?
	public static void main(String[] args) {
		new SimpleTextureExample();
	}

	// Let's generate a colored triangle
	static IMesh makeTexturedTriangle() {
		float[] position = { 0, 0, 0, 0, 0, 0.5f, 0.5f, 0, 0.5f };
		float[] color = { 1, 0.1f, 0.1f, 1, 0.1f, 1, 0.1f, 1, 0, 0, 1, 1 };
		float[] texCoord = { 0, 0, 0, 1, 1, 1 };
		float[][] data = { position, color, texCoord };
		IArrayAttribute[] attribs = { new PositionArray(), new ColorArray(), new TexCoordArray() };

		VertexGeometry g = new VertexGeometry(PrimitiveType.TRIANGLE, attribs, data);

		BufferedImage image = null;
		try {
			URL resource = SimpleTextureExample.class.getResource("assets/fhnw_logo.jpg");
			System.out.println(resource);
			image = ImageIO.read(resource);

			// flip the image vertically (alternatively, we could adjust tex coords, but for clarity, we flip the image)
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);
		} catch (Exception e) {
			System.err.println("cant load image");
			System.exit(1);
		}

		Texture texture = new Texture();
		texture.setData(image.getWidth(), image.getHeight(), ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()), GL.GL_RGB);

		IMaterial material = new TextureMaterial(texture);

		return new GenericMesh(g, material);
	}

	// Setup the whole thing
	public SimpleTextureExample() {

		// As always, make first a controller
		IController controller = new DefaultController();

		// And now the default view
		ICamera camera = new Camera();
		view = new DefaultView(controller, 100, 100, 500, 500, IView.ViewType.INTERACTIVE_VIEW, "Test", camera);

		// Use our own scene
		IScene scene = new CoolScene(camera);

		// Setup MVC
		controller.addView(view);
		controller.setScene(scene);
	}
}
