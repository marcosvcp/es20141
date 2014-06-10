package com.gridrunner.game.android;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.gridrunner.game.GridRunner;
import com.gridrunner.game.Obstacle;

public class AndroidLauncher extends AndroidApplication {

	private GridRunner grid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;
		grid = new GridRunner();
		config.useGLSurfaceView20API18 = true;
		initialize(grid, config);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);

	}
}
