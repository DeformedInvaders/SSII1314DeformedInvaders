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

	private TSocialEstado estadoTwitter, estadoFacebook;
	private TwitterConnector conectorTwitter;
	private FacebookConnector conectorFacebook;

	/* Constructora */

	public SocialConnector(Context context)
	{
		mContext = context;

		estadoTwitter = TSocialEstado.Desconectado;
		estadoFacebook = TSocialEstado.Desconectado;

		conectorTwitter = new TwitterConnector();
		conectorFacebook = new FacebookConnector();
	}
	
	/* Métodos Abstractos */
	
	public abstract void onConectionStatusChange();

	/* Métodos Comprobación de Conexión */

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

	private void evaluarRespuestaOAuth(String url, String title)
	{
		WebAlert alert = new WebAlert(mContext, title, mContext.getString(R.string.text_button_close)) {
			@Override
			public boolean evaluarURL(String url)
			{
				if (url.toString().startsWith(SocialInformation.TWITTER_CALLBACK_URL))
				{
					if (estadoTwitter == TSocialEstado.OAuth)
					{
						conectarTwitterFinal(Uri.parse(url));
						dismiss();
						return false;
					}
				}
				else if (url.toString().startsWith(SocialInformation.FACEBOOK_CALLBACK_URL))
				{
					if (estadoFacebook == TSocialEstado.OAuth)
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

	/* Métodos Conexión y Desconexión */

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
			estadoTwitter = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorTwitter.getAuthorizationURL(), mContext.getString(R.string.text_twitter_title));
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
			estadoTwitter = TSocialEstado.Conectado;

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
			estadoTwitter = TSocialEstado.Desconectado;
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
			estadoFacebook = TSocialEstado.OAuth;
			evaluarRespuestaOAuth(conectorFacebook.getAuthorizationURL(), mContext.getString(R.string.text_facebook_title));
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
			estadoFacebook = TSocialEstado.Conectado;

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
			estadoFacebook = TSocialEstado.Desconectado;
		}

		onConectionStatusChange();
	}

	/* Métodos Publicación de Estados */

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

	/* Métodos Publicación de Fotos */

	public void publicar(String text, File foto)
	{
		if (evaluarConexionInternet() && evaluarConexionSocial())
		{
			if (estadoTwitter == TSocialEstado.Conectado)
			{
				publicarTwitter(text, foto);
			}

			if (estadoFacebook == TSocialEstado.Conectado)
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

	/* Métodos de Obtención de Información */

	public boolean isTwitterConnected()
	{
		return estadoTwitter == TSocialEstado.Conectado;
	}

	public boolean isFacebookConnected()
	{
		return estadoFacebook == TSocialEstado.Conectado;
	}
}
