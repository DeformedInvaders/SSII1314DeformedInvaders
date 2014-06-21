package com.character.display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.BitmapImage;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.game.game.TStateGame;
import com.main.model.GamePreferences;

public class DisplayOpenGLRenderer extends OpenGLRenderer 
{
	private TStateDisplay mState;

	// Personaje
	private Character character;
	private boolean characterLoaded;

	// Captura
	private Bitmap screenshot;
	private TStateScreenshot mStateScreenshot;

	/* Constructura */

	public DisplayOpenGLRenderer(Context context)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character);

		GamePreferences.SET_GAME_PARAMETERS(TStateGame.Nothing);
		
		characterLoaded = false;

		mState = TStateDisplay.Nothing;
		mStateScreenshot = TStateScreenshot.Nothing;
	}

	public DisplayOpenGLRenderer(Context context, Character p)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character);
		
		GamePreferences.SET_GAME_PARAMETERS(TStateGame.Nothing);
		
		characterLoaded = true;
		character = p;

		mState = TStateDisplay.Nothing;
		mStateScreenshot = TStateScreenshot.Nothing;
	}

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		if (characterLoaded)
		{
			character.loadTexture(gl, this, mContext);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (characterLoaded)
		{
			if (mStateScreenshot == TStateScreenshot.Preparing)
			{
				// Marco Oscuro
				drawFrameInside(gl, Color.WHITE, GamePreferences.DEEP_INSIDE_FRAMES);
			}
			
			// Centrado de Marco
			drawInsideFrameBegin(gl);

			character.drawTexture(gl, this);

			// Centrado de Marco
			drawInsideFrameEnd(gl);

			if (mState == TStateDisplay.Nothing || mState == TStateDisplay.Screenshot)
			{
				if (mState == TStateDisplay.Screenshot)
				{
					if (mStateScreenshot == TStateScreenshot.Capturing)
					{
						// Capturar Pantalla
						BitmapImage textura = getScreenshot(gl);
						screenshot = textura.getBitmap();

						// Desactivar Modo Captura
						mStateScreenshot = TStateScreenshot.Finished;

						// Restaurar posición anterior de la Cámara
						camaraRestore();

						// Reiniciar Renderer
						super.onDrawFrame(gl);

						// Centrado de Marco
						drawInsideFrameBegin(gl);

						character.drawTexture(gl, this);

						// Centrado de Marco
						drawInsideFrameEnd(gl);
					}
				}
			}
		}
	}

	/* Métodos de Modificación de Estado */

	public void selectPreparing(float height, float width)
	{
		mState = TStateDisplay.Screenshot;
		mStateScreenshot = TStateScreenshot.Preparing;
	}

	public void selectCapturing()
	{
		if (mState == TStateDisplay.Screenshot)
		{
			mStateScreenshot = TStateScreenshot.Capturing;
		}
	}

	public void selectFinished()
	{
		if (mState == TStateDisplay.Screenshot)
		{
			mState = TStateDisplay.Nothing;
			mStateScreenshot = TStateScreenshot.Nothing;
		}
	}

	public boolean playAnimation()
	{
		return character.animateTexture();
	}

	public void stopAnimation()
	{
		character.stopAnimation();
		
		mState = TStateDisplay.Nothing;
		mStateScreenshot = TStateScreenshot.Nothing;
	}

	public void startAnimation(TTypeMovement movimiento)
	{
		character.selectMovement(movimiento);
		
		mState = TStateDisplay.Animation;
		mStateScreenshot = TStateScreenshot.Nothing;
	}

	/* Métodos de Obtención de Información */

	public boolean isStateNothing()
	{
		return mState == TStateDisplay.Nothing;
	}

	public boolean isStatePreparing()
	{
		return mState == TStateDisplay.Screenshot && mStateScreenshot == TStateScreenshot.Preparing;
	}

	public boolean isStateCapturing()
	{
		return mState == TStateDisplay.Screenshot && mStateScreenshot == TStateScreenshot.Capturing;
	}

	public boolean isStateFinished()
	{
		return mState == TStateDisplay.Screenshot && mStateScreenshot == TStateScreenshot.Finished;
	}

	public boolean isStateAnimation()
	{
		return mState != TStateDisplay.Nothing && mState != TStateDisplay.Screenshot;
	}

	public Bitmap getScreenshot()
	{
		if (mStateScreenshot == TStateScreenshot.Capturing)
		{
			while (mStateScreenshot != TStateScreenshot.Finished);

			return screenshot;
		}

		return null;
	}

	/* Métodos de Guardado de Información */

	public void saveData()
	{
		if (characterLoaded)
		{
			character.deleteTexture(this);
		}
	}
}
