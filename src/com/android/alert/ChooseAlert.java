package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;

public abstract class ChooseAlert extends WindowAlert
{
	private int selected;
	private String[] lista;

	/* Constructora */

	public ChooseAlert(Context context, int title, int textYes, int textNo, String[] list)
	{
		super(context, title);

		selected = -1;
		lista = list;
		
		setCancelable(false);

		setSingleChoiceItems(lista, selected, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				selected = which;
			}
		});

		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				if (selected != -1)
				{
					onSelectedPossitiveButtonClick(lista[selected]);
				}
				else
				{
					onNoSelectedPossitiveButtonClick();
				}
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}

	/* Métodos Abstractos */

	public abstract void onSelectedPossitiveButtonClick(String selected);

	public abstract void onNoSelectedPossitiveButtonClick();

	public abstract void onNegativeButtonClick();

}
