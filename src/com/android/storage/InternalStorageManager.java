package com.android.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.project.data.Personaje;
import com.project.loading.LoadingFragment;
import com.project.main.R;

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
	
	private boolean cargarNombres()
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
			
			Log.d("TEST", "Names loaded");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Name file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Name sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Name ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Names not loadead");
		return false;
	}
	
	private boolean eliminarNombre(String nombre)
	{
		if(nombres.remove(nombre))
		{
			Log.d("TEST", "Name deleted");
			return guardarNombres();
		}
		
		Log.d("TEST", "Name not deleted");
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
			
			Log.d("TEST", "Names saved");			
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Name file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Name sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Name ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Names not loadead");
		return false;
	}
	
	/* LISTA DE PERSONAJES */
	
	public List<Personaje> cargarListaPersonajes(LoadingFragment fragment)
	{
		int i = 0;
		List<Personaje> lista = new ArrayList<Personaje>();
		
		// Cargar Lista de Personajes
		Iterator<String> it = nombres.iterator();
		while(it.hasNext())
		{
			String name = it.next();
			
			fragment.updateProgressBarStatus(100 * i / nombres.size(), name);
			
			lista.add(cargarPersonaje(name));
			i++;
		}
		
		return lista;
	}
	
	/* PERSONAJE SELECCIONADO */
	
	public int cargarSeleccionado()
	{
		try
		{
			FileInputStream file = activity.openFileInput(getCharacterChosenFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personaje Seleccionado
			int seleccionado = data.readInt();
			
			data.close();
			file.close();
			
			Log.d("TEST", "Chosen loadead");
			return seleccionado;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Chosen file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Chosen not loadead");
		return -1;
	}
	
	public boolean guardarSeleccionado(int seleccionado)
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getCharacterChosenFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personaje Seleccionado
			data.writeInt(seleccionado);
			
			data.close();
			file.close();
			
			Log.d("TEST", "Chosen saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Chosen file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Chosen not saved");
		return false;
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
			
			Log.d("TEST", "Character Loadead");
			return p;
		}
		catch (ClassNotFoundException e)
		{
			Log.d("TEST", "File Character class not found");
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Character file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Character sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Character ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Character not loadead");
		return null;
	}
	
	public boolean guardarPersonaje(Personaje personaje)
	{
		// Comprobar Nombre de Fichero usado por el Sistema
		if(!comprobarNombresInternos(personaje.getNombre())) return false;
		
		// Comprobar Nombres de Personajes ya existentes
		if(comprobarNombresUsados(personaje.getNombre()))
		{
			// Personaje Reemplazado
			Toast.makeText(activity.getApplication(), R.string.text_replace_character_confirmation, Toast.LENGTH_SHORT).show();
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
			
			Log.d("TEST", "Character saved");
			return guardarNombres();
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Character file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Character sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Character ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Character not saved");
		return false;
	}
	
	public boolean eliminarPersonaje(Personaje personaje)
	{
		if(activity.deleteFile(personaje.getNombre()))
		{
			Log.d("TEST", "File deleted");
			return eliminarNombre(personaje.getNombre());
		}
		
		Log.d("TEST", "File not deleted");
		return false;
	}
}
