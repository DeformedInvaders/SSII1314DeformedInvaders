package com.view.select;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.storage.ExternalStorageManager;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.R;
import com.view.display.DisplayGLSurfaceView;

public class SelectFragment extends Fragment
{
	private ExternalStorageManager manager;
	
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	private String nombreActual;

	private DisplayGLSurfaceView canvas;
	private ImageButton botonCamara;
	
	/* Constructora */
	
	public static final SelectFragment newInstance(Esqueleto e, Textura t, String n, ExternalStorageManager m)
	{
		SelectFragment fragment = new SelectFragment();
		fragment.setParameters(e, t, n, m);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t, String n, ExternalStorageManager m)
	{	
		esqueletoActual = e;
		texturaActual = t;
		nombreActual = n;
		manager = m;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewSelect1);
		canvas.setParameters(esqueletoActual, texturaActual);
		
		botonCamara = (ImageButton) rootView.findViewById(R.id.imageButtonSelect1);
		botonCamara.setOnClickListener(new OnCamaraClickListener());
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		canvas = null;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.saveData();
		canvas.onPause();
	}	
	
	private class OnCamaraClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Bitmap bitmap = canvas.capturaPantalla();
			if(manager.guardarImagen(bitmap, nombreActual))
			{
				Toast.makeText(getActivity(), R.string.text_picture_character_confirmation, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.error_picture_character, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
