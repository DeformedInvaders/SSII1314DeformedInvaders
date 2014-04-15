package com.android.storage;

import android.widget.ProgressBar;
import android.widget.TextView;

public class OnLoadingListener
{
	private static final int FINISH_PROGRESS = 100;
	
	private TextView cuadroTexto;
	private ProgressBar barraProgreso;
	
	protected void setProgreso(int progreso, String texto)
	{
		if (cuadroTexto != null)
		{
			cuadroTexto.setText(texto);
		}
		
		if (barraProgreso != null)
		{
			barraProgreso.setProgress(progreso);
		}
	}
	
	protected void setProgresoTerminado(String texto)
	{
		setProgreso(FINISH_PROGRESS, texto);
		
		cuadroTexto = null;
		barraProgreso = null;
	}
	
	public void setCuadroTexto(TextView texto)
	{
		cuadroTexto = texto;
	}
	
	public void setBarraProgreso(ProgressBar barra)
	{
		barraProgreso = barra;
	}
}
