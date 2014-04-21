package com.android.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.creation.data.Esqueleto;
import com.creation.data.Textura;
import com.game.data.Enemigo;
import com.game.select.TTipoLevel;
import com.lib.buffer.VertexArray;
import com.project.model.GameResources;

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

			Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " imported");
			return enemigo;
		}
		catch (ClassNotFoundException e)
		{
			Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " class not found");
		}
		catch (FileNotFoundException e)
		{
			Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " file not found");
		}
		catch (StreamCorruptedException e)
		{
			Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " sream corrupted");
		}
		catch (IOException e)
		{
			Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " ioexception");
		}

		Log.d(ASSETS_STORAGE_TAG, "Character " + idEnemigo + " Level " + nivel.toString() + " not imported");
		return null;
	}
}
