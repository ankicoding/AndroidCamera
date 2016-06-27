package com.newgen.nemp.client.phonegap.plugin.files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.content.ContentResolver;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class FileChooser extends CordovaPlugin {

    private static final String TAG = "FileChooser";
    private static final String ACTION_OPEN = "open";
    private static final int PICK_FILE_REQUEST = 1;
    private String pathToFile = "";
    private CallbackContext callback;
     //  private MyContentProvider context;
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext);
            return true;
        }

        return false;
    }

    //public void chooseFile(CallbackContext callbackContext) {
    public String chooseFile(CallbackContext callbackContext) {
        // type and title should be configurable
    	String path = "";
      //  Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.setType("*/*");
     //   intent.setType("sdcard/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
        path = pathToFile;
        return path;
    }

    @SuppressWarnings("unused")
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
//    		if (requestCode == PICK_FILE_REQUEST && callback != null) {
    	//JSONObject documentListJson = new JSONObject();
    	Context context = this.cordova.getActivity().getApplicationContext();
//            if (resultCode == Activity.RESULT_OK) {
    	if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            	 //pathToFile = data.getDataString();
                // String temp = data.getStringExtra("path");
           Uri uri = data.getData();
            //  Uri uri1= Uri.parse(uri.toString());
    		// String uri = data.getData().toString();
           
       // Uri uri2= Uri.fromFile(file);
   
//               data.putExtra(MediaStore.EXTRA_OUTPUT,
//						Uri.fromFile(file));

               // String str= uri.getPath().toString();
              //  String all_path =data.getStringExtra("all_path");
               // File path =new File(str);
         
              //  Context context=null;
				//new File(getRealPathFromURI(uri,webView.getContext()));
	//	new File(getRealPathFromURI(uri,context));
           String path=getRealPathFromURI(uri,context);
           File file = new File(path);
           if(!file.exists())
           {
//        	   try {
//				 //documentListJson.put("file",  JSONObject.NULL);
//        	   } catch (JSONException e) {
//			// TODO Auto-generated catch block
//        		   e.printStackTrace();
//        	   }
        	   callback.error("File doesnot exists");
			
           }else
        	   
           {
				long fileSize=file.length();
				double fileSizeinKB=(double)fileSize/1024;
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();  
//				byte[] bFile = baos.toByteArray(); 
//				String js_out=Base64.encodeToString(bFile, Base64.DEFAULT);
				 String encodedfile = null;
				  byte[] bFile = new byte[(int)file.length()];
				 try{
				  FileInputStream fileInputStreamReader = new FileInputStream(file);
				
				  fileInputStreamReader.read(bFile);
			        encodedfile = Base64.encodeToString(bFile, Base64.DEFAULT).toString();
			        
				 }
				 catch (FileNotFoundException e) {
				        // TODO Auto-generated catch <span id="IL_AD4" class="IL_AD">block
				        e.printStackTrace();
				 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            // String realpath= path.getAbsolutePath();
//                if (uri != null) {
//
//                    Log.w(TAG, uri.toString());
//                 
//                   
//                    callback.success(uri.toString());
				//if(bFile != null){
	    	
					//try {
						if(bFile != null){
    			//imageListJson.put("image"+i, js_out);
    			//changed by ankita
						//documentListJson.put("filedoc", path+"*"+fileSizeinKB+"*"+encodedfile);
							callback.success(path+"*"+fileSizeinKB+"*"+encodedfile);
    		//	imageListJson.put("image_size"+i,fileSizeinKB);
    	
					} 
						else
						{
							//documentListJson.put("filedoc", JSONObject.NULL);
							callback.error("File path was null");
						}
					//}catch (JSONException e) {
				// TODO Auto-generated catch block
						//e.printStackTrace();
					//}
    		
				//}
				//	else
				//	{
    		
					}
    	//}
		
//			
//				if (path != null) {
//			 
//
//			 	callback.success(path);
//
//                } else {
//
//                    callback.error("File path was null");
//
//                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                // TODO NO_RESULT or error callback?
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                callback.sendPluginResult(pluginResult);

            } else {

                callback.error(resultCode);
            }
    	//callback.success(documentListJson);
    	

        }
//}
    
