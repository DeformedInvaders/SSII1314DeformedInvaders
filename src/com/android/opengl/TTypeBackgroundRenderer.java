package com.android.opengl;

import com.main.model.GamePreferences;

public enum TTypeBackgroundRenderer
{
	Blank, Static, Swappable, Movable;
	
	public int getNumBackgrounds()
	{
		switch(this)
		{
			case Static:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_STATIC;
			case Swappable:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO;
			case Movable:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL;
			default:
				return 0;
		}
	}
}
