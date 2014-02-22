package com.project.data;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class MapaBits implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Integer> pixelsCompressed;
	private int width, height;
	
	/* SECTION Constructora */
	
	public MapaBits()
	{
		
	}
	
	public MapaBits(int[] pixelsBuffer, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.pixelsCompressed = new ArrayList<Integer>();
		
		comprimirBitmap(pixelsBuffer);
	}
	
	public MapaBits(ArrayList<Integer> pixels, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.pixelsCompressed = pixels;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}

	public Bitmap getBitmap()
	{
		int arrayLong = width*height;	    
	    int buffer[] = descomprimirBitmap();

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(buffer, arrayLong-width, -width, 0, 0, width, height);
	    return bitmap;
	}
	
	/* SECTION Métodos Privados */
	
	private void comprimirBitmap(int[] array)
	{
		int arrayLong = width*height;
		int lastColor = 0;
	    
	    int i = 0;
	    while(i < arrayLong)
	    {
	    	int color = array[i];
	    	
	    	if(color != lastColor)
	    	{
	    		pixelsCompressed.add(i);
	    		pixelsCompressed.add(color);
	    		
	    		lastColor = color;
	    	}
	    	
	    	i++;
	    }
	}
	
	private int[] descomprimirBitmap()
	{
		int arrayLong = width*height;
		int[] buffer = new int[arrayLong];
		
		int color = 0;
		
		int i = 0;
		int j = 0;
		while(i < arrayLong)
		{
			if(j < pixelsCompressed.size() && i == pixelsCompressed.get(j))
			{
				color = pixelsCompressed.get(j+1);
				j = j+2;
			}
			
			buffer[i] = color;			
			i++;
		}
		
		return buffer;
	}
}
