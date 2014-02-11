package com.android.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.create.paint.PaintFragment;
import com.project.main.R;

@SuppressLint("ViewConstructor")
public class StickerPicker extends WindowPicker implements OnClickListener
{
	private PaintFragment fragmento;
	
	private ImageButton botonEye1, botonEye2, botonEye3, botonEye4, botonEye5;
	private ImageButton botonMouth1, botonMouth2, botonMouth3, botonMouth4, botonMouth5;

	public StickerPicker(Context context, PaintFragment view)
	{		
		super(context, R.layout.dialog_sticker_layout);
		
		fragmento = view;  
		
		botonEye1 = (ImageButton) findViewById(R.id.imageButtonStickerEye1);
		botonEye2 = (ImageButton) findViewById(R.id.imageButtonStickerEye2);
		botonEye3 = (ImageButton) findViewById(R.id.imageButtonStickerEye3);
		botonEye4 = (ImageButton) findViewById(R.id.imageButtonStickerEye4);
		botonEye5 = (ImageButton) findViewById(R.id.imageButtonStickerEye5);
		
		botonMouth1 = (ImageButton) findViewById(R.id.imageButtonStickerMouth1);
		botonMouth2 = (ImageButton) findViewById(R.id.imageButtonStickerMouth2);
		botonMouth3 = (ImageButton) findViewById(R.id.imageButtonStickerMouth3);
		botonMouth4 = (ImageButton) findViewById(R.id.imageButtonStickerMouth4);
		botonMouth5 = (ImageButton) findViewById(R.id.imageButtonStickerMouth5);
		
		botonEye1.setOnClickListener(this);
		botonEye2.setOnClickListener(this);
		botonEye3.setOnClickListener(this);
		botonEye4.setOnClickListener(this);
		botonEye5.setOnClickListener(this);
		
		botonMouth1.setOnClickListener(this);
		botonMouth2.setOnClickListener(this);
		botonMouth3.setOnClickListener(this);
		botonMouth4.setOnClickListener(this);
		botonMouth5.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v)
	{
		int pegatina = -1;
		
		switch(v.getId())
		{
			case R.id.imageButtonStickerEye1:
				pegatina = 0;
			break;
			case R.id.imageButtonStickerEye2:
				pegatina = 1;
			break;
			case R.id.imageButtonStickerEye3:
				pegatina = 2;
			break;
			case R.id.imageButtonStickerEye4:
				pegatina = 3;
			break;
			case R.id.imageButtonStickerEye5:
				pegatina = 4;
			break;
			case R.id.imageButtonStickerMouth1:
				pegatina = 5;
			break;
			case R.id.imageButtonStickerMouth2:
				pegatina = 6;
			break;
			case R.id.imageButtonStickerMouth3:
				pegatina = 7;
			break;
			case R.id.imageButtonStickerMouth4:
				pegatina = 8;
			break;
			case R.id.imageButtonStickerMouth5:
				pegatina = 9;
			break;					
		}
		
		fragmento.seleccionarPegatina(pegatina);
		dismiss();		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
		{
			fragmento.seleccionarPegatina(-1);
			dismiss();
			return true;
		}
		
		return false;
	}
}