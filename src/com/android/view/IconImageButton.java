package com.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.project.main.R;

public class IconImageButton extends ImageButton
{
	private boolean activate, visible;
	private int backgroundResource, backgroundResourcePressed;
	
	public IconImageButton(Context context)
	{
		super(context);
		
		visible = true;
		activate = false;
		backgroundResource = -1;
		backgroundResourcePressed = -1;
	}
	
	public IconImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		visible = true;
		activate = false;
		backgroundResource = -1;
		backgroundResourcePressed = -1;
		
		if (attrs != null)
		{
			backgroundResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
			backgroundResourcePressed = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.project.main", "background_pressed", -1);
		}
	}
	
	public IconImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
		visible = true;
		activate = false;
		backgroundResource = -1;
		backgroundResourcePressed = -1;
		
		if (attrs != null)
		{
			backgroundResource = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
			backgroundResourcePressed = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/com.project.main", "background_pressed", -1);
		}
	}
	
	@Override
	public void setBackgroundResource(int rsc)
	{
		backgroundResource = rsc;
		
		updateBackground();
	}
	
	@Override
	public void setVisibility(int visibility)
	{
		visible = visibility == View.VISIBLE;
		super.setEnabled(visible);
		
		updateBackground();
	}
	
	public void setActivo(boolean pressed)
	{		
		activate = pressed;
		
		updateBackground();
	}
	
	private void updateBackground()
	{
		if (visible)
		{
			if (activate)
			{
				if (backgroundResourcePressed != -1)
				{
					super.setBackgroundResource(backgroundResourcePressed);
				}
				else if (backgroundResource != -1)
				{
					super.setBackgroundResource(backgroundResource);
				}
			}
			else
			{
				if (backgroundResource != -1)
				{
					super.setBackgroundResource(backgroundResource);
				}
			}
		}
		else
		{
			super.setBackgroundResource(R.drawable.icon_gone);
		}
	}
}
