package com.creation.data;

import com.project.main.R;

public enum TTipoMovimiento
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
}
