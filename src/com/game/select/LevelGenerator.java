package com.game.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;

import com.android.storage.AssetsStorageManager;
import com.game.data.Background;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.InstanciaNivel;
import com.game.data.Misil;
import com.game.data.Nivel;
import com.game.data.Obstaculo;
import com.game.data.TTipoEntidad;
import com.game.game.TTipoEndgame;
import com.main.model.GamePreferences;
import com.main.model.GameResources;

public class LevelGenerator
{
	private Context mContext;

	private List<Nivel> listaNiveles;
	private List<String> listaNombres;
	private List<List<Entidad>> listaEnemigos;
	private List<Background> listaFondos;
	
	AssetsStorageManager manager;

	public LevelGenerator(Context context)
	{
		mContext = context;
		
		manager = new AssetsStorageManager(mContext);
	}
	
	public void cargarEnemigos()
	{
		listaNiveles = new ArrayList<Nivel>();
		listaNombres = new ArrayList<String>();
		listaEnemigos = new ArrayList<List<Entidad>>();
		listaFondos = new ArrayList<Background>();

		TTipoLevel[] niveles = TTipoLevel.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_LEVELS; i++)
		{
			listaEnemigos.add(new ArrayList<Entidad>());

			crearNivel(niveles[i], listaNiveles, listaEnemigos.get(i), listaNombres, listaFondos);
		}
	}

	private int obtenerID(String id)
	{
		return mContext.getResources().getIdentifier(id, GameResources.RESOURCE_DRAWABLE, mContext.getPackageName());
	}
	
	private void crearNivel(TTipoLevel nivel, List<Nivel> listaNiveles, List<Entidad> listaEnemigos, List<String> listaNombres, List<Background> listaBackground)
	{
		Typeface textFont = Typeface.createFromAsset(mContext.getAssets(), GameResources.GET_FONT_PATH(nivel));
		int logroCompletado = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTipoEndgame.LevelCompleted));
		int logroPerfecto = obtenerID(GameResources.GET_ACHIEVEMENTS(nivel, TTipoEndgame.LevelPerfected));
		
		listaNiveles.add(new Nivel(nivel, nivel.getFondoDisplay(), logroCompletado, logroPerfecto, nivel.getTitle(), nivel.getDescription(), nivel.getColor(), textFont, nivel.getMusica()));
		listaNombres.add(mContext.getString(nivel.getTitle()));
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_MISSILES; i++)
		{
			listaEnemigos.add(new Misil(obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Misil, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_OBSTACLES; i++)
		{
			listaEnemigos.add(new Obstaculo(obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Obstaculo, nivel, i)), i));
		}
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENEMIES; i++)
		{
			int id = obtenerID(GameResources.GET_ENEMIES(TTipoEntidad.Enemigo, nivel, i));
			listaEnemigos.add(manager.importarEnemigo(nivel, id, i));
		}
		
		int[] fondos = new int[GamePreferences.NUM_TYPE_BACKGROUNDS];
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			fondos[i] = obtenerID(GameResources.GET_BACKGROUND(nivel, i));
		}
		
		int[] polaroids = new int[GamePreferences.NUM_TYPE_ENDGAME];
		TTipoEndgame[] finJuegos = TTipoEndgame.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_ENDGAME; i++)
		{
			polaroids[i] = obtenerID(GameResources.GET_POLAROID(nivel, finJuegos[i]));
		}
		
		listaBackground.add(new Background(nivel.getFondoSol(), fondos, polaroids));
	}

	private List<InstanciaEntidad> getColaEnemigos(int indice)
	{
		List<InstanciaEntidad> listaEnemigos = new ArrayList<InstanciaEntidad>();

		float posXActual = GamePreferences.POS_ENEMIES_INICIO();
		while (posXActual < GamePreferences.POS_ENEMIES_FINAL())
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_OPPONENTS);
			float posXEnemigo = posXActual + (float) (Math.random() * GamePreferences.DISTANCE_BETWEEN_ENEMY());
			float posYEnemigo = GamePreferences.DISTANCE_ENEMY_GROUND();
			
			if (tipoEnemigo == 0) posYEnemigo = GamePreferences.DISTANCE_ENEMY_AIR();
					
			listaEnemigos.add(new InstanciaEntidad(tipoEnemigo, posXEnemigo, posYEnemigo));
			posXActual = posXEnemigo + GamePreferences.DISTANCE_BETWEEN_ENEMY();
		}

		return listaEnemigos;
	}

	public InstanciaNivel getInstanciaLevel(TTipoLevel level)
	{
		int indice = level.ordinal();
		return new InstanciaNivel(level, listaNombres.get(indice), listaEnemigos.get(indice), getColaEnemigos(indice), listaFondos.get(indice));
	}
	
	public Nivel getLevel(TTipoLevel level)
	{
		int indice = level.ordinal();
		return listaNiveles.get(indice);
	}

	public List<Nivel> getListaNiveles()
	{
		return listaNiveles;
	}
}
