package ch.fhnw.ether.formats.obj.parser;


//import java.util.Hashtable;

class ObjLineParserFactory extends LineParserFactory {
	public ObjLineParserFactory(WavefrontObject object) {
		this.object = object;
		parsers.put("v", new VertexParser());
		parsers.put("vn", new NormalParser());
		parsers.put("vp", new FreeFormParser());
		parsers.put("vt", new TextureCooParser());
		parsers.put("f", new FaceParser(object));
		parsers.put("#", new CommentParser());
		parsers.put("mtllib", new MaterialFileParser(object));
		parsers.put("usemtl", new MaterialParser());
		parsers.put("g", new GroupParser());
		parsers.put("o", new GroupParser());
	}

}
