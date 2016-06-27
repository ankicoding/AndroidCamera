package com.newgen.nemf.client.plugin.gallery;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;




import org.json.JSONObject;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

public class GallerySingleSelectPlugin extends CordovaPlugin{
	 private static final String TAG = "GallerySingleSelectPlugin";
	private static final String ACTION_OPEN_GALLERY = "selectPictures";
	private static final String MEDIA_NOT_MOUNTED_ERROR = "Media not Mounted";
	private static final int PICK_IMAGE_REQUEST = 1;
	private static final int MAX_IMAGE_DIMENSION = 140;
	
	private CallbackContext callbackContext; 
	private ImageLoader imageLoader;


	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		
		this.callbackContext = callbackContext;
		if(ACTION_OPEN_GALLERY.equals(action)){
			
			initImageLoader();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			Intent chooser=Intent.createChooser(intent, "Select Picture");
			cordova.startActivityForResult(this, chooser, PICK_IMAGE_REQUEST);
		}else{
			return false;
		}
	return true;
	}
	
	private void initImageLoader() {
	
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this.cordova.getActivity()).defaultDisplayImageOptions(defaultOptions).memoryCache(new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		super.onActivityResult(requestCode, resultCode, data);
		Context context = this.cordova.getActivity().getApplicationContext();
		String path =null;
		String strUri= null;
		String[] splitUri=null;
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();

			if ("content".equalsIgnoreCase(uri.getScheme()))
					path=getRealPathFromURI(uri,context);
			else if("file".equalsIgnoreCase(uri.getScheme())) {
				strUri=uri.getPath();
				//splitUri= path.split("file://");
				path=strUri; //splitUri[1];
				}
			
		//	String	path=getRealPathFromURI(uri,context);
			  File imageFile = new File(path);
			 
			 if(!imageFile.exists()) {
				 callbackContext.error("File doesnot exists");
	           }else {
	        		Bitmap bitmap = null;
					try {
						if ("file".equalsIgnoreCase(uri.getScheme())) {
		                       try {
		                                  uri = Uri.parse(
		                                               android.provider.MediaStore.Images.Media.insertImage(
		                                                         context.getContentResolver(),
		                                               uri.getPath(), null, null));
		                           } catch (FileNotFoundException e) {
		                                  // TODO Auto-generated catch block
		                                  e.printStackTrace();
		                           }  
		                       
		                }						

						bitmap = scaleImage(context,uri);
						
			 		} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();  
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object  
					long fileSize=imageFile.length();
					double fileSizeinKB=(double)fileSize/1024;
					String encodedfile = null;
					byte[] bFile = baos.toByteArray(); 
				    encodedfile = Base64.encodeToString(bFile, Base64.DEFAULT).toString();
				    if(bFile != null){
						callbackContext.success(path+"*"+fileSizeinKB+"*"+encodedfile);
						} else {
								callbackContext.error("File path was null");
							}
	           }
		 } else if (resultCode == Activity.RESULT_CANCELED) {

             // TODO NO_RESULT or error callback?
             PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
             callbackContext.sendPluginResult(pluginResult);

         } else {

        	 callbackContext.error(resultCode);
         }
		}

	
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
	    		 
	    		 public static Bitmap scaleImage(Context context, Uri photoUri) throws IOException {
	    		        InputStream is = context.getContentResolver().openInputStream(photoUri);
	    		        BitmapFactory.Options dbo = new BitmapFactory.Options();
	    		        dbo.inJustDecodeBounds = true;
	    		        BitmapFactory.decodeStream(is, null, dbo);
	    		        is.close();

	    		        int rotatedWidth, rotatedHeight;
	    		        int orientation = getOrientation(context, photoUri);

	    		        if (orientation == 90 || orientation == 270) {
	    		            rotatedWidth = dbo.outHeight;
	    		            rotatedHeight = dbo.outWidth;
	    		        } else {
	    		            rotatedWidth = dbo.outWidth;
	    		            rotatedHeight = dbo.outHeight;
	    		        }

	    		        Bitmap srcBitmap;
	    		        is = context.getContentResolver().openInputStream(photoUri);
	    		        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
	    		            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
	    		            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
	    		            float maxRatio = Math.max(widthRatio, heightRatio);

	    		            // Create the bitmap from file
	    		            BitmapFactory.Options options = new BitmapFactory.Options();
	    		            options.inSampleSize = (int) maxRatio;
	    		            srcBitmap = BitmapFactory.decodeStream(is, null, options);
	    		        } else {
	    		            srcBitmap = BitmapFactory.decodeStream(is);
	    		        }
	    		        is.close();

	    		        /*
	    		         * if the orientation is not 0 (or -1, which means we don't know), we
	    		         * have to do a rotation.
	    		         */
	    		        if (orientation > 0) {
	    		            Matrix matrix = new Matrix();
	    		            matrix.postRotate(orientation);

	    		            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
	    		                    srcBitmap.getHeight(), matrix, true);
	    		        }

	    		        String type = context.getContentResolver().getType(photoUri);
	    		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		        if (type.equals("image/png")) {
	    		            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	    		        } else if (type.equals("image/jpg") || type.equals("image/jpeg")) {
	    		            srcBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	    		        }
	    		        byte[] bMapArray = baos.toByteArray();
	    		        baos.close();
	    		        return BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
	    		    }

	    		    public static int getOrientation(Context context, Uri photoUri) {
	    		        /* it's on the external media. */
	    		        Cursor cursor = context.getContentResolver().query(photoUri,
	    		                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

	    		        if (cursor == null) {
	    		            return -1;
	    		        }
	    		        if (cursor.getCount() != 1) {
	    		            return -1;
	    		        }

	    		        cursor.moveToFirst();
	    		        return cursor.getInt(0);
	    		    }
	    		 
	    		}
	


