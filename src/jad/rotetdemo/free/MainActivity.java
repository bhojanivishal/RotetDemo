package jad.rotetdemo.free;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity
{
	protected static final int PHOTO_PICKED = 0;
	private File[] listFile;
	GridView gv;
	private String[] FilePathStrings;
	private String[] FileNameStrings;
	private ImageButton btn_camera;
	private ImageButton btn_gallery,btn_rateus;
	private Intent intent;
	public static Bitmap bmA_gallery ;
	View localView;
	static Boolean isClick;
	static LinearLayout txt_nofav;
	int width;
    int height;
	public static String TEMP_PHOTO_FILE,TEMP_PHOTO_FILE1;
	protected boolean return_data = false;
	protected boolean scale = true;
	protected boolean faceDetection = true;
	protected boolean circleCrop = false;
	
	private final static String TAG = "MediaStoreTest";
	View folderlocalView;
	String save_location;
	String jj;
	File f=null;
	ImageButton folder;
	public static Bitmap myBitmap;
	File folderfile;
	
	ImageButton gallery;
	
	
	
	public static void SetTEXTNOFAV() {
		// TODO Auto-generated method stub
		// txt_nofav = (TextView) findViewById(R.id.txt_nofav);
		txt_nofav.setVisibility(View.VISIBLE);
	}
	



	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(),
				inImage, "Title", null);
		return Uri.parse(path);
	}

	

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.activity_main);
		
		
		//banner add start

		

		//banner add end
		
		
		gallery=(ImageButton)findViewById(R.id.gallery);
		
		btn_camera = ((ImageButton)findViewById(R.id.btn_camera));
		
		
		gallery.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View arg0)
			{
				try 
				{
					Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					intent.putExtra("crop", "true");
					intent.putExtra("aspectX", 4);
					intent.putExtra("aspectY", 6);
					intent.putExtra("outputX", 480);	
					intent.putExtra("outputY", 600);
					intent.putExtra("scale", true);
					intent.putExtra("return-data", false);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
					intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
					intent.putExtra("noFaceDetection",true); // lol, negative boolean noFaceDetection

					startActivityForResult(intent, PHOTO_PICKED);
				} 
				catch (ActivityNotFoundException e) 
				{
					Toast.makeText(MainActivity.this, "photo not found", Toast.LENGTH_LONG).show();
				} 
			}
		});
		
		
		btn_camera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent(getApplicationContext(), CameraPreviewActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				
			}
		});
		
		
		
		
		
	}
	
	
	
	

	

	
	private Uri getTempUri() 
    {
		return Uri.fromFile(getTempFile());
	}
	
	private File getTempFile() 
	{
		if (isSDCARDMounted()) 
		{
			
			try 
			{
			  TEMP_PHOTO_FILE = "croppedPhoto"+ String.valueOf(System.currentTimeMillis()) + ".jpg";
			  save_location = Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/EditedImage";
			  File dir = new File(save_location);
			  if (!dir.exists())
			 	dir.mkdirs();
	         f = new File(save_location,TEMP_PHOTO_FILE);
			 jj = f.toString();
			
				//.createNewFile();
			} catch (Exception e)
			{
				Toast.makeText(this, "file not found", Toast.LENGTH_LONG).show();
			}
			return f;
		} else {
			return null;
		}
	}
	
	private boolean isSDCARDMounted()
	{
        String status = Environment.getExternalStorageState();
       
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
	
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode)
		{
		case PHOTO_PICKED:
			if (resultCode == RESULT_OK)
			{
				
				imageFromGallery(resultCode, data);
				
				
				Intent localIntent = new Intent(MainActivity.this,PhotoEditorActivity.class);
				
				startActivity(localIntent);
				
			}			
		}
	}
	
	private void imageFromGallery(int paramInt, Intent paramIntent)
	{ 
		try
		{
			Uri localUri = paramIntent.getData();
			String[] arrayOfString = { "_data" };
			Cursor localCursor = getContentResolver().query(localUri, arrayOfString, null, null, null);
			localCursor.moveToFirst();
			String str = localCursor.getString(localCursor.getColumnIndex(arrayOfString[0]));
			localCursor.close();
			myBitmap=BitmapFactory.decodeFile(str);
//			updateImageView(BitmapFactory.decodeFile(str));
			return;
		}
		catch (OutOfMemoryError localOutOfMemoryError)
		{
			Toast.makeText(this, "Image Cann't load.Try Again!!", 0).show();
			return;
		}
		catch (Exception localException)
		{
			try
			{
				File tempFile = f;
				InputStream input = null;
				BufferedInputStream buf;
				try 
				{
					input = new FileInputStream(new File(tempFile.toString()));
					buf = new BufferedInputStream(input);
					myBitmap = BitmapFactory.decodeStream(buf);
//					myBitmap = BitmapFactory.decodeStream(buf);
//					updateImageView(myBitmap);
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			catch (OutOfMemoryError localOutOfMemoryError)
			{
				Toast.makeText(this, "Image Cann't load.Try Again!!", 0).show();
				return;
			}
		}
	}

	protected void onPause()
	{
		super.onPause();
	}

	protected void onStart()
	{
		super.onStart();
	}

	protected void onStop()
	{
		super.onStop();
	}
	
	
	
}