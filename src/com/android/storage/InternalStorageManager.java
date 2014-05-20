package com.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.widget.Toast;

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.main.model.GameStatistics;
import com.project.main.R;

public class InternalStorageManager
{
	private static final String INTERNAL_STORAGE_TAG = "INTERNAL";
	
	private static final String CHARACTER_DIRECTORY = "CHARACTERS";
	private static final String GAMEDATA_DIRECTORY = "GAMEDATA";
	private static final String TEMP_DIRECTORY = "TEMP";
	
	private static final String DATA_FILE = "DATA";
	private static final String AUDIO_FILE = "AUDIO";
	private static final String PREFERENCES_FILE = "PREFERENCES";
	private static final String LEVELS_FILE = "LEVELS";

	private Context mContext;

	/* Constructora */

	public InternalStorageManager(Context context)
	{
		mContext = context;
		
		obtenerDirectorio(CHARACTER_DIRECTORY);
		obtenerDirectorio(GAMEDATA_DIRECTORY);
		obtenerDirectorio(TEMP_DIRECTORY);
	}

	/* Métodos Nombre de Directorios */
	
	private File obtenerDirectorio(String name)
	{
		return mContext.getDir(name, Context.MODE_PRIVATE);
	}
	
	private File obtenerDirectorio(String path, String name)
	{
		File file = new File(mContext.getDir(path, Context.MODE_PRIVATE), evaluarNombre(name));
		if(!file.exists())
		{
			file.mkdir();		
		}
		
		return file;
	}
	
	private boolean eliminarDirectorio(File file)
	{
		if(file.exists())
		{
			if(file.isDirectory())
			{
				File[] ficheros = file.listFiles();
				if(ficheros != null)
				{
					for (int i = 0; i < ficheros.length; i++)
					{
						eliminarDirectorio(ficheros[i]);
					}
				}
			}
			
			return file.delete();
		}
		
		return false;
	}
	
	private boolean comprobarDirectorio(String path, String name)
	{
		File dir = new File(mContext.getDir(path, Context.MODE_PRIVATE), evaluarNombre(name));
		return dir.exists();
	}
	
	private boolean comprobarFichero(String name)
	{
		File file = new File(name);
		return file.exists();
	}

	private String evaluarNombre(String nombre)
	{
		return nombre.toUpperCase(Locale.getDefault());
	}

	/* Métodos Personajes */

	public List<Personaje> cargarListaPersonajes()
	{
		List<Personaje> lista = new ArrayList<Personaje>();
		
		File file = obtenerDirectorio(CHARACTER_DIRECTORY);
		if (file.exists() && file.isDirectory())
		{
			File[] personajes = file.listFiles();		
			
			if(personajes != null)
			{
				Arrays.sort(personajes, new Comparator<File>() {
					@Override
					public int compare(File file1, File file2)
					{
						long timeFile1 = file1.lastModified();
						long timeFile2 = file2.lastModified();
						
						if (timeFile1 < timeFile2)
						{
							return -1;
						}
						else if (timeFile1 > timeFile2)
						{
							return 1;
						}
						
						return 0;
					}
				});
				
				for (int i = 0; i < personajes.length; i++)
				{					
					Personaje p = cargarPersonaje(personajes[i].getName());
					if (p != null)
					{
						lista.add(p);
					}
				}
			}
		}
		
		return lista;
	}

	public Personaje cargarPersonaje(String nombre)
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(obtenerDirectorio(CHARACTER_DIRECTORY, nombre), DATA_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personajes
			Personaje p = new Personaje();
			p.setEsqueleto((Esqueleto) data.readObject());
			p.setTextura((Textura) data.readObject());
			p.setMovimientos((Movimientos) data.readObject());
			p.setNombre((String) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character Loadead");
			return p;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character not loadead");
		return null;
	}

	public boolean guardarPersonaje(Personaje personaje)
	{
		// Comprobar Nombres de Personajes ya existentes
		if (comprobarDirectorio(CHARACTER_DIRECTORY, personaje.getNombre()))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}

		return actualizarPersonaje(personaje, 0);
	}
	
	public boolean actualizarPersonaje(Personaje personaje)
	{
		return actualizarPersonaje(personaje, 0);
	}
	
	private boolean actualizarPersonaje(Personaje personaje, long fileDate)
	{
		try
		{	
			File file = obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre());
			long timeFileCreated = file.lastModified();
			
			FileOutputStream outFile = new FileOutputStream(new File(obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre()), DATA_FILE));
			ObjectOutputStream data = new ObjectOutputStream(outFile);
			
			if (fileDate > 0)
			{
				timeFileCreated = fileDate;
			}

