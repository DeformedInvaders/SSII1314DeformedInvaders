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
	
	public Enemy importEnemy(TTypeLevel level, int textureId, int id)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ENEMIES_FILES(level, id));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Enemy enemy = new Enemy(textureId, id);
			enemy.setSkeleton((Skeleton) data.readObject());
			enemy.setTexture((Texture) data.readObject());
			enemy.setMovements((List<VertexArray>) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " imported");
			return enemy;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Enemy " + id + " Level " + level.toString() + " not imported.");
		return null;
	}
	
	public Boss importBoss(TTypeLevel level, int textureId, int id)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_BOSS_FILES(level, id));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Boss boss = new Boss(textureId, id);
			boss.setSkeleton((Skeleton) data.readObject());
			boss.setTexture((Texture) data.readObject());
			boss.setMovements((List<VertexArray>) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " imported");
			return boss;
		}
		catch (ClassNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " class not found. "+e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " file not found. "+e.getMessage());
		}
		catch (StreamCorruptedException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " sream corrupted. "+e.getMessage());
		}
		catch (IOException e)
		{
			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " ioexception. "+e.getMessage());
		}

		ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Boss Level " + level.toString() + " not imported");
		return null;
	}
	
	public Character importActor(TTypeActors actor)
	{
		try
		{
			InputStream file = mContext.getAssets().open(GameResources.GET_ACTORS_FILES(actor));
			ObjectInputStream data = new ObjectInputStream(file);

			// Guardar Personajes
			Character character = new Character(actor.ordinal());
			character.setSkeleton((Skeleton) data.readObject());
			character.setTexture((Texture) data.readObject());
			character.setMovements((Movements) data.readObject());
			character.setName((String) data.readObject());

			data.close();
			file.close();

			ExternalStorageManager.writeLogcat(ASSETS_STORAGE_TAG, "Actor " + actor.toString() + " imported");
			return character;
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
