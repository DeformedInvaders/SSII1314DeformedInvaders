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

	private List<Level> listaNiveles;
	private List<String> listaNombres;
	private List<List<Entity>> listaEnemigos;
	private List<Boss> listaJefes;
	private List<Background> listaFondos;
	
	private AssetsStorageManager assetsManager;

	public LevelGenerator(Context context, AssetsStorageManager manager)
	{
		mContext = context;
		assetsManager = manager;
	}
	
	public void cargarEnemigos()
	{
		listaNiveles = new ArrayList<Level>();
		listaNombres = new ArrayList<String>();
		listaEnemigos = new ArrayList<List<Entity>>();
		listaJefes = new ArrayList<Boss>();
		listaFondos = new ArrayList<Background>();

		TTypeLevel[] niveles = TTypeLevel.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			listaEnemigos.add(new ArrayList<Entity>());

			crearNivel(niveles[i], listaNiveles, listaEnemigos.get(i), listaNombres, listaFondos);
		}
	}

	private int obtenerID(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	private void crearNivel(TTypeLevel nivel, List<Level> listaNiveles, List<Entity> listaEnemigos, List<String> listaNombres, List<Background> listaBackground)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GameResources.GET_FONT_PATH(nivel));
		int logroCompleted = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelCompleted));
		int logroPerfected = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelPerfected));
		int logroMastered = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTypeEndgame.LevelMastered));
		
		listaNiveles.add(new Level(nivel, nivel.getFondoDisplay(), logroCompleted, logroPerfected, logroMastered, nivel.getTitle(), nivel.getDescription(), nivel.getColor(), textFont, nivel.getMusica()));
		listaNombres.add(mContext.getString(nivel.getTitle()));
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_MISSILES; i++)
		{
			listaEnemigos.add(new Missil(obtenerID(GameResources.GET_ENEMIES(TTypeEntity.Missil, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OBSTACLES; i++)
		{
			listaEnemigos.add(new Obstacle(obtenerID(GameResources.GET_ENEMIES(TTypeEntity.Obstacle, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENEMIES; i++)
		{
			int id = obtenerID(GameResources.GET_ENEMIES(TTypeEntity.Enemy, nivel, i));
			listaEnemigos.add(assetsManager.importEnemy(nivel, id, i));
		}
		
		int id = obtenerID(GameResources.GET_ENEMIES(TTypeEntity.Boss, nivel, 0));
		listaJefes.add(assetsManager.importBoss(nivel, id, 0));
		
		int[] fondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS_LEVEL; i++)
		{
			fondos[i] = obtenerID(GameResources.GET_BACKGROUND(nivel, i));
		}
		
		int[] polaroids = new int[GamePreferences.NUM_TYPE_ENDGAME];
		TTypeEndgame[] finJuegos = TTypeEndgame.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENDGAME; i++)
		{
			polaroids[i] = obtenerID(GameResources.GET_POLAROID(nivel, finJuegos[i]));
		}
		
		listaBackground.add(new Background(nivel.getFondoSol(), fondos, polaroids));
	}

	private List<InstanceEntity> getColaEnemigos(int indice)
	{
		List<InstanceEntity> lista = new ArrayList<InstanceEntity>();
		List<Entity> enemigos = listaEnemigos.get(indice);
		
		float posXActual = GamePreferences.POS_ENEMIES_INICIO();
		while (posXActual < GamePreferences.POS_ENEMIES_FINAL())
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

	public InstanceLevel getInstanciaLevel(TTypeLevel level)
	{
		int indice = level.ordinal();
		return new InstanceLevel(level, listaNombres.get(indice), listaEnemigos.get(indice), listaJefes.get(indice), getColaEnemigos(indice), listaFondos.get(indice));
	}
	
	public Level getLevel(TTypeLevel level)
	{
		int indice = level.ordinal();
		return listaNiveles.get(indice);
	}

	public List<Level> getListaNiveles()
	{
		return listaNiveles;
	}
}
