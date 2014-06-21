package com.creation.paint;

import com.project.main.R;

public enum TTypeSize
{
	Small, Medium, Big;
	
	public int getSize()
	{
		switch(this)
		{
			case Small: 
				return 6;
			case Big:
				return 16;
			default:
				return 11;
		}
	}
	
	public int getImage()
	{
		switch (this)
		{
			case Small:
				return R.drawable.image_size_small;
			case Medium:
				return R.drawable.image_size_medium;
			case Big:
				return R.drawable.image_size_big;
			default:
				return -1;
		}
	}
}
