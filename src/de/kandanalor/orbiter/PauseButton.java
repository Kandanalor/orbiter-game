package de.kandanalor.orbiter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import de.kandanalor.orbiter.game.World;

public class PauseButton extends ImageButton implements OnClickListener {
	
	public static final int PLAY = 0;
	public static final int PAUSE = 1;
	public static final int REPLAY = 2;
	
	Bitmap pause_img = null;
	Bitmap play_img = null;
	Bitmap replay_img = null;
	
	GameLoop gameloop = null;
	
	
	public PauseButton(Context context) {
		super(context);
		init();
	}
	public PauseButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public PauseButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init(){
	    setOnClickListener(this);
	    
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		pause_img = BitmapFactory.decodeResource(getResources(), R.drawable.av_pause, options);
		play_img = BitmapFactory.decodeResource(getResources(), R.drawable.av_play, options);
		replay_img = BitmapFactory.decodeResource(getResources(), R.drawable.av_replay, options);
	}
	@Override
	public void onDraw(Canvas c) {
		switch(getState()) {
			case PLAY:
				setImageBitmap(play_img); break;
			case PAUSE:
				setImageBitmap(pause_img); break;
			case REPLAY:
				setImageBitmap(replay_img); break;
		}
		
		super.onDraw(c);
	}
	@Override
	public void onClick(View v) {
		int state = getState();
		if(state == PLAY) {
			SaveGameProvider.save(SaveGameProvider.QUICKSAVE, gameloop.getWorld().clone());
			gameloop.resumeL();
		}
		else if(state == PAUSE) {
			gameloop.pauseL();
		}
		else if(state == REPLAY) {
			World world = SaveGameProvider.load(SaveGameProvider.QUICKSAVE);
			world.loadBitmaps(getContext());
			
			gameloop.setWorld(world);
			gameloop.replay();
		}
	}
	public int getState() {
		if(gameloop == null) {
			return PLAY;
			//throw new IllegalArgumentException("Gameloop is not set!");
		}
		if(gameloop.isGame_over()) {
			return REPLAY;
		}
		else if(gameloop.isPaused()){
			return PLAY;
		}
		else {
			return PAUSE;
		}
	}
	public GameLoop getGameloop() {
		return gameloop;
	}
	public void setGameloop(GameLoop gameloop) {
		this.gameloop = gameloop;
	}




}
