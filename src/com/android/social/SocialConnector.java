package com.android.social;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.widget.Toast;

import com.android.alert.WebAlert;
import com.project.main.MainActivity;
import com.project.main.R;
import com.test.social.SocialInformation;

public class SocialConnector
{
	private MainActivity activity;
	
	private TSocialEstado estadoTwitter, estadoFacebook;
	private TwitterConnector conectorTwitter;
	private FacebookConnector conectorFacebook;
	
	public SocialConnector(MainActivity context)
	{
		activity = context;
		
		estadoTwitter = TSocialEstado.Desconectado;
		estadoFacebook = TSocialEstado.Desconectado;
		
		conectorTwitter = new TwitterConnector();
		conectorFacebook = new FacebookConnector();
	}
	
	private boolean comprobarConexionInternet()
	{
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}
	
	private boolean evaluarConexionInternet()
	{
		if(!comprobarConexionInternet())
		{
			Toast.makeText(activity, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();
			
			desconectarTwitter();
			desconectarFacebook();
			
			return false;
		}

		return true;
	}
	
	private void evaluarRespuestaOAuth(String url, String title)
	{
		WebAlert alert = new WebAlert(activity, title, activity.getString(R.string.text_button_close)) {
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
	
	public void conectarTwitter()
	{
		if(evaluarConexionInternet())
		{
			conectarTwitterInicial();
		}
		else
		{
			desconectarTwitter();
		}
	}
	
	private void conectarTwitterInicial()
	{		
		if(conectorTwitter.iniciarAutorizacion())
		{
			estadoTwitter = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorTwitter.getAuthorizationURL(), activity.getString(R.string.text_twitter_title));
		}
		else
		{
			Toast.makeText(activity, R.string.error_twitter_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void conectarTwitterFinal(Uri uri)
	{		
		if(conectorTwitter.finalizarAutorizacion(uri))
		{
			estadoTwitter = TSocialEstado.Conectado;			
			
			Toast.makeText(activity, R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
	
		activity.actualizarActionBar();
	}
	
	public void desconectarTwitter()
	{
		if(conectorTwitter.desconexion())
		{		
			estadoTwitter = TSocialEstado.Desconectado;
		}
		
		activity.actualizarActionBar();
	}
	
	public void conectarFacebook()
	{
		if(evaluarConexionInternet())
		{
			conectarFacebookInicial();
		}
		else
		{
			desconectarFacebook();
		}
	}
	
	public void conectarFacebookInicial()
	{
		if(conectorFacebook.iniciarAutorizacion())
		{
			estadoFacebook = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorFacebook.getAuthorizationURL(), activity.getString(R.string.text_facebook_title));
		}
		else
		{
			Toast.makeText(activity, R.string.error_facebook_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void conectarFacebookFinal(Uri uri)
	{
		if(conectorFacebook.finalizarAutorizacion(uri))
		{
			estadoFacebook = TSocialEstado.Conectado;			
			
			Toast.makeText(activity, R.string.text_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		
		activity.actualizarActionBar();
	}
	
	public void desconectarFacebook()
	{		
		if(conectorFacebook.desconexion())
		{	
			estadoFacebook = TSocialEstado.Desconectado;
		}
		
		activity.actualizarActionBar();
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
			Toast.makeText(activity, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void publicarFacebook(String text)
	{
		if(conectorFacebook.enviarPost(text))
		{
			Toast.makeText(activity, R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
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
			Toast.makeText(activity, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void publicarFacebook(String text, File foto)
	{
		if(conectorFacebook.enviarPost(text, foto))
		{
			Toast.makeText(activity, R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(activity, R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}
	
	public boolean isTwitterConnected()
	{
		return estadoTwitter == TSocialEstado.Conectado;
	}
	
	public boolean isFacebookConnected()
	{
		return estadoFacebook == TSocialEstado.Conectado;
	}
}
