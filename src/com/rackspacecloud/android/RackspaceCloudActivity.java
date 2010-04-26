package com.rackspacecloud.android;

import com.rackspace.cloud.servers.api.client.Account;
import com.rackspace.cloud.servers.api.client.http.Authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class RackspaceCloudActivity extends Activity implements View.OnClickListener, OnEditorActionListener {
	
	private static final String OPT_USERNAME = "username";
	private static final String OPT_USERNAME_DEF = "";
	private static final String OPT_API_KEY = "apiKey";
	private static final String OPT_API_KEY_DEF = "";
	
	private Intent tabViewIntent;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((Button) findViewById(R.id.button)).setOnClickListener(this);
        ((EditText) findViewById(R.id.login_apikey)).setOnEditorActionListener(this);
        loadLoginPreferences();
        tabViewIntent = new Intent(this, TabViewActivity.class);
    }

    public void login() {
    	if (hasValidInput()) {
        	showActivityIndicators();
        	setLoginPreferences();
        	new AuthenticateTask().execute((Void[]) null);
    	} else {
    		showAlert("Fields Missing", "User Name and API Key are required.");
    	}
    }
    
    public void onClick(View view) {
    	login();
    }
    
	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		login();
		return false;
	}    

	private void loadLoginPreferences() {
    	SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);
    	String username = sp.getString(OPT_USERNAME, OPT_USERNAME_DEF);    	
    	String apiKey = sp.getString(OPT_API_KEY, OPT_API_KEY_DEF);
    	EditText usernameText = (EditText) findViewById(R.id.login_username);
    	usernameText.setText(username);
    	EditText apiKeyText = (EditText) findViewById(R.id.login_apikey);
    	apiKeyText.setText(apiKey);
    }
    
    private void setLoginPreferences() {
    	String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
    	String apiKey = ((EditText) findViewById(R.id.login_apikey)).getText().toString();
    	Account.setUsername(username);
    	Account.setApiKey(apiKey);

    	Editor e = this.getPreferences(Context.MODE_PRIVATE).edit();
    	e.putString(OPT_USERNAME, username);
    	e.putString(OPT_API_KEY, apiKey);
    	e.commit();        	
    }
    
    private void showAlert(String title, String message) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setButton("OK", new DialogInterface.OnClickListener() {
	      public void onClick(DialogInterface dialog, int which) {
	        return;
	    } }); 
		alert.show();
    }
    
    private boolean hasValidInput() {
    	String username = ((EditText) findViewById(R.id.login_username)).getText().toString();
    	String apiKey = ((EditText) findViewById(R.id.login_apikey)).getText().toString();
    	return !"".equals(username) && !"".equals(apiKey);
    }

    private void setActivityIndicatorsVisibility(int visibility) {
        ProgressBar pb = (ProgressBar) findViewById(R.id.login_progress_bar);
    	TextView tv = (TextView) findViewById(R.id.login_authenticating_label);
        pb.setVisibility(visibility);
        tv.setVisibility(visibility);
    }

    private void showActivityIndicators() {
    	setActivityIndicatorsVisibility(View.VISIBLE);
    }
    
    private void hideActivityIndicators() {
    	setActivityIndicatorsVisibility(View.INVISIBLE);
    }
    
    private class AuthenticateTask extends AsyncTask<Void, Void, Boolean> {
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			return new Boolean(Authentication.authenticate());
		}
    	
		@Override
		protected void onPostExecute(Boolean result) {
			if (result.booleanValue()) {
				startActivity(tabViewIntent);
			} else {
				showAlert("Login Failure", "Authentication failed.  Please check your User Name and API Key.");
			}
			hideActivityIndicators();
		}
    }

}