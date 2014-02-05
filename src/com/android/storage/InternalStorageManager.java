package com.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.project.data.Personaje;

public class InternalStorageManager
{
	private static final String CHARACTERSFILE = "CharactersDataBase";
	private static final String CHARACTERCHOSENFILE = "CharacterChosen";
	private static final String CHARACTERSNAMESFILE = "CharactersNamesDataBase";
	
	private Activity activity;
	private List<String> nombres;
	
	public InternalStorageManager(Activity activity)
	{
		this.activity = activity;
		this.nombres = new ArrayList<String>();
		
		// Cargar Lista de Nombres
		cargarNombres();		
	}
	
	private String getCharactersFileName()
	{
		return CHARACTERSFILE;
	}
	
	private String getCharacterChosenFileName()
	{
		return CHARACTERCHOSENFILE;
	}
	
	private String getCharacterNamesFileName()
	{
		return CHARACTERSNAMESFILE;
	}
	
	private boolean comprobarNombresInternos(String nombre)
	{
		if(nombre.equals(getCharactersFileName())) return false;
		if(nombre.equals(getCharacterChosenFileName())) return false;
		if(nombre.equals(getCharacterNamesFileName())) return false;
		
		return true;
	}
	
	private boolean comprobarNombresUsados(String nombre)
	{
		return nombres.contains(nombre);
	}
	
	/* LISTA NOMBRES */
	
	private void cargarNombres()
	{
		try
		{
			FileInputStream file = activity.openFileInput(getCharacterNamesFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personaje Seleccionado
			int numPersonajes = data.readInt();
			
			for(int i = 0; i < numPersonajes; i++)
			{
				nombres.add(data.readUTF());
			}
			
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - CARGAR NOMBRES");
			e.printStackTrace();
		}
	}
	
	private boolean eliminarNombre(String nombre)
	{
		if(nombres.remove(nombre))
		{
			return guardarNombres();
		}
		
		return false;
	}
	
	private boolean guardarNombres()
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getCharacterNamesFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Número de Personajes
			data.writeInt(nombres.size());
			
			// Guardar Nombres
			Iterator<String> it = nombres.iterator();
			while(it.hasNext())
			{
				data.writeUTF(it.next());
			}
			
			data.close();
			file.close();
			
			return true;
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - GUARDAR NOMBRES");
			e.printStackTrace();
			
			return false;
		}
	}
	
	/* LISTA DE PERSONAJES */
	
	public List<Personaje> cargarListaPersonajes()
	{
		List<Personaje> lista = new ArrayList<Personaje>();
		
		// Cargar Lista de Personajes
		Iterator<String> it = nombres.iterator();
		while(it.hasNext())
		{
			Personaje p = cargarPersonaje(it.next());
			lista.add(p);
		}
		
		return lista;
	}
	
	/* PERSONAJE SELECCIONADO */
	
	public int cargarSeleccionado()
	{
		int seleccionado = -1;
		
		try
		{
			FileInputStream file = activity.openFileInput(getCharacterChosenFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personaje Seleccionado
			seleccionado = data.readInt();
			
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - CARGAR SELECCIONADO");
			e.printStackTrace();
		}
		
		return seleccionado;
	}
	
	public void guardarSeleccionado(int seleccionado)
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getCharacterChosenFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personaje Seleccionado
			data.writeInt(seleccionado);
			
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - GUARDAR SELECCIONADO");
			e.printStackTrace();
		}
	}
	
	/* PERSONAJE ACTUAL */
	
	public Personaje cargarPersonaje(String nombre)
	{
		try
		{
			FileInputStream file = activity.openFileInput(nombre);
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personajes
			Personaje p = (Personaje) data.readObject();
			
			data.close();
			file.close();
			
			return p;
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - CARGAR PERSONAJE");
			e.printStackTrace();
			
			return null;
		}
		catch (ClassNotFoundException e)
		{
			Log.d("TEST", "CLASS NOT FOUND EXCEPTION - CARGAR PERSONAJE");
			e.printStackTrace();
			
			return null;
		}
	}
	
	public boolean guardarPersonaje(Personaje personaje)
	{
		// Comprobar Nombre de Fichero usado por el Sistema
		if(!comprobarNombresInternos(personaje.getNombre())) return false;
		
		// Comprobar Nombres de Personajes ya existentes
		if(!comprobarNombresUsados(personaje.getNombre()))
		{
			// Personaje Reemplazado
			Toast.makeText(activity.getApplication(), "Personaje Reemplazado", Toast.LENGTH_SHORT).show();
		}
		
		String nombreActual = personaje.getNombre().toUpperCase(Locale.getDefault());
		nombres.add(nombreActual);
		
		try
		{
			FileOutputStream file = activity.openFileOutput(nombreActual, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personajes
			data.writeObject(personaje);
			
			data.close();
			file.close();
			
			return guardarNombres();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION - GUARDAR PERSONAJE");
			e.printStackTrace();
			
			return false;
		}
	}
	
	public boolean eliminarPersonaje(Personaje personaje)
	{
		if(activity.deleteFile(personaje.getNombre()))
		{
			return eliminarNombre(personaje.getNombre());
		}
		
		return false;
	}
}
