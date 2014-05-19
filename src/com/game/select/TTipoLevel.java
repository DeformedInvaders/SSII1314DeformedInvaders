package com.game.select;

import android.graphics.Color;

import com.main.model.GamePreferences;
import com.project.main.R;

public enum TTipoLevel
{
	Moon, NewYork, Rome, Egypt, Stonehenge;
	
	public int getTitle()
	{
		switch(this)
		{
			case Moon: 
				return R.string.title_level_section_moon;
			case NewYork:
				return R.string.title_level_section_newyork;
			case Rome:
				return R.string.title_level_section_rome;
			case Egypt:
				return R.string.title_level_section_egypt;
			case Stonehenge:
				return R.string.title_level_section_stonehenge;
			default:
				return -1;
		}
	}
	
	public int getDescription()
	{
		switch(this)
		{
			case Moon: 
				return R.string.text_level_description_moon;
			case NewYork:
				return R.string.text_level_description_newyork;
			case Rome:
				return R.string.text_level_description_rome;
			case Egypt:
				return R.string.text_level_description_egypt;
			case Stonehenge:
				return R.string.text_level_description_stonehenge;
			default:
				return -1;
		}
	}
	
	public int getColor()
	{
		switch(this)
		{
			case Moon:
				return Color.WHITE;
			case NewYork:
				return Color.BLACK;
			case Rome:
				return Color.WHITE;
			case Egypt:
				return Color.BLACK;
			case Stonehenge:
				return Color.BLACK;
			default:
				return -1;
		}		
	}
	
	public int getMusica()
	{
		switch(this)
		{
			case Moon:
				return R.raw.music_game_moon;
			case NewYork:
				return R.raw.music_game_newyork;
			case Rome:
				return R.raw.music_game_rome;
			case Egypt:
				return R.raw.music_game_egypt;
			case Stonehenge:
				return R.raw.music_game_stonehenge;
			default:
				return -1;
		}		
	}
	
	public int getFondoSol()
	{
		if (GamePreferences.IS_LONG_RATIO())
		{
			switch(this)
			{
				case Moon:
					return R.drawable.background_long_moon_sun;
				case NewYork:
					return R.drawable.background_long_newyork_sun;
				case Rome:
					return R.drawable.background_long_rome_sun;
				case Egypt:
					return R.drawable.background_long_egypt_sun;
				case Stonehenge:
					return R.drawable.background_long_stonehenge_sun;
				default:
					return -1;
			}
		}
		else
		{
			switch(this)
			{
				case Moon:
					return R.drawable.background_notlong_moon_sun;
				case NewYork:
					return R.drawable.background_notlong_newyork_sun;
				case Rome:
					return R.drawable.background_notlong_rome_sun;
				case Egypt:
					return R.drawable.background_notlong_egypt_sun;
				case Stonehenge:
					return R.drawable.background_notlong_stonehenge_sun;
				default:
					return -1;
			}
		}
	}
	
	public int getFondoDisplay()
	{
		if(GamePreferences.IS_LONG_RATIO())
		{
			switch(this)
			{
				case Moon:
					return R.drawable.background_long_moon_display;
				case NewYork:
					return R.drawable.background_long_newyork_display;
				case Rome:
					return R.drawable.background_long_rome_display;
				case Egypt:
					return R.drawable.background_long_egypt_display;
				case Stonehenge:
					return R.drawable.background_long_stonehenge_display;
				default:
					return -1;
			}	
		}
		else
		{
			switch(this)
			{
				case Moon:
					return R.drawable.background_notlong_moon_display;
				case NewYork:
					return R.drawable.background_notlong_newyork_display;
				case Rome:
					return R.drawable.background_notlong_rome_display;
				case Egypt:
					return R.drawable.background_notlong_egypt_display;
				case Stonehenge:
					return R.drawable.background_notlong_stonehenge_display;
				default:
					return -1;
			}				
		}
	}
}
