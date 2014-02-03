package com.project.main;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.EditText;
import android.widget.Toast;

import com.android.multitouch.MultitouchFragment;
import com.android.storage.InternalStorageManager;
import com.create.deform.AnimationFragment;
import com.create.design.DesignFragment;
import com.create.paint.PaintFragment;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Personaje;
import com.project.data.Textura;
import com.view.select.SelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, SelectionFragment.SelectionFragmentListener
{	
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	private int personajeSeleccionado;
	
	private InternalStorageManager manager;
	private TEstado estado;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		
		personajeSeleccionado = -1;
		listaPersonajes = new ArrayList<Personaje>();
		
		manager = new InternalStorageManager();
		
        if (findViewById(R.id.frameLayoutMain1) != null)
        {
			personajeSeleccionado = manager.cargarSeleccionado(this);
			manager.cargarPersonajes(this, listaPersonajes);

    		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
    		estado = TEstado.Loading;
        }
	}
	
	@Override
	public void onBackPressed()
	{
		if(estado != TEstado.Loading)
		{
			super.onBackPressed();
		}
	}
	
	private void changeFragment(Fragment fragment)
	{
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		
		if(estado == TEstado.Loading)
		{
			clearBackStack();
		}
		
		transaction.replace(R.id.frameLayoutMain1, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	private void clearBackStack()
	{
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	/* LOADING ACTIVITY */
	
	public void onLoadingCreateButtonClicked()
	{
		personajeActual = new Personaje();
		
		changeFragment(DesignFragment.newInstance());
		estado = TEstado.Design;
	}
	
    public void onLoadingSelectButtonClicked()
    {
    	changeFragment(SelectionFragment.newInstance(listaPersonajes));
    	estado = TEstado.Selection;
    }
    
    public void onLoadingPlayButtonClicked()
    {
    	//Toast.makeText(getApplication(), "Play Game", Toast.LENGTH_SHORT).show();
    	changeFragment(MultitouchFragment.newInstance());
		estado = TEstado.Game;
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
    		changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()));
    		estado = TEstado.Paint;
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
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura()));
    		estado = TEstado.Animation;
    	}
    }
    
	/* ANIM ACTIVITY */
    
    public void onAnimationReadyButtonClicked(Movimientos movimientos)
    {
    	//TODO Controlar los movimientos recibidos
    	
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
					
					manager.guardarPersonajes(MainActivity.this, listaPersonajes);
						
					changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
					estado = TEstado.Loading;
				}
			});
	
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
					estado = TEstado.Loading;
				}
			});
	
			alert.show();
		//}
	}
    
	/* SELECTION ACTIVITY */
    
    public void onSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		manager.guardarSeleccionado(this, personajeSeleccionado);
		
		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
		estado = TEstado.Loading;
    }
    
    public void onSelectionDeleteButtonClicked(int indice)
    {
		listaPersonajes.remove(indice);
		
		if(personajeSeleccionado == indice)
		{
			personajeSeleccionado = -1;
		}
		
		manager.guardarSeleccionado(this, personajeSeleccionado);
		manager.guardarPersonajes(this, listaPersonajes);
		
		if(listaPersonajes.size() > 0 && personajeSeleccionado == -1)
		{
			changeFragment(SelectionFragment.newInstance(listaPersonajes));
			estado = TEstado.Selection;
		}
		else 
		{
			changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
			estado = TEstado.Loading;
		}
    }
}
