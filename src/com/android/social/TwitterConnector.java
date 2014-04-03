package com.android.social;

import java.io.File;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.net.Uri;

public class TwitterConnector
{
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";

	private Twitter twitter;
	private RequestToken requestToken;

	/* SECTION Métodos Públicos */

	public boolean iniciarAutorizacion()
	{
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(SocialInformation.TWITTER_CONSUMER_KEY, SocialInformation.TWITTER_CONSUMER_SECRET);

		try
		{
			requestToken = twitter.getOAuthRequestToken(SocialInformation.TWITTER_CALLBACK_URL);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public String getAuthorizationURL()
	{
		return requestToken.getAuthorizationURL();
	}

	public boolean finalizarAutorizacion(Uri uri)
	{
		try
		{
			twitter.getOAuthAccessToken(requestToken, uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER));
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean desconexion()
	{
		requestToken = null;

		return true;
	}

	public boolean enviarPost(String message)
	{
		try
		{
			StatusUpdate status = new StatusUpdate(message);

			twitter.updateStatus(status);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public boolean enviarPost(String message, File file)
	{
		try
		{
			StatusUpdate status = new StatusUpdate(message);
			status.setMedia(file);

			twitter.updateStatus(status);
			return true;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}

		return true;
	}

	public ResponseList<Status> getTimeLine()
	{
		try
		{
			ResponseList<Status> timeline = twitter.getUserTimeline(10);
			return timeline;
		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
