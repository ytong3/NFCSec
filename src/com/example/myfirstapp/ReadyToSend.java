package com.example.myfirstapp;

import java.io.IOException;

import cpabe.Common;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

public class ReadyToSend extends Activity {
	

    NfcAdapter mNfcAdapter;
    NdefMessage NdefMsgToSend;
    

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ready_to_send);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent intent = getIntent();
		String attr_str = intent.getStringExtra(AddingAttributes.POLICY_STR);
		attr_str = "Message encrypted under the policy: \n"+attr_str;
		String NFCInstruction = "Ready to send encrypted message via NFC. Please touch the other device.";
		
		SpannableStringBuilder spStr = new SpannableStringBuilder(attr_str+"\n\n\n"+NFCInstruction);
		spStr.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, attr_str.length(),
		            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		spStr.setSpan(new RelativeSizeSpan(1.5f), 0, attr_str.length(),
		            Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		spStr.setSpan(new ForegroundColorSpan(Color.rgb(255, 0, 0)), attr_str.length()+3,
		            attr_str.length() + NFCInstruction.length()+3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		
		TextView textview =(TextView) findViewById(R.id.send_status);
		textview.setText(spStr);

		
		
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
		
		//encapsulate the file encrypted bytes to Ndef msgs.
		Common.setContext(getApplicationContext());
		try {
			NdefMsgToSend = new NdefMessage(new NdefRecord[]{
					NdefRecord.createMime("application/vnd.com.example.android.beam",Common.suckFile(MainActivity.currentFile+".enc"))
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "I/O Exception in creating Ndef Messages", Toast.LENGTH_LONG).show();
			finish();
			e.printStackTrace();
		}
		
		mNfcAdapter.setNdefPushMessage(NdefMsgToSend, this);
		
		//clean up the files
		//deleteFile(MainActivity.currentFile+".enc");
		//deleteFile(MainActivity.currentFile);	
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void onResume(){
		super.onResume();
		
		Common.setContext(getApplicationContext());
		try {
			NdefMsgToSend = new NdefMessage(new NdefRecord[]{
				NdefRecord.createMime("application/vnd.com.example.android.beam",Common.suckFile(MainActivity.currentFile+".enc"))
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "I/O Exception", Toast.LENGTH_LONG).show();
			finish();
			e.printStackTrace();
		}
		
		mNfcAdapter.setNdefPushMessage(NdefMsgToSend, this);
		//deleteFile(MainActivity.currentFile+".enc");
		//deleteFile(MainActivity.currentFile);	
	}

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
		getMenuInflater().inflate(R.menu.ready_to_send, menu);
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
	

}
