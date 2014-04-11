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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.project.main.GamePreferences;
import com.project.main.LoadingFragment;
import com.project.main.R;

public class InternalStorageManager
{
	private static final String PREFERENCES_GAME_FILE = "PreferencesGameDataBase";
	private static final String CHARACTERS_NAMES_FILE = "CharactersNamesDataBase";
	private static final String LOCKED_LEVELS_FILE = "LockedLevelDataBase";
	private static final String SCORE_LEVELS_FILE = "ScoreLevelsDataBase";

	private Context mContext;
	private List<String> nombres;

	/* Constructora */

	public InternalStorageManager(Context context)
	{
		mContext = context;
		nombres = new ArrayList<String>();

		cargarNombres();
	}

	/* Métodos Nombre de Directorios */

	private boolean comprobarNombresInternos(String nombre)
	{
		if (nombre.equals(PREFERENCES_GAME_FILE) || nombre.equals(CHARACTERS_NAMES_FILE) || nombre.equals(LOCKED_LEVELS_FILE) || nombre.equals(SCORE_LEVELS_FILE))
		{
			return false;
		}

		return true;
	}

	private boolean comprobarNombresUsados(String nombre)
	{
		Iterator<String> it = nombres.iterator();
		while(it.hasNext())
		{
			if(it.next().equals(nombre))
			{
				return false;
			}
		}
		return true;
	}

	private String evaluarNombre(String nombre)
	{
		return nombre.toUpperCase(Locale.getDefault());
	}

	/* Métodos Lista de Nombres */

	private boolean cargarNombres()
	{
		try
		{
			FileInputStream file = mContext.openFileInput(CHARACTERS_NAMES_FILE);
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personaje Seleccionado
			int numPersonajes = data.readInt();

			for (int i = 0; i < numPersonajes; i++)
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
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Name sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Name ioexception");
		}

		Log.d("TEST", "Names not loadead");
		return false;
	}

	private boolean eliminarNombre(String nombre)
	{
		if (nombres.remove(nombre))
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
			FileOutputStream file = mContext.openFileOutput(CHARACTERS_NAMES_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Número de Personajes
			data.writeInt(nombres.size());

			// Guardar Nombres
			Iterator<String> it = nombres.iterator();
			while (it.hasNext())
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
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Name sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Name ioexception");
		}

		Log.d("TEST", "Names not loadead");
		return false;
	}

	/* Métodos Lista de Personajes */

	public List<Personaje> cargarListaPersonajes(LoadingFragment fragment)
	{
		int i = 0;
		List<Personaje> lista = new ArrayList<Personaje>();

		// Cargar Lista de Personajes
		Iterator<String> it = nombres.iterator();
		while (it.hasNext())
		{
			String name = it.next();

			fragment.updateProgressBarStatus(100 * i / nombres.size(), name);

			Personaje p = cargarPersonaje(name);
			if (p != null)
			{
				lista.add(p);
			}

			i++;
		}

		return lista;
	}

	/* Métodos Personaje Seleccionado */

	public boolean cargarPreferencias()
	{
		try
		{
			FileInputStream file = mContext.openFileInput(PREFERENCES_GAME_FILE);
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personaje Seleccionado			
			GamePreferences.setCharacterParameters(data.readInt());
			GamePreferences.setMusicParameters(data.readBoolean());
			GamePreferences.setTipParameters(data.readBoolean());

			data.close();
			file.close();

			Log.d("TEST", "Chosen loadead");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Chosen file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
		}

		Log.d("TEST", "Chosen not loadead");
		GamePreferences.setCharacterParameters(-1);
		GamePreferences.setMusicParameters(true);
		GamePreferences.setTipParameters(true);
		return false;
	}

