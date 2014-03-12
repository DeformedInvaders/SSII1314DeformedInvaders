package com.game.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.Entidad;
import com.game.data.Personaje;
import com.game.data.TTipoEntidad;

public class GameOpenGLRenderer extends OpenGLRenderer
{	
	// Background
	private Background background;
	
	// Protagonista
	private Personaje personaje;
	
	// Enemigos
	private List<Entidad> listaEnemigos;
	private List<InstanciaEntidad> listaEnemigosDerrotados;
	private Queue<InstanciaEntidad> colaEnemigos;
	
	/* SECTION Constructura */
	
	public GameOpenGLRenderer(Context context, List<Entidad> lista, Queue<InstanciaEntidad> cola, Background b, Personaje p)
	{
        super(context);
        
        personaje = p;        
        background = b;
        
        listaEnemigos = lista;
        colaEnemigos = cola;
        
        listaEnemigosDerrotados = new ArrayList<InstanciaEntidad>();
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// BackGround
		seleccionarTexturaFondo(background.getIdTextura1(), background.getIdTextura2(), background.getIdTextura3());
			
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
		
		// Cola Enemigos
		Iterator<InstanciaEntidad> it = colaEnemigos.iterator();
		while(it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = listaEnemigos.get(instancia.getEntidad());
			instancia.dibujar(gl, this, entidad);
		}
		
		//Enemigos Derrotados
		it = listaEnemigosDerrotados.iterator();
		while(it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = listaEnemigos.get(instancia.getEntidad());
			instancia.dibujar(gl, this, entidad);
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
		Iterator<InstanciaEntidad> it = colaEnemigos.iterator();
		while(it.hasNext())
		{
			InstanciaEntidad instancia = it.next();
			Entidad entidad = listaEnemigos.get(instancia.getEntidad());
			instancia.avanzar(this, entidad);
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
		if(fondoFinalFijado)
		{
			return 1;
		}
		
		if(!colaEnemigos.isEmpty())
		{
			InstanciaEntidad instancia = colaEnemigos.peek();
			switch(personaje.colision(listaEnemigos.get(instancia.getEntidad()), instancia))
			{
				case 0:
					return 0;
				case 1:
					return 2;
				case 2:
					Entidad entidad = listaEnemigos.get(instancia.getEntidad());
					if(entidad.getTipo() == TTipoEntidad.Enemigo)
						instancia.setDerrotado();
					listaEnemigosDerrotados.add(colaEnemigos.poll());
					return 0;					
			}
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
