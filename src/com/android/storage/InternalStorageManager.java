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

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.project.loading.LoadingFragment;
import com.project.main.R;

public class InternalStorageManager
{
	private static final int NUM_LEVELS = 5;
	
	private static final String CHARACTERS_FILE = "CharactersDataBase";
	private static final String CHARACTER_CHOSEN_FILE = "CharacterChosen";
	private static final String CHARACTERS_NAMES_FILE = "CharactersNamesDataBase";
	private static final String LEVELS_FILE = "LevelDataBase";
	
	private Activity activity;
	private List<String> nombres;
	
	/* SECTION Constructora */
	
	public InternalStorageManager(Activity activity)
	{
		this.activity = activity;
		this.nombres = new ArrayList<String>();
		
		cargarNombres();		
	}
	
	/* SECTION Métodos Nombre de Directorios */
	
	private String getCharactersFileName()
	{
		return CHARACTERS_FILE;
	}
	
	private String getCharacterChosenFileName()
	{
		return CHARACTER_CHOSEN_FILE;
	}
	
	private String getCharacterNamesFileName()
	{
		return CHARACTERS_NAMES_FILE;
	}
	
	private String getLevelsFileName()
	{
		return LEVELS_FILE;
	}
	
	private boolean comprobarNombresInternos(String nombre)
	{
		if(nombre.equals(getCharactersFileName())) return false;
		if(nombre.equals(getCharacterChosenFileName())) return false;
		if(nombre.equals(getCharacterNamesFileName())) return false;
		if(nombre.equals(getLevelsFileName())) return false;
		
		return true;
	}
	
	private boolean comprobarNombresUsados(String nombre)
	{
		return nombres.contains(nombre);
	}
	
	private String evaluarNombre(String nombre)
	{
		return nombre.toUpperCase(Locale.getDefault());
	}
	
	/* SECTION Métodos Lista de Nombres */
	
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
			
			data.flush();
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
	
	/* SECTION Métodos Lista de Personajes */
	
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
			
			Personaje p = cargarPersonaje(name);
			if(p != null)
			{
				lista.add(p);
			}
			
			i++;
		}
		
		return lista;
	}
	
	/* SECTION Métodos Personaje Seleccionado */
	
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
			
			data.flush();
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
	
	/* SECTION Métodos Personaje Actual */
	
	public Personaje cargarPersonaje(String nombre)
	{
		try
		{
			FileInputStream file = activity.openFileInput(nombre);
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personajes
			Personaje p = new Personaje();
			p.setEsqueleto((Esqueleto) data.readObject());
			p.setTextura((Textura) data.readObject());
			p.setMovimientos((Movimientos) data.readObject());
			p.setNombre(nombre);
			
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
		
		String nombreActual = evaluarNombre(personaje.getNombre());
		nombres.add(nombreActual);
		
		try
		{
			FileOutputStream file = activity.openFileOutput(nombreActual, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personajes
			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos());
			
			data.flush();
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
		String nombreActual = evaluarNombre(personaje.getNombre());
		
		if(activity.deleteFile(nombreActual))
		{
			Log.d("TEST", "File deleted");
			return eliminarNombre(nombreActual);
		}
		
		Log.d("TEST", "File not deleted");
		return false;
	}

	/* SECTION Métodos Niveles */
	
	public boolean[] cargarNiveles()
	{
		boolean[] niveles = new boolean[NUM_LEVELS];
		
		try
		{
			FileInputStream file = activity.openFileInput(getLevelsFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Niveles Jugados
			for(int i = 0; i < NUM_LEVELS; i++)
			{
				niveles[i] = data.readBoolean();
			}
			
			data.close();
			file.close();
			
			Log.d("TEST", "Levels loadead");
			return niveles;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Levels file not found");
			e.printStackTrace();
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Levels sream corrupted");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Levels ioexception");
			e.printStackTrace();
		}
		
		Log.d("TEST", "Levels not loadead");
		niveles[0] = true;
		
		return niveles;
	}
	
	public boolean guardarNiveles(boolean[] niveles)
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getLevelsFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personaje Seleccionado
			for(int i = 0; i < niveles.length; i++)
			{
				data.writeBoolean(niveles[i]);
			}
			
			data.flush();
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

}
