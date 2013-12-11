package com.example.paint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.animation.MoveActivity;
import com.example.deform.DeformActivity;
import com.example.dialog.ColorPickerDialog;
import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;

public class PaintActivity extends Activity implements ColorPickerDialog.OnColorChangedListener
{
	private InternalStorageManager manager;
    
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Recuperar Esqueleto de la Memoria Interna
		manager = new InternalStorageManager();

		Esqueleto esqueleto = null;
		try
		{
			FileInputStream file = openFileInput(manager.getFileName());
			esqueleto = manager.cargarEsqueleto(file);
			deleteFile(manager.getFileName());
		}
		catch (FileNotFoundException e1)
		{
			Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}

		// Seleccionar Animación entre Actividades
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		// Seleccionar Layout
		setContentView(R.layout.paint_layout_landscape);

		// Instanciar Elementos de la GUI
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
		canvas.setEsqueleto(esqueleto);
		
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
				//TODO Lanzar Color Picker
				//int color = PreferenceManager.getDefaultSharedPreferences(PaintActivity.this).getInt(COLOR_PREFERENCE_KEY, Color.RED);
				//new ColorPickerDialog(PaintActivity.this, PaintActivity.this,color, canvas).show();
				canvas.seleccionarColor();
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
				canvas.capturaPantalla();
				Esqueleto esqueleto = canvas.getEsqueleto();
				if(esqueleto != null)
				{
					try
					{
						FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
						manager.guardarEsqueleto(file, esqueleto);
					}
					catch (FileNotFoundException e)
					{
						Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "FILE NOT FOUND EXCEPTION");
						e.printStackTrace();
					}
					
					Intent intent = new Intent(PaintActivity.this, MoveActivity.class);
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
				canvas.capturaPantalla();
				Esqueleto esqueleto = canvas.getEsqueleto();
				if(esqueleto != null)
				{
					try
					{
						FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
						manager.guardarEsqueleto(file, esqueleto);
					}
					catch (FileNotFoundException e)
					{
						Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
						Log.d("TEST", "FILE NOT FOUND EXCEPTION");
						e.printStackTrace();
					}
					
					Intent intent = new Intent(PaintActivity.this, DeformActivity.class);
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
    public void colorChanged(int color)
    {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("color", color).commit();
    }
    
    //TODO Lanzar Size Picker
 	//private static final int ID_FINO = 1;
 	//private static final int ID_NORMAL = 2;
 	//private static final int ID_ANCHO = 3;
    
    public void cargarSizePicker()
    {
    	/*ActionItem finoItem = new ActionItem(ID_FINO, "1", getResources().getDrawable(R.drawable.linea1));
		ActionItem normalItem = new ActionItem(ID_NORMAL, "6", getResources().getDrawable(R.drawable.linea2));
		ActionItem anchoItem = new ActionItem(ID_ANCHO, "11", getResources().getDrawable(R.drawable.linea3));
		// create QuickAction. Use QuickAction.VERTICAL or
		// QuickAction.HORIZONTAL param to define layout
		// orientation
		final QuickAction quickAction = new QuickAction(this,
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
						canvas.seleccionarSize(pos);

						Toast.makeText(
								getApplicationContext(),
								"grosor " + actionItem.getTitle()
										+ " seleccionado", Toast.LENGTH_SHORT)
								.show();

					}
				});
    	 */
		// set listnener for on dismiss event, this listener will be called only
		// if QuickAction dialog was dismissed
		// by clicking the area outside the dialog.
		/*quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(), "Dismissed",
						Toast.LENGTH_SHORT).show();
			}
		});*/
    }
}
