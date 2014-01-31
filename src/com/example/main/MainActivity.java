package com.example.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.storage.InternalStorageManager;
import com.create.deform.AnimationFragment;
import com.create.design.DesignFragment;
import com.create.paint.PaintFragment;
import com.example.data.Esqueleto;
import com.example.data.Movimientos;
import com.example.data.Personaje;
import com.example.data.Textura;
import com.example.loading.LoadingFragment;
import com.view.select.SelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, SelectionFragment.SelectionFragmentListener
{	
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	private int personajeSeleccionado;
	
	private InternalStorageManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		
		personajeSeleccionado = -1;
		listaPersonajes = new ArrayList<Personaje>();
		
		manager = new InternalStorageManager();
		
        if (findViewById(R.id.fragment_container) != null)
        {
    		try
    		{
    			FileInputStream file = openFileInput(manager.getFileName());
    			manager.cargarPersonajes(file, listaPersonajes);
    			personajeSeleccionado = manager.getEsqueletoSeleccionado();
    		}
    		catch (FileNotFoundException e)
    		{
    			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
    			e.printStackTrace();
    		}

    		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado), false);
        }
	}
	
	private void changeFragment(Fragment fragment, boolean clearStack)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// TODO
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	/* LOADING ACTIVITY */
	
	public void onLoadingCreateButtonClicked()
	{
		personajeActual = new Personaje();
		
		changeFragment(DesignFragment.newInstance(), false);
	}
	
    public void onLoadingSelectButtonClicked()
    {
    	changeFragment(SelectionFragment.newInstance(listaPersonajes), true);
    }
    
    public void onLoadingPlayButtonClicked()
    {
    	Toast.makeText(getApplication(), "Play Game", Toast.LENGTH_SHORT).show();
    }
	
	/* DESIGN ACTIVITY */
    
    public void onDesignReadyButtonClicked(Esqueleto esqueleto)
    {
    	if(esqueleto == null)
    	{
    		Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setEsqueleto(esqueleto);    		
    		changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()), true);
    	}
    }
    
    public void onDesignTestButtonClicked(boolean test)
    {
    	if(!test)
    	{
    		Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
    	}
    }
	
	/* PAINT ACTIVITY */
    
    public void onPaintReadyButtonClicked(Textura textura)
    {
    	if(textura == null)
    	{
    		Toast.makeText(getApplication(), "The Texture is Corrupted", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setTextura(textura);
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura()), true);
    	}
    }
    
	/* ANIM ACTIVITY */
    
    public void onAnimationReadyButtonClicked(Movimientos movimientos)
    {
    	//TODO
		//if(movimientos != null)
		//{
			personajeActual.setMovimientos(movimientos);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
			alert.setTitle("Personaje Finalizado");
			alert.setMessage("Introduzca un nombre si desea guardar el personaje");
	
			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);
	
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					String value = input.getText().toString();
					personajeActual.setNombre(value);
					
					listaPersonajes.add(personajeActual);
					
					try
					{
						FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
						manager.guardarPersonajes(file, listaPersonajes);
						
						changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado), false);
					}
					catch (FileNotFoundException e)
					{
						Log.d("TEST", "FILE NOT FOUND EXCEPTION");
						e.printStackTrace();
					}
				}
			});
	
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado), false);
				}
			});
	
			alert.show();
		//}
	}
    
	/* SELECTION ACTIVITY */
    
    public void onSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado), false);
    }
    
    public void onSelectionDeleteButtonClicked(int indice)
    {
		listaPersonajes.remove(indice);
		
		if(personajeSeleccionado == indice)
		{
			personajeSeleccionado = -1;
		}
		
		try
		{
			FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
			manager.guardarPersonajes(file, listaPersonajes);
			
			if(listaPersonajes.size() > 0)
			{
				changeFragment(SelectionFragment.newInstance(listaPersonajes), true);
			}
			else 
			{
				changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado), false);
			}
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e.printStackTrace();
		}
    }
}
