package com.test.social;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.alert.ChooseAlert;
import com.android.alert.TextInputAlert;
import com.android.alert.WebAlert;
import com.android.social.FacebookConnector;
import com.android.social.SocialInformation;
import com.android.social.TSocialEstado;
import com.android.social.TwitterConnector;
import com.android.storage.ExternalStorageManager;
import com.project.main.R;

public class SocialFragment extends Fragment
{
	private ExternalStorageManager manager;
	
	private TSocialEstado estadoTwitter, estadoFacebook;
	private TwitterConnector conectorTwitter;
	private FacebookConnector conectorFacebook;
	
	private ImageButton botonTwitter, botonFacebook, botonShare, botonFoto, botonWifi;
	
	public static final SocialFragment newInstance()
	{
		SocialFragment fragment = new SocialFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		manager = new ExternalStorageManager();
		
		estadoTwitter = TSocialEstado.Desconectado;
		estadoFacebook = TSocialEstado.Desconectado;
		
		conectorTwitter = new TwitterConnector();
		conectorFacebook = new FacebookConnector();
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_social_layout, container, false);
		
		botonTwitter = (ImageButton) rootView.findViewById(R.id.imageButtonSocial1);
		botonFacebook = (ImageButton) rootView.findViewById(R.id.imageButtonSocial2);
		botonShare = (ImageButton) rootView.findViewById(R.id.imageButtonSocial3);
		botonFoto = (ImageButton) rootView.findViewById(R.id.imageButtonSocial4);
		botonWifi = (ImageButton) rootView.findViewById(R.id.imageButtonSocial5);
		
		botonTwitter.setOnClickListener(new OnTwitterClickListener());
		botonFacebook.setOnClickListener(new OnFacebookClickListener());
		botonShare.setOnClickListener(new OnShareClickListener());
		botonFoto.setOnClickListener(new OnFotoClickListener());
		botonWifi.setOnClickListener(new OnWifiClickListener());
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		actualizarBotones();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		manager = null;
		
