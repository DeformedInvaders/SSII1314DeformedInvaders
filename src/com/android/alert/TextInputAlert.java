package com.android.alert;

import com.project.main.R;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public abstract class TextInputAlert extends WindowAlert
{
	private EditText inputText;

	/* Constructora */

	public TextInputAlert(Context context, int title, int messege, int textYes, int textNo, boolean restringido)
	{
		super(context, title, false);

		setMessage(messege);

		if (restringido)
		{
			setView(R.layout.alert_textinput_restricted_layout);
		}
		else
		{
			setView(R.layout.alert_textinput_layout);
		}
		
		inputText = (EditText) findViewById(R.id.editTextInputTextAlert1);
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick(inputText.getText().toString());
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick(inputText.getText().toString());
			}
		});
	}

	public TextInputAlert(Context context, int title, int messege, int text, int textYes, int textNo, boolean restringido)
	{
		this(context, title, messege, textYes, textNo, restringido);
		
		inputText.setText(text);
	}
	
	public TextInputAlert(Context context, int title, int messege, String text, int textYes, int textNo, boolean restringido)
	{
		this(context, title, messege, textYes, textNo, restringido);
		
		inputText.setText(text);
	}

	/* M�todos Abstractos */

	public abstract void onPossitiveButtonClick(String text);

	public abstract void onNegativeButtonClick(String text);
}
