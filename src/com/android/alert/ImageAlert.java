package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;

public abstract class ImageAlert extends WindowAlert
{	
	/* SECTION Constructora */
	
	public ImageAlert(Context context, String title, String message, String textYes, String textNo, int idImage)
	{
		super(context, title);

		setMessage(message);
		
		ImageView image = new ImageView(context);
		image.setImageResource(idImage);
		
		setView(image);

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
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();

}
