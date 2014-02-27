package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;

public abstract class WindowAlert
{
	private AlertDialog.Builder builder;
	private AlertDialog dialog;
	
	/* SECTION Constructora */
	
	public WindowAlert(Context context, String title)
	{
		builder = new AlertDialog.Builder(context);
		
		builder.setTitle(title);
	}
	
	/* SECTION M�todos Protegidos */
	
	protected void setPositiveButton(CharSequence text, OnClickListener listener)
	{
		builder.setPositiveButton(text, listener);
	}
	
	protected void setNegativeButton(CharSequence text, OnClickListener listener)
	{
		builder.setNegativeButton(text, listener);
	}
	
	protected void setNeutralButton(CharSequence text, OnClickListener listener)
	{
		builder.setNeutralButton(text, listener);
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
	
	protected void setView(View view)
	{
		builder.setView(view);
	}
	
	/* SECTION M�todos P�blicos */
	
	public void show()
	{
		dialog = builder.create();
		builder.show();
	}
	
	public void dismiss()
	{
		if(dialog != null)
		{
			dialog.dismiss();
		}
	}
}