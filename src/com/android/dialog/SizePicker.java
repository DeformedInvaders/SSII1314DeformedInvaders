package com.android.dialog;

import android.app.ActionBar.LayoutParams;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.create.paint.PaintGLSurfaceView;
import com.example.main.R;

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

	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context
	 *            Context
	 */
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

	/**
	 * Constructor allowing orientation override
	 * 
	 * @param context
	 *            Context
	 * @param orientation
	 *            Layout orientation, can be vartical or horizontal
	 */
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
	
	/**
	 * Set root view.
	 * 
	 * @param id
	 *            Layout resource id
	 */
	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);

		mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);

		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		setContentView(mRootView);
	}



	
	/**
	 * Show quickaction popup. Popup is automatically positioned, on top or
	 * bottom of anchor view.
	 * 
	 */
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

	

	/**
	 * Show arrow
	 * 
	 * @param whichArrow
	 *            arrow type resource id
	 * @param requestedX
	 *            distance from left screen
	 */
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
	
	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
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

	/**
	 * Listener for item click
	 * 
	 */
	public interface OnActionItemClickListener {
		public abstract void onItemClick(SizePicker source, int pos,
				int actionId);
	}

	/**
	 * Listener for window dismiss
	 * 
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}



}