package com.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.example.data.Esqueleto;

import android.util.Log;

public class InternalStorageManager
{
	private static final String FILENAME = "Esqueleto";
	
	public String getFileName()
	{
		return FILENAME;
	}
	
	public Esqueleto cargarEsqueleto(FileInputStream file)
	{
		Esqueleto esqueleto = null;

		ObjectInputStream data;
		try
		{
			data = new ObjectInputStream(file);
			esqueleto = (Esqueleto) data.readObject();
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			Log.d("TEST", "CLASS NOT FOUND EXCEPTION");
			e.printStackTrace();
		}
		
		return esqueleto;
	}
	
	public void guardarEsqueleto(FileOutputStream file, Esqueleto esqueleto)
	{
		ObjectOutputStream data;
		try
		{
			data = new ObjectOutputStream(file);
			data.writeObject(esqueleto);
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION");
			e.printStackTrace();
		}
	}
}
