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
	
	/* SECTION Constructora */
	
	public ExternalStorageManager()
	{		
		comprobarDirectorioRaiz();
		comprobarDirectorioTemp();
	}
	
	/* SECTION Métodos Dirección de Ficheros y Directorios */
	
	private String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOTDIRECTORY;
	}
	
	private String getDirectorioPersonaje(String nombre)
	{
		return getDirectorioRaiz() + "/" + nombre.toUpperCase(Locale.getDefault());
	}
	
	private String getDirectorioAudio(String nombre)
	{
		return getDirectorioPersonaje(nombre) + MUSICDIRECTORY;
	}
	
	private String getFicheroAudio(String nombre, String movimiento)
	{
		return getDirectorioAudio(nombre) + "/" + movimiento.toUpperCase(Locale.getDefault()) + MUSICEXTENSION;
	}
	
	private String getDirectorioImagen(String nombre)
	{
		return getDirectorioPersonaje(nombre) + IMAGEDIRECTORY;
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
	
	/* SECTION Métodos Comprobación existencia y creación de Directorios */
	
	private boolean comprobarDirectorio(String file)
	{
		File dir = new File(file);
		if(!dir.exists())
		{
			dir.mkdirs();
			return false;
		}
		
		return dir.isDirectory();
	}
	
	private void comprobarDirectorioRaiz()
	{
		comprobarDirectorio(getDirectorioRaiz());
	}
	
	private void comprobarDirectorioPersonaje(String nombre)
	{
		comprobarDirectorio(getDirectorioPersonaje(nombre));
		comprobarDirectorio(getDirectorioImagen(nombre)); 
		comprobarDirectorio(getDirectorioAudio(nombre));
	}
	
	private void comprobarDirectorioTemp()
	{
		comprobarDirectorio(getDirectorioTemp());
	}
	
	/* SECTION Métodos Número de ficheros de Directorios */
	
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
	
	/* SECTION Métodos Ficheros de Directorios */
	
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
	
	/* SECTION Métodos de Comprobación de Existencia de Ficheros */
	
	private boolean existeFichero(String nombre)
	{
		File file = new File(nombre);
		return file.exists();
	}
	
	public boolean existeFicheroAudio(String nombre, String movimiento)
	{
		return existeFichero(getFicheroAudio(nombre, movimiento));
	}
	
	public boolean existeFicheroImagen(String nombre)
	{
		return existeFichero(getFicheroImagen(nombre));
	}
	
	public boolean existeFicheroTemp(String nombre)
	{
		return existeFichero(getFicheroTemp(nombre));
	}
	
	/* SECTION Métodos de Eliminación de Directorios */
	
	private void eliminarFichero(String nombre)
	{
		File file = new File(nombre);
		if(file.isDirectory())
		{
			String[] list = file.list();
			for(int i = 0; i < list.length; i++)
			{
				eliminarFichero(list[i]);
			}
			
			file.delete();
		}
		else if(file.isFile())
		{
			file.delete();
		}
	}
	
	public void eliminarDirectorioPersonaje(String nombre)
	{
		eliminarFichero(getDirectorioPersonaje(nombre));
	}
	
	/* SECTION Métodos Lectura y Escritura */
	
	public String cargarAudio(String nombre, String movimiento)
	{
		comprobarDirectorioPersonaje(nombre);
		
		return getFicheroAudio(nombre, movimiento);
	}
	
	public boolean guardarAudio(String nombre, String movimiento)
	{
		comprobarDirectorioPersonaje(nombre);
		
		if(existeFicheroTemp(movimiento))
		{
			File audiotemp = new File(cargarAudioTemp(movimiento));
			if(audiotemp.exists())
			{
				return audiotemp.renameTo(new File(getFicheroAudio(nombre, movimiento)));
			}
		}
		
		return false;
	}	
	
	public File cargarImagen(String nombre)
	{
		comprobarDirectorioPersonaje(nombre);
		
		return new File(getFicheroImagen(nombre));
	}
	
	public boolean guardarImagen(Bitmap bitmap, String nombre)
	{
		comprobarDirectorioPersonaje(nombre);
	
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
	
	/* SECTION Métodos Lectura y Escritura Temporal */
	
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
	
	public boolean eliminarAudioTemp(String movimiento)
	{
		if(existeFicheroTemp(movimiento))
		{
			File file = new File(getFicheroTemp(movimiento));
			file.delete();
			
			return true;
		}
		
		return false;
	}
}
