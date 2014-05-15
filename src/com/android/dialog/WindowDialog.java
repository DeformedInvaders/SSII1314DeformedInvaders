package com.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

public class WindowDialog
{
	protected Context mContext;
	private PopupWindow popupWindow;
	
	private View rootView;
	private LayoutInflater layoutInflater;

	/* Constructora */

	public WindowDialog(Context context, int layoutId, boolean cancelable)
	{
		mContext = context;

		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		rootView = layoutInflater.inflate(layoutId, null);
		popupWindow = new PopupWindow(rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(cancelable);
		popupWindow.setFocusable(cancelable);
	}
	/* Métodos Protegidos */

	protected View findViewById(int id)
	{
		if (rootView != null)
		{
			return rootView.findViewById(id);
		}
		
		return null;
	}

	protected ViewTreeObserver getViewTreeObserver()
	{
		if (rootView != null)
		{
			return rootView.getViewTreeObserver();
		}

		return null;
	}

	protected void removeGlobalLayoutListener(OnGlobalLayoutListener listener)
	{
		rootView.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
	}

	protected void showKeyBoard(Activity activity, EditText editText)
	{
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
	}

	protected void dismissKeyBoard(Activity activity, EditText editText)
	{
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/* Métodos Públicos */

	public void show(View view)
	{
		rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int posX = 0;
		int posY = (int) (rootView.getMeasuredHeight() + view.getHeight() / 2.0f);

		popupWindow.showAsDropDown(view, posX, -posY);
	}
	
	public void show(View view, int posX, int posY)
	{
		popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, posX, posY);
	}
	
	public void dismiss()
	{
		popupWindow.dismiss();
	}
}
