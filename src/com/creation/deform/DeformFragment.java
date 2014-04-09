package com.creation.deform;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.alert.AudioAlert;
import com.android.storage.ExternalStorageManager;
import com.android.view.OpenGLFragment;
import com.creation.data.Esqueleto;
import com.creation.data.Textura;
import com.lib.utils.FloatArray;
import com.project.main.R;

public class DeformFragment extends OpenGLFragment
{
	private DeformGLSurfaceView canvas;
	private Esqueleto esqueleto;
	private Textura textura;

	private DeformDataSaved dataSaved;
	private ExternalStorageManager manager;
	private String movimiento;

	private ImageButton botonAnyadir, botonEliminar, botonDeformar, botonReiniciar, botonGrabar, botonAudio, botonReproducir;

	/* Constructora */

	public static final DeformFragment newInstance(ExternalStorageManager m, Esqueleto e, Textura t, String n)
	{
		DeformFragment fragment = new DeformFragment();
		fragment.setParameters(m, e, t, n);
		return fragment;
	}

	private void setParameters(ExternalStorageManager m, Esqueleto e, Textura t, String n)
	{
		esqueleto = e;
		textura = t;
		manager = m;
		movimiento = n;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_creation_deform_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceViewDeform1);
		canvas.setParameters(esqueleto, textura, this);

		botonAnyadir = (ImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		botonEliminar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		botonDeformar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonReiniciar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		botonGrabar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform5);
		botonAudio = (ImageButton) rootView.findViewById(R.id.imageButtonDeform6);
		botonReproducir = (ImageButton) rootView.findViewById(R.id.imageButtonDeform7);

		botonAnyadir.setOnClickListener(new OnAddClickListener());
		botonEliminar.setOnClickListener(new OnRemoveClickListener());
		botonDeformar.setOnClickListener(new OnMoveClickListener());
		botonReiniciar.setOnClickListener(new OnResetClickListener());
		botonGrabar.setOnClickListener(new OnRecordClickListener());
		botonAudio.setOnClickListener(new OnAudioClickListener());
		botonReproducir.setOnClickListener(new OnPlayClickListener());

		setCanvasListener(canvas);

		reiniciarInterfaz();
		actualizarInterfaz();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		botonAnyadir = null;
		botonEliminar = null;
		botonDeformar = null;
		botonReiniciar = null;
		botonGrabar = null;
		botonReproducir = null;
		botonAudio = null;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		canvas = null;
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
			botonAudio.setVisibility(View.VISIBLE);

			if (!canvas.isEstadoGrabacion())
			{
				botonAnyadir.setVisibility(View.VISIBLE);
				botonEliminar.setVisibility(View.VISIBLE);
				botonDeformar.setVisibility(View.VISIBLE);
				botonReiniciar.setVisibility(View.VISIBLE);

				if (canvas.isGrabacionReady())
				{
					botonAudio.setVisibility(View.VISIBLE);
					botonReproducir.setVisibility(View.VISIBLE);
				}
			}
		}

		if (canvas.isEstadoAnyadir())
		{
			botonAnyadir.setBackgroundResource(R.drawable.icon_tool_add_selected);
		}
		else if (canvas.isEstadoEliminar())
		{
			botonEliminar.setBackgroundResource(R.drawable.icon_tool_remove_selected);
		}
		else if (canvas.isEstadoGrabacion())
		{
			botonGrabar.setBackgroundResource(R.drawable.icon_media_videocamara_selected);
		}
		else if (canvas.isEstadoDeformar())
		{
			botonDeformar.setBackgroundResource(R.drawable.icon_tool_hand_selected);
		}
		else if (canvas.isEstadoAudio())
		{
			botonAudio.setBackgroundResource(R.drawable.icon_media_microphone_selected);
		}
		else if (canvas.isEstadoReproduccion())
		{
			botonReproducir.setBackgroundResource(R.drawable.icon_media_play_selected);
		}
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
		botonAudio.setVisibility(View.INVISIBLE);

		botonAnyadir.setBackgroundResource(R.drawable.icon_tool_add);
		botonEliminar.setBackgroundResource(R.drawable.icon_tool_remove);
		botonDeformar.setBackgroundResource(R.drawable.icon_tool_hand);
		botonGrabar.setBackgroundResource(R.drawable.icon_media_videocamara);
		botonAudio.setBackgroundResource(R.drawable.icon_media_microphone);
		botonReproducir.setBackgroundResource(R.drawable.icon_media_play);
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

	private class OnAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAudio();

			reiniciarInterfaz();
			actualizarInterfaz();

			AudioAlert alert = new AudioAlert(getActivity(), getString(R.string.text_audio_record_title), getString(R.string.text_audio_record_description), getString(R.string.text_button_yes), getString(R.string.text_button_no), manager, movimiento)
			{
				@Override
				public void onPossitiveButtonClick()
				{
					canvas.seleccionarReposo();

					Toast.makeText(getActivity(), R.string.text_audio_record_confirmation, Toast.LENGTH_SHORT).show();

					reiniciarInterfaz();
					actualizarInterfaz();
				}

				@Override
				public void onNegativeButtonClick()
				{
					canvas.seleccionarReposo();

					reiniciarInterfaz();
					actualizarInterfaz();
				}
			};

			alert.show();
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

	public List<FloatArray> getMovimientos()
	{
		if (canvas != null)
		{
			return canvas.getMovimientos();
		}

		return null;
	}
}
