package com.newgen.nemp.client.omnidesk.plugin.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

//import com.newgen.nemp.client.omnidesk.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.Toast;
//import com.newgen.nemp.client.omnidesk.plugin.camera.CompressImage;

public class CropImage extends CordovaPlugin{
public final String ACTION_GET_IMAGE_NAME = "GetImageName";
Uri myUri;

private static final String IMAGE_FILE_LOCATION = " file:///sdcard/temp.jpg";//temp file";
private CallbackContext callbackContext; 
private static Uri tmpUri = Uri.parse(IMAGE_FILE_LOCATION);
private static final int CROP_CAMERA =2;
@Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext)throws JSONException {
   // Log.e(TAG,"Inside Version plugin.");
	this.callbackContext = callbackContext;
    boolean result = false;
    if(action.equals(ACTION_GET_IMAGE_NAME)) {
        try {
            myUri = Uri.parse(args.getString(0));
            cropCapturedImage(myUri);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        result = true;
    }


    return result;
}

public void cropCapturedImage(Uri picUri){
	  Context context = this.cordova.getActivity().getApplicationContext();
	 
	try{
    //call the standard crop action intent 
    Intent cropIntent = new Intent("com.android.camera.action.CROP");
    //indicate image type and Uri of image
    cropIntent.setDataAndType(picUri, "image/*");
    //set crop properties
    cropIntent.putExtra("crop", "true");
    cropIntent.putExtra("aspectX", 1);
    cropIntent.putExtra("aspectY", 1);
    cropIntent.putExtra("outputX", 256);
    cropIntent.putExtra("outputY", 256);
  //  cropIntent.putExtra("scale", true);
   cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
    String uri =picUri.getPath();
    // for save the image in same location with same name.
//    final String docId = DocumentsContract.getDocumentId(picUri);
//    final String[] split = docId.split(":");
//    final String type = split[0];
 //   File f = new File(Environment.getExternalStorageDirectory() + "/" );
    File f= new File(uri);
   // cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));            
   // cropIntent.putExtra("output", Uri.fromFile(f)); 
    cropIntent.putExtra("return-data", true);
    
    //start the activity - we handle returning in onActivityResult
    this.cordova.startActivityForResult((CordovaPlugin) this,cropIntent, 2);
    
	} catch (ActivityNotFoundException anfe) {
        // display an error message
        String errorMessage = "Whoops - your device doesn't support the crop action!";
        Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
        toast.show();
    }
}
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //Log.e("final", String.valueOf(requestCode));
	Context context = this.cordova.getActivity().getApplicationContext();
	 CompressImage compressimg =new CompressImage(context);
	super.onActivityResult(requestCode, resultCode, data);
	//Bitmap bitmap = null;
	 Bitmap thePic =null;
    if(requestCode == 2){
//    	if(data!=null){
//        //Create an instance of bundle and get the returned data
//        Bundle extras = data.getExtras();
//        //get the cropped bitmap from extras
//        Bitmap thePic = extras.getParcelable("data");
//        
//      //converting bitmap to byte array/base64 string
//    	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object  
//		String encodedfile = null;
//		byte[] bFile = baos.toByteArray(); 
//	    encodedfile = Base64.encodeToString(bFile, Base64.DEFAULT).toString();
    	if(data!=null){
    		if(data.getData()==null){
    		 Bundle extras = data.getExtras();
//            //get the cropped bitmap from extras
    	 // Bitmap thePic = extras.getParcelable("data");
    		  thePic = (Bitmap)extras.get("data");
    		}
    		else{
    			
            try {
				thePic = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		}
        //    Uri cropImage = getImageUri(context.getContentResolver(), thePic);
	    Uri uri = data.getData();
	    if (uri != null) {
	    	
	    	Bitmap photo= compressimg.compressImage(uri.toString());
	   
	    	// Bitmap photo = decodeBitmapFromUri(uri);
	    	//converting bitmap to byte array/base64 string
	     	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	     	
	     	photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object     		
	     
	 		String encodedfile = null;
	 		byte[] bFile = baos.toByteArray(); 
	 	    encodedfile = Base64.encodeToString(bFile, Base64.DEFAULT).toString();
	 	   if(bFile != null){
	 	    callbackContext.success(encodedfile);
	    }
	 	   else
	 		  callbackContext.error("File path was null");
	    }
    }
       
    	
    }
}

//private Bitmap decodeBitmapFromUri(Uri uri){
//    Bitmap bitmap = null;
//    Context context = this.cordova.getActivity().getApplicationContext();
//    try {
//        bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
//    } catch (FileNotFoundException e) {
//        e.printStackTrace();
//        return null;
//    }
//    return bitmap;
//}
}