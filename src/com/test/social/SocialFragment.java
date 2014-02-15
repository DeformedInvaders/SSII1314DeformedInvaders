package com.test.social;

import android.app.Activity;
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

import com.android.alert.TextInputAlert;
import com.android.alert.WebAlert;
import com.project.main.R;

public class SocialFragment extends Fragment
{
	private TSocialEstado estadoTwitter, estadoFacebook;
	private TwitterConnector conectorTwitter;
	private FacebookConnector conectorFacebook;
	
	private ImageButton botonTwitter, botonFacebook, botonShare;
	
	public static final SocialFragment newInstance()
	{
		SocialFragment fragment = new SocialFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
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
		
		botonTwitter.setOnClickListener(new OnTwitterClickListener());
		botonFacebook.setOnClickListener(new OnFacebookClickListener());
		botonShare.setOnClickListener(new OnShareClickListener());
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		actualizarBotones();		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
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
		}
		else
		{
			botonShare.setVisibility(View.INVISIBLE);
		}
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
			botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter_connected);
			actualizarBotones();			
			
			Toast.makeText(getActivity(), R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void desconectarTwitter()
	{
		if(conectorTwitter.desconexion())
		{		
			estadoTwitter = TSocialEstado.Desconectado;
			botonTwitter.setBackgroundResource(R.drawable.icon_social_twitter);
			actualizarBotones();
		}
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
			botonFacebook.setBackgroundResource(R.drawable.icon_social_facebook_connected);
			actualizarBotones();			
			
			Toast.makeText(getActivity(), R.string.text_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(getActivity(), R.string.error_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void desconectarFacebook()
	{		
		if(conectorFacebook.desconexion())
		{	
			estadoFacebook = TSocialEstado.Desconectado;
			botonFacebook.setBackgroundResource(R.drawable.icon_social_facebook);
			actualizarBotones();
		}
	}
	
	/* Publicación */
	
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
	
	private class OnTwitterClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
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
	}
	
	private class OnFacebookClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
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
	}
	
	private class OnShareClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			TextInputAlert alert = new TextInputAlert(getActivity(), getString(R.string.text_social_title), getString(R.string.text_social_description), getString(R.string.text_button_send), getString(R.string.text_button_cancel)) {

				@Override
				public void onPossitiveButtonClick()
				{
					publicar(getText()+" "+getString(R.string.text_social_via));
				}

				@Override
				public void onNegativeButtonClick() { }
				
			};

			alert.show();
		}
	}
}
