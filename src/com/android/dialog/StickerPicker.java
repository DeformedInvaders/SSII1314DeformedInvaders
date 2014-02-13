package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.create.paint.PaintFragment;
import com.project.main.R;

public class StickerPicker extends WindowPicker
{
	private PaintFragment fragmento;
	
	private OnEyeClickListener eyeListener;
	private OnMouthClickListener mouthListener;
	private OnWeaponClickListener weaponListener;
	
	private ImageView imagenEye1, imagenEye2, imagenEye3, imagenEye4, imagenEye5;
	private ImageView imagenMouth1, imagenMouth2, imagenMouth3, imagenMouth4, imagenMouth5;
	private ImageView imagenWeapon1, imagenWeapon2, imagenWeapon3, imagenWeapon4;

	public StickerPicker(Context context, PaintFragment view)
	{		
		super(context, R.layout.dialog_sticker_layout);
		
		fragmento = view;  
		
		eyeListener = new OnEyeClickListener();
		mouthListener = new OnMouthClickListener();
		weaponListener = new OnWeaponClickListener();
		
		imagenEye1 = (ImageView) findViewById(R.id.imageButtonSticker1);
		imagenEye2 = (ImageView) findViewById(R.id.imageButtonSticker2);
		imagenEye3 = (ImageView) findViewById(R.id.imageButtonSticker3);
		imagenEye4 = (ImageView) findViewById(R.id.imageButtonSticker4);
		imagenEye5 = (ImageView) findViewById(R.id.imageButtonSticker5);
		
		imagenEye1.setTag(R.drawable.texture_eyes1);
		imagenEye2.setTag(R.drawable.texture_eyes2);
		imagenEye3.setTag(R.drawable.texture_eyes3);
		imagenEye4.setTag(R.drawable.texture_eyes4);
		imagenEye5.setTag(R.drawable.texture_eyes5);
		
		imagenEye1.setOnClickListener(eyeListener);
		imagenEye2.setOnClickListener(eyeListener);
		imagenEye3.setOnClickListener(eyeListener);
		imagenEye4.setOnClickListener(eyeListener);
		imagenEye5.setOnClickListener(eyeListener);
		
		imagenMouth1 = (ImageView) findViewById(R.id.imageButtonSticker6);
		imagenMouth2 = (ImageView) findViewById(R.id.imageButtonSticker7);
		imagenMouth3 = (ImageView) findViewById(R.id.imageButtonSticker8);
		imagenMouth4 = (ImageView) findViewById(R.id.imageButtonSticker9);
		imagenMouth5 = (ImageView) findViewById(R.id.imageButtonSticker10);
		
		imagenMouth1.setTag(R.drawable.texture_mouth1);
		imagenMouth2.setTag(R.drawable.texture_mouth2);
		imagenMouth3.setTag(R.drawable.texture_mouth3);
		imagenMouth4.setTag(R.drawable.texture_mouth4);
		imagenMouth5.setTag(R.drawable.texture_mouth5);
		
		imagenMouth1.setOnClickListener(mouthListener);
		imagenMouth2.setOnClickListener(mouthListener);
		imagenMouth3.setOnClickListener(mouthListener);
		imagenMouth4.setOnClickListener(mouthListener);
		imagenMouth5.setOnClickListener(mouthListener);
		
		imagenWeapon1 = (ImageView) findViewById(R.id.imageButtonSticker11);
		imagenWeapon2 = (ImageView) findViewById(R.id.imageButtonSticker12);
		imagenWeapon3 = (ImageView) findViewById(R.id.imageButtonSticker13);
		imagenWeapon4 = (ImageView) findViewById(R.id.imageButtonSticker14);
		
		imagenWeapon1.setTag(R.drawable.texture_weapon1);
		imagenWeapon2.setTag(R.drawable.texture_weapon2);
		imagenWeapon3.setTag(R.drawable.texture_weapon3);
		imagenWeapon4.setTag(R.drawable.texture_weapon4);
		
		imagenWeapon1.setOnClickListener(weaponListener);
		imagenWeapon2.setOnClickListener(weaponListener);
		imagenWeapon3.setOnClickListener(weaponListener);
		imagenWeapon4.setOnClickListener(weaponListener);
	}
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		fragmento.seleccionarPegatina(-1, -1);
		dismiss();
	}
	
	private class OnEyeClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			fragmento.seleccionarPegatina((Integer) v.getTag(), 0);		
			dismiss();	
		}
	}
	
	private class OnMouthClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			fragmento.seleccionarPegatina((Integer) v.getTag(), 1);		
			dismiss();	
		}
	}
	
	private class OnWeaponClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			fragmento.seleccionarPegatina((Integer) v.getTag(), 2);		
			dismiss();	
		}
	}
}