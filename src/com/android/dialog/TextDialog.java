package com.android.dialog;

import android.content.Context;
import android.widget.TextView;

import com.project.main.R;

public class TextDialog extends WindowDialog
{
	private TextView textView;
	
	public TextDialog(Context context, int layout)
	{
		super(context, layout, false);
		textView = (TextView) findViewById(R.id.textViewDialog1);
	}

	/* Métodos Públicos */
	
	public void setText(int message)
	{
		textView.setText(message);
	}
}
