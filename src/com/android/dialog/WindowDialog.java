package com.android.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;

public abstract class WindowDialog implements OnTouchListener
{
	protected Context mContext;
	
	private View rootView;
	private PopupWindow popupWindow;
	private LayoutInflater layoutInflater;

	/* SECTION Constructora */

	public WindowDialog(Context context, int id)
	{
		mContext = context;

		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		rootView = layoutInflater.inflate(id, null);
		popupWindow = new PopupWindow(rootView, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchInterceptor(this);
		popupWindow.setFocusable(true);
	}

	/* SECTION Métodos Abstractos */

	protected abstract void onTouchOutsidePopUp(View v, MotionEvent event);

	/* SECTION Métodos Protegidos */

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

	protected void dismiss()
	{
		popupWindow.dismiss();
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

	/* SECTION Métodos Públicos */

	public void show(View view)
	{
		rootView.measure(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		int posX = view.getWidth() / 4;
		int posY = rootView.getMeasuredHeight() + view.getHeight();

		popupWindow.showAsDropDown(view, posX, -posY);
	}

	/* SECTION Métodos Listener onTouch */

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		int action = event.getAction();

		if (action == MotionEvent.ACTION_OUTSIDE)
		{
			onTouchOutsidePopUp(v, event);
			return true;
		}

		return false;
	}
}
