package com.example.paint;

import java.io.Serializable;

import android.graphics.Bitmap;

public class TexturaBMP implements Serializable
{
	private int[] pixelsBuffer;
	private int width, height;
	
	public TexturaBMP()
	{

	}
	
	public void setBitmap(int[] pixelsBuffer, int width, int height)
	{
		this.width = width;
		this.height = height;
		this.pixelsBuffer = pixelsBuffer;
	}
	
	public Bitmap getBitmap()
	{
		int screenshotSize = width * height;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
	    return bitmap;
	}
}
