package com.android.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.creation.data.Skeleton;
import com.creation.data.Movements;
import com.creation.data.TTypeMovement;
import com.creation.data.Texture;
import com.game.data.Character;
import com.main.model.GameResources;

public class ExternalStorageManager
{
	private static final String EXTERNAL_STORAGE_TAG = "EXTERNAL";
	
	private static final String ROOT_DIRECTORY = "/DEFORMEDINVADERS";
	private static final String TEMP_FILE = "/FILE";
	private static final String LOG_FILE = "/LOG";

	private Context mContext;
	
	/* Constructora */

	public ExternalStorageManager(Context context)
	{
		mContext = context;
		
		checkDirectory(getMainDirectory());
	}

	/* Métodos Dirección de Ficheros y Directorios */

	private static String getMainDirectory()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIRECTORY;
	}
	
	private String getTempFile()
	{
		return getMainDirectory() + TEMP_FILE + GameResources.EXTENSION_IMAGE_FILE;
	}
	
	private static String getLogFile()
	{
		return getMainDirectory() + LOG_FILE + GameResources.EXTENSION_TEXT_FILE;
	}

	/* Métodos Comprobación existencia y creación de Directorios */

	private static boolean checkDirectory(String file)
	{
		File dir = new File(file);
		if (!dir.exists())
		{
			dir.mkdirs();
			return false;
		}

		return dir.isDirectory();
	}
	
	/* Métodos Escritura Logcat */
	
	public static boolean writeLogcat(String tag, String text)
	{
		checkDirectory(getMainDirectory());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getLogFile()), true);
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(file);
	        outputStreamWriter.write(tag + " :: "+text+"\n");
	        outputStreamWriter.close();
	        
	        Log.d(tag, text);

			return true;
		}
		catch (IOException e)
		{
			
		}
		
		return false;
	}

	/* Métodos Lectura y Escritura Temporal */

	public File loadTempImage()
	{
		checkDirectory(getMainDirectory());

		return new File(getTempFile());
	}
	
	public boolean deleteTempImage()
	{
		checkDirectory(getMainDirectory());
		
		File file = new File(getTempFile());
		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage deleted");
		
		return file.delete();
	}
	
	public boolean saveTempImage(int imagen)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imagen);
		return saveTempImage(bitmap);
	}

	public boolean saveTempImage(Bitmap bitmap)
	{
		checkDirectory(getMainDirectory());

		try
		{
			File file = new File(getTempFile());
			FileOutputStream data = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.PNG, 85, data);

			data.flush();
			data.close();
			
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage file not found. "+e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage ioexception. "+e.getMessage());
			e.printStackTrace();
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage not saved");
		return false;
	}
	
	/* Método Temporal de Exportación de Personajes a Enemigos */
	
	public String[] getFileList()
	{
		checkDirectory(getMainDirectory());
		
		File file = new File(getMainDirectory());
		return file.list();
	}
	
	public String[] getFileList(String extension)
	{
		List<String> list = new ArrayList<String>();
		checkDirectory(getMainDirectory());
		
		File file = new File(getMainDirectory());
		String[] listFiles = file.list();
		
		for (int i = 0; i < listFiles.length; i++)
		{
			if (listFiles[i].endsWith(extension))
			{
				list.add(listFiles[i]);
			}
		}
		
		if (list.isEmpty())
		{
			return null;
		}
		
		String[] listFilter = new String[list.size()];
		int i = 0;
		Iterator<String> it = list.iterator();
		while (it.hasNext())
		{
			listFilter[i] = it.next();
			i++;
		}
		
		return listFilter;
	}
	
	public Character importCharacter(String name)
	{
		checkDirectory(getMainDirectory());
		
		try
		{
			FileInputStream file = new FileInputStream(new File(getMainDirectory() + "/" + name));
			ObjectInputStream data = new ObjectInputStream(file);

			Character character = new Character();
			character.setSkeleton((Skeleton) data.readObject());
			character.setTexture((Texture) data.readObject());
			character.setMovements((Movements) data.readObject());
			character.setName((String) data.readObject());
			
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " imported");
			return character;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + name + " not imported.");
		return null;
	}
	
	public boolean exportCharacter(Character character)
	{
		checkDirectory(getMainDirectory());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getMainDirectory() + "/" + character.getName() + ".cdi"));
			ObjectOutputStream data = new ObjectOutputStream(file);

			data.writeObject(character.getSkeleton());
			data.writeObject(character.getTexture());
			data.writeObject(character.getMovements());
			data.writeObject(character.getName());

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + character.getName() + " exported");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + character.getName() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + character.getName() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + character.getName() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + character.getName() + " not exported");
		return false;
	}
	
	public boolean exportEnemy(Character character)
	{
		checkDirectory(getMainDirectory());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getMainDirectory() + "/" + character.getName() + ".edi"));
			ObjectOutputStream data = new ObjectOutputStream(file);

			data.writeObject(character.getSkeleton());
			data.writeObject(character.getTexture());
			data.writeObject(character.getMovements().get(TTypeMovement.Run));

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + character.getName() + " exported");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + character.getName() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + character.getName() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + character.getName() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + character.getName() + " not exported");
		return false;
	}
}
