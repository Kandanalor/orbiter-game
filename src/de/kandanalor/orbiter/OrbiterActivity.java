package de.kandanalor.orbiter;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ZoomControls;
import de.kandanalor.orbiter.interfaces.GameStateListener;

public class OrbiterActivity extends Activity implements GameStateListener {
	
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private DrawPanel drawpanel = null;
    private ZoomControls zoomctrl = null;
    
    private static final String TAG = "OrbiterActivity";
    

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.game);
	    
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	      
	    drawpanel = (DrawPanel)findViewById(R.id.drawPanel);
	    drawpanel.setGameStateListener(this);
	    
	    zoomctrl = (ZoomControls)findViewById(R.id.zoomCtrl);
	    zoomctrl.setOnZoomInClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				drawpanel.setZoom(drawpanel.getZoom()*1.1f);
			}
		});
	    zoomctrl.setOnZoomOutClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				drawpanel.setZoom(drawpanel.getZoom()/1.1f);
			}
		});
	    
	    
	    final PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
	    pause.setState(PauseButton.PLAY);
	    pause.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(pause.getState() == PauseButton.PAUSE) {
					drawpanel.getGameLoop().pauseL();
				}
				else if(pause.getState() == PauseButton.PLAY) {
					drawpanel.getGameLoop().resumeL();
					drawpanel.setSelected_obj(null);
				}
			}
		});
	}
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(drawpanel, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        drawpanel.setOnTouchListener(drawpanel);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(drawpanel);
    }
	@Override
	public void onGamePaused() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		pause.setState(PauseButton.PLAY);
		//Log.d(TAG, "onGamePaused");
	}
	@Override
	public void onGameResumed() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		pause.setState(PauseButton.PAUSE);
	}
	@Override
	public void onGameOver() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		pause.setState(PauseButton.PLAY);
	}	 
}