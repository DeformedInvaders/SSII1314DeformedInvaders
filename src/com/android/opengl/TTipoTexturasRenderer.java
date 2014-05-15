package com.android.opengl;

import com.main.model.GamePreferences;

public enum TTipoTexturasRenderer
{
	Personaje, Video, Juego;
	
	public int getNumCharacters()
	{
		switch(this)
		{
			case Personaje:
				return GamePreferences.NUM_TYPE_CHARACTER_DESIGN;
			case Video:
				return GamePreferences.NUM_TYPE_CHARACTER_VIDEO;
			case Juego:
				return GamePreferences.NUM_TYPE_CHARACTER_JUEGO;
			default:
				return 0;
		}
	}
	
	public int getNumTextures()
	{
		switch(this)
		{
			case Personaje:
				return GamePreferences.NUM_TYPE_CHARACTER_DESIGN + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_DESIGN);
			case Video:
				return GamePreferences.NUM_TYPE_CHARACTER_VIDEO + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_VIDEO);
			case Juego:
				return GamePreferences.NUM_TYPE_CHARACTER_JUEGO + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_JUEGO) + 2 * GamePreferences.NUM_TYPE_BUBBLES + 2 * GamePreferences.NUM_TYPE_PLATFORMS + 2 * GamePreferences.NUM_TYPE_WEAPONS + GamePreferences.NUM_TYPE_OPPONENTS + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_ENEMIES) + 2*GamePreferences.NUM_TYPE_SHOTS;
			default:
				return 0;
		}
	}
}
