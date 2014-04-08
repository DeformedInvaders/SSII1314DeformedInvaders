package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.deform.TDeformTipo;
import com.game.game.TEstadoColision;
import com.lib.math.Circle;
import com.lib.math.Intersector;
import com.project.main.GamePreferences;
import com.project.main.R;

public class Personaje extends Malla
{
	private Movimientos movimientos;

	private TDeformTipo estado;
	
	private int vidas;
	private boolean burbuja;

	/* SECTION Constructora */

	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
		
		posicionX = 0.0f;
		posicionY = 0.0f;
		
		vidas = GamePreferences.MAX_LIVES;
		burbuja = false;		
	}

	/* SECTION Métodos abstractos de Entidad */
	
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
			renderer.cargarTexturaRectangulo(gl, GamePreferences.WIDTH_CHARACTER, GamePreferences.WIDTH_CHARACTER, indiceBurbuja(i), TTipoEntidad.Burbuja, i, TTipoSticker.Nada);
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
		
		if (estado == TDeformTipo.Jump)
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

	/* SECTION Métodos de Animación */

	public void mover()
	{
		listaVerticesAnimacion = movimientos.get(0);

		estado = TDeformTipo.Run;
		iniciar();
	}

	public void saltar()
	{
		listaVerticesAnimacion = movimientos.get(1);

		estado = TDeformTipo.Jump;
		iniciar();
	}

	public void agachar()
	{
		listaVerticesAnimacion = movimientos.get(2);

		estado = TDeformTipo.Crouch;
		iniciar();
	}

	public void atacar()
	{
		listaVerticesAnimacion = movimientos.get(3);

		estado = TDeformTipo.Attack;
		iniciar();
	}

	public TEstadoColision colision(Entidad entidad, InstanciaEntidad instancia)
	{
		float posicionXEntidad = instancia.getPosicionX();
		float posicionYEntidad = instancia.getPosicionY();
		float widthEntidad = entidad.getWidth();

		Circle areaPersonaje = new Circle(posicionX + width / 4, posicionY, width / 5);
		Circle areaEntidad = new Circle(posicionXEntidad + widthEntidad / 4, posicionYEntidad, widthEntidad / 5);

		// Hay colisión entre el personaje y el enemigo
		if (Intersector.overlaps(areaPersonaje, areaEntidad))
		{
			// Enemigo derrotado
			if (entidad.getTipo() == TTipoEntidad.Enemigo && estado == TDeformTipo.Attack)
			{
				instancia.setDerrotado();
				return TEstadoColision.EnemigoDerrotado;
			}

			return TEstadoColision.Colision;
		}

		return TEstadoColision.Nada;
	}

	/* SECTION Métodos de Modificación de Información */

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

	/* SECTION Métodos de Obtención de Información */

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
