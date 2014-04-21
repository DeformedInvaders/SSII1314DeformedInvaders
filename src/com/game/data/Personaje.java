package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.TTipoSticker;
import com.game.game.TEstadoColision;
import com.lib.math.Circle;
import com.lib.math.Intersector;
import com.project.main.R;
import com.project.model.GamePreferences;

public class Personaje extends Malla
{
	private Movimientos movimientos;

	private TTipoMovimiento estado;
	
	private int vidas;
	private boolean burbuja;

	/* Constructora */

	public Personaje()
	{
		tipoEntidad = TTipoEntidad.Personaje;
		idEntidad = 0;
		texturaEntidad = -1;
		
		posicionX = 0.0f;
		posicionY = 0.0f;
		
		vidas = GamePreferences.MAX_LIVES;
		burbuja = false;		
	}

	/* Métodos abstractos de Entidad */
	
	private int indiceBurbuja(int vidas)
	{
		switch (vidas)
		{
			case 0:
				return R.drawable.lives_bubble_1;
			case 1:
				return R.drawable.lives_bubble_2;
			default:
				return R.drawable.lives_bubble_3;
		}
	}

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		super.cargarTextura(gl, renderer, context);
		
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
		{
			renderer.cargarTexturaRectangulo(gl, GamePreferences.DISTANCE_CHARACTER_WIDTH(), GamePreferences.DISTANCE_CHARACTER_WIDTH(), indiceBurbuja(i), TTipoEntidad.Burbuja, i, TTipoSticker.Nada);
		}
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		super.descargarTextura(renderer);
		
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
		{
			renderer.descargarTexturaRectangulo(TTipoEntidad.Burbuja, i, TTipoSticker.Nada);
		}
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, boolean escala)
	{
		gl.glPushMatrix();
		
			if (escala)
			{
				gl.glScalef(GamePreferences.GAME_SCALE_FACTOR, GamePreferences.GAME_SCALE_FACTOR, 1.0f);
			}
			
			dibujar(gl, renderer);
			
			if (burbuja && vidas > 0)
			{
				renderer.dibujarTexturaRectangulo(gl, posicionX, posicionY, TTipoEntidad.Burbuja, vidas - 1, TTipoSticker.Nada);
			}
		
		gl.glPopMatrix();
	}

	@Override
	public boolean animar()
	{
		boolean finAnimacion = super.animar();
		
		if (movimientosReady)
		{
			if (estado == TTipoMovimiento.Jump)
			{
				if (posicionAnimacion < 2 * listaVerticesAnimacion.size() / 6)
				{
					posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER;
				}
				else if (posicionAnimacion >= 4 * listaVerticesAnimacion.size() / 6)
				{
					posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER;
				}
			}
		}

		return finAnimacion;
	}
	
	public boolean animar(boolean desplazamiento)
	{
		if(desplazamiento)
		{
			return animar();
		}
		
		return super.animar();
	}

	/* Métodos de Animación */

	public void mover()
	{
		if (movimientosReady)
		{
			estado = TTipoMovimiento.Run;
			listaVerticesAnimacion = movimientos.get(estado);
			
			iniciar();
		}
	}

	public void saltar()
	{
		if (movimientosReady)
		{
			estado = TTipoMovimiento.Jump;
			listaVerticesAnimacion = movimientos.get(estado);
			
			iniciar();
		}
	}

	public void agachar()
	{
		if (movimientosReady)
		{
			estado = TTipoMovimiento.Crouch;
			listaVerticesAnimacion = movimientos.get(estado);
			
			iniciar();
		}
	}

	public void atacar()
	{
		estado = TTipoMovimiento.Attack;
		listaVerticesAnimacion = movimientos.get(estado);
		
		iniciar();
	}

	public TEstadoColision colision(Entidad entidad, InstanciaEntidad instancia)
	{
		float posicionXEntidad = instancia.getPosicionX();
		float posicionYEntidad = instancia.getPosicionY();
		float widthEntidad = entidad.getWidth();
		float heightEntidad = entidad.getHeight();

		Circle areaPersonaje = new Circle(getPosicionX() + getWidth() / 2.0f, getPosicionY() + getHeight() / 2.0f, getWidth() / 2.5f);
		Circle areaEntidad = new Circle(posicionXEntidad + widthEntidad / 2.0f, posicionYEntidad + heightEntidad / 2.0f, heightEntidad / 3.0f);

		// Hay colisión entre el personaje y el enemigo
		if (Intersector.overlaps(areaPersonaje, areaEntidad))
		{
			// Enemigo derrotado
			if (entidad.getTipo() == TTipoEntidad.Enemigo && estado == TTipoMovimiento.Attack)
			{
				instancia.setDerrotado();
				return TEstadoColision.EnemigoDerrotado;
			}
			
			if (entidad.getTipo() == TTipoEntidad.Misil && estado == TTipoMovimiento.Crouch)
			{
				return TEstadoColision.Nada;
			}

			return TEstadoColision.Colision;
		}

		return TEstadoColision.Nada;
	}

	/* Métodos de Modificación de Información */

	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		movimientosReady = true;
		
		reposo();
	}
	
	public void activarBurbuja()
	{
		burbuja = true;
	}
	
	public void desactivarBurbuja()
	{
		burbuja = false;
	}
	
	public void reiniciarVidas()
	{
		vidas = GamePreferences.MAX_LIVES;
	}
	
	public void quitarVida()
	{
		vidas--;
	}

	/* Métodos de Obtención de Información */

	public Movimientos getMovimientos()
	{
		return movimientos;
	}
	
	public boolean isAlive()
	{
		return vidas > 0;
	}
	
	public int getVidas()
	{
		return vidas;
	}
	
	private float getPosicionX()
	{
		return posicionX * GamePreferences.GAME_SCALE_FACTOR;
	}
	
	private float getPosicionY()
	{
		return posicionY * GamePreferences.GAME_SCALE_FACTOR;
	}
}
