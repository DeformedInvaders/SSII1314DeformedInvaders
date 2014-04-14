package com.loading.load;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.storage.InternalStorageManager;
import com.game.data.Personaje;
import com.project.main.R;
import com.project.model.GameStatistics;

public class LoadingFragment extends Fragment implements OnLoadingListener
{
	private LoadingFragmentListener mCallback;

	private InternalStorageManager internalManager;
	private List<Personaje> lista;
	private GameStatistics[] niveles;

	private Handler handler;
	private Thread threadProgressBar, threadLoadData;
	private TextView textView;
	private ProgressBar progressBar;
	private int progressBarStatus;
	private boolean progressCompleted;

	/* Constructora */

	public static final LoadingFragment newInstance(LoadingFragmentListener c, InternalStorageManager m)
	{
		LoadingFragment fragment = new LoadingFragment();
		fragment.setParameters(c, m);
		return fragment;
	}

	private void setParameters(LoadingFragmentListener c, InternalStorageManager m)
	{
		internalManager = m;
		mCallback = c;
		
		handler = new Handler();

		progressBarStatus = 0;
		progressCompleted = false;
	}

	public interface LoadingFragmentListener
	{
		public void onLoadingDataFinished(List<Personaje> lista, GameStatistics[] niveles);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_project_loading_layout, container, false);

		// Instanciar Elementos de la GUI
		textView = (TextView) rootView.findViewById(R.id.textViewLoading1);
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarLoading1);

		threadProgressBar = new Thread() {
			@Override
			public void run()
			{
				while (!progressCompleted)
				{
					// Actualizar el ProgressBar
					handler.post(new Runnable()
					{
						@Override
						public void run()
						{
							progressBar.setProgress(progressBarStatus);
						}
					});
				}
			}
		};

		threadLoadData = new Thread() {
			@Override
			public void run()
			{
				cargarDatos();
			}
		};

		threadProgressBar.start();
		threadLoadData.start();

		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		threadProgressBar = null;
		threadLoadData = null;
		textView = null;
		progressBar = null;
	}

	/* Métodos abstractos de OnLoadingFragment */

	public void onProgress(final int progress, final String name)
	{
		progressBarStatus = progress;

		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				textView.setText(getString(R.string.text_progressBar_character_list) + " " + name + " (" + progress + "%)");
			}
		});
	}

	/* Métodos Privados */
	
	private void cargarDatos()
	{
		// Cargar Niveles
		niveles = internalManager.cargarEstadisticas();
		
		textView.setText(getString(R.string.text_progressBar_level));

		// Cargar Seleccionado
		internalManager.cargarPreferencias();
		textView.setText(getString(R.string.text_progressBar_preferences));

		// Cargar ListaPersonajes
		lista = internalManager.cargarListaPersonajes(this);

		progressBarStatus = 100;
		progressCompleted = true;

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run()
			{
				progressBar.setProgress(progressBarStatus);
				textView.setText(getString(R.string.text_progressBar_completed) + " (100%)");

				// Guardar datos				
				mCallback.onLoadingDataFinished(lista, niveles);
			}
		});
	}
}
