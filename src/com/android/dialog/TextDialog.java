package com.android.dialog;

import android.content.Context;
import android.widget.TextView;

import com.project.main.R;

public class TextDialog extends WindowDialog
{
	private TextView text;
	
	public TextDialog(Context context)
	{
		super(context, R.layout.dialog_text_layout, false);
		
		text = (TextView) findViewById(R.id.textViewDialog1);
	}

	/* Métodos Públicos */
	
	public void setText(int message)
	{
		text.setText(message);
	}
}