	public boolean guardarPreferencias()
	{
		try
		{
			FileOutputStream file = mContext.openFileOutput(PREFERENCES_GAME_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			data.writeInt(GamePreferences.GET_CHARACTER_GAME());
			data.writeBoolean(GamePreferences.MUSIC_ENABLED());
			data.writeBoolean(GamePreferences.TIPS_ENABLED());
			
			data.flush();
			data.close();
			file.close();

			Log.d("TEST", "Chosen saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Chosen file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
		}

		Log.d("TEST", "Chosen not saved");
		return false;
	}

	/* Métodos Personaje Actual */

	public Personaje cargarPersonaje(String nombre)
	{
		try
		{
			FileInputStream file = mContext.openFileInput(nombre);
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
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Character file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Character ioexception");
		}

		Log.d("TEST", "Character not loadead");
		return null;
	}

	public boolean guardarPersonaje(Personaje personaje)
	{
		// Comprobar Nombre de Fichero usado por el Sistema
		if (!comprobarNombresInternos(personaje.getNombre()))
		{
			Toast.makeText(mContext, R.string.error_storage_internal_name, Toast.LENGTH_SHORT).show();
			return false;
		}

		// Comprobar Nombres de Personajes ya existentes
		if (comprobarNombresUsados(personaje.getNombre()))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}

		String nombreActual = evaluarNombre(personaje.getNombre());
		nombres.add(nombreActual);

		try
		{
			FileOutputStream file = mContext.openFileOutput(nombreActual, Context.MODE_PRIVATE);
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
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Character ioexception");
		}

		Log.d("TEST", "Character not saved");
		return false;
	}
	
	public boolean actualizarPersonaje(Personaje personaje)
	{
		try
		{
			FileOutputStream file = mContext.openFileOutput(evaluarNombre(personaje.getNombre()), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personajes
			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos());

			data.flush();
			data.close();
			file.close();

			Log.d("TEST", "Character saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Character file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Character ioexception");
		}

		Log.d("TEST", "Character not saved");
		return false;
	}

	public boolean eliminarPersonaje(Personaje personaje)
	{
		String nombreActual = evaluarNombre(personaje.getNombre());

		if (mContext.deleteFile(nombreActual))
		{
			Log.d("TEST", "File deleted");
			return eliminarNombre(nombreActual);
		}

		Log.d("TEST", "File not deleted");
		return false;
	}

	/* Métodos Niveles */

	public boolean[] cargarNiveles()
	{
		boolean[] niveles = new boolean[GamePreferences.NUM_LEVELS];

		try
		{
			FileInputStream file = mContext.openFileInput(LOCKED_LEVELS_FILE);
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Niveles Jugados
			for (int i = 0; i < GamePreferences.NUM_LEVELS; i++)
			{
				niveles[i] = data.readBoolean();
			}

			niveles[0] = true;

			data.close();
			file.close();

			Log.d("TEST", "Levels loadead");
			return niveles;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Levels file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Levels sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Levels ioexception");
		}

		Log.d("TEST", "Levels not loadead");

		niveles[0] = true;
		return niveles;
	}

	public boolean guardarNiveles(boolean[] niveles)
	{
		try
		{
			FileOutputStream file = mContext.openFileOutput(LOCKED_LEVELS_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			for (int i = 0; i < niveles.length; i++)
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
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
		}

		Log.d("TEST", "Chosen not saved");
		return false;
	}

	public int[] cargarPuntuaciones()
	{
		int[] puntuacion = new int[GamePreferences.NUM_LEVELS];

		try
		{
			FileInputStream file = mContext.openFileInput(SCORE_LEVELS_FILE);
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Niveles Jugados
			for (int i = 0; i < GamePreferences.NUM_LEVELS; i++)
			{
				puntuacion[i] = data.readInt();
			}

			data.close();
			file.close();

			Log.d("TEST", "Levels loadead");
			return puntuacion;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File Levels file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Levels sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Levels ioexception");
		}

		Log.d("TEST", "Levels not loadead");

		return puntuacion;
	}

	public boolean guardarPuntuacion(int[] puntuacion)
	{
		try
		{
			FileOutputStream file = mContext.openFileOutput(SCORE_LEVELS_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			for (int i = 0; i < puntuacion.length; i++)
			{
				data.writeInt(puntuacion[i]);
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
		}
		catch (StreamCorruptedException e)
		{
			Log.d("TEST", "File Chosen sream corrupted");
		}
		catch (IOException e)
		{
			Log.d("TEST", "File Chosen ioexception");
		}

		Log.d("TEST", "Chosen not saved");
		return false;
	}
}
