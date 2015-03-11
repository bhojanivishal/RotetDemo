package jad.rotetdemo.free;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ViewFlipper;

public class CameraPreviewActivity extends Activity 
{

	private CameraPreview mPreview;
	private RelativeLayout mLayout;
	ImageView botonCapture, gallery, switch_cam, localImageView, galleryimage,
			camera, rateus, more, folder, suiteview, camerareturn;
	static ViewFlipper viewFlipper;
	@SuppressWarnings("deprecation")
	private final GestureDetector detector = new GestureDetector(
			new MyGestureDetector());
	
	
	
	static Bitmap bmppp, map, correctBmp, localBitmap, bmp;
	public float scaleHeight = 0.0F;
	public float scaleWidth = 0.0F;
	private static Context context;
	int shareheight;
	int sharewidth;
	Bitmap gallerybitmap;
	Boolean frontcamclick = false;
	static Boolean clickonsuiteview = false;
	int position;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		setContentView(R.layout.camera);


		CameraPreviewActivity.context = getApplicationContext();

		mLayout = (RelativeLayout) findViewById(R.id.rl_mlayout);

		viewFlipper = ((ViewFlipper) findViewById(R.id.flipper));

		botonCapture = (ImageView) findViewById(R.id.capture);
		
		switch_cam = (ImageView) findViewById(R.id.swich_camera);

		viewFlipper.setBackgroundColor(Color.TRANSPARENT);

		botonCapture.setOnClickListener(new View.OnClickListener() {

			public void onClick(View paramView) {

				CameraPreview.mCamera.takePicture(myShutterCallback,
						myPictureCallback_RAW, myPictureCallback_JPG);

			}
		});

		viewFlipper.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				CameraPreviewActivity.this.detector.onTouchEvent(arg1);
				return true;
			}
		});

		camerareturn = (ImageView) findViewById(R.id.camera_return);
		camerareturn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				onPause();
				onResume();
				switch_cam.setVisibility(View.VISIBLE);
				camerareturn.setVisibility(View.GONE);

				frontcamclick = false;
			}
		});

		switch_cam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				frontcamclick = true;

				mPreview.stop();
				mLayout.removeView(mPreview);
				mPreview = null;

				String jaydeep = "helloo";
				// TODO Auto-generated method stub
				mPreview = new CameraPreview(CameraPreviewActivity.this, 0,
						CameraPreview.LayoutMode.FitToParent, jaydeep);
				LayoutParams previewLayoutParams = new LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

				mLayout.addView(mPreview, 0, previewLayoutParams);

				switch_cam.setVisibility(View.GONE);
				camerareturn.setVisibility(View.VISIBLE);

			}
		});

	
	}


	static void setFlipperImage(int paramInt) {
		ImageView localImageView = new ImageView(CameraPreviewActivity.context);
		localImageView.setBackgroundResource(paramInt);
		viewFlipper.addView(localImageView);
	}

	ShutterCallback myShutterCallback = new ShutterCallback() {

		public void onShutter() {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback myPictureCallback_RAW = new PictureCallback() {

		public void onPictureTaken(byte[] arg0, Camera arg1) {
			// TODO Auto-generated method stub
		}
	};

	PictureCallback myPictureCallback_JPG = new PictureCallback() {

		public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
			if (paramArrayOfByte != null) {
				BitmapFactory.Options localOptions = new BitmapFactory.Options();
				localOptions.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(new ByteArrayInputStream(
						paramArrayOfByte), null, localOptions);

				int i = localOptions.outWidth;
				int j = localOptions.outHeight;

				System.out.println("LocalOptions:" + i);
				System.out.println("LocalOptions:" + j);

				CameraPreviewActivity.this.getShareAspectRatio(i, j);

				localBitmap = getResizedOriginalBitmap(paramArrayOfByte,
						sharewidth, shareheight);

				bmp = Bitmap.createBitmap(localBitmap, 0, 0,
						localBitmap.getWidth(), localBitmap.getHeight());
			}
			Intent localIntent = new Intent(CameraPreviewActivity.this,
					PhotoEditorActivity.class);
			startActivity(localIntent);
		}

	};

	public void getShareAspectRatio(int paramInt1, int paramInt2) {
		float f = paramInt1 / paramInt2;
		if (f > 1.0F) {
			scaleWidth = 500.0F;
			scaleHeight = (scaleWidth / f);

		} else {
			scaleHeight = 500.0F;
			scaleWidth = (f * scaleHeight);
		}
		sharewidth = (int) scaleWidth;
		shareheight = (int) scaleHeight;

	}

	public Bitmap getResizedOriginalBitmap(byte[] paramArrayOfByte,
			int paramInt1, int paramInt2) {
		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(new ByteArrayInputStream(paramArrayOfByte),
				null, localOptions);
		int i = localOptions.outWidth;
		int j = localOptions.outHeight;
		int k = 1;

		if (k == 1) {

			Log.i("in if", "ajsdgasd");
			float f1 = paramInt1 / i;
			float f2 = paramInt2 / j;
			localOptions.inJustDecodeBounds = false;
			localOptions.inDither = false;
			localOptions.inSampleSize = k;
			localOptions.inScaled = false;
			localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
			localBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(
					paramArrayOfByte), null, localOptions);
			Matrix matrix = new Matrix();
			if (frontcamclick) {
				Log.i("inclick", "asdfa");

				matrix.preRotate(90);

				float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1 };
				matrix = new Matrix();
				Matrix matrixMirrorY = new Matrix();
				matrixMirrorY.setValues(mirrorY);

				matrix.postConcat(matrixMirrorY);

				matrix.preRotate(270);

				// matrix.preRotate(90);
			} else {
				Log.i("not click", "asdfa");
				matrix.postRotate(90);
			}
			return Bitmap.createBitmap(localBitmap, 0, 0,
					localBitmap.getWidth(), localBitmap.getHeight(), matrix,
					true);

		}
		i /= 2;
		j /= 2;
		k *= 2;

		return localBitmap;
	}

	public static Bitmap getResizedBitmap(Bitmap paramBitmap, int paramInt1,
			int paramInt2) {

		BitmapFactory.Options localOptions = new BitmapFactory.Options();
		localOptions.inJustDecodeBounds = true;
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		paramBitmap.compress(Bitmap.CompressFormat.PNG, 100,
				localByteArrayOutputStream);
		byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
		BitmapFactory.decodeStream(new ByteArrayInputStream(arrayOfByte), null,
				localOptions);
		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		int k = 1;
		if (i / 2 <= paramInt1) {
			float f1 = paramInt1 / i;
			float f2 = paramInt2 / j;
			localOptions.inJustDecodeBounds = false;
			localOptions.inDither = false;
			localOptions.inSampleSize = k;
			localOptions.inScaled = false;
			localOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

			Log.e("PictureDemo", "*************");

			Bitmap localBitmap = BitmapFactory.decodeStream(
					new ByteArrayInputStream(arrayOfByte), null, localOptions);
			localBitmap.getWidth();
			localBitmap.getHeight();
			return Bitmap.createBitmap(localBitmap, 0, 0,
					localBitmap.getWidth(), localBitmap.getHeight());
		}
		i /= 2;
		j /= 2;
		k *= 2;
		return paramBitmap;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPreview = new CameraPreview(this, 0,
				CameraPreview.LayoutMode.FitToParent);
		LayoutParams previewLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		mLayout.addView(mPreview, 0, previewLayoutParams);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mPreview.stop();
		mLayout.removeView(mPreview);
		mPreview = null;
	}

	class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		MyGestureDetector() {

		}

	}
	

}
