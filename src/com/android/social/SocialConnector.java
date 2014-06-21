package com.android.social;

import java.io.File;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.widget.Toast;

import com.android.alert.WebAlert;
import com.project.main.R;

public abstract class SocialConnector
{
	private Context mContext;

	private TStateSocial estadoTwitter, estadoFacebook;
	private TwitterConnector conectorTwitter;
	private FacebookConnector conectorFacebook;

	/* Constructora */

	public SocialConnector(Context context)
	{
		mContext = context;

		estadoTwitter = TStateSocial.Disconnected;
		estadoFacebook = TStateSocial.Disconnected;

		conectorTwitter = new TwitterConnector();
		conectorFacebook = new FacebookConnector();
	}
	
	/* M�todos Abstractos */
	
	public abstract void onConectionStatusChange();

	/* M�todos Comprobaci�n de Conexi�n */

	private boolean comprobarConexionInternet()
	{
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	private boolean comprobarConexionSocial()
	{
		return isTwitterConnected() || isFacebookConnected();
	}

	private boolean evaluarConexionInternet()
	{
		if (!comprobarConexionInternet())
		{
			Toast.makeText(mContext, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();

			desconectarTwitter();
			desconectarFacebook();

			return false;
		}

		return true;
	}

	private boolean evaluarConexionSocial()
	{
		if (!comprobarConexionSocial())
		{
			Toast.makeText(mContext, R.string.error_social_connection, Toast.LENGTH_SHORT).show();

			return false;
		}

		return true;
	}

	private void evaluarRespuestaOAuth(String url, int title)
	{
		WebAlert alert = new WebAlert(mContext, title, R.string.text_button_close) {
			@Override
			public boolean evaluarURL(String url)
			{
				if (url.toString().startsWith(SocialInformation.TWITTER_CALLBACK_URL))
				{
					if (estadoTwitter == TStateSocial.OAuth)
					{
						conectarTwitterFinal(Uri.parse(url));
						dismiss();
						return false;
					}
				}
				else if (url.toString().startsWith(SocialInformation.FACEBOOK_CALLBACK_URL))
				{
					if (estadoFacebook == TStateSocial.OAuth)
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

	/* M�todos Conexi�n y Desconexi�n */

	public void conectarTwitter()
	{
		if (evaluarConexionInternet())
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
		if (conectorTwitter.iniciarAutorizacion())
		{
			estadoTwitter = TStateSocial.OAuth;
			evaluarRespuestaOAuth(conectorTwitter.getAuthorizationURL(), R.string.text_twitter_title);
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}

	private void conectarTwitterFinal(Uri uri)
	{
		if (conectorTwitter.finalizarAutorizacion(uri))
		{
			estadoTwitter = TStateSocial.Connected;

			Toast.makeText(mContext, R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}

		onConectionStatusChange();
	}

	public void desconectarTwitter()
	{
		if (conectorTwitter.desconexion())
		{
			estadoTwitter = TStateSocial.Disconnected;
		}

		onConectionStatusChange();
	}

	public void conectarFacebook()
	{
		if (evaluarConexionInternet())
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
		if (conectorFacebook.iniciarAutorizacion())
		{
			estadoFacebook = TStateSocial.OAuth;
			evaluarRespuestaOAuth(conectorFacebook.getAuthorizationURL(), R.string.text_facebook_title);
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}

	private void conectarFacebookFinal(Uri uri)
	{
		if (conectorFacebook.finalizarAutorizacion(uri))
		{
			estadoFacebook = TStateSocial.Connected;

			Toast.makeText(mContext, R.string.text_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}

		onConectionStatusChange();
	}

	public void desconectarFacebook()
	{
		if (conectorFacebook.desconexion())
		{
			estadoFacebook = TStateSocial.Disconnected;
		}

		onConectionStatusChange();
	}

	/* M�todos Publicaci�n de Estados */

	public void publicar(String text)
	{
		if (evaluarConexionInternet() && evaluarConexionSocial())
		{
			if (isTwitterConnected()) 
			{
				publicarTwitter(text);
			}

			if (isFacebookConnected())
			{
				publicarFacebook(text);
			}
		}
	}

	private void publicarTwitter(String text)
	{
		if (conectorTwitter.enviarPost(text))
		{
			Toast.makeText(mContext, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else 
		{
			Toast.makeText(mContext, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}

	private void publicarFacebook(String text) 
	{
		if (conectorFacebook.enviarPost(text))
		{
			Toast.makeText(mContext, R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}

	/* M�todos Publicaci�n de Fotos */

	public void publicar(String text, File foto)
	{
		if (evaluarConexionInternet() && evaluarConexionSocial())
		{
			if (estadoTwitter == TStateSocial.Connected)
			{
				publicarTwitter(text, foto);
			}

			if (estadoFacebook == TStateSocial.Connected)
			{
				publicarFacebook(text, foto);
			}
		}
	}

	private void publicarTwitter(String text, File foto)
	{
		if (conectorTwitter.enviarPost(text, foto))
		{
			Toast.makeText(mContext, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}

	private void publicarFacebook(String text, File foto)
	{
		if (conectorFacebook.enviarPost(text, foto))
		{
			Toast.makeText(mContext, R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public boolean isTwitterConnected()
	{
		return estadoTwitter == TStateSocial.Connected;
	}

	public boolean isFacebookConnected()
	{
		return estadoFacebook == TStateSocial.Connected;
	}
}
