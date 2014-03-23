package com.creation.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DesignOpenGLRenderer extends OpenGLRenderer
{		
	//Estructura de Datos de la Escena
	private TDesignEstado estado;
	private Triangulator triangulator;
	
	private FloatArray puntos;	
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;
	
	private FloatBuffer bufferPoligono;	
	private FloatBuffer bufferMalla;
	
	private boolean poligonoSimple;
	
	private final static int NUM_BSPLINE_VERTICES = 60;
	private FloatArray lineasBSpline;
	private float minX, minY, maxX, maxY, ladoX, ladoY, distanciaX, distanciaY;
	
	/* SECTION Constructora */
	
	public DesignOpenGLRenderer(Context context)
	{        
		super(context);
		
        estado = TDesignEstado.Dibujando;

        puntos = new FloatArray();
        poligonoSimple = false;
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		if(estado == TDesignEstado.Dibujando)
		{
			if(puntos.size > 0)
			{
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPoligono);
				
				if(puntos.size > 2)
				{
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPoligono);
				}
			}
		}
		else
		{
			dibujarBuffer(gl, GL10.GL_LINES, SIZELINE, Color.BLACK, bufferMalla);
			
			if(estado == TDesignEstado.Retocando)
			{
				// Marco Oscuro
				dibujarMarcoLateral(gl);
				dibujarMarcoCentral(gl);
			}
		}
	}
	
	/* SECTION Métodos Abstractos de OpenGLRenderer */
	
	@Override
	protected boolean reiniciar()
	{
		estado = TDesignEstado.Dibujando;
		
		puntos.clear();
		
		vertices = null;
		triangulos = null;
		contorno = null;
		
		return true;
	}
	
	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			return anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		
		return false;
	}
	
	private boolean anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		boolean anyadir = true;
		
		if(puntos.size > 0)
		{
			float lastWorldX = puntos.get(puntos.size-2);
			float lastWorldY = puntos.get(puntos.size-1);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);
			
			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > MAX_DISTANCE_PIXELS;
		}
		
		if(anyadir)
		{
			puntos.add(worldX);
			puntos.add(worldY);
			
			bufferPoligono = BufferManager.construirBufferListaPuntos(puntos);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
		
		return false;
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			minX = obtenerMinX(puntos);
			minY = obtenerMinY(puntos);
			maxX = obtenerMaxX(puntos);
			maxY = obtenerMaxY(puntos);
			
			ladoX = maxX - minX;
			ladoY = maxY - minY;
			
			triangulator = new Triangulator(puntos);
			if(puntos.size > 4){	
					lineasBSpline = triangulator.calcularBSpline(puntos, 3, NUM_BSPLINE_VERTICES);
			}

			float area = triangulator.calcularAreaMesh(lineasBSpline);
			distanciaX = hallarDistancia(area, ladoX, ladoY, true);
			distanciaY = hallarDistancia(area, ladoX, ladoY, false);
			
			FloatArray verticesInterseccion;

			verticesInterseccion =  obtenerPuntosInterseccion(GL10.GL_LINE_LOOP, SIZELINE, Color.GREEN,  minX, minY, maxX, maxY, distanciaX, distanciaY, lineasBSpline);

			triangulator = null;
			triangulator = new Triangulator(puntos, verticesInterseccion, lineasBSpline);
			
			poligonoSimple = triangulator.getPoligonSimple();
			vertices = triangulator.getVertices();
			triangulos = triangulator.getTriangulos();
			contorno = triangulator.getContorno();
			if(poligonoSimple)
			{
				bufferMalla = BufferManager.construirBufferListaTriangulos(triangulos, vertices);
			}
			return true;
		}
		
		return false;
	}
	
	private float obtenerMinX(FloatArray contornoTest) {
		
		float min = Float.MAX_VALUE;
		for (int i = 0; i<contornoTest.size; i++){
			if((i%2==0) && (contornoTest.get(i) < min)){
				min = contornoTest.get(i);
			}
		}
		return min;
	}
	
	private float obtenerMinY(FloatArray contornoTest) {
			
		float min = Float.MAX_VALUE;
		for (int i = 0; i<contornoTest.size; i++){
			if((i%2==1) && (contornoTest.get(i) < min)){
				min = contornoTest.get(i);
			}
		}
		return min;
	}
	
	private float obtenerMaxX(FloatArray contornoTest) {
		
		float max = 0;
		for (int i = 0; i<contornoTest.size; i++){
			if((i%2==0) && (contornoTest.get(i) > max)){
				max = contornoTest.get(i);
			}
		}
		return max;
	}
	
	private float obtenerMaxY(FloatArray contornoTest) {
		
		float max = 0;
		for (int i = 0; i<contornoTest.size; i++){
			if((i%2==1) && (contornoTest.get(i) > max)){
				max = contornoTest.get(i);
			}
		}
		return max;
	}
	
	private float hallarDistancia(float areaFigura, float ladoX, float ladoY, boolean esDistanciaX) {
		
		float areaRectangulo = ladoX * ladoY;
		float areaDivision = (areaFigura/areaRectangulo)*100; //En porcentaje
		
		double separacion;
		float lado;
		
		if(esDistanciaX){
			separacion = ladoX / 30;
			lado = ladoX;
		}else{
			separacion = ladoY / 30;
			lado = ladoY;
		}
		
		//Miramos la propocion del ladoX respecto al ladoY
	 	if (ladoX * 0.5 >= ladoY || ladoY * 0.5 >= ladoX){
			separacion /= 1.15;
	 	}
		
		if(areaDivision <10){
			separacion *= 1.1;
		}
		else if(areaDivision <20){
			separacion *= 1.2;
		}
		else if(areaDivision <30){
			separacion *= 1.3;
		}
		else if(areaDivision <40){
			separacion *= 1.3;
		}
		else if(areaDivision <50){
			separacion *= 1.4;
		}
		else if(areaDivision <60){
			separacion *= 1.5;
		}
		else if(areaDivision <70){
			separacion *= 1.7;
		}
		else if(areaDivision <80){
			separacion *= 1.7;
		}
		else if(areaDivision <90){
			separacion *= 1.8;
		}
		else{
			separacion *= 2;
		}
		
		
		if(areaFigura < 1000){
			if (areaDivision < 50 ) separacion*= 8;
			else separacion *= 8;
		}
		else if(areaFigura < 2500){
			if (areaDivision < 50 ) separacion*= 7;
			else separacion *= 7;
		}
		else if(areaFigura < 5000){
			if (areaDivision < 50 ) separacion*= 6;
			else separacion *= 6;
		}
		else if(areaFigura < 10000){
			if (areaDivision < 50 ) separacion*= 6;
			else separacion *= 6;
		}
		else if(areaFigura < 20000){
			if (areaDivision < 50 ) separacion*= 6;
			else separacion *= 6;
		}
		else if(areaFigura < 30000){
			if (areaDivision < 50 ) separacion*= 4.5;
			else separacion *= 4.75;
		}
		else if(areaFigura < 40000){
			if (areaDivision < 50 ) separacion*= 3.25;
			else separacion *= 3.75;
		}
		else if(areaFigura < 50000){
			if (areaDivision < 50 ) separacion*= 3.25;
			else separacion *= 3.75;
		}
		else if(areaFigura < 60000){
			if (areaDivision < 50 ) separacion*= 3.75;
			else separacion *= 4.25;
		}
		else if(areaFigura < 70000){
			if (areaDivision < 50 ) separacion*= 3.75;
			else separacion *= 4.25;
		}
		else if(areaFigura < 80000){
			if (areaDivision < 50 ) separacion*= 4;
			else separacion *= 4.5;
		}
		else if(areaFigura < 90000){
			if (areaDivision < 50 ) separacion*= 4.25;
			else separacion *= 4.25;
		}
		else if(areaFigura < 100000){
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 4.5;
		}
		else if(areaFigura < 110000){
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 4.5;
		}
		else if(areaFigura < 120000){
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 5;
		}
		else if(areaFigura < 130000){
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 5;
		}
		else if(areaFigura < 140000){
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 5;
		}
		else{
			if (areaDivision < 50 ) separacion*= 3.5;
			else separacion *= 5; 
		}
				
//		Log.i("LOGTAG", "En hallarDistancia - areaFigura: "+areaFigura);
//		Log.i("LOGTAG", "En hallarDistancia - areaDivision: "+areaDivision);
//		Log.i("LOGTAG", "En hallarDistancia - separacion: "+separacion);
		
		float aux = (float) (lado / separacion);
		aux = Math.round(aux);
		return lado/aux ;
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}
	
	@Override
	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);
			
			float cWorldX = (lastWorldX + worldX) / 2.0f;
			float cWorldY = (lastWorldY + worldY) / 2.0f;
			
			escalarVertices(factor, factor, cWorldX, cWorldY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}
	
	@Override
	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);

			float dWorldX = worldX - lastWorldX;
			float dWorldY = worldY - lastWorldY;
			
			trasladarVertices(dWorldX, dWorldY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float cWorldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float cWorldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			rotarVertices(ang, cWorldX, cWorldY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	public boolean seleccionarTriangular()
	{
		if(poligonoSimple)
		{
			estado = TDesignEstado.Triangulando;
			return true;
		}
		
		return false;
	}
	
	public void seleccionarRetoque()
	{
		estado = TDesignEstado.Retocando;
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		if(estado == TDesignEstado.Terminado)
		{
			recortarPoligonoDentroMarco(vertices);
			
			return new Esqueleto(contorno, vertices, triangulos);
		}
		
		return null;
	}
	
	public boolean isEstadoDibujando()
	{
		return estado == TDesignEstado.Dibujando;
	}
	
	public boolean isEstadoTriangulando()
	{
		return estado == TDesignEstado.Triangulando;
	}
	
	public boolean isEstadoRetocando()
	{
		return estado == TDesignEstado.Retocando;
	}

	public boolean isPoligonoCompleto()
	{
		return puntos.size >= 6;
	}
	
	public boolean isPoligonoDentroMarco()
	{
		if(isPoligonoDentroMarco(vertices))
		{
			estado = TDesignEstado.Terminado;
			return true;
		}
				
		return false;
	}
	
	/* SECTION Métodos de Guardado de Información */
	
	public DesignDataSaved saveData()
	{
		return new DesignDataSaved(puntos, vertices, triangulos, contorno, estado, poligonoSimple);
	}
	
	public void restoreData(DesignDataSaved data)
	{
		estado = data.getEstado();
		puntos = data.getPuntos();
		vertices = data.getVertices();
		triangulos = data.getTriangulos();
		contorno = data.getContorno();
		poligonoSimple = data.getPoligonoSimple();
		
		if(poligonoSimple)
		{
			bufferPoligono = BufferManager.construirBufferListaPuntos(puntos); 
			bufferMalla = BufferManager.construirBufferListaTriangulos(triangulos, vertices);
		}
	}
}
