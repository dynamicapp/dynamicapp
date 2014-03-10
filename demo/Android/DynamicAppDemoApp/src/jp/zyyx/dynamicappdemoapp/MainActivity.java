package jp.zyyx.dynamicappdemoapp;

import android.os.Bundle;
import jp.zyyx.dynamicapp.DynamicAppActivity;
import android.view.Menu;

public class MainActivity extends DynamicAppActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
