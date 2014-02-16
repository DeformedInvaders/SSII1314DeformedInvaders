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
	private static final String ROOTDIRECTORY = "/DeformInvaders";
	private static final String MUSICDIRECTORY = "/Music";
	private static final String IMAGEDICRETORY = "/Image";
	
	public ExternalStorageManager()
	{		
		comprobarDirectorio(getDirectorioRaiz());
		comprobarDirectorio(getDirectorioMusica());
		comprobarDirectorio(getDirectorioImagen());
	}
	
	private String getDirectorioRaiz()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOTDIRECTORY;
	}
	
	private String getDirectorioMusica()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOTDIRECTORY + MUSICDIRECTORY;
	}
	
	public String getDirectorioMusica(String name)
	{
		return getDirectorioMusica(name, "3gp");
	}
	
	public String getDirectorioMusica(String name, String extension)
	{
		return getDirectorioMusica() + "/" + name.toUpperCase(Locale.getDefault()) + "." + extension;
	}
	
	private String getDirectorioImagen()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath() + ROOTDIRECTORY + IMAGEDICRETORY;
	}
	
	public String getDirectorioImagen(String name)
	{
		return getDirectorioImagen(name, "png");
	}
	
	public String getDirectorioImagen(String name, String extension)
	{
		return getDirectorioImagen() + "/" + name.toUpperCase(Locale.getDefault()) + "." + extension;
	}
	
	private boolean comprobarDirectorio(String directory)
	{
		File dir = new File(directory);
		if(!dir.exists())
		{
			dir.mkdirs();
			return false;
		}
		
		return true;
	}
	
	public String[] getContenidoDirectorioImagen()
	{
		File dir = new File(getDirectorioImagen());
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
	
	public File cargarImagen(String nombre)
	{
		comprobarDirectorio(getDirectorioImagen());
		
		return new File(getDirectorioImagen(nombre));
	}
	
	public boolean guardarImagen(Bitmap bitmap, String nombre)
	{
		comprobarDirectorio(getDirectorioImagen());
	
		try
		{
			File file = new File(getDirectorioImagen(nombre));
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
}
