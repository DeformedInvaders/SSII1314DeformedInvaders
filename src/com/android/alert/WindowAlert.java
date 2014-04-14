package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.view.View;
import android.widget.TextView;

public abstract class WindowAlert
{
	private AlertDialog.Builder builder;
	private AlertDialog dialog;

	/* Constructora */

	public WindowAlert(Context context, int title)
	{
		builder = new AlertDialog.Builder(context);

		builder.setTitle(context.getString(title));
	}
	
	public WindowAlert(Context context, String title)
	{
		builder = new AlertDialog.Builder(context);
		
		builder.setTitle(title);
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

	protected void setView(View view)
	{
		builder.setView(view);
	}
	
	protected void setCancelable(boolean cancelable)
	{
		builder.setCancelable(cancelable);
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
