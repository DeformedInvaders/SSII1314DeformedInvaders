package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;

import com.project.main.R;

public abstract class ImageAlert extends WindowAlert
{
	/* Constructora */

	public ImageAlert(Context context, int title, int textYes, int textNo, int idImage)
	{
		super(context, title, false);

		setView(R.layout.alert_image_layout);
		
		ImageView image = (ImageView) findViewById(R.id.imageViewImageAlert1);
		image.setImageResource(idImage);
	
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
	
	public ImageAlert(Context context, String title, int textYes, int textNo, int idImage)
	{
		super(context, title, false);

		ImageView image = (ImageView) findViewById(R.id.imageViewImageAlert1);
		image.setImageResource(idImage);
		
		setView(R.layout.alert_image_layout);
	
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
