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
	private OnClickListener eyeListener, mouthListener, weaponListener, trinketListener, helmetListener;
	private ImageView[] imagenEye, imagenMouth, imagenWeapon, imagenTrinket, imagenHelmet;

	/* SECTION Constructora */

	public StickerDialog(Context context)
	{
		super(context, R.layout.dialog_sticker_layout);

		eyeListener = new OnEyeClickListener();
		mouthListener = new OnMouthClickListener();
		weaponListener = new OnWeaponClickListener();
		trinketListener = new OnTrinketClickListener();
		helmetListener = new OnHelmetClickListener();
		
		imagenEye = new ImageView[GamePreferences.NUM_TYPE_STICKERS_EYES];
		imagenMouth = new ImageView[GamePreferences.NUM_TYPE_STICKERS_MOUTH];
		imagenWeapon = new ImageView[GamePreferences.NUM_TYPE_STICKERS_WEAPON];
		imagenTrinket = new ImageView[GamePreferences.NUM_TYPE_STICKERS_TRINKET];
		imagenHelmet = new ImageView[GamePreferences.NUM_TYPE_STICKERS_HELMET];

		configurarPegatinas(GamePreferences.RESOURCE_BUTTON_STICKER_EYES, GamePreferences.NUM_TYPE_STICKERS_EYES, imagenEye, eyeListener);
		configurarPegatinas(GamePreferences.RESOURCE_BUTTON_STICKER_MOUTH, GamePreferences.NUM_TYPE_STICKERS_MOUTH, imagenMouth, mouthListener);
		configurarPegatinas(GamePreferences.RESOURCE_BUTTON_STICKER_WEAPON, GamePreferences.NUM_TYPE_STICKERS_WEAPON, imagenWeapon, weaponListener);
		configurarPegatinas(GamePreferences.RESOURCE_BUTTON_STICKER_TRINKET, GamePreferences.NUM_TYPE_STICKERS_TRINKET, imagenTrinket, trinketListener);
		configurarPegatinas(GamePreferences.RESOURCE_BUTTON_STICKER_HELMET, GamePreferences.NUM_TYPE_STICKERS_HELMET, imagenHelmet, helmetListener);
	}
	
	private void configurarPegatinas(String idPegatinas, int numPegatinas, ImageView[] listaPegatinas, OnClickListener listener)
	{
		for (int i = 0; i < numPegatinas; i++)
		{
			int id = mContext.getResources().getIdentifier(idPegatinas + (i + 1), "id", mContext.getPackageName());
			
			listaPegatinas[i] = (ImageView) findViewById(id);
			listaPegatinas[i].setOnClickListener(listener);
			listaPegatinas[i].setTag(i);
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
	
	private class OnTrinketClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerSelected((Integer) v.getTag(), TTipoSticker.Trinket);
			dismiss();
		}
	}
	
	private class OnHelmetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerSelected((Integer) v.getTag(), TTipoSticker.Helmet);
			dismiss();
		}
	}
}