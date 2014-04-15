package com.android.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;

public class ExternalStorageManager
{
	private static final String EXTERNAL_STORAGE_TAG = "EXTERNAL";
	
	private static final String ROOT_DIRECTORY = "/DEFORMINVADERS";
	private static final String TEMP_FILE = "/FILE.png";

	private Context mContext;
	
	/* Constructora */

	public ExternalStorageManager(Context context)
	{
		mContext = context;
		
		comprobarDirectorio(getDirectorioRaiz());
	}

	/* Métodos Dirección de Ficheros y Directorios */

	private String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIRECTORY;
	}
	
	private String getFicheroTemp()
	{
		return getDirectorioRaiz() + TEMP_FILE;
	}
	
	private String getDirectorioExterno(String nombre)
	{
		return getDirectorioRaiz() + "/" + nombre + ".di";
	}

	/* Métodos Comprobación existencia y creación de Directorios */

	private boolean comprobarDirectorio(String file)
	{
		File dir = new File(file);
		if (!dir.exists())
		{
			dir.mkdirs();
			return false;
		}

		return dir.isDirectory();
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
		Log.d(EXTERNAL_STORAGE_TAG, "File SaveImage deleted");
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
			
			Log.d(EXTERNAL_STORAGE_TAG, "File SaveImage saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d(EXTERNAL_STORAGE_TAG, "File SaveImage file not found");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d(EXTERNAL_STORAGE_TAG, "File SaveImage ioexception");
			e.printStackTrace();
		}

		Log.d(EXTERNAL_STORAGE_TAG, "File SaveImage not saved");
		return false;
	}
	
	/* Método Temporal de Exportación de Personajes a Enemigos */
	
	public boolean exportarPersonaje(Personaje personaje)
	{
		try
		{
			FileOutputStream file = new FileOutputStream(new File(getDirectorioExterno(personaje.getNombre())));
			ObjectOutputStream data = new ObjectOutputStream(file);

			// Guardar Personajes
			data.writeObject(personaje.getEsqueleto());
			data.writeObject(personaje.getTextura());
			data.writeObject(personaje.getMovimientos().get(TTipoMovimiento.Run));
			data.writeObject(personaje.getNombre());

			data.flush();
			data.close();
			file.close();

			Log.d(EXTERNAL_STORAGE_TAG, "Character exported");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d(EXTERNAL_STORAGE_TAG, "Character file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d(EXTERNAL_STORAGE_TAG, "Character sream corrupted");
		}
		catch (IOException e)
		{
			Log.d(EXTERNAL_STORAGE_TAG, "Character ioexception");
		}

		Log.d(EXTERNAL_STORAGE_TAG, "Character not exported");
		return false;
	}
}
