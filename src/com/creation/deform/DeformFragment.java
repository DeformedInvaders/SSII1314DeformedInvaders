package com.creation.deform;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.game.data.Personaje;
import com.lib.buffer.VertexArray;
import com.project.main.R;

public class DeformFragment extends OpenGLFragment implements OnDeformListener
{
	private OnDeformationListener mListener;
	
	private Personaje personaje;

	private DeformOpenGLSurfaceView canvas;
	private IconImageButton botonAnyadir, botonEliminar, botonDeformar, botonReiniciar, botonGrabar, botonReproducir;

	private DeformDataSaved dataSaved;
	
	/* Constructora */

	public static final DeformFragment newInstance(OnDeformationListener l, Personaje p)
	{
		DeformFragment fragment = new DeformFragment();
		fragment.setParameters(l, p);
		return fragment;
	}

	private void setParameters(OnDeformationListener l, Personaje p)
	{
		mListener = l;
		personaje = p;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_creation_deform_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (DeformOpenGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceViewDeform1);
		canvas.setParameters(this, personaje);
		setCanvasListener(canvas);
		
		botonAnyadir = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		botonEliminar = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		botonDeformar = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonReiniciar = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		botonGrabar = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform5);
		botonReproducir = (IconImageButton) rootView.findViewById(R.id.imageButtonDeform6);

		botonAnyadir.setOnClickListener(new OnAddClickListener());
		botonEliminar.setOnClickListener(new OnRemoveClickListener());
		botonDeformar.setOnClickListener(new OnMoveClickListener());
		botonReiniciar.setOnClickListener(new OnResetClickListener());
		botonGrabar.setOnClickListener(new OnRecordClickListener());
		botonReproducir.setOnClickListener(new OnPlayClickListener());

		reiniciarInterfaz();
		actualizarInterfaz();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
		
		botonAnyadir = null;
		botonEliminar = null;
		botonDeformar = null;
		botonReiniciar = null;
		botonGrabar = null;
		botonReproducir = null;
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

	/* Métodos Abstractos OpenGLFramgent */

	@Override
	protected void actualizarInterfaz()
	{
		if (canvas.isHandlesVacio())
		{
			botonAnyadir.setVisibility(View.VISIBLE);
		}
		else
		{
			botonGrabar.setVisibility(View.VISIBLE);

			if (!canvas.isEstadoGrabacion())
			{
				botonAnyadir.setVisibility(View.VISIBLE);
				botonEliminar.setVisibility(View.VISIBLE);
				botonDeformar.setVisibility(View.VISIBLE);
				botonReiniciar.setVisibility(View.VISIBLE);

				if (canvas.isGrabacionReady())
				{
					botonReproducir.setVisibility(View.VISIBLE);
				}
			}
		}

		botonAnyadir.setActivo(canvas.isEstadoAnyadir());
		botonEliminar.setActivo(canvas.isEstadoEliminar());
		botonGrabar.setActivo(canvas.isEstadoGrabacion());
		botonDeformar.setActivo(canvas.isEstadoDeformar());
		botonReproducir.setActivo(canvas.isEstadoReproduccion());
	}

	@Override
	protected void reiniciarInterfaz()
	{
		botonAnyadir.setVisibility(View.INVISIBLE);
		botonEliminar.setVisibility(View.INVISIBLE);
		botonDeformar.setVisibility(View.INVISIBLE);
		botonReiniciar.setVisibility(View.INVISIBLE);
		botonGrabar.setVisibility(View.INVISIBLE);
		botonReproducir.setVisibility(View.INVISIBLE);
	}

	/* Métodos Listener onClick */

	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnyadir();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarEliminar();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMover();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarGrabado();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPlay();

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	/* Métodos de Obtención de Información */

	public List<VertexArray> getMovimientos()
	{
		if (canvas != null)
		{
			return canvas.getMovimientos();
		}

		return null;
	}
	
	/* Métodos abstractos de OnDeformListener */

	@Override
	public void onPlaySoundEffect()
	{
		mListener.onPlaySoundEffect();
	}

	@Override
	public void onAnimationFinished()
	{
		getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run()
	        {
	    		reiniciarInterfaz();
	    		actualizarInterfaz();
	        }
	    });
	}
}
