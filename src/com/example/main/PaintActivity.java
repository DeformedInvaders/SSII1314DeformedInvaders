package com.example.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.paint.PaintGLSurfaceView;

public class PaintActivity extends Activity  implements ColorPickerDialog.OnColorChangedListener
{
	/** Called when the activity is first created. */
    //private static final String BRIGHTNESS_PREFERENCE_KEY = "brightness";
    private static final String COLOR_PREFERENCE_KEY = "color";
    
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		Esqueleto e = (Esqueleto) bundle.get("Esqueleto");
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		int result = this.getResources().getConfiguration().orientation;
		if(result == 1)
		{
			setContentView(R.layout.paint_layout_portrait);
		}
		else
		{
		    setContentView(R.layout.paint_layout_landscape);
		}
		
		botonPincel = (ImageButton) findViewById(R.id.imageButton1);
		botonCubo = (ImageButton) findViewById(R.id.imageButton2);
		botonMano = (ImageButton) findViewById(R.id.imageButton3);
		botonPrev = (ImageButton) findViewById(R.id.imageButton4);
		botonNext = (ImageButton) findViewById(R.id.imageButton5);
		botonDelete = (ImageButton) findViewById(R.id.imageButton6);
		botonReady = (ImageButton) findViewById(R.id.imageButton7);
		botonColor = (ImageButton) findViewById(R.id.imageButton8);
		botonSize = (ImageButton) findViewById(R.id.imageButton9);
		botonEye = (ImageButton) findViewById(R.id.imageButton10);
		
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		canvas.setEsqueleto(e);
		
		botonPincel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarPincel();
			}
		});
		
		botonCubo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarCubo();
			}
		});
		
		botonColor.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				int color = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this).getInt(COLOR_PREFERENCE_KEY, Color.RED);
				new ColorPickerDialog(PaintActivity.this, PaintActivity.this,color, canvas).show();
				//canvas.seleccionarColor(color);
			}
		});
		
		botonSize.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//TODO Lanzar Size Picker
				canvas.seleccionarSize();
			}
		});
		
		botonEye.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//TODO Lanzar Textures Picker
				Intent intent = new Intent(PaintActivity.this, AnimationActivity.class);
				startActivity(intent);
			}
		});
		
		botonMano.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarMano();
			}
		});
		
		botonNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.siguienteAccion();
			}
		});
		
		botonPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.anteriorAccion();
			}
		});
		
		botonDelete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.reiniciar();
				Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonReady.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Intent a Animaciones
			    canvas.testBitMap();
			}
		});
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		int result = this.getResources().getConfiguration().orientation;
		if(result == 1)
		{
			setContentView(R.layout.paint_layout_portrait);
		}
		else
		{
		    setContentView(R.layout.paint_layout_landscape);
		}
	}
	
    @Override
    public void colorChanged(int color)
    {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(COLOR_PREFERENCE_KEY, color).commit();
    }
}
