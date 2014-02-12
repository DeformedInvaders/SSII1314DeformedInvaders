package com.android.dialog;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
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
	
	protected View findViewById(int id)
	{
		if(rootView != null)
		{
			return rootView.findViewById(id);
		}
		
		else return null;
	}
	
	protected ViewTreeObserver getViewTreeObserver()
	{
		if(rootView != null)
		{
			return rootView.getViewTreeObserver();
		}
		
		else return null;
	}
	
	protected void removeGlobalLayoutListener(OnGlobalLayoutListener listener)
	{
		rootView.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	}
	
	public void show(View view)
	{
		rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		int posX = view.getWidth()/4;
		int posY = rootView.getMeasuredHeight() + view.getHeight();
		
		popupWindow.showAsDropDown(view, posX, -posY);
	}
	
	protected void dismiss()
	{
		popupWindow.dismiss();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int action = event.getAction();
		
		if(action == MotionEvent.ACTION_OUTSIDE)
		{
			onTouchOutsidePopUp(v, event);
			return true;
		}
		
		return false;
	}
	
	protected abstract void onTouchOutsidePopUp(View v, MotionEvent event);
}
