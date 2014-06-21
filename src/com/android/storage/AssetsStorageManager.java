package com.android.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.content.Context;

import com.creation.data.Skeleton;
import com.creation.data.Movements;
import com.creation.data.Texture;
import com.game.data.Enemy;
import com.game.data.Boss;
import com.game.data.Character;
import com.game.select.TTypeLevel;
import com.lib.buffer.VertexArray;
import com.main.model.GameResources;
import com.video.data.TTypeActors;

public class AssetsStorageManager
{
	private static final String ASSETS_STORAGE_TAG = "ASSETS";
	
	private Context mContext;
	
	/* Constructora */

	public AssetsStorageManager(Context context)
	{
		mContext = context;
	}
	
	public Enemy importarEnemigo(TTypeLevel nivel, int indiceTextura, int idEnemigo)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ENEMIES_FILES(nivel, idEnemigo));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Enemy enemigo = new Enemy(indiceTextura, idEnemigo);
			enemigo.setEsqueleto((Skeleton) data.readObject());
			enemigo.setTextura((Texture) data.readObject());
			enemigo.setMovimientos((List<VertexArray>) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " imported");
			return enemigo;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + idEnemigo + " Level " + nivel.toString() + " not imported.");
		return null;
	}
	
	public Boss importarJefe(TTypeLevel nivel, int indiceTextura, int idEnemigo)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_BOSS_FILES(nivel, idEnemigo));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Boss jefe = new Boss(indiceTextura, idEnemigo);
			jefe.setEsqueleto((Skeleton) data.readObject());
			jefe.setTextura((Texture) data.readObject());
			jefe.setMovimientos((List<VertexArray>) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " imported");
			return jefe;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + nivel.toString() + " not imported");
		return null;
	}
	
	public Character importarActor(TTypeActors actor)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ACTORS_FILES(actor));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Character personaje = new Character(actor.ordinal());
			personaje.setEsqueleto((Skeleton) data.readObject());
			personaje.setTextura((Texture) data.readObject());
			personaje.setMovimientos((Movements) data.readObject());
			personaje.setNombre((String) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " imported");
			return personaje;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " not imported.");
		return null;
	}
}
