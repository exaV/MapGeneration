package ch.fhnw.util;

import java.io.PrintStream;

public class Log {
	private final PrintStream out = System.err;

	private Log() {}
	
	public static Log create() {
		return new Log();
	}

	public void severe(Throwable t) {
		t.printStackTrace(out);
	}

	public void warning(Throwable t) {
		t.printStackTrace(out);
	}

	public void warning(String msg) {
		out.println(msg);
	}

	public void info(Throwable t) {
		t.printStackTrace(out);
	}

}