//   private String getRealPathFromURI(Uri contentURI,Context context ) {
////      //  String result;
////       // context=new MyContentProvider();
////      //  Context c;
////        //ContentResolver resolver = context.getContentResolver(); 
//   Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
////      //Cursor cursor =  c.getContentResolver(contentURI, null, null, null, null);
//////        if (cursor == null) { // Source is Dropbox or other similar local file path
//////            result = contentURI.getPath();
//////            
//////            Log.w(TAG,"Result of IF:"+result);
//////        } else { 
//////            cursor.moveToFirst(); 
//////            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
//////            result = cursor.getString(idx);
//////            Log.w(TAG,"Result of Else:"+result);
//////            cursor.close();
//////        }
//////        return result;
//    int column_index = cursor .getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA); 
//    cursor.moveToFirst(); 
//    return cursor.getString(column_index);
//    }

    private String getRealPathFromURI(Uri uri,Context context ) {
    	
    	//check here to KITKAT or new version
    	  final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    	 
    	  // DocumentProvider
    	  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
    	    
    	   // ExternalStorageProvider
    	   if (isExternalStorageDocument(uri)) {
    	    final String docId = DocumentsContract.getDocumentId(uri);
    	    final String[] split = docId.split(":");
    	    final String type = split[0];
    	 
    	   // if ("primary".equalsIgnoreCase(type)) {
    	    	String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                return Environment.getExternalStorageDirectory() + "/" + split[1];
           // }
    	   }
    	   // DownloadsProvider
    	   else if (isDownloadsDocument(uri)) {
    	 
    	    final String id = DocumentsContract.getDocumentId(uri);
    	    final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
    	   }
    	   // MediaProvider
    	   else if (isMediaDocument(uri)) {
    	    final String docId = DocumentsContract.getDocumentId(uri);
    	    final String[] split = docId.split(":");
    	    final String type = split[0];
    	 
    	    Uri contentUri = null;
    	    if ("image".equals(type)) {
    	     contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    	    } else if ("video".equals(type)) {
    	     contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    	    } else if ("audio".equals(type)) {
    	     contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    	    }
    	 
    	    final String selection = "_id=?";
    	    final String[] selectionArgs = new String[] {
    	      split[1]
    	    };
    	 
    	    return getDataColumn(context, contentUri, selection, selectionArgs);
    	   }
    	  }
    	  // MediaStore (and general)
    	  else if ("content".equalsIgnoreCase(uri.getScheme())) {
    	 
    	   // Return the remote address
    	   if (isGooglePhotosUri(uri))
    	    return uri.getLastPathSegment();
    	 
    	   return getDataColumn(context, uri, null, null);
    	  }
    	  // File
    	  else if ("file".equalsIgnoreCase(uri.getScheme())) {
    	   return uri.getPath();
    	  }
    	 
    	  return null;
    	
    	
    }
    
    public static String getDataColumn(Context context, Uri uri, String selection,
    		   String[] selectionArgs) {
    		 
    		  Cursor cursor = null;
    		  final String column = "_data";
    		  final String[] projection = {
    		    column
    		  };
    		 
    		  try {
    		   cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
    		     null);
    		   if (cursor != null && cursor.moveToFirst()) {
    		    final int index = cursor.getColumnIndexOrThrow(column);
    		    return cursor.getString(index);
    		   }
    		  } finally {
    		   if (cursor != null)
    		    cursor.close();
    		  }
    		  return null;
    		 }
    		 
    		 /**
    		  * @param uri The Uri to check.
    		  * @return Whether the Uri authority is ExternalStorageProvider.
    		  */
    		 public static boolean isExternalStorageDocument(Uri uri) {
    		  return "com.android.externalstorage.documents".equals(uri.getAuthority());
    		 }
    		 
    		 /**
    		  * @param uri The Uri to check.
    		  * @return Whether the Uri authority is DownloadsProvider.
    		  */
    		 public static boolean isDownloadsDocument(Uri uri) {
    		  return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    		 }
    		 
    		 /**
    		  * @param uri The Uri to check.
    		  * @return Whether the Uri authority is MediaProvider.
    		  */
    		 public static boolean isMediaDocument(Uri uri) {
    		  return "com.android.providers.media.documents".equals(uri.getAuthority());
    		 }
    		 
    		 /**
    		  * @param uri The Uri to check.
    		  * @return Whether the Uri authority is Google Photos.
    		  */
    		 public static boolean isGooglePhotosUri(Uri uri) {
    		  return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    		 }
    		}
    
//	private ContentResolver getContentResolver() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}