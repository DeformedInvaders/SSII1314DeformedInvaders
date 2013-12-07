package com.example.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.paint.PaintGLSurfaceView;

public class PaintActivity extends Activity  implements ColorPickerDialog.OnColorChangedListener
{
    private static final String COLOR_PREFERENCE_KEY = "color";
    private String FILENAME;
 // action id
 	private static final int ID_FINO = 1;
 	private static final int ID_NORMAL = 2;
 	private static final int ID_ANCHO = 3;
    
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		FILENAME = bundle.getString("Esqueleto");
		Esqueleto e = null;

		FileInputStream file;
		ObjectInputStream data;
		try
		{
			file = openFileInput(FILENAME);
			data = new ObjectInputStream(file);
			e = (Esqueleto) data.readObject();
			data.close();
			file.close();
		}
		catch (FileNotFoundException e1)
		{
			Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			Toast.makeText(getApplication(), "IO Exception", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "IO EXCEPTION");
			e1.printStackTrace();
		}
		catch (ClassNotFoundException e1)
		{
			Toast.makeText(getApplication(), "Objecto not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "CLASS NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}
		
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
		ActionItem finoItem = new ActionItem(ID_FINO, "1", getResources().getDrawable(R.drawable.linea1));
		ActionItem normalItem = new ActionItem(ID_NORMAL, "6", getResources().getDrawable(R.drawable.linea2));
		ActionItem anchoItem = new ActionItem(ID_ANCHO, "11", getResources().getDrawable(R.drawable.linea3));
		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		// orientation
/*		final QuickAction quickAction = new QuickAction(this,
				QuickAction.VERTICAL);

		// add action items into QuickAction
		quickAction.addActionItem(finoItem);
		quickAction.addActionItem(normalItem);
		quickAction.addActionItem(anchoItem);

		// Set listener for action item clicked
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);
						// TODO Aqui mirar que item es y hacer el
						// canvas.seleccionarSize();
						// here we can filter which action item was clicked with
						// pos or actionId parameter

						Toast.makeText(
								getApplicationContext(),
								"grosor " + actionItem.getTitle()
										+ " seleccionado", Toast.LENGTH_SHORT)
								.show();

					}
				});

		// set listnener for on dismiss event, this listener will be called only
		// if QuickAction dialog was dismissed
		// by clicking the area outside the dialog.
		quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(), "Dismissed",
						Toast.LENGTH_SHORT).show();
			}
		});*/

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
				//quickAction.show(arg0);
				canvas.seleccionarSize();
			}
		});
		
		botonEye.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//TODO Lanzar Textures Picker
				//canvas.testBitMap();
				Esqueleto e = canvas.getEsqueleto();
				if(e != null)
				{
					FileOutputStream file;
					ObjectOutputStream data;
					try
					{
						file = openFileOutput(FILENAME, Context.MODE_PRIVATE);
						data = new ObjectOutputStream(file);
						data.writeObject(e);
						data.close();
						file.close();
					}
					catch (FileNotFoundException e1)
					{
						Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "FILE NOT FOUND EXCEPTION");
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						Toast.makeText(getApplication(), "IO Exception", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "IO EXCEPTION");
						e1.printStackTrace();
					}
					
					Intent intent = new Intent(PaintActivity.this, MoveActivity.class);
					intent.putExtra("Esqueleto", FILENAME);
					startActivity(intent);
				}
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
				canvas.testBitMap();
				Esqueleto e = canvas.getEsqueleto();
				if(e != null)
				{
					FileOutputStream file;
					ObjectOutputStream data;
					try
					{
						file = openFileOutput(FILENAME, Context.MODE_PRIVATE);
						data = new ObjectOutputStream(file);
						Log.d("TEST", "ANTES WRITE");
						data.writeObject(e);
						Log.d("TEST", "DESPUES WRITE");
						data.close();
						file.close();
					}
					catch (FileNotFoundException e1)
					{
						Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "FILE NOT FOUND EXCEPTION");
						e1.printStackTrace();
					}
					catch (IOException e1)
					{
						Toast.makeText(getApplication(), "IO Exception", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "IO EXCEPTION");
						e1.printStackTrace();
					}
					
					Intent intent = new Intent(PaintActivity.this, DeformActivity.class);
					intent.putExtra("Esqueleto", FILENAME);
					startActivity(intent);
				}
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
			//setContentView(R.layout.paint_layout_portrait);
		}
		else
		{
		    //setContentView(R.layout.paint_layout_landscape);
		}
	}
	
    @Override
    public void colorChanged(int color)
    {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(COLOR_PREFERENCE_KEY, color).commit();
    }
}
