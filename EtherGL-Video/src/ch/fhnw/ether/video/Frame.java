package ch.fhnw.ether.video;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public abstract class Frame {
	protected static final byte B0   = 0;
	protected static final byte B255 = (byte)255;
	private   static final ByteBuffer EMPTY = BufferUtilities.allocateDirect(0);

	public    ByteBuffer  pixels = EMPTY;
	public    int         dimI;
	public    int         dimJ;
	public    int         dimK;
	public    int         pixelSize;
	public    boolean     isPOT;
	private   boolean     transientBuffer;
	private   int         modCount;
	
	protected Frame(int pixelSize) {
		this.pixelSize = pixelSize;
	}

	protected Frame(int dimI, int dimJ, int dimK, byte[] frameBuffer, int pixelSize) {
		this.pixels = BufferUtilities.allocateDirect(frameBuffer.length);
		this.pixels.put(frameBuffer);
		this.pixelSize   = pixelSize;
		init(dimI, dimJ, dimK);
	}

	protected Frame(int dimI, int dimJ, int dimK, ByteBuffer frameBuffer, int pixelSize) {
		if(frameBuffer.isDirect()) {
			this.pixels = frameBuffer;
		} else {
			this.pixels = BufferUtilities.allocateDirect(frameBuffer.capacity());
			this.pixels.put(frameBuffer);
		}
		this.pixelSize = pixelSize;
		init(dimI, dimJ, dimK);
	}

	@Override
	public int hashCode() {
		return dimI << 18 | dimJ << 2 | pixelSize;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Frame) {
			Frame other = (Frame)obj;
			pixels.rewind();
			other.pixels.rewind();
			return 
					dimI         == other.dimI
					&& dimJ      == other.dimJ
					&& dimK      == other.dimK
					&& pixelSize == other.pixelSize
					&& pixels.equals(other.pixels);
		}
		return false;
	}

	public void setTransient() {
		this.transientBuffer = true;
	}

	@Override
	public String toString() {
		return dimI + "x"  + dimJ + "x" + dimK;
	}

	public void init(int dimI, int dimJ, int dimK) {
		this.dimI           = dimI;
		this.dimJ           = dimJ;
		this.dimK           = dimK;
		int bufsize         = dimI * dimJ * dimK * pixelSize;
		if(this.pixels.capacity() < bufsize)
			this.pixels = BufferUtilities.allocateDirect(bufsize);
		isPOT = isPOT(dimI) && isPOT(dimJ) && isPOT(dimK);
	}

	public static Frame newFrame(int dimI, int dimJ, int dimK, int pixelSize, ByteBuffer buffer) {
		switch(pixelSize) {
		case 2:  return new Grey16Frame(dimI, dimJ, dimK, buffer);
		case 3:  return new RGB8Frame(  dimI, dimJ, dimK, buffer);
		case 4:  return new RGBA8Frame( dimI, dimJ, dimK, buffer);
		default: throw new IllegalArgumentException("Can't create frame wiht pixelSize=" + pixelSize);
		}
	}

	public static Frame newFrame(int dimI, int dimJ, int dimK, int pixelSize, byte[] pixelsData) {
		return newFrame(dimI, dimJ, dimK, pixelSize, ByteBuffer.wrap(pixelsData));
	}

	public static Frame newFrame(BufferedImage img) {
		return newFrame(img, 0);
	}

	public static Frame newFrame(BufferedImage img, int flags) {
		Frame result = null;
		switch (img.getType()) {
		case BufferedImage.TYPE_BYTE_BINARY:
		case BufferedImage.TYPE_CUSTOM:
			if(img.getColorModel().getNumColorComponents() == 1)
				result = new Grey16Frame(img.getWidth(), img.getHeight(), 1);
			else {
				if(img.getColorModel().hasAlpha())
					result = new RGBA8Frame(img.getWidth(), img.getHeight(), 1);
				else
					result = new RGB8Frame(img.getWidth(), img.getHeight(), 1);
			}
			break;
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_BYTE_INDEXED:
			result = new RGBA8Frame(img.getWidth(), img.getHeight(), 1);
			break;
		case BufferedImage.TYPE_USHORT_555_RGB:
		case BufferedImage.TYPE_USHORT_565_RGB:
		case BufferedImage.TYPE_INT_RGB:
		case BufferedImage.TYPE_3BYTE_BGR:
			result = new RGB8Frame(img.getWidth(), img.getHeight(), 1);
			break;
		case BufferedImage.TYPE_BYTE_GRAY:
		case BufferedImage.TYPE_USHORT_GRAY:
			result = new Grey16Frame(img.getWidth(), img.getHeight(), 1);
			break;			
		default:
			throw new RuntimeException("Unsupported image type " + img.getType());
		}	

		result.setPixels(0, 0, img.getWidth(), img.getHeight(), img, flags);		
		return result;
	}

	public final void setPixels(int i, int j, int width, int height, BufferedImage img) {
		setPixels(i, j, width, height, img, 0);
	}

	public abstract void setPixels(int i, int j, int width, int height, BufferedImage img, int flags);

	public abstract Frame getSubframe(int i, int j, int k, int dimI, int dimJ, int dimK);

	public abstract void  setSubframe(int i, int j, int k, Frame frame);

	protected void getSubframeImpl(int i, int j, int k, Frame dst) {
		if(i + dst.dimI > dimI) throw new IllegalArgumentException("i(" + i + ")+dst.dimI(" + dst.dimI + ") > dimI(" + dimI + ")");
		if(j + dst.dimJ > dimJ) throw new IllegalArgumentException("j(" + j + ")+dst.dimJ(" + dst.dimJ + ") > dimJ(" + dimJ + ")");
		if(k + dst.dimK > dimK) throw new IllegalArgumentException("k(" + k + ")+dst.dimK(" + dst.dimK + ") > dimK(" + dimK + ")");
		int sslsize = dimI     * dimJ;
		int dslsize = dst.dimI * dst.dimJ;
		for(int kk = 0; kk < dst.dimK; kk++) {
			int slnsize = dimI;
			int dlnsize = dst.dimI;
			for(int jj = 0; jj < dst.dimJ; jj++)
				BufferUtilities.arraycopy(pixels, ((k + kk) * sslsize + (j + jj) * slnsize + i) * pixelSize, dst.pixels, (kk * dslsize + jj * dlnsize) * pixelSize, dlnsize * pixelSize);
		}
	}	

	protected void setSubframeImpl(int i, int j, int k, Frame src) {
		if(i + src.dimI > dimI) throw new IllegalArgumentException("i(" + i + ")+src.dimI(" + src.dimI + ") > dimI(" + dimI + ")");
		if(j + src.dimJ > dimJ) throw new IllegalArgumentException("j(" + j + ")+src.dimJ(" + src.dimJ + ") > dimJ(" + dimJ + ")");
		if(k + src.dimK > dimK) throw new IllegalArgumentException("k(" + k + ")+src.dimK(" + src.dimK + ") > dimK(" + dimK + ")");
		int dslsize = dimI     * dimJ;
		int sslsize = src.dimI * src.dimJ;
		for(int kk = 0; kk < src.dimK; kk++) {
			int slnsize = src.dimI;
			int dlnsize = dimI;
			for(int jj = 0; jj <  src.dimJ; jj++)
				BufferUtilities.arraycopy(src.pixels, (kk * sslsize + jj * slnsize) * pixelSize, pixels, ((k + kk) * dslsize + (j + jj) * dlnsize + i) * pixelSize, slnsize * pixelSize);
		}
	}

	public static Frame copyTo(Frame src, Frame dst) {
		if(src.getClass() == dst.getClass()) {
			for(int k = Math.min(src.dimK, dst.dimK); --k >= 0;)
				for(int j = Math.min(src.dimJ, dst.dimJ); --j >= 0;)
					BufferUtilities.arraycopy(src.pixels, (k * src.dimI * src.dimJ + j * src.dimI) * src.pixelSize, dst.pixels, (k * dst.dimI * dst.dimJ + j * dst.dimI) * dst.pixelSize, Math.min(src.dimI, dst.dimI) * src.pixelSize);
		}
		else {
			for(int k = Math.min(src.dimK, dst.dimK); --k >= 0;)
				for(int j = Math.min(src.dimJ, dst.dimJ); --j >= 0;)
					for(int i = Math.min(src.dimI, dst.dimI); --i >= 0;)
						dst.setARGB(i, j, k, src.getARGB(i, j, k));
		}
		dst.modified();
		return dst;
	}

	public abstract BufferedImage toBufferedImage();

	private static boolean isPOT(int value) {
		for(int i = 0; i < 23; i++)
			if(value == (1 << i))
				return true;
		return false;
	}

	public void disposeBuffer() {
		if(transientBuffer)
			pixels = null;
	}

	public void modified() {
		modCount++;
	}

	public int getModCount() {
		return modCount;
	}

	public abstract Frame copy();
	public abstract Frame alloc();

	public void  getRGBUnsigned(int i, int j, int k, int[] rgb) {
		int irgb = getARGB(i, j, k);
		rgb[0] = (irgb >> 16) & 0xFF;
		rgb[1] = (irgb >> 8) & 0xFF;
		rgb[2] = irgb & 0xFF;
	}

	public void  getRGB(int i, int j, int k, byte[] rgb) {
		int irgb = getARGB(i, j, k);
		rgb[0] = (byte) (irgb >> 16);
		rgb[1] = (byte) (irgb >> 8);
		rgb[2] = (byte) irgb;
	}

	public void getLUV(int i, int j, int k, float[] luv) {
		ColorUtilities.getLUVfromRGB(getARGB(i, j, k), luv);
	}

	public void setRGB(int i, int j, int k, byte[] rgb) {
		int argb = (rgb[0] & 0xFF) << 16;
		argb |= (rgb[1] & 0xFF) << 8;
		argb |= (rgb[2] & 0xFF);
		argb |= 0xFF000000;
		setARGB(i, j, k, argb);
	}

	public final float getComponentBilinear(double u, double v, int k, int component) {
		// bilinear interpolation
		final int dimI_ = dimI-1;
		final int dimJ_ = dimJ-1;

		int i0 = (int) (u * dimI_);
		int j0 = (int) (v * dimJ_);

		if(i0 < 0) i0 = 0; else if(i0 > dimI_) i0 = dimI_;
		if(j0 < 0) j0 = 0; else if(j0 > dimJ_) j0 = dimJ_;

		int i1 = i0 + 1;
		int j1 = j0 + 1;

		if(i1 < 0) i1 = 0; else if(i1 > dimI_) i1 = dimI_;
		if(j1 < 0) j1 = 0; else if(j1 > dimJ_) j1 = dimJ_;

		// interpolate
		final double w = (u - i0 / (double) dimI_) * dimI_;
		final double h = (v - j0 / (double) dimJ_) * dimJ_;

		float c00 = getFloatComponent(i0, j0, k, component);
		float c01 = getFloatComponent(i0, j1, k, component);
		float c10 = getFloatComponent(i1, j0, k, component);
		float c11 = getFloatComponent(i1, j1, k, component);

		float c = (float) (h * ((1 - w) * c01 + w * c11) + (1-h) * ((1 - w) * c00 + w * c10));

		return c;
	}
	
	public boolean hasAlpha() {
		return false;
	}
	
	public float getAlphaBilinear( double u, double v ) {
		// bilinear interpolation
		final int dimI_ = dimI-1;
		final int dimJ_ = dimJ-1;
		
		int i0 = (int) (u * dimI_);
		int j0 = (int) (v * dimJ_);
		
		if(i0 < 0) i0 = 0; else if(i0 > dimI_) i0 = dimI_;
		if(j0 < 0) j0 = 0; else if(j0 > dimJ_) j0 = dimJ_;
		
		int i1 = i0 + 1;
		int j1 = j0 + 1;
		
		if(i1 < 0) i1 = 0; else if(i1 > dimI_) i1 = dimI_;
		if(j1 < 0) j1 = 0; else if(j1 > dimJ_) j1 = dimJ_;
		
		// interpolate
		final double w = (u - i0 / (double) dimI_) * dimI_;
		final double h = (v - j0 / (double) dimJ_) * dimJ_;
		
		float c00 = getAlphaComponent(i0, j0);
		float c01 = getAlphaComponent(i0, j1);
		float c10 = getAlphaComponent(i1, j0);
		float c11 = getAlphaComponent(i1, j1);
		
		float c = (float) (h * ((1 - w) * c01 + w * c11) + (1-h) * ((1 - w) * c00 + w * c10));
		
		return c;
	}
	
	public float getAlphaComponent( int x, int y ) {
		throw new Error("Alpha not defined");
	}
	
	public void clear() {
		BufferUtilities.fill(pixels, 0, pixels.capacity(), B0);
	}

	public final Frame getSlice(int k) {
		pixels.position(dimI * dimJ * k * pixelSize);
		pixels.limit(pixels.position() + dimI * dimJ * pixelSize);
		try {
			return getClass().getConstructor(int.class, int.class, int.class, ByteBuffer.class).newInstance(Integer.valueOf(dimI), Integer.valueOf(dimJ), Integer.valueOf(1), pixels.slice());
		} catch(Throwable t) {
			return null;
		}
	}

	public abstract float   getBrightness(int i, int j, int k);
	public abstract float   getBrightnessBilinear(double u, double v, int k);
	public abstract void    getRGBBilinear(double u, double v, int k, byte[] rgb);
	public abstract float   getFloatComponent(int i, int j, int k, int component);
	public abstract void    setARGB(int i, int j, int k, int argb);
	public abstract int     getARGB(int i, int j, int k);

	
	public Frame flipJ() {
		Frame  result  = alloc();
		int    linelen = dimI * pixelSize;
		for(int k = 0; k < dimK; k++) {
			int base = k * dimJ * dimI * pixelSize;
			for(int j = dimJ / 2; --j >= 0;) {
				int top    = base + j * linelen;
				int bottom = base + (dimJ - 1 - j) * linelen;
				BufferUtilities.arraycopy(pixels, top,    result.pixels, bottom, linelen);
				BufferUtilities.arraycopy(pixels, bottom, result.pixels, top,    linelen);				
			}
			if((dimJ & 1) == 1) {
				int line  = base + (dimJ/2) * linelen;
				BufferUtilities.arraycopy(pixels, line,    result.pixels, line, linelen);				
			}
		}
		return result;
	}
	
	protected static final float linearInterpolate(float low, float high, float weight) {
		return low + ((high - low) * weight);
	}
}