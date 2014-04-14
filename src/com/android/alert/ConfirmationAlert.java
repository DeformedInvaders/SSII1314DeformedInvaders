package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;

public abstract class ConfirmationAlert extends WindowAlert
{
	/* Constructora */

	public ConfirmationAlert(Context context, int title, int messege, int textYes, int textNo)
	{
		super(context, title);

		setMessage(messege);
		
		setCancelable(false);

		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}

	/* Métodos Abstractos */

	public abstract void onPossitiveButtonClick();

	public abstract void onNegativeButtonClick();
}
