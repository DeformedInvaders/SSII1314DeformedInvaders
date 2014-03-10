package com.game.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.Enemigo;
import com.game.data.Entidad;
import com.game.data.Obstaculo;
import com.game.data.Personaje;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{	
	// Protagonista
	private Personaje personaje;
	
	// Enemigos
	private List<Entidad> listaEnemigos;
	
	private int level;
	
	/* SECTION Constructura */
	
	public GameOpenGLRenderer(Context context, Personaje p, int l)
	{
        super(context);
        
        personaje = p;
        level = l;
        
        listaEnemigos = new ArrayList<Entidad>();
        
        listaEnemigos.add(new Obstaculo(R.drawable.obstacle_egypt, 0, 1200));
        
        listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt1, 0, 2000));
        listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt2, 1, 3500));
        listaEnemigos.add(new Enemigo(R.drawable.enemy_egypt3, 2, 4100));
        listaEnemigos.add(new Enemigo(R.drawable.boss_egypt, 3, 5000));
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// BackGround
		switch(level)
		{
			// FIXME
			case 0:
				seleccionarTexturaFondo(R.drawable.background_moon, R.drawable.background_moon, R.drawable.background_display);
			break;
			case 1:
				seleccionarTexturaFondo(R.drawable.background_newyork, R.drawable.background_newyork, R.drawable.background_display);
			break;
			case 2:
				seleccionarTexturaFondo(R.drawable.background_rome, R.drawable.background_rome, R.drawable.background_display);
			break;
			case 3:
				seleccionarTexturaFondo(R.drawable.background_egypt2, R.drawable.background_egypt3, R.drawable.background_egypt4);
			break;
			case 4:
				seleccionarTexturaFondo(R.drawable.background_stonehenge, R.drawable.background_stonehenge, R.drawable.background_display);
			break;
		}
		
		// Protagonista
		personaje.cargarTextura(gl, this);
		
		// Lista Enemigos
		Iterator<Entidad> it = listaEnemigos.iterator();
		while(it.hasNext())
		{
			it.next().cargarTextura(gl, this);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Escala del Juego
		gl.glPushMatrix();
		
			gl.glScalef(0.5f, 0.5f, 0.0f);
			
			// Protagonista
			personaje.dibujar(gl, this);
		
		gl.glPopMatrix();
		
		// Lista Enemigos
		Iterator<Entidad> it = listaEnemigos.iterator();
		while(it.hasNext())
		{
			it.next().dibujar(gl, this);
		}
	}
	
	/* SECTION Métodos abstractos de OpenGLRenderer */
	
	@Override
	protected boolean reiniciar()
	{
		return false;
	}
	
	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}
	
	/* SECTION Métodos de Modificación de Estado */

	public void reproducirAnimacion(boolean primerosCiclos)
	{
		// FIXME
		// Background
		desplazarFondo();
		
		// Personaje
		personaje.animar();
		personaje.avanzar(this, primerosCiclos);
		
		// Lista Enemigos
		Iterator<Entidad> it = listaEnemigos.iterator();
		while(it.hasNext())
		{
			it.next().avanzar(this, primerosCiclos);
		}
	}
	
	public void pararAnimacion()
	{
		personaje.reposo();
	}
	
	public void seleccionarRun() 
	{
		personaje.mover();
	}
	
	public void seleccionarJump() 
	{
		personaje.saltar();
	}
	
	public void seleccionarCrouch() 
	{
		personaje.agachar();
	}
	
	public void seleccionarAttack() 
	{
		personaje.atacar();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public int isJuegoFinalizado()
	{
		boolean colisionPersonaje = false;
		
		if(fondoFinalFijado)
		{
			return 1;
		}
	
		// Lista Enemigos
		Iterator<Entidad> it = listaEnemigos.iterator();
		while(it.hasNext() && !colisionPersonaje)
		{
			colisionPersonaje = personaje.colision(it.next());
		}
		
		if(colisionPersonaje)
		{
			return 2;
		}
		
		return 0;
	}
	
	/* SECTION Métodos de Guardado de Información */
	
	public void saveData()
	{
		// Personaje
		personaje.descargarTextura(this);
		
		// Lista Enemigos
		Iterator<Entidad> it = listaEnemigos.iterator();
		while(it.hasNext())
		{
			it.next().descargarTextura(this);
		}
	}
}
