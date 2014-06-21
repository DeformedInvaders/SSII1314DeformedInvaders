package com.creation.data;

import com.project.main.R;

public enum TTypeMovement
{
	Run, Jump, Crouch, Attack;
	
	public int getTitle()
	{
		switch(this)
		{
			case Run: 
				return R.string.title_animation_section_run;
			case Jump:
				return R.string.title_animation_section_jump;
			case Crouch:
				return R.string.title_animation_section_crouch;
			case Attack:
				return R.string.title_animation_section_attack;
			default:
				return -1;
		}
	}
	
	public int getSound()
	{
		switch(this)
		{
			case Jump:
				return R.raw.effect_game_jump;
			case Crouch:
				return R.raw.effect_game_crouch;
			case Attack:
				return R.raw.effect_game_attack;
			default:
				return -1;
		}
	}
}
