package de.kandanalor.orbiter.game;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import de.kandanalor.orbiter.R;
import de.kandanalor.orbiter.exceptions.ObjectCollisionException;
import de.kandanalor.orbiter.physics.Force;

public class World extends GameObject{
	private Bitmap background = null;
	
	private static final String TAG = "World";
	

	
	
	private ArrayList<GameObject> objects = new ArrayList<GameObject>(); 

	
	public World(Context context) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		background = BitmapFactory.decodeResource(context.getResources(), R.drawable.stars, options);
		
		Moon moon = new Moon(context);
		Merkur merkur = new Merkur(context);
		Earth earth = new Earth(context);
		
        earth.setPos(0,-200);
        earth.setMovement(0,0);
        
        merkur.setPos(0,0);
        merkur.setMovement(-50,0);

        
        moon.setPos(200,200);
        moon.setMovement(0,-50);
        
        objects.add(moon);
        
        objects.add(merkur);
        objects.add(earth);
	}
	
	public void onDraw(Canvas canvas) {
		//Log.d(TAG, "onDraw");
		Paint paint = new Paint();
		int isRusty = 1;

		canvas.save();
		canvas.setMatrix(new Matrix());
		//canvas.drawColor(Color.BLACK);
		for(int x = canvas.getClipBounds().left; x < canvas.getClipBounds().right; x += background.getWidth()) {
			for(int y = canvas.getClipBounds().top; y < canvas.getClipBounds().bottom; y += background.getHeight()) {
				canvas.drawBitmap(background, x, y, null);
			}
		}
		canvas.restore();
		
		
		for(GameObject o : objects) {
			o.onDraw(canvas);
		}
		//moon.onDraw(canvas);
		//earth.onDraw(canvas);
	}

	int gravc = 0;

	@Override
	public void updatePhysics() throws ObjectCollisionException {
		int dtime = getDeltaTime();
		for(int i = 0; i < objects.size(); i++) {
			for(int j = i+1; j < objects.size(); j++) {
				float normierung = (float)dtime / 1000;
				Force gravitation = objects.get(i).getGravitation(objects.get(j));
				gravitation.x *= normierung;
				gravitation.y *= normierung;
				//Log.d(TAG, "getGravitation "+gravc);
				objects.get(i).applyForce(gravitation);
				objects.get(j).applyForce(gravitation.invert());
			}
			objects.get(i).updatePhysics();
		}
		/*
		GameObject a = objects.get(0);//objects.get(0);
		GameObject b = objects.get(1);//objects.get(1);
		Force gravitation = a.getGravitation(b);
		//Log.d(TAG, "getGravitation "+gravitation);
		a.applyForce(gravitation);
		b.applyForce(gravitation.invert());
		a.updatePhysics();
		b.updatePhysics();*/
		gravc++;
		for(int i = 0; i < objects.size(); i++) {
			for(int j = i+1; j < objects.size(); j++) {
				if(objects.get(i).isCollision(objects.get(j))) {
					throw new ObjectCollisionException(objects.get(i), objects.get(j));
				}

			}
		}
	}
	@Override
	public void resumePhysics() {
		super.resumePhysics();
		for(GameObject o : objects) {
			o.resumePhysics();
		}
	}

	public GameObject getObjectOn(PointF position) {
		for(GameObject o : objects) {
			PointF op = o.getPos();
			
			float sq_dist = (float) Math.sqrt((op.x - position.x) * (op.x - position.x) + (op.y - position.y) * (op.y - position.y));
			Log.d(TAG, "Distance " + o.getName() + " " + sq_dist + " <=> " +  o.getRadius() );
			if(sq_dist <= o.getRadius()) {
				return o;
			}
		}
		return null;
	}

	public ArrayList<GameObject> getObjects() {
		return objects;
	}

}
