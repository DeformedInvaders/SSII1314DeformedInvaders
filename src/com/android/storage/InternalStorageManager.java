package com.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.example.data.Personaje;

public class InternalStorageManager
{
	private static final String FILENAME = "SkeletonDataBase";
	private int seleccionado = -1;
	
	public String getFileName()
	{
		return FILENAME;
	}
	
	public void cargarPersonajes(FileInputStream file, List<Personaje> lista)
	{
		int numEsqueletos = 0;

		ObjectInputStream data;
		try
		{
			data = new ObjectInputStream(file);
			
			// Cargar Numero de Esqueletos
			numEsqueletos = data.readInt();
			
			// Cargar Esqueleto Seleccionado
			seleccionado = data.readInt();
			
			// Cargar Lista de Esqueletos
			for(int i = 0; i < numEsqueletos; i++)
			{
				lista.add((Personaje) data.readObject());
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
	
	public void guardarPersonajes(FileOutputStream file, List<Personaje> lista)
	{
		ObjectOutputStream data;
		try
		{
			data = new ObjectOutputStream(file);
			
			// Guardar Numero de Esqueletos
			data.writeInt(lista.size());
			
			// Guardar Esqueleto Seleccionado
			data.writeInt(seleccionado);
			
			// Guardar Lista de Esqueletos
			Iterator<Personaje> it = lista.iterator();
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
	
	public int getEsqueletoSeleccionado()
	{
		return seleccionado;
	}
}
