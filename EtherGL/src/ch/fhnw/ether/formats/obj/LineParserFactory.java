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

import java.util.Hashtable;

public abstract class LineParserFactory {

	protected Hashtable<String, LineParser> parsers = new Hashtable<>();
	protected WavefrontObject object = null;

	public LineParser getLineParser(String line) {
		if (line == null)
			return null;

		// String[] lineWords = line.split(" "); // Nhaaaaaaaaaaa, 3DS max doesn't use clean space but some other shity
		// character :( !

		// So I could use something like String regularExpression = "[A-Za-z]*([^\\-\\.0-9]*(\\-\\.0-9]*))";
		// .
		// .
		// .
		// or be nasty :P
		// line = line.replaceAll("[^ \\.\\-A-Za-z0-9#/]","");
		line = line.replaceAll("  ", " ");
		line = line.replaceAll("	", "");
		String[] lineWords = line.split(" ");

		// lineType is the first word in the line (except v,vp,vn,vt)

		if (lineWords.length < 1)
			return new DefaultParser();
		;

		String lineType = lineWords[0];

		LineParser parser = parsers.get(lineType);
		if (parser == null) {
			// System.out.println("ParserFactory: Cannot find the type of the line "+filename+"#"+lineNumber+"for key:'"+lineType+"' , returning default constructor");
			parser = new DefaultParser();
		}

		parser.setWords(lineWords);
		return parser;
	}
}
