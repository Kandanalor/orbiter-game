package de.kandanalor.orbiter.activities;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ZoomControls;
import de.kandanalor.orbiter.DrawPanel;
import de.kandanalor.orbiter.PauseButton;
import de.kandanalor.orbiter.R;
import de.kandanalor.orbiter.SaveGameProvider;
import de.kandanalor.orbiter.game.World;
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
	    
	    
	    //get the world

		Intent intent= getIntent();
		String levelname = intent.getStringExtra("level");
		World stdworld = null;
		
	    boolean orientchanged = false; 
		stdworld = (World)getLastNonConfigurationInstance();
		if(stdworld == null) {
		    if(levelname != null) {
		    	stdworld = SaveGameProvider.load(levelname);
		    }
		    else {
		    	stdworld = SaveGameProvider.load(SaveGameProvider.QUICKSAVE);
		    }
		}
		else {
			orientchanged = true;
		}
	    Log.d(TAG, "Load world: "+stdworld);
	    drawpanel.setWorld(stdworld);
	    
	    zoomctrl = (ZoomControls)findViewById(R.id.zoomCtrl);
	    zoomctrl.setOnZoomInClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				drawpanel.getZoom().zoom(drawpanel.getZoom().getCenter(), drawpanel.getZoom().getScale() + 0.1f);
			}
		});
	    zoomctrl.setOnZoomOutClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				drawpanel.getZoom().zoom(drawpanel.getZoom().getCenter(), drawpanel.getZoom().getScale() - 0.1f);
			}
		});
	    
	    
	    PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
	    pause.setGameloop(drawpanel.getGameLoop());
	    
	    final CheckBox autozoom_chk = (CheckBox)findViewById(R.id.autozoom_chk);
	    autozoom_chk.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if(autozoom_chk.isChecked()) {
					drawpanel.getZoom().autoZoom();
					drawpanel.getZoom().enableAutoZoom();
				}
				else {
					drawpanel.getZoom().disableAutoZoom();
				}
			}
		});
	    if(orientchanged) 
	    	drawpanel.getGameLoop().setRunning(true);
	}
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(drawpanel, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        drawpanel.setOnTouchListener(drawpanel);
        //drawpanel.getGameLoop().resumeL();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(drawpanel);
        drawpanel.getGameLoop().pauseL();
    }
    @Override
    protected void onDestroy() {
        super.onPause();
        SaveGameProvider.save(SaveGameProvider.QUICKSAVE, drawpanel.getWorld());
    }
	@Override
	public void onGamePaused() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		drawpanel.getZoom().resetAutozoom();
		//pause.setState(PauseButton.PLAY);
		//Log.d(TAG, "onGamePaused");
	}
	@Override
	public void onGameResumed() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		//pause.setState(PauseButton.PAUSE);
	}
	@Override
	public void onGameOver() {
		PauseButton pause = (PauseButton)findViewById(R.id.pause_btn);
		//pause.setState(PauseButton.PLAY);
	}	
    @Override
    public Object onRetainNonConfigurationInstance() {
        return drawpanel.getWorld();
    }
	@Override
	public void onGamePhysikUpdate(World world) {
		TextView score_txv = (TextView)findViewById(R.id.score_txv);
		
	}
}