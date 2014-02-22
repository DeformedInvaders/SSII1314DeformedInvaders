package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public abstract class TextInputAlert
{
	private AlertDialog.Builder alert;
	private EditText input;
	
	/* SECTION Constructora */
	
	public TextInputAlert(Context context, String title, String messege, String text, String textYes, String textNo)
	{
		alert = new AlertDialog.Builder(context);
		
		alert.setTitle(title);
		alert.setMessage(messege);

		input = new EditText(context);
		input.setText(text);
		alert.setView(input);

		alert.setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		alert.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}
	
	public TextInputAlert(Context context, String title, String messege, String textYes, String textNo)
	{
		this(context, title, messege, "", textYes, textNo);
	}
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	/* SECTION Métodos Públicos */
	
	public String getText()
	{
		return input.getText().toString();
	}
	
	public void show()
	{
		alert.show();
	}
}
