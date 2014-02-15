package com.test.social;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import android.net.Uri;

public class TwitterConnectorTwitter4J implements SocialConnector
{
	private static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	
	private Twitter twitter;
	private RequestToken requestToken;

	
	@Override
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
	
	@Override
	public String getAuthorizationURL()
	{
		return requestToken.getAuthorizationURL();
	}
	
	@Override
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
	
	@Override
	public boolean desconexion()
	{
		requestToken = null;
		
		return true;
	}

	@Override
	public boolean enviarPost(String text)
	{
        try
        {
			twitter.updateStatus(text);
			return true;
		}
        catch (TwitterException e)
        {
			e.printStackTrace();
		}
        
		return true;
	}	
}
