package com.lib.search;

import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.Polygon;

public class TriangleQuadTreeSearcher
{
	QuadTreeSearcher searcher;
	
	public TriangleQuadTreeSearcher(TriangleArray triangulos, VertexArray vertices, float x, float y, float width, float height)
	{
		searcher = new QuadTreeSearcher(x, y, width, height);
		
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getCVertex(i);
			short c = triangulos.getBVertex(i);
			
			float[] triangle = new float[6];
			triangle[0] = vertices.getXVertex(a);
			triangle[1] = vertices.getYVertex(a);
			triangle[2] = vertices.getXVertex(b);
			triangle[3] = vertices.getYVertex(b);
			triangle[4] = vertices.getXVertex(c);
			triangle[5] = vertices.getYVertex(c);
			
			searcher.insertElement(new Polygon(triangle), i);
		}
	}
	
	public short searchTriangle(float x, float y)
	{
		return searcher.searchElement(x, y);
	}
}
