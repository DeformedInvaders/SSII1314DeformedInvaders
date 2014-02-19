package com.project.social;

import java.io.File;

import com.test.social.SocialInformation;

import android.net.Uri;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Media;
import facebook4j.PhotoUpdate;
import facebook4j.Post;
import facebook4j.PostUpdate;
import facebook4j.ResponseList;

public class FacebookConnector
{
	private static final String URL_FACEBOOK_OAUTH_VERIFIER = "code";
	
	private Facebook facebook;

	public boolean iniciarAutorizacion()
	{
        facebook = new FacebookFactory().getInstance();
        facebook.setOAuthAppId(SocialInformation.FACEBOOK_APP_ID, SocialInformation.FACEBOOK_APP_SECRET);
        facebook.setOAuthPermissions("publish_actions");
        return true;
	}
	
	public String getAuthorizationURL()
	{
		return facebook.getOAuthAuthorizationURL(SocialInformation.FACEBOOK_CALLBACK_URL);
	}
	
	public boolean finalizarAutorizacion(Uri uri)
	{
		try
		{
			facebook.getOAuthAccessToken(uri.getQueryParameter(URL_FACEBOOK_OAUTH_VERIFIER));
			return true;
		}
		catch (FacebookException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean desconexion()
	{		
		return true;
	}

	public boolean enviarPost(String message)
	{
		try
		{
			PostUpdate post = new PostUpdate(message);
			
			facebook.postFeed(post);
			//facebook.postStatusMessage(post);
			return true;
		}
		catch (FacebookException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean enviarPost(String message, File file)
	{
	    try
	    {
	    	PhotoUpdate photo = new PhotoUpdate(new Media(file));
	    	photo.setMessage(message);
	    	
	    	facebook.postPhoto(photo);
	    	return true;
    	}
    	catch (FacebookException e)
    	{
    		e.printStackTrace();
	    }
	    
	    return true;
	}
	
	public ResponseList<Post> getTimeLine()
	{
		try
		{
			ResponseList<Post> timeline = facebook.getHome();
			return timeline;
		}
		catch (FacebookException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
