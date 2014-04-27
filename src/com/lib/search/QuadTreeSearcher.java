package com.lib.search;

import com.lib.math.Intersector;
import com.lib.math.Polygon;
import com.lib.math.Rectangle;

public class QuadTreeSearcher
{
	private static final int MAX_ELEMENTS = 4;
	private static final int MAX_SUBDIVISIONS = 2;
	
	private short[] idElements;
	private Polygon[] polygonElements;
	private int numElements;
	
	private Rectangle region;
	
	private QuadTreeSearcher[] childrens;
	private boolean subdivided;
	
	public QuadTreeSearcher(float x, float y, float width, float height)
	{		
		childrens = new QuadTreeSearcher[MAX_SUBDIVISIONS * MAX_SUBDIVISIONS];
		subdivided = false;
		
		idElements = new short[MAX_ELEMENTS];
		polygonElements = new Polygon[MAX_ELEMENTS];
		for (int i = 0; i < MAX_ELEMENTS; i++)
		{
			idElements[i] = -1;
		}
		
		region = new Rectangle(x, y, width, height);
	}
	
	public boolean insertElement(Polygon polygon, short id)
	{
		if (!Intersector.overlaps(region, polygon) && !Intersector.contains(region, polygon))
		{
			return false;
		}
		
		if (!subdivided)
		{
			if (numElements < MAX_ELEMENTS)
			{
				idElements[numElements] = id;
				polygonElements[numElements] = polygon;
				numElements++;
				return true;
			}
			else
			{
				subdivideRegion();
			}
		}
		
		for (int i = 0; i < MAX_SUBDIVISIONS * MAX_SUBDIVISIONS; i++)
		{
			childrens[i].insertElement(polygon, id);	
		}
		
		return true;
	}
	
	private void subdivideRegion()
	{
		for (int i = 0; i < MAX_SUBDIVISIONS; i++)
		{
			for (int j = 0; j < MAX_SUBDIVISIONS; j++)
			{
				childrens[i * MAX_SUBDIVISIONS + j] = new QuadTreeSearcher(region.x + i * region.width / MAX_SUBDIVISIONS, region.y + j * region.height / MAX_SUBDIVISIONS, region.width / MAX_SUBDIVISIONS, region.height / MAX_SUBDIVISIONS); 
			}
		}
		
		for (int i = 0; i < MAX_ELEMENTS; i++)
		{
			for (int j = 0; j < MAX_SUBDIVISIONS * MAX_SUBDIVISIONS; j++)
			{
				childrens[j].insertElement(polygonElements[i], idElements[i]);
			}
			
			polygonElements[i] = null;
			idElements[i] = -1;
		}
		
		subdivided = true;
	}
	
	public short searchElement(float x, float y)
	{
		if (!region.contains(x, y))
		{
			return -1;
			
		}
		
		if (!subdivided)
		{
			for (int i = 0; i < numElements; i++)
			{
				if (polygonElements[i].contains(x, y))
				{
					return idElements[i];
				}
			}
			
			return -1;
		}
		else
		{
			for (int i = 0; i < MAX_SUBDIVISIONS * MAX_SUBDIVISIONS; i++)
			{
				short result = childrens[i].searchElement(x, y);
				if (result != -1)
				{
					return result;
				}
			}
			
			return -1;
		}
	}
}
