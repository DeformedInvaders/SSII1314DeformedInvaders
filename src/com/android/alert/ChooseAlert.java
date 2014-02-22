package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ChooseAlert
{
	private AlertDialog.Builder builder;
	private int selected;
	private String[] lista;
	
	/* SECTION Constructora */
	
	public ChooseAlert(Context context, String title, String textYes, String textNo, String[] list)
	{
		selected = -1;
		lista = list;
		
		builder = new AlertDialog.Builder(context);
		
		builder.setTitle(title);
		
		builder.setSingleChoiceItems(lista, selected, new DialogInterface.OnClickListener() {
            
			@Override
			public void onClick(DialogInterface dialog, int which)
            {
	            selected = which;
            }
		});	

		builder.setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				if(selected != -1)
				{
					onSelectedPossitiveButtonClick(lista[selected]);
				}
				else
				{
					onNoSelectedPossitiveButtonClick();
				}
			}
		});

		builder.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onSelectedPossitiveButtonClick(String selected);
	public abstract void onNoSelectedPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	/* SECTION Métodos Públicos */
	
	public void show()
	{
		builder.show();
	}
}