			// Guardar Personajes
			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos());
			data.writeObject(personaje.getNombre());

			data.flush();
			data.close();
			outFile.close();
			
			file.setLastModified(timeFileCreated);

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character not saved");
		return false;
	}

	public boolean eliminarPersonaje(Personaje personaje)
	{
		File file = obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre());
		
		return eliminarDirectorio(file);
	}

	public boolean renombrarPersonaje(Personaje personaje, String nombre)
	{
		// Comprobar Nombres de Personajes ya existentes
		if (comprobarDirectorio(CHARACTER_DIRECTORY, nombre))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		File file = obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre());
		long fileDate = file.lastModified();
		
		eliminarPersonaje(personaje);
		personaje.setNombre(evaluarNombre(nombre));
		return actualizarPersonaje(personaje, fileDate);
	}
	
	/* Métodos Audio */
	
	public String cargarAudioTemp(TTipoMovimiento tipo)
	{
		return obtenerDirectorio(TEMP_DIRECTORY).getAbsolutePath() + "/" + AUDIO_FILE + tipo.ordinal() + GameResources.EXTENSION_AUDIO_FILE;
	}
	
	public String guardarAudioTemp(TTipoMovimiento tipo)
	{
		return cargarAudioTemp(tipo);
	}
	
	public boolean comprobarAudioTemp(TTipoMovimiento tipo)
	{
		return comprobarFichero(cargarAudioTemp(tipo));
	}
	
	public boolean eliminarAudioTemp(TTipoMovimiento tipo)
	{
		return eliminarDirectorio(new File(cargarAudioTemp(tipo)));
	}
	
	public String cargarAudio(String nombre, TTipoMovimiento tipo)
	{
		return obtenerDirectorio(CHARACTER_DIRECTORY, nombre).getAbsolutePath() + "/" + AUDIO_FILE + tipo.ordinal() + GameResources.EXTENSION_AUDIO_FILE;
	}
	
	public boolean guardarAudio(String nombre, TTipoMovimiento tipo)
	{
		File file = new File(cargarAudioTemp(tipo));
		return file.renameTo(new File(cargarAudio(nombre, tipo)));
	}
	
	public boolean comprobarAudio(String nombre, TTipoMovimiento tipo)
	{
		return comprobarFichero(cargarAudio(nombre, tipo));
	}
	
	/* Métodos Preferencias */

	public GameStatistics[] cargarEstadisticas()
	{
		GameStatistics[] niveles = new GameStatistics[GamePreferences.NUM_TYPE_LEVELS];

		try
		{
			FileInputStream file = new FileInputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), LEVELS_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Niveles Jugados
			for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
			{
				niveles[i] = (GameStatistics) data.readObject();
			}

			niveles[0].setUnlocked();

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels loadead");
			return niveles;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels not loadead");

		for(int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			niveles[i] = new GameStatistics();
		}
		
		niveles[0].setUnlocked();
		return niveles;
	}

	public boolean guardarEstadisticas(GameStatistics[] niveles)
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), LEVELS_FILE));
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			for (int i = 0; i < niveles.length; i++)
			{
				data.writeObject(niveles[i]);
			}

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Levels not saved");
		return false;
	}

	public boolean cargarPreferencias()
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), PREFERENCES_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personaje Seleccionado			
			GamePreferences.SET_CHARACTER_PARAMETERS(data.readInt());
			GamePreferences.SET_MUSIC_PARAMETERS(data.readBoolean());
			GamePreferences.SET_TIP_PARAMETERS(data.readBoolean());
			GamePreferences.SET_DEBUG_PARAMETERS(data.readBoolean());
			GamePreferences.SET_SENSOR_PARAMETERS(data.readBoolean());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences loadead");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences not loadead");
		
		GamePreferences.SET_CHARACTER_PARAMETERS(-1);
		GamePreferences.SET_MUSIC_PARAMETERS(true);
		GamePreferences.SET_TIP_PARAMETERS(true);
		GamePreferences.SET_DEBUG_PARAMETERS(true);
		GamePreferences.SET_SENSOR_PARAMETERS(true);
		return false;
	}

	public boolean guardarPreferencias()
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), PREFERENCES_FILE));
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			data.writeInt(GamePreferences.GET_CHARACTER_GAME());
			data.writeBoolean(GamePreferences.IS_MUSIC_ENABLED());
			data.writeBoolean(GamePreferences.IS_TIPS_ENABLED());
			data.writeBoolean(GamePreferences.IS_DEBUG_ENABLED());
			data.writeBoolean(GamePreferences.IS_SENSOR_ENABLED());
			
			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Preferences not saved");
		return false;
	}


}
