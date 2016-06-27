package com.newgen.nemp.client.omnidesk.plugin.camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.newgen.nemp.client.omnidesk.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.Toast;
import com.newgen.nemp.client.omnidesk.plugin.camera.CompressImage;
import com.newgen.nemp.client.omnidesk.plugin.gallery.GallerySingleSelectPlugin;

public class CameraPlugin extends CordovaPlugin {
	public final String ACTION_OPEN_CAMERA = "LaunchCamera";
	private static final String LOG_TAG= "CAMERA_CAPTURE";
	 private static final int CAMERA = 1;     
	 private static final int PICTURE = 0; 
	 private static final int JPEG = 0;  
	 private static final int PNG = 1;
	 private static final int FILE_URI = 1;
	private int mQuality;                   // Compression quality hint (0-100: 0=low quality & high compression, 100=compress of max quality)
	//private String mQuality; 
	private static final String TAG_Quality = "quality";
    private int targetWidth;                // desired width of the image
    private int targetHeight;               // desired height of the image
    private Uri imageUri;                   // Uri of captured image
    private int encodingType;               // Type of encoding to use
    private int mediaType;                  // What type of media to retrieve
    private boolean saveToPhotoAlbum;       // Should the picture be saved to the device's photo album
    private boolean correctOrientation;     // Should the pictures orientation be corrected
    private boolean orientationCorrected;   // Has the picture's orientation been corrected
    private boolean allowEdit;              // Should we allow the user to crop the image.
	private Context context;
    public CallbackContext callbackContext;
    private int numPics;

    private MediaScannerConnection conn;    // Used to update gallery app with newly-written files
    Uri myUri;
   //private Uri croppedUri;	
    private static final int CAMERA_CAPTURE = 1;
    private static final int CROP_CAMERA =2;
    
