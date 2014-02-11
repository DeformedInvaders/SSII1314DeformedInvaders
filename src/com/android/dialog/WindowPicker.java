package com.android.dialog;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

public abstract class WindowPicker implements OnTouchListener
{
	private View rootView;
	private PopupWindow popupWindow;
	private LayoutInflater layoutInflater;
	
	public WindowPicker(Context context, int id)
	{
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		rootView = layoutInflater.inflate(id, null);
		popupWindow = new PopupWindow(rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchInterceptor(this);
	}
	
	public void show(View view)
	{
		rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		int posX = view.getWidth()/4;
		int posY = rootView.getMeasuredHeight() + view.getHeight();
		
		popupWindow.showAsDropDown(view, posX, -posY);
	}
	
	protected View findViewById(int id)
	{
		if(rootView != null)
		{
			return rootView.findViewById(id);
		}
		
		else return null;
	}
	
	protected void setContextView(int id)
	{
		rootView = layoutInflater.inflate(id, null);
		popupWindow = new PopupWindow(rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
		
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchInterceptor(this);
	}
	
	protected void dismiss()
	{
		popupWindow.dismiss();
	}
}
