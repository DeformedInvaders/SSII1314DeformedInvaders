package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public abstract class ImageAlert extends WindowAlert
{
	/* Constructora */

	public ImageAlert(Context context, int title, int textYes, int textNo, int idImage)
	{
		super(context, title);

		construirAlert(context, textYes, textNo, idImage);
	}
	
	public ImageAlert(Context context, String title, int textYes, int textNo, int idImage)
	{
		super(context, title);

		construirAlert(context, textYes, textNo, idImage);
	}
	
	private void construirAlert(Context context, int textYes, int textNo, int idImage)
	{
		RelativeLayout layout = new RelativeLayout(context);
		
			ImageView image = new ImageView(context);
			image.setImageResource(idImage);
		
			layout.addView(image, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
		setView(layout);
		
		setCancelable(false);
	
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
