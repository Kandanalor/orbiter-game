package de.kandanalor.orbiter;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import de.kandanalor.orbiter.game.GameObject;
import de.kandanalor.orbiter.game.World;
import de.kandanalor.orbiter.interfaces.GameStateListener;
import de.kandanalor.orbiter.ui.GameObjectUI;
import de.kandanalor.orbiter.ui.TouchInputType;
import de.kandanalor.orbiter.ui.Zoom;

public class DrawPanel  extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, OnTouchListener {
	

	private GameLoop gameloop;
	private World world = null;
	
	private static final String TAG = "DrawPanel";
	


	
	//UI elements
	TouchInputType inputmode = null;
	//these points are translated
	private PointF[] t_startPos = new PointF[2];
	//and these arent
	private int[]  pointer_ids = new int[2];
	private PointF[] startPos = new PointF[2];
	private PointF startTranslate = null;
	float startscale = 0;
	
	private GameObject selected_obj = null;
	private Zoom zoom = new Zoom(this);
	//HashMap<GameObject,MovementKnob> mov_knobs = new HashMap<GameObject,MovementKnob>(); 
	//ArrayList<GameObjectUI> gobject_ui = new ArrayList<GameObjectUI>(); 
	
	
	public DrawPanel(Context context, AttributeSet attrs) {
		super(context, attrs); 
		init(context);
	}
	
	public DrawPanel(Context context) {
		super(context);
		init(context);
	}
	private void init(Context context) {
		getHolder().addCallback(this);
		
		setOnTouchListener(this);
		
		gameloop = new GameLoop(getHolder(), this);
		setFocusable(true);
		setWorld(null);
		
		GameObjectUI.setCtx(context);
	}
	


	@Override
	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		if(zoom.isAutozoom() && gameloop.isRunning()) {
			zoom.autoZoom();
		}
		canvas.scale(zoom.getScale(), zoom.getScale());
		canvas.translate(zoom.getVerschiebung().x, zoom.getVerschiebung().y);
		
		
		
		world.onDraw(canvas);
		
		
		for(GameObject o : world.getObjects()) {
				GameObjectUI.getUIfor(o).draw(canvas, gameloop);
		}
		
