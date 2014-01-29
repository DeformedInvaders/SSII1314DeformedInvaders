package com.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.example.data.Esqueleto;

public class InternalStorageManager
{
	private static final String FILENAME = "SkeletonDataBase";
	
	public String getFileName()
	{
		return FILENAME;
	}
	
	public void cargarEsqueleto(FileInputStream file, List<Esqueleto> lista)
	{
		int numEsqueletos = 0;

		ObjectInputStream data;
		try
		{
			data = new ObjectInputStream(file);
			
			// Cargar Numero de Esqueletos
			numEsqueletos = data.readInt();
			
			// Cargar Lista de Esqueletos
			for(int i = 0; i < numEsqueletos; i++)
			{
				Esqueleto e = (Esqueleto) data.readObject();
				lista.add(e);
			}
			
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
	}
	
	public void guardarEsqueleto(FileOutputStream file, List<Esqueleto> lista)
	{
		ObjectOutputStream data;
		try
		{
			data = new ObjectOutputStream(file);
			
			// Guardar Numero de Esqueletos
			data.writeInt(lista.size());
			
			// Guardar Lista de Esqueletos
			Iterator<Esqueleto> it = lista.iterator();
			while(it.hasNext())
			{
				data.writeObject(it.next());
			}
			
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
