package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.create.paint.PaintFragment;
import com.project.main.R;

public class StickerPicker extends WindowPicker implements OnClickListener
{
	private PaintFragment fragmento;
	
	private ImageButton botonEye1, botonEye2, botonEye3, botonEye4, botonEye5;
	private ImageButton botonMouth1, botonMouth2, botonMouth3, botonMouth4, botonMouth5;

	public StickerPicker(Context context, PaintFragment view)
	{		
		super(context, R.layout.dialog_sticker_layout);
		
		fragmento = view;  
		
		botonEye1 = (ImageButton) findViewById(R.id.imageButtonSticker1);
		botonEye2 = (ImageButton) findViewById(R.id.imageButtonSticker2);
		botonEye3 = (ImageButton) findViewById(R.id.imageButtonSticker3);
		botonEye4 = (ImageButton) findViewById(R.id.imageButtonSticker4);
		botonEye5 = (ImageButton) findViewById(R.id.imageButtonSticker5);
		
		botonMouth1 = (ImageButton) findViewById(R.id.imageButtonSticker6);
		botonMouth2 = (ImageButton) findViewById(R.id.imageButtonSticker7);
		botonMouth3 = (ImageButton) findViewById(R.id.imageButtonSticker8);
		botonMouth4 = (ImageButton) findViewById(R.id.imageButtonSticker9);
		botonMouth5 = (ImageButton) findViewById(R.id.imageButtonSticker10);
		
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
			case R.id.imageButtonSticker1:
				pegatina = 0;
			break;
			case R.id.imageButtonSticker2:
				pegatina = 1;
			break;
			case R.id.imageButtonSticker3:
				pegatina = 2;
			break;
			case R.id.imageButtonSticker4:
				pegatina = 3;
			break;
			case R.id.imageButtonSticker5:
				pegatina = 4;
			break;
			case R.id.imageButtonSticker6:
				pegatina = 5;
			break;
			case R.id.imageButtonSticker7:
				pegatina = 6;
			break;
			case R.id.imageButtonSticker8:
				pegatina = 7;
			break;
			case R.id.imageButtonSticker9:
				pegatina = 8;
			break;
			case R.id.imageButtonSticker10:
				pegatina = 9;
			break;					
		}
		
		fragmento.seleccionarPegatina(pegatina);
		dismiss();		
	}

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		fragmento.seleccionarPegatina(-1);
		dismiss();
	}
}