package jad.rotetdemo.free;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class PhotoEditorActivity extends Activity
{

	ImageView main;
	ImageView text;



	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		setContentView(R.layout.photoedit);

		main=(ImageView)findViewById(R.id.main);
		text=(ImageView)findViewById(R.id.text);


		if(MainActivity.myBitmap!=null)
		{
			main.setImageBitmap(MainActivity.myBitmap);
		}
		if(CameraPreviewActivity.bmp!=null)
		{
			main.setImageBitmap(CameraPreviewActivity.bmp);
		}

		//	main.bringToFront();
		//main.setOnTouchListener(new TouchWithRotate());



	}




}