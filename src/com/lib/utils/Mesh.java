package com.lib.utils;

public class Mesh {

	private FloatArray vertices;
	private ShortArray triangulos;

	public Mesh() {
		this.vertices = new FloatArray();
		this.triangulos = new ShortArray();
	}

	public Mesh(FloatArray vertices, ShortArray triangulos) {
		this.vertices = vertices;
		this.triangulos = triangulos;
	}

	public FloatArray getVertices() {
		return vertices;
	}

	public void setVertices(FloatArray vertices) {
		this.vertices = vertices;
	}

	public ShortArray getTriangulos() {
		return triangulos;
	}

	public void setTriangulos(ShortArray triangulos) {
		this.triangulos = triangulos;
	}

}
