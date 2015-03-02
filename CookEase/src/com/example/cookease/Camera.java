package com.example.cookease;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class Camera extends Activity {
	static final int REQUEST_TAKE_PHOTO = 1;
	private static Context context;
	private String mCurrentPhotoPath;
	
	public Camera(Context c){
	context = c;	
	}
	/**
	 * Camera functions
	 */
		void dispatchTakePictureIntent() {
		    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
		    	// Create the File where the photo should go
		        File photoFile = null;
		        try {
		            photoFile = createImageFile();
		        } catch (IOException ex) {
		            // Error occurred while creating the File
		        }
		        // Continue only if the File was successfully created
		        if (photoFile != null) {
		            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
		                    Uri.fromFile(photoFile));
		            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO); //TODO can't figure out why this returns nullpointer
		        }		    }
		}
		
		//Save photo taken
		File createImageFile() throws IOException {
		    // Create an image file name
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(0));
		    String imageFileName = "JPEG_" + timeStamp + "_";
		    File storageDir = Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_PICTURES);
		    File image = File.createTempFile(
		        imageFileName,  /* prefix */
		        ".jpg",         /* suffix */
		        storageDir      /* directory */
		    );

		    // Save a file: path for use with ACTION_VIEW intents
		    mCurrentPhotoPath = image.getAbsolutePath();
		    //displayedRecipe.setPic(mCurrentPhotoPath);
		    //Log.w("RCP SET?", displayedRecipe.getPic());
		    return image;
		}
		
		void deleteExternalStoragePrivateFile(String deleteImgPath) {
		    // Get path for the file on external storage.  If external
		    // storage is not currently mounted this will fail.
		    File file = new File(getExternalFilesDir(null), deleteImgPath);
		    if (file != null) {
		        file.delete();
		    }
		}
		
		public String getImgPath() {
			return mCurrentPhotoPath;
		}
	
}
