package com.android.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class ExternalStorageManager
{
	private static final String ROOTDIRECTORY = "/DEFORMINVADERS";
	private static final String MUSICDIRECTORY = "/MUSIC";
	private static final String IMAGEDIRECTORY = "/IMAGE";
	private static final String TEMPDIRECTORY = "/TMP";
	private static final String MUSICEXTENSION = ".3gp";
	private static final String IMAGEEXTENSION = ".png";
	
	public ExternalStorageManager()
	{		
		comprobarDirectorioRaiz();
		comprobarDirectorioTemp();
	}
	
	/* Direcci�n de Ficheros y Directorios */
	
	private String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOTDIRECTORY;
	}
	
	private String getDirectorioAudio(String nombre)
	{
		return getDirectorioRaiz() + "/" + nombre.toUpperCase(Locale.getDefault()) + MUSICDIRECTORY;
	}
	
	private String getFicheroAudio(String nombre, String movimiento)
	{
		return getDirectorioAudio(nombre) + "/" + movimiento.toUpperCase(Locale.getDefault()) + MUSICEXTENSION;
	}
	
	private String getDirectorioImagen(String nombre)
	{
		return getDirectorioRaiz() + "/" + nombre.toUpperCase(Locale.getDefault()) + IMAGEDIRECTORY;
	}
	
	private String getFicheroImagen(String nombre)
	{
		return getDirectorioImagen(nombre) + "/" + nombre.toUpperCase(Locale.getDefault()) + IMAGEEXTENSION;
	}
	
	private String getDirectorioTemp()
	{
		return getDirectorioRaiz() + TEMPDIRECTORY;
	}
	
	private String getFicheroTemp(String nombre)
	{
		return getDirectorioTemp() + "/" + nombre.toUpperCase(Locale.getDefault()) + MUSICEXTENSION; 
	}
	
	/* Comprobar existencia y creaci�n de Directorios */
	
	private boolean comprobarDirectorio(String file)
	{
		File dir = new File(file);
		if(dir.isDirectory() && !dir.exists())
		{
			dir.mkdirs();
			return false;
		}
		
		return true;
	}
	
	private boolean comprobarDirectorioRaiz()
	{
		return comprobarDirectorio(getDirectorioRaiz());
	}
	
	private boolean comprobarDirectorioAudio(String nombre)
	{
		return comprobarDirectorio(getDirectorioAudio(nombre));
	}
	
	private boolean comprobarDirectorioImagen(String nombre)
	{
		return comprobarDirectorio(getDirectorioImagen(nombre));
	}
	
	private boolean comprobarDirectorioTemp()
	{
		return comprobarDirectorio(getDirectorioTemp());
	}
	
	/* N�mero de ficheros de Directorios */
	
	private int getNumFicherosDirectorio(String file)
	{
		File dir = new File(file);
		if(dir.isDirectory() && dir.exists())
		{
			return dir.list().length;
		}
		
		return 0;
	}
	
	public int getNumFicherosDirectorioAudio(String nombre)
	{
		return getNumFicherosDirectorio(getDirectorioAudio(nombre));
	}
	
	public int getNumFicherosDirectorioImagen(String nombre)
	{
		return getNumFicherosDirectorio(getDirectorioImagen(nombre));
	}
	
	public int getNumFicherosDirectorioTemp()
	{
		return getNumFicherosDirectorio(getDirectorioTemp());
	}
	
	/* Ficheros de Directorios */
	
	private String[] getFicherosDirectorio(String file)
	{
		File dir = new File(file);
		if(dir.exists())
		{
			String[] list = dir.list();
			
			for(int i = 0; i < list.length; i++)
			{
				String s = list[i];
				String ns = s.substring(0, s.lastIndexOf('.'));
				list[i] = ns;
			}			
			
			return list;
		}
		
		return null;
	}
	
	public String[] getFicherosDirectorioAudio(String nombre)
	{
		return getFicherosDirectorio(getDirectorioAudio(nombre));
	}
	
	public String[] getFicherosDirectorioImagen(String nombre)
	{
		return getFicherosDirectorio(getDirectorioImagen(nombre));
	}
	
	public String[] getFicherosDirectorioTemp()
	{
		return getFicherosDirectorio(getDirectorioTemp());
	}
	
	/* Lectura y Escritura */
	
	public String cargarAudio(String nombre, String movimiento)
	{
		comprobarDirectorioAudio(nombre);
		
		return getFicheroAudio(nombre, movimiento);
	}
	
	public boolean guardarAudio(String nombre, String movimiento)
	{
		comprobarDirectorioAudio(nombre);
		
		File audiotemp = new File(cargarAudioTemp(nombre));
		if(audiotemp.exists())
		{
			return audiotemp.renameTo(new File(getFicheroAudio(nombre, movimiento)));
		}
		
		return false;
	}	
	
	public File cargarImagen(String nombre)
	{
		comprobarDirectorioImagen(nombre);
		
		return new File(getFicheroImagen(nombre));
	}
	
	public boolean guardarImagen(Bitmap bitmap, String nombre)
	{
		comprobarDirectorioImagen(nombre);
	
		try
		{
			File file = new File(getFicheroImagen(nombre));
			FileOutputStream data = new FileOutputStream(file);
			
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, data);
			
			data.flush();
			data.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "File SaveImage file not found");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.d("TEST", "File SaveImage ioexception");
			e.printStackTrace();
		}
		
		return false;
	}
	
	/* Lectura y Escritura Temporal */
	
	public String cargarAudioTemp(String movimiento)
	{
		comprobarDirectorioTemp();
		
		return getFicheroTemp(movimiento);
	}
	
	public String guardarAudioTemp(String movimiento)
	{
		comprobarDirectorioTemp();
		
		return getFicheroTemp(movimiento);
	}
}
