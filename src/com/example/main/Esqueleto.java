package com.example.main;

import java.io.Serializable;

import android.graphics.Color;

import com.example.paint.TexturaBMP;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class Esqueleto implements Serializable
{
	private FloatArray hull;
	private FloatArray mesh;
	private ShortArray triangles;
	private int color;
	private TexturaBMP texture;
	private FloatArray coords;
	
	public Esqueleto(FloatArray hull, FloatArray mesh, ShortArray triangles)
	{
		this.hull = hull;
		this.mesh = mesh;
		this.triangles = triangles;
		this.color = Color.WHITE;
	}
	
	public void setColor(int color)
	{
		this.color = color;
	}
	
	public void setTexture(TexturaBMP texture, FloatArray coords)
	{
		this.texture = texture;
		this.coords = coords;
	}

	public FloatArray getHull()
	{
		return hull;
	}

	public FloatArray getMesh()
	{
		return mesh;
	}

	public ShortArray getTriangles()
	{
		return triangles;
	}

	public int getColor()
	{
		return color;
	}

	public TexturaBMP getTexture()
	{
		return texture;
	}

	public FloatArray getCoords()
	{
		return coords;
	}
}
