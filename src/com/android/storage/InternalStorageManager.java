package com.android.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.project.data.Personaje;

public class InternalStorageManager
{
	private static final String CHARACTERSFILENAME = "CharactersDataBase";
	private static final String CHOSENCHARACTERFILENAME = "ChosenCharacterFile";
	
	private String getCharactersFileName()
	{
		return CHARACTERSFILENAME;
	}
	
	private String getChosenCharacterFileName()
	{
		return CHOSENCHARACTERFILENAME;
	}
	
	public int cargarSeleccionado(Activity activity)
	{
		int seleccionado = -1;
		
		try
		{
			FileInputStream file = activity.openFileInput(getChosenCharacterFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Personaje Seleccionado
			seleccionado = data.readInt();
			
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION");
			e.printStackTrace();
		}
		
		return seleccionado;
	}
	
	public void cargarPersonajes(Activity activity, List<Personaje> lista)
	{
		try
		{
			FileInputStream file = activity.openFileInput(getCharactersFileName());
			ObjectInputStream data = new ObjectInputStream(file);
			
			// Cargar Numero de Personajes
			int numPersonajes = data.readInt();
			
			// Cargar Lista de Personajes
			for(int i = 0; i < numPersonajes; i++)
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
	
	public void guardarSeleccionado(Activity activity, int seleccionado)
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getChosenCharacterFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Personaje Seleccionado
			data.writeInt(seleccionado);
			
			data.close();
			file.close();
		}
		catch (IOException e)
		{
			Log.d("TEST", "IO EXCEPTION");
			e.printStackTrace();
		}
	}
	
	public void guardarPersonajes(Activity activity, List<Personaje> lista)
	{
		try
		{
			FileOutputStream file = activity.openFileOutput(getCharactersFileName(), Context.MODE_PRIVATE);
			ObjectOutputStream data = new ObjectOutputStream(file);
			
			// Guardar Numero de Personajes
			data.writeInt(lista.size());
			
			// Guardar Lista de Personajes
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
}
