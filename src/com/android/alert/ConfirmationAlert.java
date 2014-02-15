package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ConfirmationAlert
{
	private AlertDialog.Builder alert;
	
	public ConfirmationAlert(Context context, String title, String messege, String textYes, String textNo)
	{
		alert = new AlertDialog.Builder(context);
		
		alert.setTitle(title);
		alert.setMessage(messege);

		alert.setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		alert.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	public void show()
	{
		alert.show();
	}
}
