package com.example.myfirstapp;

import java.io.IOException;

import cpabe.Common;
import cpabe.Cpabe;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AddingAttributes extends Activity {
	//the keys and input/output filenames
	static String pubfile = "keys/pub_key";
	static String mskfile = "keys/master_key";
	static String prvfile = "keys/prv_key";

	static String inputfile = MainActivity.currentFile;
	static String encfile = inputfile+".enc";
	static String decfile = inputfile+".dec";
	
	public static String POLICY_STR="com.example.myfirstapp.POLICY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adding_attributes);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	/*
	@Override
	protected void onResume(){
		super.onResume();
		deleteFile(MainActivity.currentFile+".enc");
	}
	*/

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.adding_attributes, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void encrypt(View view) throws Exception{
		//set up the wheel(spinner)
		final ProgressDialog progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Encryption in progress...");
		progDialog.setIcon(R.drawable.ic_launcher);
		progDialog.setIndeterminate(false);
		progDialog.setProgress(100);
		progDialog.setCancelable(false);
		//get the policy
		EditText editText = (EditText) findViewById(R.id.edit_policy);
		final String policy = editText.getText().toString();
		
		final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				progDialog.dismiss();
				Toast.makeText(getApplicationContext(), "Encryption succeed", Toast.LENGTH_SHORT).show();
				super.handleMessage(msg);
			}
		};
		
		progDialog.show();
		new Thread(){
			public void run(){
				Common.setContext(getApplicationContext());
				Cpabe cipher = new Cpabe();
				try {
					cipher.enc(pubfile, policy, inputfile, encfile, Common.FROM_ASSET);
					sleep(100);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
				
				Intent intent = new Intent(getApplicationContext(),ReadyToSend.class);
				intent.putExtra(POLICY_STR, policy);
				startActivity(intent);
			}
		}.start();
		//TODO error-checking, leave it later
		
		//just do encryption
		//get the access policy in the EdditText
		
	}
}
