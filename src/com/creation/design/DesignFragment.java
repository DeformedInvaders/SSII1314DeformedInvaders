package com.creation.design;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLFragment;
import com.creation.data.Esqueleto;
import com.project.main.R;
import com.project.model.GameResources;

public class DesignFragment extends OpenGLFragment
{
	private DesignFragmentListener mCallback;

	private DesignGLSurfaceView canvas;
	private ImageButton botonReset, botonTriangular, botonListo;

	private DesignDataSaved dataSaved;

	/* Constructora */

	public static final DesignFragment newInstance(DesignFragmentListener c)
	{
		DesignFragment fragment = new DesignFragment();
		fragment.setParameters(c, null);
		return fragment;
	}
	
	public static final DesignFragment newInstance(DesignFragmentListener c, DesignDataSaved s)
	{
		DesignFragment fragment = new DesignFragment();
		fragment.setParameters(c, s);
		return fragment;
	}
	
	private void setParameters(DesignFragmentListener c, DesignDataSaved s)
	{
		mCallback = c;
		dataSaved = s;
	}

	public interface DesignFragmentListener
	{
		public void onDesignReady(final Esqueleto esqueleto, final DesignDataSaved datosSalvados);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_design_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) rootView.findViewById(R.id.designGLSurfaceViewDesign1);

		botonListo = (ImageButton) rootView.findViewById(R.id.imageButtonDesign1);
		botonReset = (ImageButton) rootView.findViewById(R.id.imageButtonDesign2);
		botonTriangular = (ImageButton) rootView.findViewById(R.id.imageButtonDesign3);

		botonListo.setOnClickListener(new OnReadyClickListener());
		botonReset.setOnClickListener(new onResetClickListener());
		botonTriangular.setOnClickListener(new onTriangularClickListener());

		setCanvasListener(canvas);
		
		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);
		}

		reiniciarInterfaz();
		actualizarInterfaz();
		
		sendAlertMessage(R.string.text_tip_design_draw_title, R.string.text_tip_design_draw_description, GameResources.VIDEO_DESIGN_DRAW_PATH);
		
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
		botonListo = null;
		botonReset = null;
		botonTriangular = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();

		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();

		dataSaved = canvas.saveData();
	}

	/* Métodos Abstractos OpenGLFragment */

	@Override
	protected void reiniciarInterfaz()
	{
		botonListo.setVisibility(View.INVISIBLE);
		botonReset.setVisibility(View.INVISIBLE);
		botonTriangular.setVisibility(View.INVISIBLE);
		
		botonTriangular.setBackgroundResource(R.drawable.icon_tool_triangulate);
		
		canvas.setEstado(TEstadoDetector.SimpleTouch);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (canvas.isPoligonoCompleto())
		{
			botonListo.setVisibility(View.VISIBLE);
			botonReset.setVisibility(View.VISIBLE);
			botonTriangular.setVisibility(View.VISIBLE);
		}
		
		if (canvas.isEstadoTriangulando())
		{
			botonTriangular.setBackgroundResource(R.drawable.icon_tool_triangulate_selected);
		}
		
		if (canvas.isEstadoRetocando())
		{
			canvas.setEstado(TEstadoDetector.CoordDetectors);
		}
	}

	/* Métodos Listener onClick */

	public class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (canvas.isEstadoDibujando())
			{
				if (canvas.isPoligonoSimple())
				{
					List<Integer> listaMensajes = new ArrayList<Integer>();
					listaMensajes.add(R.string.text_tip_design_drag_description);
					listaMensajes.add(R.string.text_tip_design_zoom_description);
					listaMensajes.add(R.string.text_tip_design_rotate_description);
					
					List<String> listaVideos = new ArrayList<String>();
					listaVideos.add(GameResources.VIDEO_DESIGN_DRAG_PATH);
					listaVideos.add(GameResources.VIDEO_DESIGN_ZOOM_PATH);
					listaVideos.add(GameResources.VIDEO_DESIGN_ROTATE_PATH);
					
					sendAlertMessage(R.string.text_tip_design_touch_title, listaMensajes, listaVideos);
					canvas.seleccionarRetoque();
				}
				else
				{					
					sendMessage(R.string.text_tip_problem_title, R.string.text_tip_design_noregular_description, GameResources.VIDEO_DESIGN_NOREGULAR_PATH, R.string.error_triangle);
				}
			}
			else if (canvas.isEstadoRetocando())
			{
				if (canvas.isPoligonoDentroMarco())
				{
					mCallback.onDesignReady(canvas.getEsqueleto(), canvas.saveData());
				}
				else
				{
					sendMessage(R.string.text_tip_problem_title, R.string.text_tip_design_outside_description, GameResources.VIDEO_DESIGN_OUTSIDE_PATH, R.string.error_retouch);
				}
			}

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class onResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class onTriangularClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarTriangular();
			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}
}
