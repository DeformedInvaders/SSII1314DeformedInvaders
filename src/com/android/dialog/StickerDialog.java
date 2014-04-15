package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.creation.data.TTipoSticker;
import com.project.main.R;
import com.project.model.GamePreferences;
import com.project.model.GameStatistics;

public abstract class StickerDialog extends WindowDialog
{
	/* Constructora */
	private int imageWidth, imageHeight;
	private OnClickListener eyeListener, mouthListener, weaponListener, trinketListener, helmetListener, deleteListener;
	
	public StickerDialog(Context context, GameStatistics[] estadisticas)
	{
		super(context, R.layout.dialog_sticker_layout);
		
		imageWidth = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutWidth_Dimen);
		imageHeight = (int) mContext.getResources().getDimension(R.dimen.StickerButton_LayoutHeight_Dimen);

		eyeListener = new OnEyeClickListener();
		mouthListener = new OnMouthClickListener();
		weaponListener = new OnWeaponClickListener();
		trinketListener = new OnTrinketClickListener();
		helmetListener = new OnHelmetClickListener();
		deleteListener = new OnDeleteClickListener();
		
		LinearLayout eyeLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker1);
		LinearLayout mouthLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker2);
		LinearLayout weaponLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker3);
		LinearLayout trinketLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker4);
		LinearLayout helmetLayout = (LinearLayout) findViewById(R.id.linearLayoutSticker5);
		
		configurarPegatinas(eyeLayout, TTipoSticker.Eyes, GamePreferences.RESOURCE_ID_STICKER_EYES, GamePreferences.NUM_TYPE_STICKERS_EYES, eyeListener);
		configurarPegatinas(mouthLayout, TTipoSticker.Mouth, GamePreferences.RESOURCE_ID_STICKER_MOUTH, GamePreferences.NUM_TYPE_STICKERS_MOUTH, mouthListener);
		configurarPegatinas(weaponLayout, estadisticas, TTipoSticker.Weapon, GamePreferences.RESOURCE_ID_STICKER_WEAPON, GamePreferences.NUM_TYPE_STICKERS_WEAPON, weaponListener);
		configurarPegatinas(trinketLayout, estadisticas, TTipoSticker.Trinket, GamePreferences.RESOURCE_ID_STICKER_TRINKET, GamePreferences.NUM_TYPE_STICKERS_TRINKET, trinketListener);
		configurarPegatinas(helmetLayout, estadisticas, TTipoSticker.Helmet, GamePreferences.RESOURCE_ID_STICKER_HELMET, GamePreferences.NUM_TYPE_STICKERS_HELMET, helmetListener);
	}
	
	private void cargarPegatina(LinearLayout layout, String nombrePegatina, int tagImagen, OnClickListener listener)
	{
		int idImagen = mContext.getResources().getIdentifier(nombrePegatina + tagImagen, "drawable", mContext.getPackageName());
		cargarPegatina(layout, nombrePegatina, idImagen, tagImagen, listener);
	}
	
	private void cargarPegatina(LinearLayout layout, String nombrePegatina, int idImagen, int tagImagen, OnClickListener listener)
	{
		ImageView image = new ImageView(mContext, null, R.style.Button_Fragment_Style);
		image.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
		image.setBackgroundResource(idImagen);
		image.setOnClickListener(listener);
		image.setTag(tagImagen);
		
		layout.addView(image);		
	}
	
	private void configurarPegatinas(LinearLayout layout, TTipoSticker tipo, String nombrePegatina, int numPegatinas, OnClickListener listener)
	{
		for (int i = 1; i <= numPegatinas; i++)
		{		
			cargarPegatina(layout, nombrePegatina, i, listener);
		}
		
		cargarPegatina(layout, nombrePegatina, R.drawable.sticker_delete, tipo.ordinal(), deleteListener);
	}
	
	private void configurarPegatinas(LinearLayout layout, GameStatistics[] estadisticas, TTipoSticker tipo, String nombrePegatina, int numPegatinas, OnClickListener listener)
	{
		for (int i = 1; i <= numPegatinas; i++)
		{
			// FIXME Quitar el +-1 al añadir pegatinas al nivel Luna
			int pos = (( i - 1) / (GamePreferences.NUM_LEVELS - 1)) + 1;
			if(estadisticas[pos].isPerfected())
			{
				cargarPegatina(layout, nombrePegatina, i, listener);
			}
		}
		
		cargarPegatina(layout, nombrePegatina, R.drawable.sticker_delete, tipo.ordinal(), deleteListener);
	}

	/* Métodos Abstractos */

	public abstract void onStickerSelected(int tag, TTipoSticker tipo);
	public abstract void onStickerDeleted(TTipoSticker tipo);

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
	
	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStickerDeleted(TTipoSticker.values()[(Integer) v.getTag()]);
			dismiss();
		}		
	}
}