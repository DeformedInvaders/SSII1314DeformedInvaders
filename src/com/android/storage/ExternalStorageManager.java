package com.android.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class ExternalStorageManager
{
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
	
	public static final String getDirectorioExterno(String nombre)
	{
		return getDirectorioRaiz() + "/" + nombre + ".di";
	}

	private static String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOT_DIRECTORY;
	}
	
	private static String getFicheroTemp()
	{
		return getDirectorioRaiz() + TEMP_FILE;
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
		Log.d("EXTERNAL", "File SaveImage deleted");
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
			
			Log.d("EXTERNAL", "File SaveImage saved");
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("EXTERNAL", "File SaveImage file not found");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("EXTERNAL", "File SaveImage ioexception");
			e.printStackTrace();
		}

		Log.d("EXTERNAL", "File SaveImage not saved");
		return false;
	}
}
