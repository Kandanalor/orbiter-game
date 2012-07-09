package de.kandanalor.orbiter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import de.kandanalor.orbiter.R;
import de.kandanalor.orbiter.SaveGameProvider;

public class Hauptmenu extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
		setContentView(R.layout.main);
		Button campain_btn = (Button)findViewById(R.id.campain_btn);
		campain_btn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Hauptmenu.this,LevelDescription.class);
					intent.putExtra("type", SaveGameProvider.CAMPAIGN);
					startActivity(intent);
				}
		});
    }
	@Override
    protected void onStart() {
    	super.onStart();
    }
	@Override
    protected void onRestart() {
    	super.onRestart();
    }
	@Override
    protected void onResume() {
    	super.onResume();
    }
	@Override
    protected void onPause() {
    	super.onPause();
    }
	@Override
    protected void onStop() {
    	super.onStop();
    }
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    }
}
