package com.android.opengl;

import com.main.model.GamePreferences;

public enum TTypeTexturesRenderer
{
	Character, Video, Game;
	
	public int getNumCharacters()
	{
		switch(this)
		{
			case Character:
				return GamePreferences.NUM_TYPE_CHARACTER_DESIGN;
			case Video:
				return GamePreferences.NUM_TYPE_CHARACTER_VIDEO;
			case Game:
				return GamePreferences.NUM_TYPE_CHARACTER_GAME;
			default:
				return 0;
		}
	}
	
	public int getNumTextures()
	{
		switch(this)
		{
			case Character:
				return GamePreferences.NUM_TYPE_CHARACTER_DESIGN + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_DESIGN);
			case Video:
				return GamePreferences.NUM_TYPE_CHARACTER_VIDEO + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_VIDEO) + GamePreferences.NUM_TYPE_ANIMATED_OBJECTS * GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS + GamePreferences.NUM_TYPE_INANIMATED_OBJECTS;
			case Game:
				return GamePreferences.NUM_TYPE_CHARACTER_GAME + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_CHARACTER_GAME) + 2 * GamePreferences.NUM_TYPE_SHIELD + 2 * GamePreferences.NUM_TYPE_PLATFORMS + 2 * GamePreferences.NUM_TYPE_WEAPONS + GamePreferences.NUM_TYPE_BOSS + (GamePreferences.NUM_TYPE_BOSS * GamePreferences.NUM_TYPE_STICKERS) + GamePreferences.NUM_TYPE_OPPONENTS + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_ENEMIES) + 2 * GamePreferences.NUM_TYPE_SHOTS;
			default:
				return 0;
		}
	}
}
