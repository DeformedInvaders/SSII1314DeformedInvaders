package com.game.data;

import android.graphics.Typeface;

import com.game.select.TTypeLevel;

public class Level
{
	private Typeface levelFont;
	private TTypeLevel levelType;
	private int levelBackground, levelImageCompleted, levelImagePerfected, levelImageMastered;
	private int levelName, levelDescription, levelColor, levelMusic;

	public Level(TTypeLevel level, int background, int imageCompleted, int imagePerfected, int imageMastered, int name, int description, int color, Typeface font, int music)
	{
		levelBackground = background;
		levelImageCompleted = imageCompleted;
		levelImagePerfected = imagePerfected;
		levelImageMastered = imageMastered;
		levelName = name;
		levelDescription = description;
		levelColor = color;
		levelType = level;
		levelFont = font;
		levelMusic = music;
	}

	public int getLevelBackground()
	{
		return levelBackground;
	}
	
	public int getLevelImageCompleted()
	{
		return levelImageCompleted;
	}
	
	public int getLevelImagePerfected()
	{
		return levelImagePerfected;
	}

	public int getLevelImageMastered()
	{
		return levelImageMastered;
	}
	
	public int getLevelName()
	{
		return levelName;
	}

	public int getLevelDescription()
	{
		return levelDescription;
	}

	public int getLevelColor()
	{
		return levelColor;
	}

	public TTypeLevel getLevelType()
	{
		return levelType;
	}
	
	public Typeface getLevelFont()
	{
		return levelFont;
	}
	
	public int getLevelMusic()
	{
		return levelMusic;
	}
}
