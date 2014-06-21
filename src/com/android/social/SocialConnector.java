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

	private TStateSocial mStateTwitter, mStateFacebook;
	private TwitterConnector connectorTwitter;
	private FacebookConnector connectorFacebook;

	/* Constructora */

	public SocialConnector(Context context)
	{
		mContext = context;

		mStateTwitter = TStateSocial.Disconnected;
		mStateFacebook = TStateSocial.Disconnected;

		connectorTwitter = new TwitterConnector();
		connectorFacebook = new FacebookConnector();
	}
	
	/* Métodos Abstractos */
	
	public abstract void onConectionStatusChange();

	/* Métodos Comprobación de Conexión */

	private boolean comprobarConexionInternet()
	{
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null;
	}

	private boolean isSocialConnected()
	{
		return isTwitterConnected() || isFacebookConnected();
	}

	private boolean isInternetConnected()
	{
		if (!comprobarConexionInternet())
		{
			Toast.makeText(mContext, R.string.error_internet_connection, Toast.LENGTH_SHORT).show();

			disconnectTwitter();
			disconnectFacebook();

			return false;
		}

		return true;
	}

	private boolean checkSocialConnection()
	{
		if (!isSocialConnected())
		{
			Toast.makeText(mContext, R.string.error_social_connection, Toast.LENGTH_SHORT).show();

			return false;
		}

		return true;
	}

	private void checkOAuth(String url, int title)
	{
		WebAlert alert = new WebAlert(mContext, title, R.string.text_button_close) {
			@Override
			public boolean evaluarURL(String url)
			{
				if (url.toString().startsWith(SocialInformation.TWITTER_CALLBACK_URL))
				{
					if (mStateTwitter == TStateSocial.OAuth)
					{
						connectTwitterEnd(Uri.parse(url));
						dismiss();
						return false;
					}
				}
				else if (url.toString().startsWith(SocialInformation.FACEBOOK_CALLBACK_URL))
				{
					if (mStateFacebook == TStateSocial.OAuth)
					{
						connectFacebookEnd(Uri.parse(url));
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

	public void connectTwitter()
	{
		if (isInternetConnected())
		{
			connectTwitterBegin();
		}
		else
		{
			disconnectTwitter();
		}
	}

	private void connectTwitterBegin()
	{
		if (connectorTwitter.startAuthorization())
		{
			mStateTwitter = TStateSocial.OAuth;
			checkOAuth(connectorTwitter.getAuthorizationURL(), R.string.text_twitter_title);
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}

	private void connectTwitterEnd(Uri uri)
	{
		if (connectorTwitter.finishAuthorization(uri))
		{
			mStateTwitter = TStateSocial.Connected;

			Toast.makeText(mContext, R.string.text_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}

		onConectionStatusChange();
	}

	public void disconnectTwitter()
	{
		if (connectorTwitter.disconnect())
		{
			mStateTwitter = TStateSocial.Disconnected;
		}

		onConectionStatusChange();
	}

	public void connectFacebook()
	{
		if (isInternetConnected())
		{
			connectFacebookBegin();
		}
		else
		{
			disconnectFacebook();
		}
	}

	public void connectFacebookBegin()
	{
		if (connectorFacebook.startAuthorization())
		{
			mStateFacebook = TStateSocial.OAuth;
			checkOAuth(connectorFacebook.getAuthorizationURL(), R.string.text_facebook_title);
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_oauth_permission, Toast.LENGTH_SHORT).show();
		}
	}

	private void connectFacebookEnd(Uri uri)
	{
		if (connectorFacebook.finishAuthorization(uri))
		{
			mStateFacebook = TStateSocial.Connected;

			Toast.makeText(mContext, R.string.text_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_oauth_sign_in, Toast.LENGTH_SHORT).show();
		}

		onConectionStatusChange();
	}

	public void disconnectFacebook()
	{
		if (connectorFacebook.disconnect())
		{
			mStateFacebook = TStateSocial.Disconnected;
		}

		onConectionStatusChange();
	}

	/* Métodos Publicación de Estados */

	public void sendPost(String text)
	{
		if (isInternetConnected() && checkSocialConnection())
		{
			if (isTwitterConnected()) 
			{
				sendPostTwitter(text);
			}

			if (isFacebookConnected())
			{
				sendPostFacebook(text);
			}
		}
	}

	private void sendPostTwitter(String text)
	{
		if (connectorTwitter.sendPost(text))
		{
			Toast.makeText(mContext, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else 
		{
			Toast.makeText(mContext, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}

	private void sendPostFacebook(String text) 
	{
		if (connectorFacebook.sendPost(text))
		{
			Toast.makeText(mContext, R.string.text_facebook_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_facebook_post, Toast.LENGTH_SHORT).show();
		}
	}

	/* Métodos Publicación de Fotos */

	public void sendPost(String text, File picture)
	{
		if (isInternetConnected() && checkSocialConnection())
		{
			if (mStateTwitter == TStateSocial.Connected)
			{
				sendPostTwitter(text, picture);
			}

			if (mStateFacebook == TStateSocial.Connected)
			{
				sendPostFacebook(text, picture);
			}
		}
	}

	private void sendPostTwitter(String text, File picture)
	{
		if (connectorTwitter.sendPost(text, picture))
		{
			Toast.makeText(mContext, R.string.text_twitter_post, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, R.string.error_twitter_post, Toast.LENGTH_SHORT).show();
		}
	}

	private void sendPostFacebook(String text, File picture)
	{
		if (connectorFacebook.sendPost(text, picture))
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
		return mStateTwitter == TStateSocial.Connected;
	}

	public boolean isFacebookConnected()
	{
		return mStateFacebook == TStateSocial.Connected;
	}
}
