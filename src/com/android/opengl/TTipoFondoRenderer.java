package com.android.opengl;

import com.main.model.GamePreferences;

public enum TTipoFondoRenderer
{
	Nada, Fijo, Intercambiable, Desplazable;
	
	public int getNumBackgrounds()
	{
		switch(this)
		{
			case Fijo:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_FIJO;
			case Intercambiable:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_VIDEO;
			case Desplazable:
				return GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL;
			default:
				return 0;
		}
	}
}
