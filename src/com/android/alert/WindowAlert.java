package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public abstract class WindowAlert
{
	protected Context mContext;
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	
	private View rootView;
	private LayoutInflater layoutInflater;

	/* Constructora */

	public WindowAlert(Context context, int title, boolean cancelable)
	{
		mContext = context;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(title));
		builder.setCancelable(cancelable);
	}
	
	public WindowAlert(Context context, String title, boolean cancelable)
	{
		mContext = context;
		layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setCancelable(cancelable);
	}

	/* Métodos Protegidos */

	protected void setPositiveButton(int text, OnClickListener listener)
	{
		builder.setPositiveButton(text, listener);
	}

	protected void setNegativeButton(int text, OnClickListener listener)
	{
		builder.setNegativeButton(text, listener);
	}

	protected void setNeutralButton(int text, OnClickListener listener)
	{
		builder.setNeutralButton(text, listener);
	}

	protected void setMessage(int message)
	{
		builder.setMessage(message);
	}
	
	protected void setMessage(CharSequence message)
	{
		builder.setMessage(message);
	}

	protected void setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener)
	{
		builder.setSingleChoiceItems(items, checkedItem, listener);
	}

	protected void setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener)
	{
		builder.setMultiChoiceItems(items, checkedItems, listener);
	}
	
	protected View findViewById(int id)
	{
		if (rootView != null)
		{
			return rootView.findViewById(id);
		}
		
		return null;
	}
	
	protected void setView(int layout)
	{
		rootView = layoutInflater.inflate(layout, null);
		builder.setView(rootView);
	}
	
	protected void changeMessage(int message)
	{
		if(dialog != null)
		{
			TextView text = (TextView) dialog.findViewById(android.R.id.message);
			text.setText(message);
		}
		else
		{
			setMessage(message);
		}
	}
	
	protected void changeMessage(CharSequence message)
	{
		if(dialog != null)
		{
			TextView text = (TextView) dialog.findViewById(android.R.id.message);
			text.setText(message);
		}
		else
		{
			setMessage(message);
		}
	}

	/* Métodos Públicos */

	public void show()
	{
		dialog = builder.show();
	}

	public void dismiss()
	{
		if (dialog != null)
		{
			dialog.dismiss();
		}
	}
}
