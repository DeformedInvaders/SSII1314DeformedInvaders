package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public abstract class TextInputAlert extends WindowAlert
{
	private EditText input;

	/* SECTION Constructora */

	public TextInputAlert(Context context, String title, String messege, String textYes, String textNo)
	{
		this(context, title, messege, "", textYes, textNo);
	}

	public TextInputAlert(Context context, String title, String messege, String text, String textYes, String textNo)
	{
		super(context, title);

		setMessage(messege);

		input = new EditText(context);
		input.setText(text);
		setView(input);

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

	/* SECTION Métodos Abstractos */

	public abstract void onPossitiveButtonClick(String text);

	public abstract void onNegativeButtonClick(String text);
}
