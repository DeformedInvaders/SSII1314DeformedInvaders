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

import com.creation.data.Movements;
import com.creation.data.Skeleton;
import com.creation.data.Texture;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.project.main.R;

public class InternalStorageManager
{
	private static final String INTERNAL_STORAGE_TAG = "INTERNAL";
	
	private static final String CHARACTER_DIRECTORY = "CHARACTERS";
	private static final String GAMEDATA_DIRECTORY = "GAMEDATA";
	private static final String TEMP_DIRECTORY = "TEMP";
	
	private static final String DATA_FILE = "DATA";
	private static final String PREFERENCES_FILE = "PREFERENCES";
	private static final String LEVELS_FILE = "LEVELS";

	private Context mContext;

	/* Constructora */

	public InternalStorageManager(Context context)
	{
		mContext = context;
		
		checkDirectory(CHARACTER_DIRECTORY);
		checkDirectory(GAMEDATA_DIRECTORY);
		checkDirectory(TEMP_DIRECTORY);
	}

	/* Métodos Nombre de Directorios */
	
	private File checkDirectory(String name)
	{
		return mContext.getDir(name, Context.MODE_PRIVATE);
	}
	
	private File checkDirectory(String path, String name)
	{
		File file = new File(mContext.getDir(path, Context.MODE_PRIVATE), parseName(name));
		if(!file.exists())
		{
			file.mkdir();		
		}
		
		return file;
	}
	
	private boolean deleteDirectory(File file)
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
						deleteDirectory(ficheros[i]);
					}
				}
			}
			
			return file.delete();
		}
		
		return false;
	}
	
	private boolean existsDirectory(String path, String name)
	{
		File dir = new File(mContext.getDir(path, Context.MODE_PRIVATE), parseName(name));
		return dir.exists();
	}

	private String parseName(String nombre)
	{
		return nombre.toUpperCase(Locale.getDefault());
	}

	/* Métodos Personajes */

	public List<Character> loadCharacterList()
	{
		List<Character> characterList = new ArrayList<Character>();
		
		File file = checkDirectory(CHARACTER_DIRECTORY);
		if (file.exists() && file.isDirectory())
		{
			File[] characterFiles = file.listFiles();		
			
			if(characterFiles != null)
			{
				Arrays.sort(characterFiles, new Comparator<File>() {
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
				
				for (int i = 0; i < characterFiles.length; i++)
				{					
					Character character = loadCharacter(characterFiles[i].getName());
					if (character != null)
					{
						characterList.add(character);
					}
				}
			}
		}
		
		return characterList;
	}

	public Character loadCharacter(String nombre)
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(checkDirectory(CHARACTER_DIRECTORY, nombre), DATA_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Personajes
			Character character = new Character();
			character.setSkeleton((Skeleton) data.readObject());
			character.setTexture((Texture) data.readObject());
			character.setMovements((Movements) data.readObject());
			character.setName((String) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Character Loadead");
			return character;
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

	public boolean saveCharacter(Character personaje)
	{
		// Comprobar Nombres de Personajes ya existentes
		/*if (existsDirectory(CHARACTER_DIRECTORY, personaje.getName()))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}*/

		return updateCharacter(personaje, 0);
	}
	
	public boolean updateCharacter(Character personaje)
	{
		return updateCharacter(personaje, 0);
	}
	
	private boolean updateCharacter(Character character, long fileDate)
	{
		try
		{	
			File file = checkDirectory(CHARACTER_DIRECTORY, character.getName());
			long timeFileCreated = file.lastModified();
			
			FileOutputStream outFile = new FileOutputStream(new File(checkDirectory(CHARACTER_DIRECTORY, character.getName()), DATA_FILE));
			ObjectOutputStream data = new ObjectOutputStream(outFile);
			
			if (fileDate > 0)
			{
				timeFileCreated = fileDate;
			}

			// Guardar Personajes
			data.writeObject(character.getSkeleton());
			data.writeObject(character.getTexture());
			data.writeObject(character.getMovements());
			data.writeObject(character.getName());

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

	public boolean deleteCharacter(Character character)
	{
		File file = checkDirectory(CHARACTER_DIRECTORY, character.getName());
		
		return deleteDirectory(file);
	}

	public boolean renameCharacter(Character character, String name)
	{
		// Comprobar Nombres de Personajes ya existentes
		if (existsDirectory(CHARACTER_DIRECTORY, name))
		{
			Toast.makeText(mContext, R.string.error_storage_used_name, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		File file = checkDirectory(CHARACTER_DIRECTORY, character.getName());
		long fileDate = file.lastModified();
		
		deleteCharacter(character);
		character.setName(parseName(name));
		return updateCharacter(character, fileDate);
	}
	
	
	/* Métodos Estadístics y Preferencias */

	public GameStatistics[] loadStatistics()
	{
		GameStatistics[] statistics = new GameStatistics[GamePreferences.NUM_TYPE_LEVELS];

		try
		{
			FileInputStream file = new FileInputStream(new File(checkDirectory(GAMEDATA_DIRECTORY), LEVELS_FILE));
			ObjectInputStream data = new ObjectInputStream(file);

			// Cargar Niveles Jugados
			for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
			{
				statistics[i] = (GameStatistics) data.readObject();
			}

			statistics[0].setUnlocked();

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics loadead");
			return statistics;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics not loadead");

		for(int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			statistics[i] = new GameStatistics();
		}
		
		statistics[0].setUnlocked();
		return statistics;
	}

	public boolean saveStatistics(GameStatistics[] statistics)
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(checkDirectory(GAMEDATA_DIRECTORY), LEVELS_FILE));
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personaje Seleccionado
			for (int i = 0; i < statistics.length; i++)
			{
				data.writeObject(statistics[i]);
			}

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(INTERNAL_STORAGE_TAG, "Statistics not saved");
		return false;
	}

	public boolean loadPreferences()
	{
		try
		{
			FileInputStream file = new FileInputStream(new File(checkDirectory(GAMEDATA_DIRECTORY), PREFERENCES_FILE));
			ObjectInputStream data = new ObjectInputStream(file);
			
			GamePreferences.SET_CHARACTER_PARAMETERS(data.readInt());
			GamePreferences.SET_MUSIC_PARAMETERS(data.readBoolean());
			GamePreferences.SET_TIP_PARAMETERS(data.readBoolean());
			//FIXME DEBUG
			//GamePreferences.SET_DEBUG_PARAMETERS(data.readBoolean());
			GamePreferences.SET_DEBUG_PARAMETERS(false);
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
		//FIXME DEBUG
		GamePreferences.SET_DEBUG_PARAMETERS(false);
		GamePreferences.SET_SENSOR_PARAMETERS(true);
		return false;
	}

	public boolean savePreferences()
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(checkDirectory(GAMEDATA_DIRECTORY), PREFERENCES_FILE));
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
