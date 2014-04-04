package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.game.data.TTipoSticker;
import com.project.main.GamePreferences;
import com.project.main.R;

public abstract class StickerDialog extends WindowDialog
{
	private OnEyeClickListener eyeListener;
	private OnMouthClickListener mouthListener;
	private OnWeaponClickListener weaponListener;

	private ImageView[] imagenEye, imagenMouth, imagenWeapon;

	/* SECTION Constructora */

	public StickerDialog(Context context)
	{
		super(context, R.layout.dialog_sticker_layout);

		eyeListener = new OnEyeClickListener();
		mouthListener = new OnMouthClickListener();
		weaponListener = new OnWeaponClickListener();
		
		imagenEye = new ImageView[GamePreferences.NUM_TYPE_STICKERS];
		imagenMouth = new ImageView[GamePreferences.NUM_TYPE_STICKERS];
		imagenWeapon = new ImageView[GamePreferences.NUM_TYPE_STICKERS];

		imagenEye[0] = (ImageView) findViewById(R.id.imageButtonSticker1);
		imagenEye[1] = (ImageView) findViewById(R.id.imageButtonSticker2);
		imagenEye[2] = (ImageView) findViewById(R.id.imageButtonSticker3);
		imagenEye[3] = (ImageView) findViewById(R.id.imageButtonSticker4);
		imagenEye[4] = (ImageView) findViewById(R.id.imageButtonSticker5);

		imagenMouth[0] = (ImageView) findViewById(R.id.imageButtonSticker6);
		imagenMouth[1] = (ImageView) findViewById(R.id.imageButtonSticker7);
		imagenMouth[2] = (ImageView) findViewById(R.id.imageButtonSticker8);
		imagenMouth[3] = (ImageView) findViewById(R.id.imageButtonSticker9);
		imagenMouth[4] = (ImageView) findViewById(R.id.imageButtonSticker10);

		imagenWeapon[0] = (ImageView) findViewById(R.id.imageButtonSticker11);
		imagenWeapon[1] = (ImageView) findViewById(R.id.imageButtonSticker12);
		imagenWeapon[2] = (ImageView) findViewById(R.id.imageButtonSticker13);
		imagenWeapon[3] = (ImageView) findViewById(R.id.imageButtonSticker14);
		imagenWeapon[4] = (ImageView) findViewById(R.id.imageButtonSticker15);
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			imagenEye[i].setOnClickListener(eyeListener);
			imagenMouth[i].setOnClickListener(mouthListener);
			imagenWeapon[i].setOnClickListener(weaponListener);
			
			imagenEye[i].setTag(i);
			imagenMouth[i].setTag(i);
			imagenWeapon[i].setTag(i);
		}
	}

	/* SECTION Métodos Abstractos */

	public abstract void onStickerSelected(int tag, TTipoSticker tipo);

	/* SECTION Métodos Abstractos WindowDialog */

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}

	/* SECTION Métodos Listener onClick */

	private class OnEyeClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerSelected((Integer) v.getTag(), TTipoSticker.Eyes);
			dismiss();
		}
	}

	private class OnMouthClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerSelected((Integer) v.getTag(), TTipoSticker.Mouth);
			dismiss();
		}
	}

	private class OnWeaponClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerSelected((Integer) v.getTag(), TTipoSticker.Weapon);
			dismiss();
		}
	}
}