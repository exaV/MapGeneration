package ch.fhnw.ether.formats.obj;

import ch.fhnw.ether.geom.RGB;

public class Material {
	private Texture texture;
	private RGB Ka;
	private RGB Kd;
	private RGB Ks;
	private float shininess;
	private String name;
	public String texName;

	public Material(String name) {
		Ka = new RGB(1, 1, 1);
		Kd = new RGB(1, 1, 1);
		Ks = new RGB(0.5f, 0.5f, 0.5f);
		texture = null;
		name = null;
		texName = null;
		shininess = 0;
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public RGB getKa() {
		return Ka;
	}

	public RGB getKd() {
		return Kd;
	}

	public RGB getKs() {
		return Ks;
	}

	public float getShininess() {
		return shininess;
	}

	public void setKa(RGB ka) {
		Ka = ka;
	}

	public void setKd(RGB kd) {
		Kd = kd;
	}

	public void setKs(RGB ks) {
		Ks = ks;
	}

	public void setShininess(float s) {
		shininess = s;
	}

}
