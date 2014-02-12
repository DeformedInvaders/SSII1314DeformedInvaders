package com.android.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.create.paint.PaintFragment;
import com.project.main.R;

public class SizePicker extends WindowPicker
{
	private PaintFragment fragmento;
	
	private Button botonMas, botonMenos;
	private ImageView botonPincel;
	private int posicion;
	
	public SizePicker(Context context, PaintFragment view)
	{
		super(context, R.layout.dialog_size_layout);
		
		fragmento = view; 
		posicion = 0;
		
		botonMas = (Button) findViewById(R.id.imageButtonSize1);
		botonMenos = (Button) findViewById(R.id.imageButtonSize2);
		botonPincel = (ImageView) findViewById(R.id.imageButtonSize3);
		
		botonMas.setOnClickListener(new OnMasClickListener());
		botonMenos.setOnClickListener(new OnMenosClickListener());
		botonPincel.setOnClickListener(new OnPincelClickListener());
		
		actualizarBotones();
	}
	
	private void actualizarBotones()
	{
		if(posicion > 0)
		{
			botonMenos.setEnabled(true);
		}
		else
		{
			botonMenos.setEnabled(false);
		}
		
		if(posicion < 2)
		{
			botonMas.setEnabled(true);
		}
		else
		{
			botonMas.setEnabled(false);
		}
		
		actualizarImagen();
	}
	
	private void actualizarImagen()
	{
		
		Resources resources = fragmento.getActivity().getResources();
		
		switch(posicion)
		{
			case 0:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_small));
			break;
			case 1:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_medium));
			break;
			case 2:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_big));
			break;
		}
	}
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	private class OnMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			posicion++;
			actualizarBotones();
		}
	}
	
	private class OnMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			posicion--;
			actualizarBotones();
		}
	}
	
	private class OnPincelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			fragmento.seleccionarSize(posicion);
			dismiss();
		}
	}
}

/*
public class SizePicker extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;
	private int pos; //numero de imagen a mostrar



	private boolean mDidAction;

//	private int mOrientation;
	private int rootWidth = 0;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;


	public SizePicker(final Context context, final PaintGLSurfaceView canvas) {
		this(context, VERTICAL, canvas);
		final ImageButton ibincremento = (ImageButton)this.getView().findViewById(R.id.ibIncremento);
		final ImageButton ibContenido = (ImageButton)this.getView().findViewById(R.id.ibContenido);
		final ImageButton ibdecremento = (ImageButton)this.getView().findViewById(R.id.ibDecremento);
		
		ibincremento.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						ibdecremento.setEnabled(true);
						if (pos < 2)
							pos++;
						cambiarImagen(pos, canvas, context);
						if (pos == 2)
							ibincremento.setEnabled(false);	
					}
		});
		
		ibContenido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (pos){
				case 0: 
						canvas.seleccionarSize(0);
						dismiss();
						break;
				case 1:
						canvas.seleccionarSize(1);
						dismiss();
						break;
				case 2:
						canvas.seleccionarSize(2);
						dismiss();
						break;
				}
				
			}
		});
		
		ibdecremento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				ibincremento.setEnabled(true);
				if (pos > 0)
					pos--;
				cambiarImagen(pos, canvas, context);
				if (pos == 0)
					ibdecremento.setEnabled(false);
			}
		});
	}

	public SizePicker(final Context context, int orientation, final PaintGLSurfaceView canvas) {
		super(context);

		//mOrientation = orientation;

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setRootViewId(R.layout.dialog_size_layout);
	

		
	
		final ImageButton ibIncremento = (ImageButton)this.getView().findViewById(R.id.ibIncremento);
		final ImageButton ibContenido = (ImageButton)this.getView().findViewById(R.id.ibContenido);
		final ImageButton ibDecremento = (ImageButton)this.getView().findViewById(R.id.ibDecremento);
		
		ibIncremento.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						ibDecremento.setEnabled(true);
						if (pos < 2)
							pos++;
						cambiarImagen(pos, canvas, context);
						if (pos == 2)
							ibIncremento.setEnabled(false);	
					}
		});
		
		ibContenido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch (pos){
				case 0: 
						canvas.seleccionarSize(0);
						dismiss();
						break;
				case 1:
						canvas.seleccionarSize(1);
						onDismiss();
						dismiss();
						break;
				case 2:
						canvas.seleccionarSize(2);
						dismiss();
						break;
				}
				
			}
		});
		
		ibDecremento.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				ibIncremento.setEnabled(true);
				if (pos > 0)
					pos--;
				cambiarImagen(pos, canvas, context);
				if (pos == 0)
					ibDecremento.setEnabled(false);
			}
		});
	}


	
	public View getView(){
		return mRootView;
	}

	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}

	public void show(View anchor) {
		preShow();

		int xPos, yPos, arrowPos;

		mDidAction = false;

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);

		Rect anchorRect = new Rect(location[0], location[1], location[0]
				+ anchor.getWidth(), location[1] + anchor.getHeight());

		

		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		int rootHeight = mRootView.getMeasuredHeight();

		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}

		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;

			arrowPos = anchorRect.centerX() - xPos;

		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}

			arrowPos = anchorRect.centerX() - xPos;
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;

		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = 15;
				LayoutParams l = (LayoutParams) mTrack.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = (LayoutParams) mTrack.getLayoutParams();
				l.height = dyBottom;
			}
		}

		showArrow((R.id.arrow_down), arrowPos);

		
		mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
	}

	private void showArrow(int whichArrow, int requestedX) {
		final View showArrow =mArrowDown;

		final int arrowWidth = mArrowDown.getMeasuredWidth();

		showArrow.setVisibility(View.VISIBLE);

		ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow
				.getLayoutParams();

		param.leftMargin = requestedX - arrowWidth / 2;

	}

	public void cambiarImagen(int p, PaintGLSurfaceView canvas, Context context){
		ImageView ibContenido = (ImageView)this.getView().findViewById(R.id.ibContenido);
		switch (p){
		case 0: ibContenido.setImageDrawable(context.getResources().getDrawable(R.drawable.central_fino));
				canvas.seleccionarSize(0);
				break;
		case 1:ibContenido.setImageDrawable(context.getResources().getDrawable(R.drawable.central_medio));
				canvas.seleccionarSize(1);
				break;
		case 2:
				ibContenido.setImageDrawable(context.getResources().getDrawable(R.drawable.central_grande));
				canvas.seleccionarSize(2);
				break;
		}
	}
	

	public void setOnDismissListener(SizePicker.OnDismissListener listener) {
		setOnDismissListener( (OnDismissListener) this);

		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
		mDismissListener.onDismiss();
		}
	}

	public interface OnActionItemClickListener {
		public abstract void onItemClick(SizePicker source, int pos,
				int actionId);
	}
	
	public interface OnDismissListener {
		public abstract void onDismiss();
	}



}*/