package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public abstract class ChooseAlert
{
	private AlertDialog.Builder builder;
	private int selected;
	private String[] lista;
	
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
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		builder.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	public String getSelected()
	{
		if(selected != -1)
		{
			return lista[selected];
		}
		
		return null;
	}
	
	public void show()
	{
		builder.show();
	}
}
