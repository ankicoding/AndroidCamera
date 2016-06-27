package com.newgen.nemp.client.omnidesk.plugin.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.newgen.nemp.client.omnidesk.plugin.camera.ImageLoadingUtils;
import com.newgen.nemp.client.omnidesk.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class CompressImage  {

	private final int REQUEST_CODE_CLICK_IMAGE = 01;
	//public final String ACTION_GET_IMAGE_NAME = "compressimage";
	//Uri myUri;
	private Context context;
	private ImageLoadingUtils utils;
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext)throws JSONException {
		   // Log.e(TAG,"Inside Version plugin.");
			this.callbackContext = callbackContext;
		    boolean result = false;
		    if(action.equals(ACTION_GET_IMAGE_NAME)) {
		        try {
		            myUri = Uri.parse(args.getString(0));
		            compressImage(myUri.toString());
		        } catch (JSONException e) {
		            // TODO Auto-generated catch block
		            e.printStackTrace();
		        }
		        result = true;
		    }


		    return result;
		}

	public CompressImage(Context context){
		this.context = context;
	}
	public Bitmap compressImage(String imageUri) {
		
		String filePath = getRealPathFromURI(imageUri);
		Bitmap scaledBitmap = null;
		ImageLoadingUtils utils= new ImageLoadingUtils(context);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
		
		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;
		float maxHeight = 256.0f;
		float maxWidth = 256.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;     
				
			}
		}
				
		options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16*1024];
			
		try{	
			bmp = BitmapFactory.decodeFile(filePath,options);
		}
		catch(OutOfMemoryError exception){
			exception.printStackTrace();
			
		}
		try{
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
		}
		catch(OutOfMemoryError exception){
			exception.printStackTrace();
		}
						
		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float)options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;
			
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

						
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);
			Uri photoUri= Uri.parse(imageUri);
		//	int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 
                ExifInterface.ORIENTATION_UNDEFINED);
			// int orientation = getOrientation(context, photoUri);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
//		FileOutputStream out = null;
//		String filename = getFilename();
//		try {
//			out = new FileOutputStream(filename);
//			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		
		return scaledBitmap;

	}
	
	public String getRealPathFromURI(String contentURI) {
		Uri contentUri = Uri.parse(contentURI);
		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
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
}