package com.android.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.alert.VideoAlert;
import com.android.alert.VideoListAlert;
import com.project.main.R;
import com.project.model.GamePreferences;

public class AlertFragment extends Fragment
{
	protected void sendAlertMessage(int tipsTitle, List<Integer> mensajes, List<String> videos)
	{
		if(GamePreferences.IS_TIPS_ENABLED())
		{			
			List<Uri> listaVideos = new ArrayList<Uri>();
			Iterator<String> itu = videos.iterator();
			while(itu.hasNext())
			{
				listaVideos.add(Uri.parse(itu.next()));
			}			
			
			VideoListAlert alert = new VideoListAlert(getActivity(), tipsTitle, mensajes, R.string.text_button_ready, listaVideos);
			alert.show();
		}
	}
	
	protected void sendAlertMessage(int tipsTitle, int tipsDescription, String tipsPath)
	{
		if(GamePreferences.IS_TIPS_ENABLED())
		{
			VideoAlert alert = new VideoAlert(getActivity(), tipsTitle, tipsDescription, R.string.text_button_ready, Uri.parse(tipsPath));
			alert.show();
		}
	}
	
	protected void sendToastMessage(int toastMessage)
	{
		Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
	}
	
	protected void sendMessage(int tipsTitle, int tipsDescription, String tipsPath, int toastMessage)
	{
		if(GamePreferences.IS_TIPS_ENABLED())
		{
			sendAlertMessage(tipsTitle, tipsDescription, tipsPath);
		}
		else
		{
			sendToastMessage(toastMessage);
		}
	}
}
