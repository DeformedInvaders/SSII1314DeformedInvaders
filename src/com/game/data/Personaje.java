package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.game.game.TEstadoColision;
import com.lib.math.Circle;
import com.lib.math.Intersector;
import com.project.main.GamePreferences;
import com.project.main.R;

public class Personaje extends Malla
{
	private Movimientos movimientos;

	private TTipoMovimiento estado;
	
	private int vidas;
	private boolean burbuja;

	/* Constructora */

	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
		
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
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BUBBLE; i++)
		{
			renderer.cargarTexturaRectangulo(gl, GamePreferences.DISTANCE_CHARACTER_WIDTH(), GamePreferences.DISTANCE_CHARACTER_WIDTH(), indiceBurbuja(i), TTipoEntidad.Burbuja, i, TTipoSticker.Nada);
		}
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		super.descargarTextura(renderer);
		
		// Burbuja
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BUBBLE; i++)
		{
			renderer.descargarTexturaRectangulo(TTipoEntidad.Burbuja, i, TTipoSticker.Nada);
		}
	}
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		super.dibujar(gl, renderer);
		
		if (burbuja && vidas > 0)
		{
			renderer.dibujarTexturaRectangulo(gl, posicionX, posicionY, TTipoEntidad.Burbuja, vidas - 1, TTipoSticker.Nada);
		}
	}

	@Override
	public boolean animar()
	{
		boolean finAnimacion = super.animar();
		
		if (estado == TTipoMovimiento.Jump)
		{
			if (posicionAnimacion <= listaVerticesAnimacion.size() / 2)
			{
				posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER;
			}
			else
			{
				posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER;
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
		estado = TTipoMovimiento.Run;
		listaVerticesAnimacion = movimientos.get(estado);
		
		iniciar();
	}

	public void saltar()
	{
		estado = TTipoMovimiento.Jump;
		listaVerticesAnimacion = movimientos.get(estado);
		
		iniciar();
	}

	public void agachar()
	{
		estado = TTipoMovimiento.Crouch;
		listaVerticesAnimacion = movimientos.get(estado);
		
		iniciar();
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

		Circle areaPersonaje = new Circle(posicionX + width / 4, posicionY + height / 4, width / 5);
		Circle areaEntidad = new Circle(posicionXEntidad + widthEntidad / 4, posicionYEntidad + heightEntidad / 4, widthEntidad / 5);

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
}
