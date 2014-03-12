package com.game.game;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.Obstaculo;
import com.game.select.TLevelTipo;
import com.project.main.R;

public class LevelsGenerator {

	private static final short NUM_ENEMIGOS = 20;
	private List<List<Entidad>> listaEnemigos;
	private List<Background> listaFondos;
	
	public LevelsGenerator()
	{
		listaEnemigos = new ArrayList<List<Entidad>>();
		listaFondos = new ArrayList<Background>();
		for(int i = 0; i < TLevelTipo.values().length; i++)
		{
			listaEnemigos.add(new ArrayList<Entidad>());
			listaFondos.add(new Background());
		}
		
		crearLuna(listaEnemigos.get(0), listaFondos.get(0));
		crearNewYork(listaEnemigos.get(1), listaFondos.get(1));
		crearRome(listaEnemigos.get(2), listaFondos.get(2));
		crearEgipto(listaEnemigos.get(3), listaFondos.get(3));
		crearStonehenge(listaEnemigos.get(4), listaFondos.get(4));
	}
	
	private void crearLuna(List<Entidad> lista, Background b)
	{
		b.setBackground(R.drawable.background_moon, R.drawable.background_moon, R.drawable.background_display);
	}
	
	private void crearNewYork(List<Entidad> lista, Background b)
	{
		b.setBackground(R.drawable.background_newyork, R.drawable.background_newyork, R.drawable.background_display);
	}
	
	private void crearRome(List<Entidad> lista, Background b)
	{
		b.setBackground(R.drawable.background_rome, R.drawable.background_rome, R.drawable.background_display);
	}
	
	private void crearEgipto(List<Entidad> lista, Background b) 
	{
		lista.add(new Obstaculo(R.drawable.obstacle_egypt, 0));
        
		lista.add(new Enemigo(R.drawable.enemy_egypt1, 0));
		lista.add(new Enemigo(R.drawable.enemy_egypt2, 1));
		lista.add(new Enemigo(R.drawable.enemy_egypt3, 2));
		lista.add(new Enemigo(R.drawable.boss_egypt, 3));
		
		b.setBackground(R.drawable.background_egypt2, R.drawable.background_egypt3, R.drawable.background_egypt4);
	}
	
	private void crearStonehenge(List<Entidad> lista, Background b)
	{
		b.setBackground(R.drawable.background_stonehenge, R.drawable.background_stonehenge, R.drawable.background_display);
	}
	
	public List<Entidad> getListaEnemigos(int indice)
	{
		return listaEnemigos.get(indice);
	}
	
	public Background getFondo(int indice)
	{
		return listaFondos.get(indice);
	}
	
	public Queue<InstanciaEntidad> getColaEnemigos(int indice)
	{
		Queue<InstanciaEntidad> colaEnemigos = new PriorityQueue<InstanciaEntidad>();
	
		for(int i = 0; i<NUM_ENEMIGOS; i++)
		{
			int tipoEnemigo = (int) Math.floor(Math.random()*4);
			
			int posEnemigo = (int) Math.floor((Math.random()*12800)+1280);
			
			colaEnemigos.add(new InstanciaEntidad(tipoEnemigo, posEnemigo));
		}
		
		colaEnemigos.add(new InstanciaEntidad(4, 12850));
		
		return colaEnemigos;
	}
}
