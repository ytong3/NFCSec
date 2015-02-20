package com.example.myfirstapp;

import java.io.IOException;

import cpabe.Common;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static String currentFile = "currentFile";
	private String filename;
	
	private void setTextInBox(EditText edittext, String msg){
		Editable textfield = edittext.getText();
		textfield.clear();
		textfield.append(msg);
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Automatically load message if currentFile exists
        EditText editText = (EditText) findViewById(R.id.edit_message);
        Common.setContext(getApplicationContext());
        String existingMsg;
        try {
			existingMsg = new String(Common.suckFile("currentFile"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			existingMsg="";
		}
        editText.append(existingMsg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void nextActivity(View view) throws IOException{
    	filename="currentFile";
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	
    	Common.setContext(getApplicationContext());
    	Common.spitFile(filename, message.getBytes());
    	
    	Intent intent = new Intent(this,AddingAttributes.class);

    	startActivity(intent);
    }
    
    public void saveMsg(View view) throws IOException{
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	final String message = editText.getText().toString();
    	
    	//get the filename by prompting the user to input
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Input");
    	alert.setMessage("Type in the name of the file");
    	
    	final EditText input = new EditText(this);
    	alert.setView(input);
    	
    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which){
    			filename = input.getText().toString();
    			Common.setContext(getApplicationContext());
    	    	try {
					Common.spitFile(filename, message.getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	});
			
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//Canceled
			}
		});
		alert.show();
    }
}
