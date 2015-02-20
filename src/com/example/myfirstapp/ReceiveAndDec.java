package com.example.myfirstapp;

import java.io.IOException;

import cpabe.Common;
import cpabe.Cpabe;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.PendingIntent;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.os.Build;

@SuppressLint("NewApi")
public class ReceiveAndDec extends Activity {
	//the keys and input/output filenames
	static String pubfile = "keys/pub_key";
	static String mskfile = "keys/master_key";
	static String prvfile = "keys/prv_key";
	
	private static final String TAG = "NFCSec";

	NfcAdapter mNfcAdapter;
	EditText mField;
	Cpabe cipher;
	
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mNdeExchangeFilters;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_receive_and_dec);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Find the text filed
		mField =(EditText) findViewById(R.id.msg_display);
		//set up  NFC Adapter. This activity only receive Ndef but not write.
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
				
		//Handle all of our received NFC intents in this activity
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, 
				new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
		
		//Intent filters for exchanging over P2P
		IntentFilter ndefDetected =new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try{
			ndefDetected.addDataType("application/vnd.com.example.android.beam");
		}catch(MalformedMimeTypeException e){
		}
		mNdeExchangeFilters = new IntentFilter[]{ndefDetected};
		
		//create the cipher instance
		cipher =new Cpabe();
	}

	protected void onResume(){
		super.onResume();
		
		//handling msg received from Android
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())){
			NdefMessage[] message = getNdefMessages(getIntent());
			byte[] payload=message[0].getRecords()[0].getPayload();
			//TODO:Got the byte. Now try to decrypt with the user's attributes/private keys
			//Fire up failure notification if attributes do not satisfy
			try{
				Common.setContext(getApplicationContext());
				Common.spitFile("rec_temp.abe",payload);
				Toast.makeText(this, "Message saved for decryption", Toast.LENGTH_LONG).show();
				cipher.dec(pubfile, prvfile, "rec_temp.abe", "decypted.dat", Common.FROM_ASSET);
				displayPlaintext(new String(Common.suckFile("decrypted.dat")));
			}catch(Exception e){
				displayPlaintext("Encryption failed");
			}
		}
		enableNdefExchangeMode();
	}
	
	@Override
	protected void onPause(){
		super.onPause();

		mNfcAdapter.setNdefPushMessage(null, this);
		mNfcAdapter.disableForegroundDispatch(this);
		
	}
	
	private void enableNdefExchangeMode(){
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdeExchangeFilters, null);
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
			NdefMessage[] msgs = getNdefMessages(intent);
			byte[] payload=msgs[0].getRecords()[0].getPayload();
			try{
				Common.setContext(getApplicationContext());
				Common.spitFile("rec.abe",payload);
				displayPlaintext("Message received and saved");
				cipher.dec(pubfile, prvfile, "rec.abe", "decrypted.dat", Common.FROM_ASSET);
				displayPlaintext(new String(Common.suckFile("decrypted.dat")));
				//clean up the field and delet used files
				deleteFile("rec.abe");
				deleteFile("decrypted.dat");
			}catch(IOException e){
				displayPlaintext("IOException occured");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				displayPlaintext("Decryption failed");
			}
		}
	}
	
	@SuppressLint("NewApi")
	NdefMessage[] getNdefMessages(Intent intent){
		//Parse the intent
		NdefMessage[] msgs=null;
		String action=intent.getAction();
		if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)||
				NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)){
			Parcelable[] rawMsgs = 
					intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if(rawMsgs!=null){
				msgs=new NdefMessage[rawMsgs.length];
				for(int i=0;i<rawMsgs.length;i++){
					msgs[i]=(NdefMessage) rawMsgs[i];
				}
			}else{
				//Unknown tag type
				byte[] empty = new byte[]{};
				NdefRecord record=
						new NdefRecord(NdefRecord.TNF_UNKNOWN,empty,empty,empty);
				NdefMessage msg=new NdefMessage(new NdefRecord[]{record});
				msgs=new NdefMessage[]{msg};
				
			}
		}else{
			Log.d(TAG,"Unknown intent.");
			finish();
		}
		return msgs;
	}
	
	private void displayPlaintext(String body){
		Editable text = mField.getText();
		//text.clear();
		text.append(body+"\n");
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
		getMenuInflater().inflate(R.menu.receive_and_dec, menu);
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
