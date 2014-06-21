package com.android.opengl;

public class BackgroundDataSaved
{
	private int[] backgroundId;
	private float[] backgroundPosition;
	private boolean[] backgroundEnabled;
	
	public BackgroundDataSaved(int[] index, float[] position, boolean[] enabled)
	{
		backgroundId = index;
		backgroundPosition = position;
		backgroundEnabled = enabled;
	}

	public int[] getBackgroundId()
	{
		return backgroundId;
	}

	public float[] getBackgroundPosition()
	{
		return backgroundPosition;
	}

	public boolean[] getBackgroundEnabled()
	{
		return backgroundEnabled;
	}
}
