package com.android.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.content.Context;

import com.creation.data.Esqueleto;
import com.creation.data.Movimientos;
import com.creation.data.Textura;
import com.game.data.Enemigo;
import com.game.data.Personaje;
import com.game.select.TTipoLevel;
import com.lib.buffer.VertexArray;
import com.main.model.GameResources;
import com.video.data.TTipoActores;

public class AssetsStorageManager
{
	private static final String ASSETS_STORAGE_TAG = "ASSETS";
	
	private Context mContext;
	
	/* Constructora */

	public AssetsStorageManager(Context context)
	{
		mContext = context;
	}
	
	public Enemigo importarEnemigo(TTipoLevel nivel, int indiceTextura, int idEnemigo)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ENEMIES_FILES(nivel, idEnemigo));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Enemigo enemigo = new Enemigo(indiceTextura, idEnemigo);
			enemigo.setEsqueleto((Esqueleto) data.readObject());
			enemigo.setTextura((Textura) data.readObject());
			enemigo.setMovimientos((List<VertexArray>) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " imported");
			return enemigo;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " class not found");
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " sream corrupted");
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " ioexception");
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " not imported");
		return null;
	}
	
	public Personaje importarActor(TTipoActores actor)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ACTORS_FILES(actor));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Personaje personaje = new Personaje(actor.ordinal());
			personaje.setEsqueleto((Esqueleto) data.readObject());
			personaje.setTextura((Textura) data.readObject());
			personaje.setMovimientos((Movimientos) data.readObject());
			personaje.setNombre((String) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " imported");
			return personaje;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " class not found");
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " sream corrupted");
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " ioexception");
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " not imported");
		return null;
	}
}
