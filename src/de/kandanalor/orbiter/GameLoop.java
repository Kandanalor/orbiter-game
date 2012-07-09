package de.kandanalor.orbiter;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import de.kandanalor.orbiter.exceptions.ObjectCollisionException;
import de.kandanalor.orbiter.game.World;
import de.kandanalor.orbiter.interfaces.GameStateListener;


public class GameLoop implements Runnable {
    private SurfaceHolder _surfaceHolder;
    private DrawPanel _panel;
	private GameStateListener state_listener = null;
    private boolean _run = false;
    private boolean paused = true;
    private boolean game_over = false;
    

    
    private long last_update =  System.currentTimeMillis();
    private int frames = 0;
    
    private static final String TAG = "GameLoop";
    
    private Thread thread = null;
    
    public GameLoop(SurfaceHolder surfaceHolder, DrawPanel panel) {
        _surfaceHolder = surfaceHolder;
        _panel = panel;
    }
    public void start() {
    	thread = new Thread(this);
    	thread.start();
    }
    public float getFPS() {
    	long now = System.currentTimeMillis();
    	float fps = frames / ((float)(now - last_update)/1000) ;
    	last_update = now;
    	frames = 0;
    	return fps;
    }
    public void setRunning(boolean run) {
        _run = run;
    }
    int pause = 0;
    @Override
    public void run() {
        Canvas c;
        while (_run) {
        	try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	if(isRunning()) {
	        	try {
					getWorld().updatePhysics();
				} catch (ObjectCollisionException e) {
					gameOver();
				}
        	}

        	
            c = null;
            try {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder) {
                    _panel.onDraw(c);  
                    if(paused) {
                    	 _panel.onPause(c);
                    }
                    if(game_over) {
                    	_panel.onGameOver(c);
                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            frames++;
            if(frames % 100 == 0)
            	Log.d(TAG, "FPS: "+getFPS());
        }
    }

	public void gameOver() {
		pauseL();
		game_over = true;
		if(state_listener != null)
			state_listener.onGameOver();
	}
	public void replay() {
		game_over = false;
	}
	public void pauseL() {
		this.paused = true;
		_panel.getWorld().pausePhysics();
		if(state_listener != null)
			state_listener.onGamePaused();	
	}
	public void resumeL() {
		this.paused = false;
		game_over = false;
		_panel.getWorld().resumePhysics();
		if(state_listener != null)
			state_listener.onGameResumed();
	}

	public boolean isPaused() {
		return paused;
	}
	public void setGameStateListener(GameStateListener listener) {
		state_listener = listener;
	}
	public Thread getThread() {
		return thread;
	}
	public boolean isGame_over() {
		return game_over;
	}
	public World getWorld() {
		return _panel.getWorld();
	}
	public void setWorld(World world) {
		_panel.setWorld(world);
	}
	public boolean isRunning() {
		return !paused && !game_over;
	}
    
}