package com.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.loading.load.OnLoadingListener;
import com.project.main.R;
import com.project.model.GamePreferences;
import com.project.model.GameStatistics;

public class InternalStorageManager
{
	private static final String CHARACTER_DIRECTORY = "CHARACTERS";
	private static final String GAMEDATA_DIRECTORY = "GAMEDATA";
	private static final String TEMP_DIRECTORY = "TEMP";
	
	private static final String DATA_FILE = "DATA";
	private static final String AUDIO_FILE = "AUDIO";
	private static final String PREFERENCES_FILE = "PREFERENCES";
	private static final String LEVELS_FILE = "LEVELS";
	
	private static final String MUSIC_EXTENSION = ".3gp";

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

	public List<Personaje> cargarListaPersonajes(OnLoadingListener listener)
	{
		List<Personaje> lista = new ArrayList<Personaje>();
		
		File file = obtenerDirectorio(CHARACTER_DIRECTORY);
		if (file.exists() && file.isDirectory())
		{
			String[] personajes = file.list();
			if(personajes != null)
			{
				for (int i = 0; i < personajes.length; i++)
				{
					 listener.onProgress(100 * i / personajes.length, personajes[i]);
					
					Personaje p = cargarPersonaje(personajes[i]);
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

			Log.d("INTERNAL", "Character Loadead");
			return p;
		}
		catch (ClassNotFoundException e)
		{
			Log.d("INTERNAL", "Character class not found");
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Character file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Character ioexception");
		}

		Log.d("INTERNAL", "Character not loadead");
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

		return actualizarPersonaje(personaje);
	}
	
	public boolean actualizarPersonaje(Personaje personaje)
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre()), DATA_FILE));
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personajes
			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos());
			data.writeObject(personaje.getNombre());

			data.flush();
			data.close();
			file.close();

			Log.d("INTERNAL", "Character saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Character file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Character ioexception");
		}

		Log.d("INTERNAL", "Character not saved");
		return false;
	}

	public boolean eliminarPersonaje(Personaje personaje)
	{
		return eliminarDirectorio(obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre()));
	}

	public boolean renombrarPersonaje(Personaje personaje, String nombre)
	{
		// Comprobar Nombres de Personajes ya existentes
		if (comprobarDirectorio(CHARACTER_DIRECTORY, nombre))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		eliminarPersonaje(personaje);
		
		personaje.setNombre(evaluarNombre(nombre));
		return actualizarPersonaje(personaje);
	}
	
	public boolean exportarPersonaje(Personaje personaje)
	{
		try
		{
			FileInputStream inFile = new FileInputStream(new File(obtenerDirectorio(CHARACTER_DIRECTORY, personaje.getNombre()), DATA_FILE));
			FileOutputStream outFile = new FileOutputStream(new File(ExternalStorageManager.getDirectorioExterno(evaluarNombre(personaje.getNombre()))));

			FileChannel inChannel = inFile.getChannel();
			FileChannel outChannel = outFile.getChannel();
			
			inChannel.transferTo(0, inChannel.size(), outChannel);
			    
			inChannel.close();
			outChannel.close();
			
			inFile.close();
			outFile.close();
			
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	/* Métodos Audio */
	
	public String cargarAudioTemp(TTipoMovimiento tipo)
	{
		return obtenerDirectorio(TEMP_DIRECTORY).getAbsolutePath() + "/" + AUDIO_FILE + tipo.ordinal() + MUSIC_EXTENSION;
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
		return obtenerDirectorio(CHARACTER_DIRECTORY, nombre).getAbsolutePath() + "/" + AUDIO_FILE + tipo.ordinal() + MUSIC_EXTENSION;
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
		GameStatistics[] niveles = new GameStatistics[GamePreferences.NUM_LEVELS];

		try
		{
			FileInputStream file = new FileInputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), LEVELS_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Niveles Jugados
			for (int i = 0; i < GamePreferences.NUM_LEVELS; i++)
			{
				niveles[i] = (GameStatistics) data.readObject();
			}

			niveles[0].setUnlocked();

			data.close();
			file.close();

			Log.d("INTERNAL", "Levels loadead");
			return niveles;
		}
		catch (ClassNotFoundException e)
		{
			Log.d("INTERNAL", "Levels class not found");
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Levels file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Levels sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Levels ioexception");
		}

		Log.d("INTERNAL", "Levels not loadead");

		for(int i = 0; i < GamePreferences.NUM_LEVELS; i++)
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

			Log.d("INTERNAL", "Levels saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Levels file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Levels sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Levels ioexception");
		}

		Log.d("INTERNAL", "Levels not saved");
		return false;
	}

	public boolean cargarPreferencias()
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(obtenerDirectorio(GAMEDATA_DIRECTORY), PREFERENCES_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personaje Seleccionado			
			GamePreferences.setCharacterParameters(data.readInt());
			GamePreferences.setMusicParameters(data.readBoolean());
			GamePreferences.setTipParameters(data.readBoolean());

			data.close();
			file.close();

			Log.d("INTERNAL", "Preferences loadead");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Preferences file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Preferences sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Preferences ioexception");
		}

		Log.d("INTERNAL", "Preferences not loadead");
		
		GamePreferences.setCharacterParameters(-1);
		GamePreferences.setMusicParameters(true);
		GamePreferences.setTipParameters(true);
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
			
			data.flush();
			data.close();
			file.close();

			Log.d("INTERNAL", "Preferences saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("INTERNAL", "Preferences file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("INTERNAL", "Preferences sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("INTERNAL", "Preferences ioexception");
		}

		Log.d("INTERNAL", "Preferences not saved");
		return false;
	}


}
