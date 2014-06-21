package com.game.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;

import com.android.storage.AssetsStorageManager;
import com.game.data.Background;
import com.game.data.Entity;
import com.game.data.InstanceEntity;
import com.game.data.InstanceLevel;
import com.game.data.Boss;
import com.game.data.Missil;
import com.game.data.Level;
import com.game.data.Obstacle;
import com.game.data.TTypeEntity;
import com.game.game.TTypeEndgame;
import com.main.model.GamePreferences;
import com.main.model.GameResources;

public class LevelGenerator
{
	private Context mContext;

	private List<Level> levelList;
	private List<String> nameList;
	private List<List<Entity>> enemiesList;
	private List<Boss> bossList;
	private List<Background> backgroundList;
	
	private AssetsStorageManager assetsManager;

	public LevelGenerator(Context context, AssetsStorageManager manager)
	{
		mContext = context;
		assetsManager = manager;
	}
	
	public void loadLevels()
	{
		levelList = new ArrayList<Level>();
		nameList = new ArrayList<String>();
		enemiesList = new ArrayList<List<Entity>>();
		bossList = new ArrayList<Boss>();
		backgroundList = new ArrayList<Background>();

		TTypeLevel[] niveles = TTypeLevel.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			enemiesList.add(new ArrayList<Entity>());

			loadLevel(niveles[i], levelList, enemiesList.get(i), nameList, backgroundList);
		}
	}

	private int getResourceId(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	private void loadLevel(TTypeLevel nivel, List<Level> levelList, List<Entity> enemiesList, List<String> nameList, List<Background> backgroundList)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GameResources.GET_FONT_PATH(nivel));
		int achievementCompleted = getResourceId(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelCompleted));
		int achievementPerfected = getResourceId(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelPerfected));
		int achievementMastered = getResourceId(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelMastered));
		
		levelList.add(new Level(nivel, nivel.getDisplayBackground(), achievementCompleted, achievementPerfected, achievementMastered, nivel.getTitle(), nivel.getDescription(), nivel.getColor(), textFont, nivel.getMusic()));
		nameList.add(mContext.getString(nivel.getTitle()));
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_MISSILES; i++)
		{
			enemiesList.add(new Missil(getResourceId(GameResources.GET_ENEMIES(TTypeEntity.Missil, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OBSTACLES; i++)
		{
			enemiesList.add(new Obstacle(getResourceId(GameResources.GET_ENEMIES(TTypeEntity.Obstacle, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENEMIES; i++)
		{
			int id = getResourceId(GameResources.GET_ENEMIES(TTypeEntity.Enemy, nivel, i));
			enemiesList.add(assetsManager.importEnemy(nivel, id, i));
		}
		
		int id = getResourceId(GameResources.GET_ENEMIES(TTypeEntity.Boss, nivel, 0));
		bossList.add(assetsManager.importBoss(nivel, id, 0));
		
		int[] background = new int[GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL; i++)
		{
			background[i] = getResourceId(GameResources.GET_BACKGROUND(nivel, i));
		}
		
		int[] polaroids = new int[GamePreferences.NUM_TYPE_ENDGAME];
		TTypeEndgame[] typeEndgame = TTypeEndgame.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENDGAME; i++)
		{
			polaroids[i] = getResourceId(GameResources.GET_POLAROID(nivel, typeEndgame[i]));
		}
		
		backgroundList.add(new Background(nivel.getSunBackground(), background, polaroids));
	}

	private List<InstanceEntity> buildEnemiesList(int index)
	{
		List<InstanceEntity> lista = new ArrayList<InstanceEntity>();
		List<Entity> enemigos = enemiesList.get(index);
		
		float posXActual = GamePreferences.POS_ENEMIES_BEGIN();
		while (posXActual < GamePreferences.POS_ENEMIES_END())
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_OPPONENTS);
			float posXEnemigo = posXActual + (float) (Math.random() * GamePreferences.DISTANCE_BETWEEN_ENEMIES() / 2.0f);
			float posYEnemigo = GamePreferences.DISTANCE_ENEMY_GROUND();
			TTypeEntity tipoEntidad = enemigos.get(tipoEnemigo).getType();
					
			if (tipoEntidad == TTypeEntity.Missil) posYEnemigo = GamePreferences.DISTANCE_ENEMY_AIR();
					
			lista.add(new InstanceEntity(tipoEnemigo, tipoEntidad, posXEnemigo, posYEnemigo));
			posXActual = posXEnemigo + GamePreferences.DISTANCE_BETWEEN_ENEMIES();
		}

		return lista;
	}

	public InstanceLevel getLevelInstance(TTypeLevel level)
	{
		int index = level.ordinal();
		return new InstanceLevel(level, nameList.get(index), enemiesList.get(index), bossList.get(index), buildEnemiesList(index), backgroundList.get(index));
	}
	
	public Level getLevel(TTypeLevel level)
	{
		int index = level.ordinal();
		return levelList.get(index);
	}

	public List<Level> getLevelList()
	{
		return levelList;
	}
}