		botonTwitter = null;
		botonFacebook = null;
		botonShare = null;
	}
	
	@Override
	public void onDetach()
	{		
		super.onDetach();
		
		conectorTwitter = null;
		conectorFacebook = null;
	}
	
	private void actualizarBotones()
	{
		if(estadoTwitter == TSocialEstado.Conectado || estadoFacebook == TSocialEstado.Conectado)
		{
			botonShare.setVisibility(View.VISIBLE);
			
			if(manager.getNumFicherosDirectorioImagen() > 0)
			{
				botonFoto.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			botonShare.setVisibility(View.INVISIBLE);
			botonFoto.setVisibility(View.INVISIBLE);
		}
		
		actualizarImagenesBotones();
	}
	
	private void actualizarImagenesBotones()
	{
		if(comprobarConexionInternet())
		{
			botonWifi.setBackgroundResource(R.drawable.icon_wifi_connected);
		}
		else
		{
			botonWifi.setBackgroundResource(R.drawable.icon_wifi);
		}
		
		if(estadoTwitter == TSocialEstado.Conectado)
		{
			botonTwitter.setBackgroundResource(R.drawable.icon_twitter_connected);
		}
		else
		{
			botonTwitter.setBackgroundResource(R.drawable.icon_twitter);
		}
		
		if(estadoFacebook == TSocialEstado.Conectado)
		{
			botonFacebook.setBackgroundResource(R.drawable.icon_facebook_connected);
		}
		else
		{
			botonFacebook.setBackgroundResource(R.drawable.icon_facebook);
		}
	}
	
	private boolean comprobarConexionInternet()
	{
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}
	
	private boolean evaluarConexionInternet()
	{
		if(!comprobarConexionInternet())
		{
			Toast.makeText(getActivity(), R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
			
			desconectarTwitter();
			desconectarFacebook();
			
			return false;
		}

		return true;
	}
	
	private void evaluarRespuestaOAuth(String url, String title)
	{
		WebAlert alert = new WebAlert(getActivity(), title, getString(R.string.text_button_close)) {
			@Override
			public boolean evaluarURL(String url)
			{
		        if(url.toString().startsWith(SocialInformation.TWITTER_CALLBACK_URL))
		    	{
		        	if(estadoTwitter == TSocialEstado.OAuth)
		        	{
		        		conectarTwitterFinal(Uri.parse(url));
		        		dismiss();
		        		return false;
		        	}
		        }
		        else if(url.toString().startsWith(SocialInformation.FACEBOOK_CALLBACK_URL))
		        {
		        	if(estadoFacebook == TSocialEstado.OAuth)
		        	{
		        		conectarFacebookFinal(Uri.parse(url));
		        		dismiss();
		        		return false;
		        	}
		        }
		        
		        return true;
			}
		};

		alert.loadURL(url);
		alert.show();
	}
	
	/* Conexión - Desconexión */
	
	private void conectarTwitterInicial()
	{		
		if(conectorTwitter.iniciarAutorizacion())
		{
			estadoTwitter = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorTwitter.getAuthorizationURL(), getString(R.string.text_twitter_title));
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_twitter_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void conectarTwitterFinal(Uri uri)
	{		
		if(conectorTwitter.finalizarAutorizacion(uri))
		{
			estadoTwitter = TSocialEstado.Conectado;
			botonTwitter.setBackgroundResource(R.drawable.icon_twitter_connected);			
			
			Toast.makeText(getActivity(), R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
	
		actualizarBotones();
	}
	
	private void desconectarTwitter()
	{
		if(conectorTwitter.desconexion())
		{		
			estadoTwitter = TSocialEstado.Desconectado;
		}
		
		actualizarBotones();
	}
	
	private void conectarFacebookInicial()
	{
		if(conectorFacebook.iniciarAutorizacion())
		{
			estadoFacebook = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorFacebook.getAuthorizationURL(), getString(R.string.text_facebook_title));
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_facebook_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void conectarFacebookFinal(Uri uri)
	{
		if(conectorFacebook.finalizarAutorizacion(uri))
		{
			estadoFacebook = TSocialEstado.Conectado;
			botonFacebook.setBackgroundResource(R.drawable.icon_facebook_connected);			
			
			Toast.makeText(getActivity(), R.string.text_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		
		actualizarBotones();
	}
	
	private void desconectarFacebook()
	{		
		if(conectorFacebook.desconexion())
		{	
			estadoFacebook = TSocialEstado.Desconectado;
			botonFacebook.setBackgroundResource(R.drawable.icon_facebook);
		}
	}
	
	/* Publicación Estado */
	
	public void publicar(String text)
	{
		if(estadoTwitter == TSocialEstado.Conectado)
		{
			publicarTwitter(text);
		}
		
		if(estadoFacebook == TSocialEstado.Conectado)
		{
			publicarFacebook(text);
		}
	}
	
	private void publicarTwitter(String text)
	{
		if(conectorTwitter.enviarPost(text))
		{
			Toast.makeText(getActivity(), R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void publicarFacebook(String text)
	{
		if(conectorFacebook.enviarPost(text))
		{
			Toast.makeText(getActivity(), R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	/* Publicación Foto */
	
	public void publicar(String text, File foto)
	{
		if(estadoTwitter == TSocialEstado.Conectado)
		{
			publicarTwitter(text, foto);
		}
		
		if(estadoFacebook == TSocialEstado.Conectado)
		{
			publicarFacebook(text, foto);
		}
	}
	
	private void publicarTwitter(String text, File foto)
	{
		if(conectorTwitter.enviarPost(text, foto))
		{
			Toast.makeText(getActivity(), R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void publicarFacebook(String text, File foto)
	{
		if(conectorFacebook.enviarPost(text, foto))
		{
			Toast.makeText(getActivity(), R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	private class OnTwitterClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(evaluarConexionInternet())
			{
				if(estadoTwitter == TSocialEstado.Conectado)
				{
					desconectarTwitter();
				}
				else
				{
					conectarTwitterInicial();
				}
			}
			
			actualizarBotones();
		}
	}
	
	private class OnFacebookClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(evaluarConexionInternet())
			{
				if(estadoFacebook == TSocialEstado.Conectado)
				{
					desconectarFacebook();
				}
				else
				{
					conectarFacebookInicial();
				}
			}
			
			actualizarBotones();
		}
	}
	
	private class OnShareClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(evaluarConexionInternet())
			{
				TextInputAlert alert = new TextInputAlert(getActivity(), getString(R.string.text_social_share_title), getString(R.string.text_social_share_description), getString(R.string.text_button_send), getString(R.string.text_button_cancel)) {
	
					@Override
					public void onPossitiveButtonClick()
					{
						publicar(anyadirSufijo(getText()));
					}
	
					@Override
					public void onNegativeButtonClick() { }
					
				};
	
				alert.show();
			}

			actualizarBotones();
		}
	}
	
	private class OnFotoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(evaluarConexionInternet())
			{				
				ChooseAlert alert = new ChooseAlert(getActivity(), getString(R.string.text_social_photo_title), getString(R.string.text_button_send), getString(R.string.text_button_cancel), manager.getFicherosDirectorioImagen()) {
					
					@Override
					public void onSelectedPossitiveButtonClick(String selected)
					{						
						publicar(getString(R.string.text_social_photo_initial)+" "+selected+" "+getString(R.string.text_social_photo_final), manager.cargarImagen(selected));
					}
					
					@Override
					public void onNoSelectedPossitiveButtonClick() { }
	
					@Override
					public void onNegativeButtonClick() { }
					
				};
	
				alert.show();
			}

			actualizarBotones();
		}
	}
	
	private String anyadirSufijo(String message)
	{
		return message+" "+getString(R.string.text_social_via);
	}
	
	private class OnWifiClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			evaluarConexionInternet();
			actualizarBotones();
		}
	}
}
