package ch.fhnw.ether.formats.obj;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ch.fhnw.util.math.Vec3;

public class WavefrontObject {
	private List<Vec3> vertices = new ArrayList<Vec3>();
	private List<Vec3> normals = new ArrayList<Vec3>();
	private List<TexCoord> textures = new ArrayList<TexCoord>();
	private List<Group> groups = new ArrayList<Group>();
	private Map<String, Group> groupsDirectAccess = new HashMap<String, Group>();
	private Map<String, Material> materials = new HashMap<String, Material>();
	private String fileName;

	private ObjLineParserFactory parserFactory;

	private Material currentMaterial;

	private Group currentGroup;

	private String contextfolder = "";

	private double radius = 0;

	public WavefrontObject(String fileName, InputStream in) {
		try {
			this.fileName = fileName;

			int lastSlashIndex = fileName.lastIndexOf('/');
			if (lastSlashIndex != -1)
				this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

			lastSlashIndex = fileName.lastIndexOf('\\');
			if (lastSlashIndex != -1)
				this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

			parse(in);

			calculateRadius();
		} catch (Exception e) {
			System.out.println("Error, could not load obj:" + fileName);
		}
	}

	private void calculateRadius() {
		double currentNorm = 0;
		for (Vec3 vertex : vertices) {
			currentNorm = vertex.length();
			if (currentNorm > radius)
				radius = currentNorm;
		}
	}

	public String getContextfolder() {
		return contextfolder;
	}

	private void parse(InputStream input) {
		parserFactory = new ObjLineParserFactory(this);

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(input));

			for (String currentLine = null; (currentLine = in.readLine()) != null;)
				parseLine(currentLine);

			if (in != null)
				in.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error reading file :'" + fileName + "'");
		}
	}

	private void parseLine(String currentLine) {
		if ("".equals(currentLine))
			return;

		LineParser parser = parserFactory.getLineParser(currentLine);
		parser.parse();
		parser.incoporateResults(this);
	}

	public void setMaterials(Hashtable<String, Material> materials) {
		this.materials = materials;
	}

	public void setTextures(ArrayList<TexCoord> textures) {
		this.textures = textures;
	}

	public List<TexCoord> getTextures() {
		return textures;
	}

	public void setVertices(ArrayList<Vec3> vertices) {
		this.vertices = vertices;
	}

	public List<Vec3> getVertices() {
		return vertices;
	}

	public void setNormals(ArrayList<Vec3> normals) {
		this.normals = normals;
	}

	public List<Vec3> getNormals() {
		return normals;
	}

	public Map<String, Material> getMaterials() {

		return this.materials;
	}

	public Material getCurrentMaterial() {
		return currentMaterial;
	}

	public void setCurrentMaterial(Material currentMaterial) {
		this.currentMaterial = currentMaterial;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public Map<String, Group> getGroupsDirectAccess() {
		return groupsDirectAccess;
	}

	public Group getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(Group currentGroup) {
		this.currentGroup = currentGroup;
	}

	public String getBoudariesText() {
		float minX = 0;
		float maxX = 0;
		float minY = 0;
		float maxY = 0;
		float minZ = 0;
		float maxZ = 0;

		Vec3 currentVertex = null;
		for (int i = 0; i < getVertices().size(); i++) {
			currentVertex = getVertices().get(i);
			if (currentVertex.x > maxX)
				maxX = currentVertex.x;
			if (currentVertex.x < minX)
				minX = currentVertex.x;

			if (currentVertex.y > maxY)
				maxY = currentVertex.y;
			if (currentVertex.y < minY)
				minY = currentVertex.y;

			if (currentVertex.z > maxZ)
				maxZ = currentVertex.z;
			if (currentVertex.z < minZ)
				minZ = currentVertex.z;

		}

		return "maxX=" + maxX + " minX=" + minX + " maxY=" + maxY + " minY=" + minY + " maxZ=" + maxZ + " minZ=" + minZ;
	}

	public void printBoudariesText() {
		System.out.println(getBoudariesText());
	}
}
