package com.example.myfirstapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import cpabe.Common;
import cpabe.Cpabe;

public class DisplayMessageActivity extends Activity {
	
	//pub_key, master_key, private_key, path and name
	static String pubfile = "keys/pub_key";
	static String mskfile = "keys/master_key";
	static String prvfile = "keys/prv_key";

	static String inputfile = MainActivity.currentFile;
	static String encfile = inputfile+".enc";
	static String decfile = inputfile+".dec";
	
	//attributes and policies
	static String[] attr = { "baf1", "fim1", "foo" };
	static String policy = "foo bar fim 2of3 baf 1of2";

	static String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
			+ "sn:student2 cn:student2 uid:student2 userPassword:student2 "
			+ "ou:idp o:computer mail:student2@sdu.edu.cn title:student";

	static String student_policy = "sn:student2 cn:student2 uid:student2 3of3";
	static String message="";
	
 
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Common.setContext(getApplicationContext());
           
        // Get the message from the intent
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        //String originalMsg=message;


        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        println("\n", textView);
        
        
        //saving original message to a file for encryption
        try {
			Common.spitFile(inputfile, message.getBytes());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			println("Failure saving message to files", textView);
			e1.printStackTrace();
		}
		
        
        //encryption
        String attr_str;
    	attr_str = student_attr;
		policy = student_policy;

		Cpabe test = new Cpabe();

		/*
		println("//start to setup", textView);
		try {
			test.setup(pubfile, mskfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			println("Setup failed...", textView);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
	
			e.printStackTrace();
		}
		println("//end to setup",textView);

		println("//start to keygen",textView);
		try {
			test.keygen(pubfile, prvfile, mskfile, attr_str);
		} catch (NoSuchAlgorithmException e) {
			println("Keygen failed... because of NoSuchAlgorithmException", textView);
			e.printStackTrace();
		} catch (IOException e) {
			println("Keygen failed... because of IOException",textView);
			e.printStackTrace();
		}
		println("//end to keygen", textView);
		*/

		println("//start to enc", textView);
		try {
			test.enc(pubfile, policy, inputfile, encfile, Common.FROM_ASSET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			println("Enc failed...", textView);
			e.printStackTrace();
		}
		println("//end to enc", textView);

		println("//start to dec", textView);
		try {
			test.dec(pubfile, prvfile, encfile, decfile, Common.FROM_ASSET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			println("Dec failed...", textView);
			e.printStackTrace();
		}
		println("//end to dec", textView);
        
        try {
			String newMsg = new String(Common.suckFile(decfile));
			println("Message recovered:"+newMsg, textView);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        

		
		
        // Set the text view as the activity layout
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //TODO:change the display approach to EditText
	private void println(String o, TextView textView) {
		message+=(o+'\n');
		
		textView.setText(message);
		setContentView(textView);
	}
}