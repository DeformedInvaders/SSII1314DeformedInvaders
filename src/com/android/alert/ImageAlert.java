package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public abstract class ImageAlert extends WindowAlert
{
	/* SECTION Constructora */

	public ImageAlert(Context context, String title, String textYes, String textNo, int idImage)
	{
		super(context, title);

		RelativeLayout layout = new RelativeLayout(context);
		
			ImageView image = new ImageView(context);
			image.setImageResource(idImage);
		
			layout.addView(image, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		setView(layout);

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
