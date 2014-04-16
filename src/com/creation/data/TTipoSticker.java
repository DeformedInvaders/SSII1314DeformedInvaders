package com.creation.data;

import com.project.main.R;

public enum TTipoSticker
{
	Trinket, Helmet, Eyes, Mouth, Weapon, Nada;
	
	public int getTitle()
	{
		switch(this)
		{
			case Trinket: 
				return R.string.title_dialog_trinkets;
			case Helmet:
				return R.string.title_dialog_helmets;
			case Eyes:
				return R.string.title_dialog_eyes;
			case Mouth:
				return R.string.title_dialog_mouths;
			case Weapon:
				return R.string.title_dialog_weapons;
			default:
				return -1;
		}
	}
}
