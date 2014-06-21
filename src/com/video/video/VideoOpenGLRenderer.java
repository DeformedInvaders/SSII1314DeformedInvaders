package com.video.video;

import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.project.main.R;
import com.video.data.InanimatedObject;
import com.video.data.TTypeActors;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	private TStateVideo mState;
	private int soundActive;
	
	private boolean textureLoaded;
	
	private Character scientific, guitarist;
	private List<InanimatedObject> objectList;

	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTypeBackgroundRenderer.Swappable, TTypeTexturesRenderer.Video);
		
		selectBackground(video.getListBackgrounds());
		
		soundActive = -1;
		mState = TStateVideo.Nothing;
		
		scientific = video.getActor(TTypeActors.Scientific);
		guitarist = video.getActor(TTypeActors.Guitarist);
		
		guitarist.selectMovement(TTypeMovement.Attack);
		scientific.selectMovement(TTypeMovement.Jump);
		
		objectList = video.getListObjects();
	
		textureLoaded = false;
		
		final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_video_title), mContext.getString(R.string.text_processing_video_description), true);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while(!textureLoaded);
				alert.dismiss();
			}
		});
		
		thread.start();
	
	}	
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		scientific.loadTexture(gl, this, mContext);
		guitarist.loadTexture(gl, this, mContext);
		
		Iterator<InanimatedObject> it = objectList.iterator();
		while(it.hasNext())
		{
			it.next().loadTexture(gl, this, mContext);
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		super.onSurfaceChanged(gl, width, height);
		
		textureLoaded = true;
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		// Lista Objetos
		
		for (int i = 0; i < objectList.size(); i++)
		{
			InanimatedObject objeto = objectList.get(i);
			
			if (objeto.getStateActive() == mState)
			{
				objeto.drawTexture(gl, this);
			}
		}
		
		// Centrado de Marco
		drawInsideFrameBegin(gl);

		if (mState == TStateVideo.Brief)
		{
			scientific.drawTexture(gl, this);
		}
		else if (mState == TStateVideo.Rock)
		{
			guitarist.drawTexture(gl, this);
		}
		
		// Centrado de Marco
		drawInsideFrameEnd(gl);
		
		if (mState == TStateVideo.Nothing)
		{
			drawFrameFill(gl, Color.argb(175, 0, 0, 0), GamePreferences.DEEP_OUTSIDE_FRAMES);
		}
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float worldX = convertPixelXToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);

		for (int i = 0; i < objectList.size(); i++)
		{
			InanimatedObject objeto = objectList.get(i);
			
			if (objeto.getStateActive() == mState && objeto.contains(worldX, worldY))
			{
				soundActive = objeto.getSoundActive();
				return true;
			}
		}
		
		return false;
	} 
	
	public void zoomScene(float factor)
	{
		camaraZoom(factor);
	}
	
	public void playAnimation()
	{
		if (scientific.animateTexture())
		{
			scientific.selectMovement(TTypeMovement.Jump);
		}
		
		if (guitarist.animateTexture())
		{
			guitarist.selectMovement(TTypeMovement.Attack);
		}
	}
	
	public boolean nextScene()
	{
		scientific.stopAnimation();
		guitarist.stopAnimation();
		
		moveBackground();
		return isBackgroundEnded();
	}
	
	public void selectScene(TStateVideo state)
	{
		mState = state;
		camaraRestore();
	}
	
	public void resetSoundActive()
	{
		soundActive = -1;
	}
	
	public int getSoundActive()
	{
		return soundActive;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		scientific.deleteTexture(this);
		guitarist.deleteTexture(this);
		
		Iterator<InanimatedObject> it = objectList.iterator();
		while(it.hasNext())
		{
			it.next().deleteTexture(this);
		}
	}
}