		/*
		 * If you want to see the center
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStrokeWidth(7);
		canvas.drawPoint(getCenter().x, getCenter().y, paint);*/
	}
	public void onPause(Canvas canvas) {
		/*for(GameObject o : world.getObjects()) {
			mov_knobs.put(o, new MovementKnob(o, getContext()));
		}*/
		/*canvas.setMatrix(new Matrix());
       	Paint paint = new Paint();
    	paint.setColor(Color.LTGRAY);
    	paint.setStyle(Style.FILL);

    	paint.setTextSize(80);
    	canvas.drawText("Game Paused", (canvas.getWidth())/2-200, (canvas.getHeight())/2, paint);*/
	}
	public void onGameOver(Canvas canvas) {
		canvas.setMatrix(new Matrix());
       	Paint paint = new Paint();
    	paint.setColor(Color.LTGRAY);
    	paint.setStyle(Style.FILL);

    	paint.setTextSize(80);
    	canvas.drawText("Game Over", (canvas.getWidth())/2-200, (canvas.getHeight())/2, paint);
    	//canvas.drawText("Game Over", 0, 0,  paint);
	}


	

	/*private ArrayList<GameObjectUI> getGObjectUIs() {
		for(GameObject o : world.getObjects()) {
			if(!gobject_ui.contains(o)) {
				gobject_ui.add(new GameObjectUI(o, getContext()));
			}
		}
		return gobject_ui;
	}*/

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		PointF t_point = zoom.translateTo(new PointF(event.getX(), event.getY()));
		PointF point = new PointF(event.getX(), event.getY());
		

		
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		//Log.d(TAG, "Action: " + event.getAction() + " action(mask): " + action + " pointer_index: " + pointerIndex);
		
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			//Log.d(TAG, " Pointerpos: " + t_point.x + " " + t_point.y);
			inputmode = null;
			
					
			if(event.getPointerCount() == 2) {
				inputmode = TouchInputType.SCALE;
				t_point = zoom.translateTo(new PointF(event.getX(1), event.getY(1)));
				point = new PointF(event.getX(1), event.getY(1));
				t_startPos[1] = t_point; 
				startPos[1] = point;
				pointer_ids[1] =  event.getPointerId(1);
				startscale = zoom.getScale();
			}
			else {
				pointer_ids[0] =  event.getPointerId(0);
				t_startPos[0] = t_point;
				startPos[0] = point;
				for(GameObject planet : world.getObjects()) {
					PointF movknob = GameObjectUI.getUIfor(planet).getMovKnob().getPosition();
					//Log.d(TAG, "Movknob: " + movknob.x+" "+movknob.y);
					if(new PointF(t_point.x - movknob.x, t_point.y - movknob.y).length() < 30 / zoom.getScale()) {
						inputmode = TouchInputType.SET_MOVEMENT;
						selected_obj = planet;
					}
					
				}
				if(inputmode == null) {
					GameObject planet = world.getObjectOn(t_point, 30 / zoom.getScale());				
					if(planet == null) {
						inputmode = TouchInputType.MOVE_WORLD;
					}
					else {
						inputmode = TouchInputType.MOVE_PLANET;
						t_startPos[0] = planet.getPos();	
						selected_obj = planet;
					}
				}
			}
			
			if(inputmode == TouchInputType.MOVE_WORLD) {
				startTranslate = zoom.getCenter();
			}
		}
		else if (action == MotionEvent.ACTION_MOVE){
			switch(inputmode) {
				case MOVE_WORLD:
					PointF newcenter = new PointF(startTranslate.x + (startPos[0].x - point.x) / zoom.getScale(), startTranslate.y + (startPos[0].y - point.y) / zoom.getScale()); 
					zoom.setCenter(newcenter);
					//verschiebung.x = startTranslate.x + (point.x - startPos[0].x);
					//verschiebung.y = startTranslate.y + (point.y - startPos[0].y);
					//Log.d(TAG, "Verschiebung: " + verschiebung.x + " " + verschiebung.y);
					break;
				case MOVE_PLANET:
					selected_obj.setPos(t_point.x, t_point.y);
					GameObjectUI.getUIfor(selected_obj).clearPath();
					break;
				case SET_MOVEMENT:
					GameObjectUI.getUIfor(selected_obj).clearPath();
					GameObjectUI.getUIfor(selected_obj).getMovKnob().setPosition(new PointF(t_point.x, t_point.y));
					break;
				case SCALE:
					if(event.getPointerCount() == 2) {
						PointF ptr1 = new PointF(event.getX(0), event.getY(0));
						PointF ptr2 = new PointF(event.getX(1), event.getY(1));
						

						
						PointF middle = new PointF(ptr1.x - (ptr1.x - ptr2.x)/2, ptr1.y - (ptr1.y - ptr2.y)/2);
						PointF t_middle = zoom.translateTo(middle);
						
						float startdist = (float) Math.sqrt((startPos[0].x - startPos[1].x)*(startPos[0].x - startPos[1].x) + (startPos[0].y - startPos[1].y)*(startPos[0].y - startPos[1].y));
						
						float dist = (float) Math.sqrt((ptr1.x - ptr2.x) * (ptr1.x - ptr2.x) + (ptr1.y - ptr2.y)*(ptr1.y - ptr2.y));
						float newscale = startscale * dist / startdist;
						Log.d(TAG, "SCALE" +newscale);
						//zoom(new PointF(center.x + getCenter().x, center.y + getCenter().y), newscale);
						zoom.zoom(zoom.getCenter(), newscale);
						
						PointF t_middle_new = zoom.translateTo(middle);
						PointF dxy = new PointF(t_middle.x-t_middle_new.x, t_middle.y - t_middle_new.y);
						PointF center = zoom.getCenter();
						zoom.zoom(new PointF(center.x + dxy.x, center.y + dxy.y), newscale);
					}
			}
		}
		else if (action == MotionEvent.ACTION_UP  || action == MotionEvent.ACTION_POINTER_DOWN){

		}
		//Log.d(TAG, "InputType: " + inputmode);
		return true;
	}



	/*public PointF translateFrom(PointF to) {
		return new PointF((to.x + verschiebung.x) * scale, (to.y + verschiebung.y) * scale );
	}*/
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		zoom.autoZoom();
		//verschiebung = new PointF(getWidth()/2, getHeight()/2);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		zoom.setCenter(new PointF(getWidth()/2, getHeight()/2));
		//trans_pos.setTranslate(getWidth()/2, getHeight()/2);
		// TODO Auto-generated method stub
		gameloop.setRunning(true);
		gameloop.start();
		zoom.autoZoom();
	//	ball.setPos(getWidth()/2, getHeight()/2);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		gameloop.setRunning(false);
		while (retry) {
			try {
				gameloop.getThread().join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}

	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
		if(world == null) {
			this.world = new World();
		}
		this.world.loadBitmaps(getContext());
	}



	

	public GameLoop getGameLoop() {
		return gameloop;
	}

	public GameObject getSelected_obj() {
		return selected_obj;
	}

	public void setSelected_obj(GameObject selected_obj) {
		this.selected_obj = selected_obj;
	}
	public void setGameStateListener(GameStateListener listener) {
		gameloop.setGameStateListener(listener);
	}

	public Zoom getZoom() {
		return zoom;
	}

	


}
