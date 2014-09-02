package ch.fhnw.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferUtil {

	public static ByteBuffer allocateDirect(int size) {
		ByteBuffer result = ByteBuffer.allocateDirect(size);
		result.order(ByteOrder.nativeOrder());
		return result;
	}

	public static void arraycopy(ByteBuffer src, int srcPos, ByteBuffer dst, int dstPos, int length) {
		if(src == dst) {
			src.clear();
			byte[] tmp = new byte[length];
			src.position(srcPos);
			src.get(tmp, 0, length);
			dst.position(dstPos);
			dst.put(tmp, 0, length);
		} else {
			src.position(srcPos);
			src.limit(srcPos + length);
			dst.position(dstPos);
			dst.put(src);
			src.limit(src.capacity());
		}
	}

	public static void fill(ByteBuffer buffer, int off, int len, byte val) {
		buffer.position(off);
		while(len-- >= 0)
			buffer.put(val);
	}

	public static byte[] toByteArray(ByteBuffer buffer) {
		return toByteArray(buffer, 0, buffer.capacity());
	}

	public static byte[] toByteArray(ByteBuffer buffer, int off, int len) {
		if(buffer.hasArray())
			if(off == 0 && len == buffer.capacity())
				return buffer.array();

		byte[] result = new byte[len];
		buffer.clear();
		buffer.position(off);
		buffer.get(result);
		return result;
	}
}
