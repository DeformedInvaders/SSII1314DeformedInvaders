package com.project.model;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.storage.OnLoadingListener;
import com.android.view.AlertFragment;
import com.project.main.R;

public class LoadingFragment extends AlertFragment
{
	private OnLoadingListener mListener;

	/* Constructora */

	public static final LoadingFragment newInstance(OnLoadingListener listener)
	{
		LoadingFragment fragment = new LoadingFragment();
		fragment.setParameters(listener);
		return fragment;
	}

	private void setParameters(OnLoadingListener listener)
	{
		mListener = listener;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_project_loading_layout, container, false);

		// Instanciar Elementos de la GUI
		TextView textView = (TextView) rootView.findViewById(R.id.textViewLoading1);
		ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarLoading1);
		
		mListener.setCuadroTexto(textView);
		mListener.setBarraProgreso(progressBar);

		return rootView;
	}
}
