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

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.Textura;
import com.game.data.Personaje;
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
		
		comprobarDirectorio(getDirectorioRaiz());
	}

	/* Métodos Dirección de Ficheros y Directorios */

	private static String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIRECTORY;
	}
	
	private String getFicheroTemp()
	{
		return getDirectorioRaiz() + TEMP_FILE + GameResources.EXTENSION_IMAGE_FILE;
	}
	
	private static String getFicheroLog()
	{
		return getDirectorioRaiz() + LOG_FILE + GameResources.EXTENSION_TEXT_FILE;
	}

	/* Métodos Comprobación existencia y creación de Directorios */

	private static boolean comprobarDirectorio(String file)
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
		comprobarDirectorio(getDirectorioRaiz());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getFicheroLog()), true);
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

	public File cargarImagenTemp()
	{
		comprobarDirectorio(getDirectorioRaiz());

		return new File(getFicheroTemp());
	}
	
	public boolean eliminarImagenTemp()
	{
		comprobarDirectorio(getDirectorioRaiz());
		
		File file = new File(getFicheroTemp());
		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage deleted");
		
		return file.delete();
	}
	
	public boolean guardarImagenTemp(int imagen)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), imagen);
		return guardarImagenTemp(bitmap);
	}

	public boolean guardarImagenTemp(Bitmap bitmap)
	{
		comprobarDirectorio(getDirectorioRaiz());

		try
		{
			File file = new File(getFicheroTemp());
			FileOutputStream data = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.PNG, 85, data);

			data.flush();
			data.close();
			
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage file not found");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage ioexception");
			e.printStackTrace();
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "File SaveImage not saved");
		return false;
	}
	
	/* Método Temporal de Exportación de Personajes a Enemigos */
	
	public String[] listaFicheros()
	{
		comprobarDirectorio(getDirectorioRaiz());
		
		File file = new File(getDirectorioRaiz());
		return file.list();
	}
	
	public String[] listaFicheros(String extension)
	{
		List<String> lista = new ArrayList<String>();
		comprobarDirectorio(getDirectorioRaiz());
		
		File file = new File(getDirectorioRaiz());
		String[] listFiles = file.list();
		
		for (int i = 0; i < listFiles.length; i++)
		{
			if (listFiles[i].endsWith(extension))
			{
				lista.add(listFiles[i]);
			}
		}
		
		if (lista.isEmpty())
		{
			return null;
		}
		
		String[] listFilter = new String[lista.size()];
		int i = 0;
		Iterator<String> it = lista.iterator();
		while (it.hasNext())
		{
			listFilter[i] = it.next();
			i++;
		}
		
		return listFilter;
	}
	
	public Personaje importarPersonaje(String nombre)
	{
		comprobarDirectorio(getDirectorioRaiz());
		
		try
		{
			FileInputStream file = new FileInputStream(new File(getDirectorioRaiz() + "/" + nombre));
			ObjectInputStream data = new ObjectInputStream(file);

			Personaje personaje = new Personaje();
			personaje.setEsqueleto((Esqueleto) data.readObject());
			personaje.setTextura((Textura) data.readObject());
			personaje.setMovimientos((Movimientos) data.readObject());
			personaje.setNombre((String) data.readObject());
			
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " imported");
			return personaje;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " class not found");
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " sream corrupted");
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " ioexception");
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + nombre + " not imported");
		return null;
	}
	
	public boolean exportarPersonaje(Personaje personaje)
	{
		comprobarDirectorio(getDirectorioRaiz());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getDirectorioRaiz() + "/" + personaje.getNombre() + ".cdi"));
			ObjectOutputStream data = new ObjectOutputStream(file);

			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos());
			data.writeObject(personaje.getNombre());

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + personaje.getNombre() + " exported");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + personaje.getNombre() + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + personaje.getNombre() + " sream corrupted");
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + personaje.getNombre() + " ioexception");
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Character " + personaje.getNombre() + " not exported");
		return false;
	}
	
	public boolean exportarEnemigo(Personaje personaje)
	{
		comprobarDirectorio(getDirectorioRaiz());
		
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getDirectorioRaiz() + "/" + personaje.getNombre() + ".edi"));
			ObjectOutputStream data = new ObjectOutputStream(file);

			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos().get(TTipoMovimiento.Run));

			data.flush();
			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + personaje.getNombre() + " exported");
			return true;
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + personaje.getNombre() + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + personaje.getNombre() + " sream corrupted");
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + personaje.getNombre() + " ioexception");
		}

		ExternalStorageManager.writeLogcat(EXTERNAL_STORAGE_TAG, "Enemy " + personaje.getNombre() + " not exported");
		return false;
	}
}
