package com.android.view;

public class BackgroundDataSaved
{
	private int[] indiceTexturaFondo;
	private float[] posFondo;
	private boolean[] dibujarFondo;
	
	public BackgroundDataSaved(int[] indices, float[] pos, boolean[] dibujar)
	{
		indiceTexturaFondo = indices;
		posFondo = pos;
		dibujarFondo = dibujar;
	}

	public int[] getIndiceTexturaFondo()
	{
		return indiceTexturaFondo;
	}

	public float[] getPosFondo()
	{
		return posFondo;
	}

	public boolean[] getDibujarFondo()
	{
		return dibujarFondo;
	}
}
