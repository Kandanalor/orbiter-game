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
	

	private GameLoop canvasthread;
	private World world = null;
	private GameObject selected_obj = null;
	
	private static final String TAG = "DrawPanel";
	
	PointF onTouchStart = null;
	PointF translate_onTouchStart = null;
	
	PointF verschiebung = new PointF();
	float scale = 0.5f;
	
	//UI elements
	TouchInputType inputmode = null;
	PointF movement_knob = null;
	
	
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
		
		canvasthread = new GameLoop(getHolder(), this);
		setFocusable(true);
		
		world = new World(context);
		
		
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		canvas.translate(verschiebung.x, verschiebung.y);
		canvas.scale(scale, scale);
		
		
		world.onDraw(canvas);
		
		for(GameObject planet : world.getObjects()) {
			drawPlanetUI(planet, canvas);
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
		else
			movement_knob = null;
	}
	public void drawMovVector(GameObject planet, Canvas canvas) {
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(6);
		paint.setColor(getResources().getColor(R.color.mov_arrow));
		
		
		
		PointF pos = planet.getPos();
		//der Bewegungspfeil
		
		canvas.drawCircle(pos.x, pos.y, planet.getRadius()+2, paint);
		int radius = planet.getRadius();
		
		Movement mov = planet.getMovement();
		Movement norm = new Movement(mov.x / mov.length(), mov.y / mov.length());
		PointF end = new PointF(pos.x +mov.x, pos.y + mov.y);
		//Log.d(TAG, "Movement: " + mov.x + " " + mov.y);
		
		//strahlensatz
		float dist = (float) Math.sqrt((pos.x-end.x)*(pos.x-end.x) + (pos.y-end.y) * (pos.y-end.y));
		pos = new PointF(pos.x + norm.x * radius, pos.y + norm.y * radius);
		end = new PointF(pos.x +mov.x, pos.y + mov.y);
		
		canvas.drawLine(pos.x, pos.y , end.x, end.y, paint);
		
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		canvas.drawCircle(end.x, end.y, 10, paint);
		movement_knob = new PointF(end.x, end.y);
		//canvas.drawLine(start.x - (start.y-end.y)/10, start.y - (start.x-end.x)/10, start.x + (start.y-end.y)/10, start.y + (start.x-end.x) /10, paint);
		//new ArrowShape(planet.getPos(), mov).draw(canvas, paint);		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		PointF point = new PointF(event.getX(), event.getY());
		

		
		GameObject planet = world.getObjectOn(translateTo(point));
		if(selected_obj != null) {
			if(new PointF(point.x - movement_knob.x, point.y - movement_knob.y).length() < 10) {
				
			}
		}
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			selected_obj = planet;
		if(selected_obj  != null) {
			canvasthread.pauseL();
		}
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			//set this if it is drag event
			onTouchStart = new PointF(event.getX(), event.getY());
			translate_onTouchStart = new PointF(verschiebung.x, verschiebung.y);
			

		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE){
			if(selected_obj == null) {
				float dx = event.getX() - onTouchStart.x;
				float dy = event.getY() - onTouchStart.y;
				verschiebung.x = dx + translate_onTouchStart.x;
				verschiebung.y = dy + translate_onTouchStart.y;
			}
			else {
				float dx = event.getX();
				float dy = event.getY();

				selected_obj.setPos(translateTo(new PointF(dx, dy)));
			}
			//trans_pos.postTranslate(dx,dy);
		}
		else if (event.getAction() == MotionEvent.ACTION_UP){
			//Log.d(TAG, "Action: " +event.getAction());
			//selected_obj = world.getObjectOn(translateTo(point));
			//gucken ob ein Planet ausgewaehlt wurde
			/*if(Math.abs(onTouchStart.x - event.getX()) < 10 && Math.abs(onTouchStart.y - event.getY()) < 10) {
			
				PointF point = new PointF(event.getX(), event.getY());				
				GameObject planet = world.getObjectOn(translateTo(point));
				if(planet != null) {
					canvasthread.pauseL();
					selected_obj = planet;
				
			}*/
			
			onTouchStart = null;
			translate_onTouchStart = null;
			//selected_obj = null;
		}
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
	    canvasthread.setRunning(true);
	    canvasthread.start();

	//	ball.setPos(getWidth()/2, getHeight()/2);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		canvasthread.setRunning(false);
		while (retry) {
			try {
				canvasthread.join();
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
		return canvasthread;
	}

	public GameObject getSelected_obj() {
		return selected_obj;
	}

	public void setSelected_obj(GameObject selected_obj) {
		this.selected_obj = selected_obj;
	}
	public void setGameStateListener(GameStateListener listener) {
		canvasthread.setGameStateListener(listener);
	}
}
