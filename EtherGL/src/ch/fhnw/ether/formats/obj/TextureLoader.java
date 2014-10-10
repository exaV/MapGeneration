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

package ch.fhnw.ether.formats.obj;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class TextureLoader {

	private TextureLoader() {
	}

	private static TextureLoader instance = null;
	private Hashtable<String, BufferedImage> bufferedImageCache = new Hashtable<String, BufferedImage>();

	public static TextureLoader instance() {
		if (instance == null)
			instance = new TextureLoader();
		return instance;
	}

	Texture[] loadAnimation(String path, int cols, int rows, int textWidth, int textHeight) {
		return loadAnimation(path, cols, rows, textWidth, textHeight, 0, 0);
	}

	private Texture[] loadAnimation(String path, int cols, int rows, int textWidth, int textHeight, int xOffSet, int yOffSet) {
		Texture[] toReturntextures = new Texture[cols * rows];

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++) {
				toReturntextures[i * cols + j] = loadTexture(path, j * textWidth + xOffSet, i * textHeight + yOffSet, textWidth, textHeight);
			}

		return toReturntextures;
	}

	public Texture loadTexture(String path) {
		return loadTexture(path, 0, 0, 0, 0);
	}

	private Texture loadTexture(String path, int xOffSet, int yOffSet, int textWidth, int textHeight) {

		Texture toReturn = null;

		BufferedImage buffImage = bufferedImageCache.get(path);

		if (buffImage == null)
			try {
				buffImage = ImageIO.read(getClass().getResourceAsStream(path));
				// System.out.println("URL loaded:"+path);
			} catch (Exception e) {
				try {
					buffImage = ImageIO.read(new File(path));
					// System.out.println("F loaded:"+path);
				} catch (Exception e2) {
					System.err.println("Could not load path '" + path + "'");
					e.printStackTrace();
					e2.printStackTrace();
					return null;
				}
			}

		bufferedImageCache.put(path, buffImage);

		int bytesPerPixel = buffImage.getColorModel().getPixelSize() / 8;
		if (textWidth == 0)
			textWidth = buffImage.getWidth();
		if (textHeight == 0)
			textHeight = buffImage.getHeight();

		ByteBuffer scratch = ByteBuffer.allocateDirect(textWidth * textHeight * bytesPerPixel).order(ByteOrder.nativeOrder());
		DataBufferByte data = ((DataBufferByte) buffImage.getRaster().getDataBuffer());

		for (int i = 0; i < textHeight; i++)
			scratch.put(data.getData(), (xOffSet + (yOffSet + i) * buffImage.getWidth()) * bytesPerPixel, textWidth * bytesPerPixel);

		scratch.rewind();

		// Create A IntBuffer For Image Address In Memory
		IntBuffer buf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		/*
		 * GL11.glGenTextures(buf); // Create Texture In OpenGL
		 * 
		 * // Create Nearest Filtered Texture GL11.glBindTexture(GL11.GL_TEXTURE_2D, buf.get(0));
		 * GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		 * GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR); int bpp = bytesPerPixel
		 * == 4 ? GL11.GL_RGBA : GL11.GL_RGB ; GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0,bpp,textWidth,textHeight, 0,bpp,
		 * GL11.GL_UNSIGNED_BYTE, scratch);
		 */
		toReturn = new Texture(buf.get(0), textWidth, textHeight);

		// System.out.println("Texture path '"+path+"-"+xOffSet+"-"+yOffSet+"-"+textWidth+"-"+textHeight+"', is loaded with id="+toReturn.getTextureID());

		return toReturn;
	}
}
