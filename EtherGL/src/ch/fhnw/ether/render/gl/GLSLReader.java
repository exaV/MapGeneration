package ch.fhnw.ether.render.gl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.fhnw.util.FileUtilities;

public class GLSLReader {
	private final URL library;
	private final URL shader;
	
	private int lineNumber = 1;
	
	private static final Pattern LOCAL_INCLUDE = Pattern.compile("\"(.+?)\"");
	private static final Pattern LIB_INCLUDE = Pattern.compile("<(.+?)>");

	public GLSLReader(URL library, URL shader, StringBuilder code, PrintStream out) throws IOException {
		this.library = library;
		this.shader = shader;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(shader.openStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.startsWith("#include")) {
					code.append("#line 1\n");
					lineNumber++;
					include(line, code, out);
					code.append("#line " + lineNumber + "\n");
				} else {
					code.append(line);
					code.append('\n');
					lineNumber++;
				}
			}
		} catch (FileNotFoundException e) {
			out.println("include file not found: " + shader);
			throw new IOException();
		}
	}

	private void include(String line, StringBuilder code, PrintStream out) throws IOException {
		Matcher matcher;
		
		matcher = LIB_INCLUDE.matcher(line);
		if (matcher.find()) {
			String path = library.toString();
			new GLSLReader(library, new URL(path + "/" + matcher.group(1)), code, out);
			return;
		}
		matcher = LOCAL_INCLUDE.matcher(line);
		if (matcher.find()) {
			String path = FileUtilities.getBasePath(shader.toString());
			new GLSLReader(library, new URL(path + "/" + matcher.group(1)), code, out);
			return;
		}
	}
}
