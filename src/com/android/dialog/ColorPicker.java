package com.android.dialog;

import android.app.ActionBar.LayoutParams;
import android.app.SearchManager.OnDismissListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.create.paint.PaintGLSurfaceView;
import com.example.main.R;

public class ColorPicker extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private ImageView mArrowDown;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;
	
	private boolean mDidAction;

//	private int mOrientation;
	private int rootWidth = 0;

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	View viewCuadroColores;
    ColorPickerKotak viewCuadroPrincipal;
    ImageView viewCursor;
    private View viewNewColor;
    ImageView viewTarget;
    ViewGroup viewContainer;
    final float[] currentColorHsv = new float[3];


	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context
	 *            Context
	 */
	public ColorPicker(final Context context, final PaintGLSurfaceView canvas, int color) {
		this(context, VERTICAL, canvas, color);
		final ImageButton ibAceptar = (ImageButton)this.getView().findViewById(R.id.ibAceptar);
		final ImageButton ibCancelar = (ImageButton)this.getView().findViewById(R.id.ibCancelar);
		
		ibCancelar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
			
					}
		});
		
		
		ibAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.seleccionarColor(getColor());	
				
			}
		});
		
		Color.colorToHSV(color, currentColorHsv);
		
        viewCuadroColores = mRootView.findViewById(R.id.color_cuadroColores);
        viewCuadroPrincipal = (ColorPickerKotak) mRootView.findViewById(R.id.color_viewCuadroPrincipal);
        viewCursor = (ImageView) mRootView.findViewById(R.id.color_cursor);
        viewNewColor = mRootView.findViewById(R.id.color_cuadroSeleccionado);
        viewTarget = (ImageView) mRootView.findViewById(R.id.color_target);
        viewContainer = (ViewGroup) mRootView.findViewById(R.id.color_viewContainer);

        viewCuadroPrincipal.setHue(getHue());
        viewNewColor.setBackgroundColor(color);

        viewCuadroColores.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroColores.getMeasuredHeight())
                    	y = viewCuadroColores.getMeasuredHeight() - 0.001f; // para permitir looping desde el final al principio.
                    float hue = 360.f - 360.f / viewCuadroColores.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // Actualizar la vista
                    viewCuadroPrincipal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                  

                    return true;
                }
                return false;
            }
        });
        viewCuadroPrincipal.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // evento touch en unidades dp.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewCuadroPrincipal.getMeasuredWidth()) x = viewCuadroPrincipal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroPrincipal.getMeasuredHeight()) y = viewCuadroPrincipal.getMeasuredHeight();

                    setSat(1.f / viewCuadroPrincipal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewCuadroPrincipal.getMeasuredHeight() * y));

                    // Actualizar la vista
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());

                    return true;
                }
                return false;
            }
        });
        
        // mueve el cursor y el color objetivo en su primer dibujo
        ViewTreeObserver vto = mRootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
	public ColorPicker(final Context context, int orientation, final PaintGLSurfaceView canvas, final int color) {
		super(context);

		//mOrientation = orientation;

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		setRootViewId(R.layout.color_layout);
	
	
		final ImageButton ibAceptar = (ImageButton)this.getView().findViewById(R.id.ibAceptar);
		final ImageButton ibCancelar = (ImageButton)this.getView().findViewById(R.id.ibCancelar);
		
		ibCancelar.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dismiss();
					}
		});
		
		ibAceptar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.seleccionarColor(getColor());	
				dismiss();
			}
		});
		
		Color.colorToHSV(color, currentColorHsv);
		canvas.seleccionarColor(color);

        viewCuadroColores = mRootView.findViewById(R.id.color_cuadroColores);
        viewCuadroPrincipal = (ColorPickerKotak) mRootView.findViewById(R.id.color_viewCuadroPrincipal);
        viewCursor = (ImageView) mRootView.findViewById(R.id.color_cursor);
        viewNewColor = mRootView.findViewById(R.id.color_cuadroSeleccionado);
        viewTarget = (ImageView) mRootView.findViewById(R.id.color_target);
        viewContainer = (ViewGroup) mRootView.findViewById(R.id.color_viewContainer);

        viewCuadroPrincipal.setHue(getHue());
        viewNewColor.setBackgroundColor(color);
        viewCuadroColores.setOnTouchListener(new View.OnTouchListener() {
            @Override 
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroColores.getMeasuredHeight())
                    	y = viewCuadroColores.getMeasuredHeight() - 0.001f; // para permitir looping desde el final al principio.
                    float hue = 360.f - 360.f / viewCuadroColores.getMeasuredHeight() * y;
                    if (hue == 360.f) hue = 0.f;
                    setHue(hue);

                    // Actualizar la vista
                    viewCuadroPrincipal.setHue(getHue());
                    moveCursor();
                    viewNewColor.setBackgroundColor(getColor());
                    return true;
                }
                return false;
            }
        });
        viewCuadroPrincipal.setOnTouchListener(new View.OnTouchListener() {
            @Override 
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE
                        || event.getAction() == MotionEvent.ACTION_DOWN
                        || event.getAction() == MotionEvent.ACTION_UP) {

                    float x = event.getX(); // evento touch en unidades dp.
                    float y = event.getY();

                    if (x < 0.f) x = 0.f;
                    if (x > viewCuadroPrincipal.getMeasuredWidth()) x = viewCuadroPrincipal.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > viewCuadroPrincipal.getMeasuredHeight()) y = viewCuadroPrincipal.getMeasuredHeight();

                    setSat(1.f / viewCuadroPrincipal.getMeasuredWidth() * x);
                    setVal(1.f - (1.f / viewCuadroPrincipal.getMeasuredHeight() * y));

                    // Actualizar la vista
                    moveTarget();
                    viewNewColor.setBackgroundColor(getColor());
                   
                    return true;
                }
                return false;
            }
        });
       
        // mueve el cursor y el color objetivo en su primer dibujo
        ViewTreeObserver vto = mRootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override 
            public void onGlobalLayout() {
                moveCursor();
                moveTarget();
                mRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
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


	/**
	 * Set listener for window dismissed. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or
	 * clicking on sticky item.
	 */
	public void setOnDismissListener(ColorPicker.OnDismissListener listener) {
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

	//Metodos modificacion del Cuadro de Colores
	protected void moveCursor() {
	    float y = viewCuadroColores.getMeasuredHeight() - (getHue() * viewCuadroColores.getMeasuredHeight() / 360.f);
	    if (y == viewCuadroColores.getMeasuredHeight()) y = 0.f;
	    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
	    layoutParams.leftMargin = (int) (viewCuadroColores.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
	    ;
	    layoutParams.topMargin = (int) (viewCuadroColores.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
	    ;
	    viewCursor.setLayoutParams(layoutParams);
	}
	protected void moveTarget() {
        float x = getSat() * viewCuadroPrincipal.getMeasuredWidth();
        float y = (1.f - getVal()) * viewCuadroPrincipal.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
        layoutParams.leftMargin = (int) (viewCuadroPrincipal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
        layoutParams.topMargin = (int) (viewCuadroPrincipal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
        viewTarget.setLayoutParams(layoutParams);
    }
	
	
	private int getColor() {
	    return Color.HSVToColor(currentColorHsv);
	}
	
	private float getHue() {
	    return currentColorHsv[0];
	}
	
	private float getSat() {
	    return currentColorHsv[1];
	}
	
	private float getVal() {
	    return currentColorHsv[2];
	}
	
	private void setHue(float hue) {
	    currentColorHsv[0] = hue;
	}
	
	private void setSat(float sat) {
	    currentColorHsv[1] = sat;
	}
	
	private void setVal(float val) {
	    currentColorHsv[2] = val;
	}

}