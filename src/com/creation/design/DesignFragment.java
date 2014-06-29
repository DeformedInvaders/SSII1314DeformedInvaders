package com.creation.design;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.touch.TStateDetector;
import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.creation.data.Skeleton;
import com.main.model.GameResources;
import com.project.main.R;

public class DesignFragment extends OpenGLFragment
{
	private DesignFragmentListener mCallback;

	private DesignOpenGLSurfaceView mCanvas;
	private IconImageButton buttonReset, buttonTriangulate, buttonReady;

	private DesignDataSaved dataSaved;

	/* Constructora */

	public static final DesignFragment newInstance(DesignFragmentListener callback)
	{
		DesignFragment fragment = new DesignFragment();
		fragment.setParameters(callback, null);
		return fragment;
	}
	
	public static final DesignFragment newInstance(DesignFragmentListener callback, DesignDataSaved data)
	{
		DesignFragment fragment = new DesignFragment();
		fragment.setParameters(callback, data);
		return fragment;
	}
	
	private void setParameters(DesignFragmentListener callback, DesignDataSaved data)
	{
		mCallback = callback;
		dataSaved = data;
	}

	public interface DesignFragmentListener
	{
		public void onDesignReady(Skeleton esqueleto, DesignDataSaved datosSalvados);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_creation_design_layout, container, false);

		// Instanciar Elementos de la GUI
		mCanvas = (DesignOpenGLSurfaceView) rootView.findViewById(R.id.designGLSurfaceViewDesign1);

		buttonReady = (IconImageButton) rootView.findViewById(R.id.imageButtonDesign1);
		buttonReset = (IconImageButton) rootView.findViewById(R.id.imageButtonDesign2);
		buttonTriangulate = (IconImageButton) rootView.findViewById(R.id.imageButtonDesign3);

		buttonReady.setOnClickListener(new OnReadyClickListener());
		buttonReset.setOnClickListener(new onResetClickListener());
		buttonTriangulate.setOnClickListener(new onTriangulateClickListener());

		setCanvasListener(mCanvas);
		
		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);
		}

		resetInterface();
		updateInterface();
		
		sendAlertMessage(R.string.text_tip_design_draw_title, R.string.text_tip_design_draw_description, GameResources.VIDEO_DESIGN_DRAW_PATH);
		
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		mCanvas = null;
		buttonReady = null;
		buttonReset = null;
		buttonTriangulate = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		dataSaved = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		mCanvas.onResume();

		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);

			resetInterface();
			updateInterface();
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();
		mCanvas.onPause();

		dataSaved = mCanvas.saveData();
	}

	/* Métodos Abstractos OpenGLFragment */

	@Override
	protected void resetInterface()
	{
		buttonReady.setVisibility(View.INVISIBLE);
		buttonReset.setVisibility(View.INVISIBLE);
		buttonTriangulate.setVisibility(View.INVISIBLE);
		
		mCanvas.setDetectorState(TStateDetector.SimpleTouch);
	}

	@Override
	protected void updateInterface()
	{
		if (mCanvas.isPolygonComplete())
		{
			buttonReady.setVisibility(View.VISIBLE);
			buttonReset.setVisibility(View.VISIBLE);
			buttonTriangulate.setVisibility(View.VISIBLE);
		}
		
		buttonTriangulate.setActivo(mCanvas.isStateTriangulate());
		
		if (mCanvas.isStatePreparing())
		{
			mCanvas.setDetectorState(TStateDetector.CoordDetectors);
		}
	}

	/* Métodos Listener onClick */

	public class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (mCanvas.isStateDrawing())
			{
				if (mCanvas.isPolygonSimplex())
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
					mCanvas.selectPreparing();
				}
				else
				{					
					sendMessage(R.string.text_tip_problem_title, R.string.text_tip_design_noregular_description, GameResources.VIDEO_DESIGN_NOREGULAR_PATH, R.string.error_triangle);
				}
			}
			else if (mCanvas.isStatePreparing())
			{
				if (mCanvas.isPolygonSingular())
				{
					sendToastMessage(R.string.error_singular);
				}
				
				if (mCanvas.isPolygonReady())
				{
					mCallback.onDesignReady(mCanvas.getSkeleton(), mCanvas.saveData());
				}
				else
				{
					sendMessage(R.string.text_tip_problem_title, R.string.text_tip_design_outside_description, GameResources.VIDEO_DESIGN_OUTSIDE_PATH, R.string.error_retouch);
				}
			}

			resetInterface();
			updateInterface();
		}
	}

	private class onResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.reiniciar();

			resetInterface();
			updateInterface();
		}
	}

	private class onTriangulateClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.selectTriangulate();
			resetInterface();
			updateInterface();
			
			if (!mCanvas.isPolygonSimplex())
			{					
				sendMessage(R.string.text_tip_problem_title, R.string.text_tip_design_noregular_description, GameResources.VIDEO_DESIGN_NOREGULAR_PATH, R.string.error_triangle);
			}
		}
	}
}
