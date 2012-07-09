package de.kandanalor.orbiter.game;

import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import de.kandanalor.orbiter.exceptions.ObjectCollisionException;
import de.kandanalor.orbiter.physics.Force;
import de.kandanalor.orbiter.physics.Movement;

public abstract class GameObject implements Cloneable, Serializable{
	
	/**
	 * Zum Serialisieren, weil ich es kann^^
	 */
	private static final long serialVersionUID = 42L;
	
	private PointF pos = new PointF(0,0);
	private Movement movement = new Movement(0,0); // in m/s
	private int mass = 1; //in kg
	private int radius = 0;//in m
	
	private long last_update =  System.currentTimeMillis();
	
	public static final int G = 600000; //Gravitationskonstante
	private static final String TAG = "GameObject";
	private String name = "";
	
	//this color is used as a symbol, e.g. in the GUI
	private int color = 0;
	
	
	public PointF getPos() {
		return pos;
	}

	public void setPos(float x, float y) {
		this.pos = new PointF(x,y);
	}
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}
	/**
	 * Returns the time in milliseconds since the last call of this function
	 * */
	protected int getDeltaTime() {
		long now = System.currentTimeMillis();
		int dtime = (int) (now - last_update);
		last_update = now;
		return dtime;
	}
	public void updatePhysics() throws ObjectCollisionException{
		double seconds = (double)(getDeltaTime()) / 1000;
		pos.x += movement.x * seconds;
		pos.y += movement.y * seconds;

	}
	public abstract void onDraw(Canvas canvas) ;
	public Movement getMovement() {
		return movement;
	}

	public void setMovement(float x, float y) {
		this.movement = new Movement(x,y);
	}
	public void addMovement(Movement mov) {
		movement.x += mov.x;
		movement.y += mov.y;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	public boolean isCollision(GameObject o) {
		float distance = new PointF(o.getPos().x - getPos().x, o.getPos().y - getPos().y).length();
		//Log.d(TAG, "Distance: " + distance + " Radia: " + getRadius() + " " + o.getRadius());
		return distance - (getRadius() + o.getRadius()) <= 0;
		//return false;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	public void pausePhysics() {
		// TODO Auto-generated method stub
		
	}
	public void resumePhysics() {
		getDeltaTime(); //flush paused time
	}
	/**
	 * returns Gravitation [F]=kg * (m / s^2)
	 * */
	public Force getGravitation(GameObject o) {
		/*PointF pos_a = getPos();
		PointF pos_b = o.getPos();
		float sq_distance = (pos_a.x - pos_b.x) * (pos_a.x - pos_b.x) + (pos_a.y - pos_b.y) * (pos_a.y - pos_b.y);
		float distance = (float) Math.sqrt(sq_distance);
		float grav =  G * (getMass() * o.getMass()) / sq_distance;
		
		//normierter richtungsvektor
		float richtung_x = (pos_a.x - pos_b.x)/distance;
		float richtung_y = (pos_a.y - pos_b.y)/distance;
		
		int dtime = getDeltaTime();
		
		Force gravf = new Force((-richtung_x * grav) * (float)(dtime)/1000, -richtung_y*grav * (float)(dtime)/1000); //per second
		*/
		
		PointF pos_a = getPos();
		PointF pos_b = o.getPos();
		int mass_a = getMass();
		int mass_b = o.getMass();
		
		float sq_distance = (pos_a.x - pos_b.x) * (pos_a.x - pos_b.x) + (pos_a.y - pos_b.y) * (pos_a.y - pos_b.y);
		float distance = (float) Math.sqrt(sq_distance);
		float richtung_x = (pos_b.x - pos_a.x)/distance;
		float richtung_y = (pos_b.y - pos_a.y)/distance;
		
		float grav =  G * (mass_a * mass_b) / sq_distance;
		
		Force gravf = new Force(richtung_x * grav, richtung_y * grav);
		//Log.d(TAG, "Gravitation: " + gravf.x + " " + gravf.y);
		/*float distance = new Force(getPos().x-o.getPos().x, getPos().y-o.getPos().y).length();
		float grav =  G * (getMass() * o.getMass()) / (distance * distance);
		Force richtung = new Force((getPos().x-o.getPos().x)/distance, (getPos().y-o.getPos().y)/distance);
		int dtime = getDeltaTime();
		Force movement = new Force((-richtung.x * grav) * (float)(dtime)/1000, -richtung.y*grav * (float)(dtime)/1000); //per second
		return movement;*/
		return gravf;
	}

	public void applyForce(Force gravitation) {
		addMovement(new Movement(gravitation.x/getMass(), gravitation.y/getMass()));
	}

	public void setPos(PointF point) {
		setPos(point.x, point.y);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name == null)
			name = "";
		this.name = name;
	}
	@Override
	public int hashCode(){
		return getName().hashCode();
	}
	public abstract GameObject clone();
	/**
	 * Load all your Bitmaps here is called on initialisation and deserialization
	 * */
	public void loadBitmaps(Context context) {}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
}
