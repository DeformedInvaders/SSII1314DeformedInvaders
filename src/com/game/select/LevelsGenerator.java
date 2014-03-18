package com.game.select;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import android.content.Context;

import com.game.data.Background;
import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.InstanciaEntidad;
import com.game.data.Level;
import com.game.data.Obstaculo;
import com.project.main.R;

public class LevelsGenerator
{
	private static final short TYPE_ENEMIGOS = 4;
	private static final short NUM_ENEMIGOS = 20;
	
	private static final float POS_ENEMIGOS_INICIO = 1280;
	private static final float POS_ENEMIGOS_FINAL = 12800;
	private static final float POS_BOSS = 12850;
	
	private Context mContext;
	
	private List<String> listaNiveles;
	private List<List<Entidad>> listaEnemigos;
	private List<Background> listaFondos;
	
	public LevelsGenerator(Context context)
	{
		mContext = context;
		
		listaNiveles = new ArrayList<String>();
		listaEnemigos = new ArrayList<List<Entidad>>();
		listaFondos = new ArrayList<Background>();
		
		for(int i = 0; i < TLevelTipo.values().length; i++)
		{
			listaEnemigos.add(new ArrayList<Entidad>());
			listaFondos.add(new Background());
		}
		
		crearNombreNiveles(listaNiveles);
		
		crearNivelLuna(listaEnemigos.get(0), listaFondos.get(0));
		crearNivelNewYork(listaEnemigos.get(1), listaFondos.get(1));
		crearNivelRome(listaEnemigos.get(2), listaFondos.get(2));
		crearNivelEgipto(listaEnemigos.get(3), listaFondos.get(3));
		crearNivelStonehenge(listaEnemigos.get(4), listaFondos.get(4));
	}
	
	private void crearNombreNiveles(List<String> lista)
	{
		lista.add(mContext.getString(R.string.title_level_section_moon));
		lista.add(mContext.getString(R.string.title_level_section_newyork));
		lista.add(mContext.getString(R.string.title_level_section_rome));
		lista.add(mContext.getString(R.string.title_level_section_egypt));
		lista.add(mContext.getString(R.string.title_level_section_stonehenge));
	}	
	
	private void crearNivelLuna(List<Entidad> lista, Background b)
	{
		// FIXME
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
		        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
				
		b.setBackground(R.drawable.background_moon, R.drawable.background_moon, R.drawable.background_display);
	}
	
	private void crearNivelNewYork(List<Entidad> lista, Background b)
	{
		// FIXME
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
		        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
				
		b.setBackground(R.drawable.background_newyork, R.drawable.background_newyork, R.drawable.background_display);
	}
	
	private void crearNivelRome(List<Entidad> lista, Background b)
	{
		// FIXME
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
		
		b.setBackground(R.drawable.background_rome, R.drawable.background_rome, R.drawable.background_display);
	}
	
	private void crearNivelEgipto(List<Entidad> lista, Background b) 
	{
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
		
		b.setBackground(R.drawable.background_egypt2, R.drawable.background_egypt3, R.drawable.background_egypt4);
	}
	
	private void crearNivelStonehenge(List<Entidad> lista, Background b)
	{
		// FIXME
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
				
		b.setBackground(R.drawable.background_stonehenge, R.drawable.background_stonehenge, R.drawable.background_display);
	}
	
	private List<Entidad> getListaEnemigos(int indice)
	{
		return listaEnemigos.get(indice);
	}
	
	private Background getFondo(int indice)
	{
		return listaFondos.get(indice);
	}
	
	private Queue<InstanciaEntidad> getColaEnemigos(int indice)
	{
		Queue<InstanciaEntidad> colaEnemigos = new PriorityQueue<InstanciaEntidad>();
	
		for(int i = 0; i < NUM_ENEMIGOS; i++)
		{
			int tipoEnemigo = (int) Math.floor(Math.random() * TYPE_ENEMIGOS);
			int posEnemigo = (int) Math.floor((Math.random() * POS_ENEMIGOS_FINAL) + POS_ENEMIGOS_INICIO);
			
			colaEnemigos.add(new InstanciaEntidad(tipoEnemigo, posEnemigo));
		}
		
		colaEnemigos.add(new InstanciaEntidad(TYPE_ENEMIGOS, POS_BOSS));
		
		return colaEnemigos;
	}
	
	public Level getLevel(int indice)
	{
		return new Level(indice, listaNiveles.get(indice), getListaEnemigos(indice), getColaEnemigos(indice), getFondo(indice));
	}
}
