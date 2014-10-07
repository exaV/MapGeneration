package ch.fhnw.ether.formats.mtl;

import ch.fhnw.ether.formats.obj.CommentParser;
import ch.fhnw.ether.formats.obj.LineParserFactory;
import ch.fhnw.ether.formats.obj.WavefrontObject;

class MtlLineParserFactory extends LineParserFactory {
	public MtlLineParserFactory(WavefrontObject object) {
		this.object = object;
		parsers.put("newmtl", new MaterialParser());
		parsers.put("Ka", new KaParser());
		parsers.put("Kd", new KdParser());
		parsers.put("Ks", new KsParser());
		parsers.put("Ns", new NsParser());
		parsers.put("map_Kd", new KdMapParser(object));
		parsers.put("#", new CommentParser());
	}

}
