package com.example.myfirstapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	public void go_send(View view){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	public void go_receive(View view){
		Intent intent = new Intent(this,ReceiveAndDec.class);
		startActivity(intent);
	}

}
