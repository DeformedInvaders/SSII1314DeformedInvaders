package com.android.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.alert.VideoAlert;
import com.project.main.GamePreferences;
import com.project.main.R;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public abstract class OpenGLFragment<T extends OpenGLSurfaceView> extends Fragment
{
	/* Métodos Abstractos */

	protected abstract void reiniciarInterfaz();

	protected abstract void actualizarInterfaz();

	/* Métodos Protegidos */

	protected void setCanvasListener(final T canvas)
	{
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				canvas.onTouch(view, event);

				reiniciarInterfaz();
				actualizarInterfaz();
				return true;
			}
		});
	}
	
	protected void sendAlertMessage(int tipsTitle, List<Integer> mensajes, List<String> videos)
	{
		if(GamePreferences.TIPS_ENABLED())
		{
			List<String> listaMensajes = new ArrayList<String>();
			Iterator<Integer> itm = mensajes.iterator();
			while(itm.hasNext())
			{
				listaMensajes.add(getString(itm.next()));
			}
			
			List<Uri> listaVideos = new ArrayList<Uri>();
			Iterator<String> itu = videos.iterator();
			while(itu.hasNext())
			{
				listaVideos.add(Uri.parse(itu.next()));
			}			
			
			VideoAlert alert = new VideoAlert(getActivity(), getString(tipsTitle), listaMensajes, getString(R.string.text_button_ready), listaVideos);
			alert.show();
		}
	}
	
	protected void sendAlertMessage(int tipsTitle, int tipsDescription, String tipsPath)
	{
		if(GamePreferences.TIPS_ENABLED())
		{
			VideoAlert alert = new VideoAlert(getActivity(), getString(tipsTitle), getString(tipsDescription), getString(R.string.text_button_ready), Uri.parse(tipsPath));
			alert.show();
		}
	}
	
	protected void sendToastMessage(int toastMessage)
	{
		Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
	}
	
	protected void sendMessage(int tipsTitle, int tipsDescription, String tipsPath, int toastMessage)
	{
		if(GamePreferences.TIPS_ENABLED())
		{
			sendAlertMessage(tipsTitle, tipsDescription, tipsPath);
		}
		else
		{
			sendToastMessage(toastMessage);
		}
	}
}