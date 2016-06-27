package com.newgen.nemf.client.plugin.alertList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaPlugin;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

/**
 * --------------------------------------------------------------------------------------------------
 * 							NEWGEN SOFTWARE TECHNOLOGIES LIMITED
 *
 * Product				: TP Mobile
 * Application			: TPMobile-Client
 * Module				: Client
 * File					: AlertListPlugin.java
 * Author				: Neha Ahuja
 * Date(DD/MM/YYYY)		: 04/12/2015
 * Purpose				: This file Provides List of options on device for attaching image file
 *
 *
 * Change History
 * Date 	        Author  	 	Bug ID 		    Change Description
 * --------------------------------------------------------------------------------------------------
 * 04/12/2015		Neha Ahuja		4892			Created
 * --------------------------------------------------------------------------------------------------
 **/
public class AlertListPlugin extends CordovaPlugin {

    static String TAG = "AlertListPlugin";
    private static final String ACTION_ALERT = "alertList";
    private CallbackContext callback;
	private static final int CAMERA_CAPTURE = 20;
	private int option;
    /**
     * Constructor.
     */
    public AlertListPlugin() {
    }

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action        The action to execute.
     * @param args          JSONArry of arguments for the plugin.
     * @param callbackId    The callback id used when calling back into JavaScript.
     * @return              A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
            if (action.equals(ACTION_ALERT)) {
                this.loadList(args, callbackContext);
            }
            return true;
        }

    // --------------------------------------------------------------------------
    // LOCAL METHODS
    // --------------------------------------------------------------------------
    
    public void loadList( final JSONArray thelist,  final CallbackContext callbackContext) {
    
        final CordovaInterface cordova = this.cordova;
        final Context context = this.cordova.getActivity().getApplicationContext();
        Runnable runnable = new Runnable() {
    	
            public void run() {
    	
            	List<String> list = new ArrayList<String>();            	            			    	
		    	
				// we start with index 1 because index 0 is the title
		    	for( int x = 1; x < thelist.length(); x++) {
					try {
						list.add( thelist.getString(x) );
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    	
            final CharSequence[] options = list.toArray(new CharSequence[list.size()]);		    	
				
		    	AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
		    	try {
					builder.setTitle( thelist.getString(0) );
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // index 0 contains the title
		    	builder.setItems(options, new DialogInterface.OnClickListener() {
		    	   

					public void onClick(DialogInterface dialog, int item) {
				 if (options[item].equals("Take Photo"))
		              {
		             	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		             	callbackContext.success(item);
		               }
		              else if (options[item].equals("Choose from Gallery"))
		                {
		            	 	callbackContext.success(item);
		                  }
		    	    }
					
		    	});
		    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    	   

						public void onClick(DialogInterface dialog, int item) {
							dialog.dismiss();
						}
		    	});
					
		    	AlertDialog alert = builder.create();
		    	alert.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
		    	alert.show();  
		    	
				final Resources res = alert.getContext().getResources();
				final int alertTitleId = res.getIdentifier("alertTitle", "id", "android");
		       TextView alertTitle1 = (TextView) alert.getWindow().getDecorView().findViewById(alertTitleId);
		       // Change the title divider
		        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
		        final View titleDivider = alert.findViewById(titleDividerId);
		        titleDivider.setBackgroundColor(res.getColor(android.R.color.holo_orange_dark));
		        Button btn1 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
		        btn1.setTextSize(24);
            }
        };
        this.cordova.getActivity().runOnUiThread(runnable);
		
    }
    
    

}