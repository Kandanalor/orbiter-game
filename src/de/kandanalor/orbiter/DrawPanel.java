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
import de.kandanalor.orbiter.physics.Movement;
import de.kandanalor.orbiter.ui.TouchInputType;

public class DrawPanel  extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener, OnTouchListener {
	

	private GameLoop gameloop;
	private World world = null;
	private GameObject selected_obj = null;
	
	private static final String TAG = "DrawPanel";
	
	PointF onTouchStart = null;
	PointF translate_onTouchStart = null;
	
	PointF verschiebung = new PointF();
	float scale = 0.5f;
	
	//UI elements
	TouchInputType inputmode = null;


	
	
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
		
		world = new World(context);
		
	}
	


	@Override
	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		canvas.translate(verschiebung.x, verschiebung.y);
		canvas.scale(scale, scale);
		
		
		world.onDraw(canvas);
		
		if(gameloop.isPaused()) {
			for(GameObject planet : world.getObjects()) {
				drawPlanetUI(planet, canvas);
			}
		}
		
	}
	public void drawPlanetUI(GameObject planet, Canvas canvas) {
		if(planet != null) {
			drawMovVector(planet, canvas);
			
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.planetframe));
			//paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(30);
			
			PointF pos = planet.getPos();
			
			float abstaende = 20;
			RectF rect = new RectF(pos.x-planet.getRadius()-abstaende, pos.y-planet.getRadius()-abstaende, pos.x+planet.getRadius() + abstaende, pos.y+planet.getRadius()+abstaende);
			canvas.drawRoundRect(rect, 20.0f, 20.0f, paint);
			canvas.drawText(planet.getName(), pos.x-planet.getRadius() - abstaende, pos.y-planet.getRadius()-abstaende-10, paint);
			
			paint.setStrokeWidth(6);
			paint.setColor(0xFF40d010);
			
		}

	}
	public void drawMovVector(GameObject planet, Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		paint.setColor(getResources().getColor(R.color.mov_arrow));
		
		
		
		PointF pos = planet.getPos();
		//der Bewegungspfeil
		
		canvas.drawCircle(pos.x, pos.y, planet.getRadius()+2, paint);

		PointF[] arrow = getMovArrow(planet);
		PointF knob_point = arrow[1];
		
		canvas.drawLine(arrow[0].x, arrow[0].y , arrow[1].x, arrow[1].y, paint);
		
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		
		canvas.drawCircle(knob_point.x, knob_point.y, 10, paint);
		

		//= new PointF(end.x, end.y);
		//canvas.drawLine(start.x - (start.y-end.y)/10, start.y - (start.x-end.x)/10, start.x + (start.y-end.y)/10, start.y + (start.x-end.x) /10, paint);
		//new ArrowShape(planet.getPos(), mov).draw(canvas, paint);		
	}
	
	private PointF[] getMovArrow(GameObject planet) {
		int radius = planet.getRadius();
		PointF pos = planet.getPos();
		Movement mov = planet.getMovement();
		
		if(mov.length() > 0) {
			Movement norm = new Movement(mov.x / mov.length(), mov.y / mov.length());
			PointF end = new PointF(pos.x +mov.x, pos.y + mov.y);
			//Log.d(TAG, "Movement: " + mov.x + " " + mov.y);
			
			//strahlensatz
			float dist = (float) Math.sqrt((pos.x-end.x)*(pos.x-end.x) + (pos.y-end.y) * (pos.y-end.y));
			pos = new PointF(pos.x + norm.x * radius, pos.y + norm.y * radius);
			end = new PointF(pos.x +mov.x, pos.y + mov.y);
			return new PointF[]{pos, end};
		}
		else {
			return  new PointF[]{pos, pos};
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		PointF point = translateTo(new PointF(event.getX(), event.getY()));
		
		

		
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
		if (action == MotionEvent.ACTION_DOWN) {
			Log.d(TAG, " Pointerpos: " + point.x + " " + point.y);
			inputmode = null;
			
			if(event.getPointerCount() == 2) {
				inputmode = TouchInputType.SCALE;
			}
			else {			
				for(GameObject planet : world.getObjects()) {
					PointF movknob = getMovArrow(planet)[1];
					//Log.d(TAG, "Movknob: " + movknob.x+" "+movknob.y);
					if(new PointF(point.x - movknob.x, point.y - movknob.y).length() < 30) {
						inputmode = TouchInputType.SET_MOVEMENT;
					}
					
				}
				if(inputmode == null) {
					GameObject planet = world.getObjectOn(point);				
					if(planet == null) {
						inputmode = TouchInputType.MOVE_WORLD;
					}
					else {
						inputmode = TouchInputType.MOVE_PLANET;
					}
				}
			}
			
		}
		else if (action == MotionEvent.ACTION_MOVE){

		}
		else if (action == MotionEvent.ACTION_UP){

		}
		Log.d(TAG, "InputType: " + inputmode);
		return true;
	}

	public PointF translateTo(PointF from) {
		return new PointF((from.x - verschiebung.x)/ scale, (from.y - verschiebung.y)/ scale );
	}
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
		verschiebung = new PointF(getWidth()/2, getHeight()/2);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		verschiebung = new PointF(getWidth()/2, getHeight()/2);
		//trans_pos.setTranslate(getWidth()/2, getHeight()/2);
		// TODO Auto-generated method stub
		gameloop.setRunning(true);
		gameloop.start();

	//	ball.setPos(getWidth()/2, getHeight()/2);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		gameloop.setRunning(false);
		while (retry) {
			try {
				gameloop.join();
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

	public float getZoom() {
		return scale;
	}

	public void setZoom(float scale) {
		this.scale = scale;
	}

	public void onPause(Canvas canvas) {
		/*canvas.setMatrix(new Matrix());
       	Paint paint = new Paint();
    	paint.setColor(Color.LTGRAY);
    	paint.setStyle(Style.FILL);

    	paint.setTextSize(80);
    	canvas.drawText("Game Paused", (canvas.getWidth())/2-200, (canvas.getHeight())/2, paint);*/
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
}
