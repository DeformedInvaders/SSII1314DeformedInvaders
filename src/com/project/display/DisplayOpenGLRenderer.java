package com.project.display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.view.OpenGLRenderer;
import com.creation.data.MapaBits;
import com.game.data.Personaje;
import com.project.main.R;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private TDisplayEstado estado;
	
	// Personaje
	private Personaje personaje;
	private boolean personajeCargado;
	
	// Captura
	private Bitmap captura;
	private TCapturaEstado estadoCaptura; 
	
	/* SECTION Constructura */
	
	public DisplayOpenGLRenderer(Context context)
	{
		super(context);
		
		personajeCargado = false;
		
		estado = TDisplayEstado.Nada;
		estadoCaptura = TCapturaEstado.Nada;
	}
	
	public DisplayOpenGLRenderer(Context context, Personaje p)
	{
        super(context);
        
        personajeCargado = true;
        personaje = p;        
        
        estado = TDisplayEstado.Nada;
        estadoCaptura = TCapturaEstado.Nada;
	}
	
	/* SECTION M�todos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// BackGround
		indiceTexturaFondo = R.drawable.background_display;
		
		if(personajeCargado)
		{
			personaje.cargar(gl, this);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Background
		dibujarTexturaFondo(gl);	
		
		if(personajeCargado)
		{
			if(estado == TDisplayEstado.Nada || estado == TDisplayEstado.Captura)
			{
				personaje.dibujar(gl, this);
			
				if(estado == TDisplayEstado.Captura)
				{
					if(estadoCaptura == TCapturaEstado.Capturando)
					{
						// Capturar Pantalla
					    MapaBits textura = capturaPantalla(gl);
						captura = textura.getBitmap();
						
						// Desactivar Modo Captura
						estadoCaptura = TCapturaEstado.Terminado;
						
						// Restaurar posici�n anterior de la C�mara
						camaraRestore();
						
						super.onDrawFrame(gl);
						dibujarTexturaFondo(gl);
						
						personaje.dibujar(gl, this);
					}
					else if(estadoCaptura == TCapturaEstado.Retocando)
					{
						// Marco Oscuro
						dibujarMarcoLateral(gl);
						dibujarMarcoCentral(gl);
					}
				}
			}
			else
			{
				personaje.dibujarAnimacion(gl, this);
			}
		}
	}
	
	/* SECTION M�todos abstractos de OpenGLRenderer */
	
	@Override
	protected void reiniciar() { }
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION M�todos de Modificaci�n de Estado */
	
	public void seleccionarRetoque(float height, float width)
	{
		// Construir rectangulos	
		estado = TDisplayEstado.Captura;
		estadoCaptura = TCapturaEstado.Retocando;
	}
	
	public void seleccionarCaptura()
	{
		if(estado == TDisplayEstado.Captura)
		{			
			estadoCaptura = TCapturaEstado.Capturando;
		}
	}
	
	public void seleccionarTerminado()
	{
		if(estado == TDisplayEstado.Captura)
		{
			estado = TDisplayEstado.Nada;
			estadoCaptura = TCapturaEstado.Nada;
		}
	}
	
	public void reproducirAnimacion()
	{
		personaje.animar();
	}
	
	public void seleccionarRun() 
	{
		estado = TDisplayEstado.Run;
		personaje.mover();
	}
	
	public void seleccionarJump() 
	{
		estado = TDisplayEstado.Jump;
		personaje.saltar();
	}
	
	public void seleccionarCrouch() 
	{
		estado = TDisplayEstado.Crouch;
		personaje.agachar();
	}
	
	public void seleccionarAttack() 
	{
		estado = TDisplayEstado.Attack;
		personaje.atacar();
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	public boolean isEstadoReposo()
	{
		return estado == TDisplayEstado.Nada;
	}
	
	public boolean isEstadoRetoque()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}
	
	public boolean isEstadoCapturando()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}
	
	public boolean isEstadoTerminado()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Terminado;
	}
	
	public boolean isEstadoAnimacion()
	{
		return estado != TDisplayEstado.Nada && estado != TDisplayEstado.Captura;
	}
	
	public Bitmap getCapturaPantalla()
	{
		if(estadoCaptura == TCapturaEstado.Capturando)
		{	
			while(estadoCaptura != TCapturaEstado.Terminado);
		
			return captura;
		}
		
		return null;
	}
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
	public void saveData()
	{
		if(personajeCargado)
		{
			personaje.descargar(this);
		}
	}
}