package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public abstract class TextInputAlert extends WindowAlert
{
	private EditText input;

	/* Constructora */

	public TextInputAlert(Context context, int title, int messege, int textYes, int textNo)
	{
		super(context, title);

		setMessage(messege);

		input = new EditText(context);
		setView(input);
		
		setCancelable(false);

		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick(input.getText().toString());
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick(input.getText().toString());
			}
		});
	}

	public TextInputAlert(Context context, int title, int messege, int text, int textYes, int textNo)
	{
		this(context, title, messege, textYes, textNo);
		
		input.setText(text);
	}
	
	public TextInputAlert(Context context, int title, int messege, String text, int textYes, int textNo)
	{
		this(context, title, messege, textYes, textNo);
		
		input.setText(text);
	}

	/* Métodos Abstractos */

	public abstract void onPossitiveButtonClick(String text);

	public abstract void onNegativeButtonClick(String text);
}
