package com.project.loading;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.storage.InternalStorageManager;
import com.project.data.Personaje;
import com.project.main.R;

public class LoadingFragment extends Fragment
{
	private LoadingFragmentListener mCallback;
	
	private InternalStorageManager manager;
	private int seleccionado;
	private List<Personaje> lista;
	
	private Handler handler;
	private Thread threadProgressBar, threadLoadData;
	private TextView textView;
	private ProgressBar progressBar;
    private int progressBarStatus;
    private boolean progressCompleted;
    
    /* SECTION Constructora */
	
	public static final LoadingFragment newInstance(InternalStorageManager manager)
	{
		LoadingFragment fragment = new LoadingFragment();
		fragment.setParameters(manager);
		return fragment;
	}
	
	private void setParameters(InternalStorageManager manager)
	{
		this.manager = manager;
	}
	
	public interface LoadingFragmentListener
	{
		public void onLoadingListCharacters(List<Personaje> lista, int seleccionado);
    }
	
	/* SECTION Métodos Fragment */
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (LoadingFragmentListener) activity;
		handler = new Handler();
		
		progressBarStatus = 0;
		progressCompleted = false;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
		handler = null;
	}
	
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
                while(!progressCompleted)
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
		        // Cargar Seleccionado
				seleccionado = manager.cargarSeleccionado();
				textView.setText(getString(R.string.text_progressBar_chosen));
				
				// Cargar ListaPersonajes
				lista = manager.cargarListaPersonajes(LoadingFragment.this);
				
				completeProgressBar();
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
	
	/* SECTION Métodos Públicos */
	
	public void updateProgressBarStatus(final int progress, final String name)
	{
		progressBarStatus = progress;
		
		getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
        		textView.setText(getString(R.string.text_progressBar_character_list)+" "+name+" ("+progress+"%)");
            }
        });
	}
	
	/* SECTION Métodos Privados */
	
	private void completeProgressBar()
	{
		progressBarStatus = 100;
		progressCompleted = true;
		
		getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
            	progressBar.setProgress(progressBarStatus);
        		textView.setText(getString(R.string.text_progressBar_completed)+" "+"(100%)");
        		
                // Guardar datos
                mCallback.onLoadingListCharacters(lista, seleccionado);
            }
        });
	}
}
