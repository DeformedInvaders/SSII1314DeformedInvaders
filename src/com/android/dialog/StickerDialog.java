package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.game.data.TTipoSticker;
import com.project.main.GamePreferences;
import com.project.main.GameStatistics;
import com.project.main.R;

public abstract class StickerDialog extends WindowDialog
{
	/* Constructora */
	int imageWidth, imageHeight;

	public StickerDialog(Context context, GameStatistics[] estadisticas)
	{
		super(context, R.layout.dialog_sticker_layout);
		
		imageWidth = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutWidth_Dimen);
		imageHeight = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutHeight_Dimen);

		OnClickListener eyeListener = new OnEyeClickListener();
		OnClickListener mouthListener = new OnMouthClickListener();
		OnClickListener weaponListener = new OnWeaponClickListener();
		OnClickListener trinketListener = new OnTrinketClickListener();
		OnClickListener helmetListener = new OnHelmetClickListener();
		
		LinearLayout eyeLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker1);
		LinearLayout mouthLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker2);
		LinearLayout weaponLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker3);
		LinearLayout trinketLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker4);
		LinearLayout helmetLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker5);
		
		configurarPegatinas(eyeLayout, GamePreferences.RESOURCE_ID_STICKER_EYES, GamePreferences.NUM_TYPE_STICKERS_EYES, eyeListener);
		configurarPegatinas(mouthLayout, GamePreferences.RESOURCE_ID_STICKER_MOUTH, GamePreferences.NUM_TYPE_STICKERS_MOUTH, mouthListener);
		configurarPegatinas(weaponLayout, estadisticas, GamePreferences.RESOURCE_ID_STICKER_WEAPON, GamePreferences.NUM_TYPE_STICKERS_WEAPON, weaponListener);
		configurarPegatinas(trinketLayout, estadisticas,GamePreferences.RESOURCE_ID_STICKER_TRINKET, GamePreferences.NUM_TYPE_STICKERS_TRINKET, trinketListener);
		configurarPegatinas(helmetLayout, estadisticas, GamePreferences.RESOURCE_ID_STICKER_HELMET, GamePreferences.NUM_TYPE_STICKERS_HELMET, helmetListener);
	}
	
	private void configurarPegatinas(LinearLayout layout, String nombrePegatina, int numPegatinas, OnClickListener listener)
	{
		for (int i = 0; i < numPegatinas; i++)
		{
			int imageId = mContext.getResources().getIdentifier(nombrePegatina + (i + 1), "drawable", mContext.getPackageName());
						
			ImageView image = new ImageView(mContext, null, R.style.Button_Fragment_Style);
			image.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			image.setBackgroundResource(imageId);
			image.setOnClickListener(listener);
			image.setTag(i);
			
			layout.addView(image);
		}
	}
	
	private void configurarPegatinas(LinearLayout layout, GameStatistics[] estadisticas, String nombrePegatina, int numPegatinas, OnClickListener listener)
	{
		for (int i = 0; i < numPegatinas; i++)
		{
			// FIXME Quitar el +1 al añadir pegatinas al nivel Luna
			int pos = (i / (GamePreferences.NUM_LEVELS - 1)) + 1;
			int imageId = mContext.getResources().getIdentifier(nombrePegatina + (i + 1), "drawable", mContext.getPackageName());
						
			ImageView image = new ImageView(mContext, null, R.style.Button_Fragment_Style);
			image.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			image.setBackgroundResource(imageId);
			image.setTag(i);
			
			if(estadisticas[pos].isPerfected())
			{
				image.setOnClickListener(listener);
				layout.addView(image);
			}
		}
	}

	/* Métodos Abstractos */

	public abstract void onStickerSelected(int tag, TTipoSticker tipo);

	/* Métodos Abstractos WindowDialog */

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event) { }

	/* Métodos Listener onClick */

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