package com.android.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.alert.VideoAlert;
import com.project.main.GamePreferences;
import com.project.main.R;

public class AlertFragment extends Fragment
{
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