    private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";//temp file
    private static Uri tmpUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
    
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals(ACTION_OPEN_CAMERA)) {
        	
           // int srcType =CAMERA;
            int destType =FILE_URI;
            this.saveToPhotoAlbum = false;
            this.targetHeight = 0;
            this.targetWidth = 0;
            this.encodingType = JPEG;
            this.mediaType = PICTURE;
            this.mQuality = 80;
          try{
        	   
           // this.mQuality= args.getString(TAG_Quality);
            //destType = args.getInt(1);
            //srcType = args.getInt(2);
            //this.targetWidth = args.getInt(3);
          //  this.targetHeight = args.getInt(4);
            //this.encodingType = args.getInt(5);
           // this.mediaType = args.getInt(6);
            //this.allowEdit = args.getBoolean(7);
          //  this.correctOrientation = args.getBoolean(8);
           // this.saveToPhotoAlbum = args.getBoolean(9);
        	 final JSONObject person = args.getJSONObject(0);
        	 //String array[];
//        	 Iterator keys = person.keys();
//
//        	    while(keys.hasNext()) {
//        	        // loop to get the dynamic key
//        	        String currentDynamicKey = (String)keys.next();
//
//        	        // get the value of the dynamic key
//        	       // JSONObject currentDynamicValue = person.getJSONObject(currentDynamicKey);
//        	       Object value = person.get(currentDynamicKey);
//        	      //  String value= 
//        	      
//        	        // do something here with the value...
//        	    }
      	    this.mQuality = person.getInt("quality");
            this.targetWidth = person.getInt("targetWidth");
            this.targetHeight = person.getInt("targetHeight");
            this.correctOrientation = person.getBoolean("correctOrientation");
            this.saveToPhotoAlbum = person.getBoolean("saveToPhotoAlbum");
            
            // If the user specifies a 0 or smaller width/height
            // make it -1 so later comparisons succeed
            if (this.targetWidth < 1) {
                this.targetWidth = -1;
            }
            if (this.targetHeight < 1) {
                this.targetHeight = -1;
            }
          }
         catch(Exception e ){}
             try {
            	//use standard intent to capture an image
                 Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                 

                 // Specify file so that large image is captured and returned
//                 File photo = createCaptureFile(destType,encodingType);
//                 intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//                 this.imageUri = Uri.fromFile(photo);

                 if (this.cordova != null) {
                     // Let's check to make sure the camera is actually installed. (Legacy Nexus 7 code)
                     PackageManager mPm = this.cordova.getActivity().getPackageManager();
                     if(intent.resolveActivity(mPm) != null)
                     {

                         this.cordova.startActivityForResult((CordovaPlugin) this, intent,CAMERA_CAPTURE);
                     }
                     else
                     {
                         LOG.d(LOG_TAG, "Error: You don't have a default camera.  Your device may not be CTS complaint.");
                     }
                 }
            }
            catch (IllegalArgumentException e)
            {
                callbackContext.error("Illegal Argument Exception");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                callbackContext.sendPluginResult(r);
                return true;
            }
             
            PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
            r.setKeepCallback(true);
            callbackContext.sendPluginResult(r);
            
            return true;
        }
        return false;
    }
  //perform cropping
    private void cropCapturedImage(Uri picUri){
  	  Context context = this.cordova.getActivity().getApplicationContext();
  	// Uri tmpUri = Uri.parse(picUri.toString());//The Uri to store the big bitmap
  	try{
      //call the standard crop action intent 
      Intent cropIntent = new Intent("com.android.camera.action.CROP");
      //indicate image type and Uri of image
      cropIntent.setDataAndType(picUri, "image/*");
      //set crop properties
      cropIntent.putExtra("crop", "true");
      cropIntent.putExtra("aspectX", 1);
      cropIntent.putExtra("aspectY", 1);
      if (targetWidth > 0) {
          cropIntent.putExtra("outputX", targetWidth);
      }
      if (targetHeight > 0) {
          cropIntent.putExtra("outputY", targetHeight);
      }
      if (targetHeight > 0 && targetWidth > 0 && targetWidth == targetHeight) {
          cropIntent.putExtra("aspectX", 1);
          cropIntent.putExtra("aspectY", 1);
      }
     // cropIntent.putExtra("outputX", 256);
     // cropIntent.putExtra("outputY", 256);
     // cropIntent.putExtra("scale", true);
     cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
      String uri =picUri.getPath();
      // for save the image in same location with same name.
//      final String docId = DocumentsContract.getDocumentId(picUri);
//      final String[] split = docId.split(":");
//      final String type = split[0];
      //File f = new File(Environment.getExternalStorageDirectory() + "/" );
      //File f= new File(picUri.toString());
      File f= new File(uri);
     cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));            
    cropIntent.putExtra("output", Uri.fromFile(f)); 
      cropIntent.putExtra("return-data", true);
      
      //start the activity - we handle returning in onActivityResult
      if (this.cordova != null) {
      this.cordova.startActivityForResult((CordovaPlugin) this,cropIntent,CROP_CAMERA);
      }
  	} catch (ActivityNotFoundException anfe) {
          // display an error message
          String errorMessage = "Whoops - your device doesn't support the crop action!";
          Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
          toast.show();
          this.callbackContext.success(picUri.toString());
      }
  	//return true;
  	 PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
     r.setKeepCallback(true);
     callbackContext.sendPluginResult(r);
  }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Uri picUri;
    	Bitmap bitmap=null;
    	String strUri=null;
    	GallerySingleSelectPlugin gsp = null;
    	String path =null;
    	Context context = this.cordova.getActivity().getApplicationContext();
   	 CompressImage compressimg =new CompressImage(context);
    	//CropImage cropimg = null;
    	if(requestCode ==CAMERA_CAPTURE){
    		//get the Uri for the captured image
    		//picUri = data.getData();
    		if(data.getExtras()!=null){
    			if(data.getData()==null){
    				bitmap = (Bitmap)data.getExtras().get("data");
    			
    			}else{
    				try {
					 bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
    				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
    				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
    				}
    		}
    		
    		//Converting bitmap to uri
			 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			 bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
			   path = Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
			  picUri=Uri.parse(path);
			  int orientation = getOrientation(context, picUri);
			  strUri=getRealPathFromURI(picUri.toString());
			  //rotate bitmap if orientation is different
			 // bitmap=getRotatedBitmap(bitmap,strUri);
//			  try {
//				  bitmap=gsp.scaleImage(context, picUri);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			  //Again converting bitmap to uri
//				 ByteArrayOutputStream newbytes = new ByteArrayOutputStream();
//				 bitmap.compress(Bitmap.CompressFormat.JPEG, 80, newbytes);
//				 path= Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
//				  picUri=Uri.parse(path);
//				  strUri=getRealPathFromURI(picUri.toString());
    		if ("content".equalsIgnoreCase(picUri.getScheme())){
    			
    			String fileStrUri="file://"+strUri;
    			picUri= Uri.parse(fileStrUri);
    		}
    		//picUri=Uri.parse(strUri);
    		//create instance of File with same name we created before to get image from storage
    		   //File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
    		//cropCapturedImage(Uri.fromFile(file));
    		
    		
    		
    		cropCapturedImage(picUri);
    		}
        }
    	else if(requestCode ==CROP_CAMERA){
    	
    		if(data!=null){
    			//get the returned data
//        		Bundle extras = data.getExtras();
//        		//get the cropped bitmap
//        		Bitmap photo = extras.getParcelable("data");
    		    Uri uri = data.getData();
    		    if (uri != null) {
    		    	//get the returned data
    	    		//Bundle extras = data.getExtras();
    	    		//get the cropped bitmap
    	    		//Bitmap thePic = extras.getParcelable("data");
    		    	//Bitmap photo= compressimg.compressImage(uri.toString());
    		   
    		    	//Bitmap photo = decodeBitmapFromUri(uri);
    		    	Bitmap photo = uriToBitmap(uri);
    		    	//converting bitmap to byte array/base64 string
    		     	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    		     	
    		     	photo.compress(Bitmap.CompressFormat.JPEG, 80, baos); //bm is the bitmap object     		
    		     
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
    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);


            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }	
    }
    //Convert content:// scheme: to file:// scheme 
	public String getRealPathFromURI(String contentURI) {
//		Cursor cursor=null;
//		Uri contentUri = Uri.parse(contentURI);
//		 String[] proj = { android.provider.MediaStore.Images.ImageColumns.DATA};
//		//Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
//		 try{
//		 cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//		 }
//		 catch(Exception e){}
//		if (cursor == null) {
//			return contentUri.getPath();
//		} else {
//			cursor.moveToFirst();
//			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//			Uri myUri= Uri.parse(cursor.getString(0)).getPath();
//			//return cursor.getString(idx);
//			return myUri;
//		}
		Context context = this.cordova.getActivity().getApplicationContext();
		String filePath = null;
		//Uri _uri = data.getData();
	//	Log.d("","URI = "+ _uri);
		Uri contentUri = Uri.parse(contentURI);
		if (contentUri != null && "content".equals(contentUri.getScheme())) {
		    Cursor cursor = context.getContentResolver().query(contentUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
		    cursor.moveToFirst();   
		    filePath = cursor.getString(0);
		   
		   // return filePath;
		    cursor.close();
		} else {
		    filePath = contentUri.getPath();
		    
		}
		return filePath;
	}
//    private File createCaptureFile(int encodingType, String fileName) {
//        if (fileName.isEmpty()) {
//            fileName = ".Pic";
//        }
//
//        if (encodingType == JPEG) {
//            fileName = fileName + ".jpg";
//        } else if (encodingType == PNG) {
//            fileName = fileName + ".png";
//        } else {
//            throw new IllegalArgumentException("Invalid Encoding Type: " + encodingType);
//        }
//
//        return new File(getTempDirectoryPath(), fileName);
//    }
//	
//    private String getTempDirectoryPath() {
//        File cache = null;
//
//        // SD Card Mounted
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            cache = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
//                    "/Android/data/" + cordova.getActivity().getPackageName() + "/cache/");
//        }
//        // Use internal storage
//        else {
//            cache = cordova.getActivity().getCacheDir();
//        }
//
//        // Create the cache directory if it doesn't exist
//        cache.mkdirs();
//        return cache.getAbsolutePath();
//    }
	public static Bitmap getRotatedBitmap(Bitmap bitmap,String filePath){
		ExifInterface exif = null;
		 try {
			exif = new ExifInterface(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	//	int degree = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
		Matrix matrix = new Matrix();
	    switch (orientation) {
	        case ExifInterface.ORIENTATION_NORMAL:
	            return bitmap;
	        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	            matrix.setScale(-1, 1);
	            break;
	        case ExifInterface.ORIENTATION_ROTATE_180:
	            matrix.setRotate(180);
	            break;
	        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	            matrix.setRotate(180);
	            matrix.postScale(-1, 1);
	            break;
	        case ExifInterface.ORIENTATION_TRANSPOSE:
	            matrix.setRotate(90);
	            matrix.postScale(-1, 1);
	            break;
	       case ExifInterface.ORIENTATION_ROTATE_90:
	           matrix.setRotate(90);
	           break;
	       case ExifInterface.ORIENTATION_TRANSVERSE:
	           matrix.setRotate(-90);
	           matrix.postScale(-1, 1);
	           break;
	       case ExifInterface.ORIENTATION_ROTATE_270:
	           matrix.setRotate(-90);
	           break;
	       default:
	           return bitmap;
	    }
	    try {
	    	Bitmap bmRotated  = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        bitmap.recycle();
	        return bmRotated;
	    }
	    catch (OutOfMemoryError e) {
	        e.printStackTrace();
	        return null;
	    }
		
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
	//private Bitmap decodeBitmapFromUri(Uri uri){
//   Bitmap bitmap = null;
//   Context context = this.cordova.getActivity().getApplicationContext();
//   try {
//       bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
//   } catch (FileNotFoundException e) {
//       e.printStackTrace();
//       return null;
//   }
//   return bitmap;
//}
	//}
//	@Override
//	public void onMediaScannerConnected() {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onScanCompleted(String arg0, Uri arg1) {
//		// TODO Auto-generated method stub
//		
//	}

	
}