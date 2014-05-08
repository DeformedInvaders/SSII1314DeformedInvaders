package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.creation.data.TTipoSticker;
import com.game.game.TEstadoColision;
import com.lib.math.Intersector;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Personaje extends Malla
{
	// Nombre
	private String nombre;
	
	// Movimientos
	private Movimientos movimientos;
	private TTipoMovimiento tipoMovimiento;
	
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
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			gl.glPushMatrix();
	
				gl.glTranslatef(posicionX, posicionY, 0.0f);
				
				if (burbuja)
				{
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(), GamePreferences.GAME_SCALE_FACTOR(), 1.0f);
				}
				
				super.dibujar(gl, renderer);			
	
			gl.glPopMatrix();
		}

		if (burbuja && vidas > 0)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(posicionX, posicionY, 0.0f);
				gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(), GamePreferences.GAME_SCALE_FACTOR(), 1.0f);
				
				renderer.dibujarTexturaRectangulo(gl, TTipoEntidad.Burbuja, vidas - 1, TTipoSticker.Nada);
			
			gl.glPopMatrix();
		}
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			if (burbuja)
			{
				gl.glPushMatrix();
				
					gl.glTranslatef(area.x, area.y, 0.0f);
					handle.dibujar(gl);
			
				gl.glPopMatrix();
			}
		}
	}

	@Override
	public boolean animar()
	{
		boolean finAnimacion = super.animar();
		
		if (movimientosReady)
		{
			if (tipoMovimiento == TTipoMovimiento.Jump)
			{
				if (posicionAnimacion < 2 * listaVerticesAnimacion.size() / 6)
				{
					posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER();
				}
				else if (posicionAnimacion >= 4 * listaVerticesAnimacion.size() / 6)
				{
					posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER();
				}
				
				moverArea(posicionX, posicionY);
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
			tipoMovimiento = TTipoMovimiento.Run;
			listaVerticesAnimacion = movimientos.get(tipoMovimiento);
			
			iniciar();
		}
	}

	public void saltar()
	{
		if (movimientosReady)
		{
			tipoMovimiento = TTipoMovimiento.Jump;
			listaVerticesAnimacion = movimientos.get(tipoMovimiento);
			
			iniciar();
		}
	}

	public void agachar()
	{
		if (movimientosReady)
		{
			tipoMovimiento = TTipoMovimiento.Crouch;
			listaVerticesAnimacion = movimientos.get(tipoMovimiento);
			
			iniciar();
		}
	}

	public void atacar()
	{
		tipoMovimiento = TTipoMovimiento.Attack;
		listaVerticesAnimacion = movimientos.get(tipoMovimiento);
		
		iniciar();
	}

	public TEstadoColision colision(Entidad entidad)
	{
		// Hay colisión entre el personaje y el enemigo
		if (Intersector.overlaps(area, entidad.getArea()))
		{
			entidad.restaurarArea();
			
			// Enemigo derrotado
			if (entidad.getTipo() == TTipoEntidad.Enemigo && tipoMovimiento == TTipoMovimiento.Attack)
			{
				return TEstadoColision.EnemigoDerrotado;
			}
			
			if (entidad.getTipo() == TTipoEntidad.Misil && tipoMovimiento == TTipoMovimiento.Crouch)
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
	
	public void setNombre(String n)
	{
		nombre = n;
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
	
	public float getWidth()
	{
		return width * GamePreferences.GAME_SCALE_FACTOR();
	}
	
	public float getHeight()
	{
		return height * GamePreferences.GAME_SCALE_FACTOR();
	}
	
	public boolean isAlive()
	{
		return vidas > 0;
	}
	
	public int getVidas()
	{
		return vidas;
	}
	
	public String getNombre()
	{
		return nombre;
	}

	public float getY() 
	{
		return posicionY;
	}

	public void subir() 
	{
		posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER();
	}
	
	public void bajar() 
	{
		posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER();
	}
}
