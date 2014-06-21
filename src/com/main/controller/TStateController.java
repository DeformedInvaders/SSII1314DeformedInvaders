package com.main.controller;

import com.project.main.R;

public enum TStateController
{
	Main, Design, Paint, Repaint, Deformation, Redeformation, CharacterSelection, LevelSelection, Game, Video;
	
	public int getTitle()
	{
		switch(this)
		{
			case Design: 
				return R.string.title_design_phase;
			case Paint:
			case Repaint:
				return R.string.title_paint_phase;
			case Deformation:
			case Redeformation:
				return R.string.title_deformation_phase;
			case CharacterSelection:
				return R.string.title_character_selection_phase;
			case LevelSelection:
				return R.string.title_level_selection_phase;
			default:
				return R.string.title_main_phase;
		}
	}
}
