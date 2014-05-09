package com.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.project.main.R;

public class IconImageButton extends ImageButton
{
	private int backgroundResource;
	
	public IconImageButton(Context context)
	{
		super(context);
		
		backgroundResource = -1;
	}
	
	public IconImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		backgroundResource = -1;
		
		if (attrs != null)
		{
			backgroundResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
		}
	}
	
	public IconImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		backgroundResource = -1;
		
		if (attrs != null)
		{
			backgroundResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
		}
	}
	
	@Override
	public void setBackgroundResource(int rsc)
	{
		backgroundResource = rsc;
		super.setBackgroundResource(backgroundResource);
	}
	
	@Override
	public void setVisibility(int visible)
	{
		if (visible == View.VISIBLE)
		{
			if (backgroundResource != -1)
			{
				super.setBackgroundResource(backgroundResource);
				super.setEnabled(true);
			}
		}
		else
		{
			super.setBackgroundResource(R.drawable.icon_gone);
			super.setEnabled(false);
		}
	}
}
