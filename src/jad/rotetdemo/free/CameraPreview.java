package jad.rotetdemo.free;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * This class assumes the parent layout is RelativeLayout.LayoutParams.
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private static boolean DEBUGGING = true;
	private static final String LOG_TAG = "CameraPreviewSample";
	private static final String CAMERA_PARAM_ORIENTATION = "orientation";
	private static final String CAMERA_PARAM_LANDSCAPE = "landscape";
	private static final String CAMERA_PARAM_PORTRAIT = "portrait";
	protected Activity mActivity;
	private SurfaceHolder mHolder;
	protected static Camera mCamera;
	protected List<Camera.Size> mPreviewSizeList;
	protected List<Camera.Size> mPictureSizeList;
	protected Camera.Size mPreviewSize;
	protected Camera.Size mPictureSize;
	private int mSurfaceChangedCallDepth = 0;
	private int mCameraId;
	private LayoutMode mLayoutMode;
	private int mCenterPosX = -1;
	private int mCenterPosY;
	private Camera.Parameters cameraParams;
	private static final String TAG = null;
	PreviewReadyCallback mPreviewReadyCallback = null;

	public static enum LayoutMode {
		FitToParent, // Scale to the size that no side is larger than the parent
		NoBlank // Scale to the size that no side is smaller than the parent
	};

	public interface PreviewReadyCallback {
		public void onPreviewReady();
	}

	/**
	 * State flag: true when surface's layout size is set and surfaceChanged()
	 * process has not been completed.
	 */
	protected boolean mSurfaceConfiguring = false;

	@SuppressLint("NewApi")
	public CameraPreview(Activity activity, int cameraId, LayoutMode mode) {
		super(activity); // Always necessary
		mActivity = activity;
		mLayoutMode = mode;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			if (Camera.getNumberOfCameras() > cameraId) {

				Log.i("camera is greater than cameraid", "asfas");
				mCameraId = cameraId;
			} else {
				mCameraId = 0;
			}
		} else {
			mCameraId = 0;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(mCameraId);
		} else {
			mCamera = Camera.open(0);
		}
		Camera.Parameters cameraParams = mCamera.getParameters();
		mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
		mPictureSizeList = cameraParams.getSupportedPictureSizes();
//		for (int k = 0;; k++) {
//			if (k >= CameraPreviewActivity.gallery_grid_Images.length)
//				return;
//			CameraPreviewActivity
//					.setFlipperImage(CameraPreviewActivity.gallery_grid_Images[k]);
//
//		}

	}

	@SuppressLint("NewApi")
	public CameraPreview(Activity activity, int cameraId, LayoutMode mode,
			String jaydeep) {
		super(activity); // Always necessary
		mActivity = activity;
		mLayoutMode = mode;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		String newew = jaydeep;

		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Log.d("No of cameras", Camera.getNumberOfCameras() + "");
		for (cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
			CameraInfo camInfo = new CameraInfo();
			Camera.getCameraInfo(cameraId, camInfo);

			if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
				mCameraId = cameraId;
			} else {
				mCameraId = 0;
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
		} else {
			mCamera = Camera.open(0);
		}
		Camera.Parameters cameraParams = mCamera.getParameters();
		mPreviewSizeList = cameraParams.getSupportedPreviewSizes();
		mPictureSizeList = cameraParams.getSupportedPictureSizes();
//		for (int k = 0;; k++) {
//			if (k >= CameraPreviewActivity.gallery_grid_Images.length)
//				return;
//			CameraPreviewActivity
//					.setFlipperImage(CameraPreviewActivity.gallery_grid_Images[k]);
//
//		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(mHolder);
			
			cameraParams = mCamera.getParameters();

			if (cameraParams.getSupportedWhiteBalance().contains(
					Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT)) {
				cameraParams
				.setWhiteBalance(Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
				Log.d(TAG, "white balance auto");
			}

			
		} catch (IOException e) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mSurfaceChangedCallDepth++;
		doSurfaceChanged(width, height);
		mSurfaceChangedCallDepth--;
	}

	private void doSurfaceChanged(int width, int height) {
		mCamera.stopPreview();

		Camera.Parameters cameraParams = mCamera.getParameters();
		boolean portrait = isPortrait();

		if (!mSurfaceConfiguring) {
			Camera.Size previewSize = determinePreviewSize(portrait, width,
					height);
			Camera.Size pictureSize = determinePictureSize(previewSize);
			if (DEBUGGING) {
				Log.v(LOG_TAG, "Desired Preview Size - w: " + width + ", h: "
						+ height);
			}
			mPreviewSize = previewSize;
			mPictureSize = pictureSize;
			mSurfaceConfiguring = adjustSurfaceLayoutSize(previewSize,
					portrait, width, height);
			if (mSurfaceConfiguring && (mSurfaceChangedCallDepth <= 1)) {
				return;
			}
		}

		configureCameraParameters(cameraParams, portrait);
		mSurfaceConfiguring = false;

		try {
			mCamera.startPreview();
		} catch (Exception e) {
			Log.w(LOG_TAG, "Failed to start preview: " + e.getMessage());

			// Remove failed size
			mPreviewSizeList.remove(mPreviewSize);
			mPreviewSize = null;

			// Reconfigure
			if (mPreviewSizeList.size() > 0) { // prevent infinite loop
				surfaceChanged(null, 0, width, height);
			} else {
				Toast.makeText(mActivity, "Can't start preview",
						Toast.LENGTH_LONG).show();
				Log.w(LOG_TAG, "Gave up starting preview");
			}
		}

		if (null != mPreviewReadyCallback) {
			mPreviewReadyCallback.onPreviewReady();
		}
	}

	protected Camera.Size determinePreviewSize(boolean portrait, int reqWidth,
			int reqHeight) {
		// Meaning of width and height is switched for preview when portrait,
		// while it is the same as user's view for surface and metrics.
		// That is, width must always be larger than height for setPreviewSize.
		int reqPreviewWidth; // requested width in terms of camera hardware
		int reqPreviewHeight; // requested height in terms of camera hardware
		if (portrait) {
			reqPreviewWidth = reqHeight;
			reqPreviewHeight = reqWidth;
		} else {
			reqPreviewWidth = reqWidth;
			reqPreviewHeight = reqHeight;
		}

		if (DEBUGGING) {
			Log.v(LOG_TAG, "Listing all supported preview sizes");
			for (Camera.Size size : mPreviewSizeList) {
				Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
			}
			Log.v(LOG_TAG, "Listing all supported picture sizes");
			for (Camera.Size size : mPictureSizeList) {
				Log.v(LOG_TAG, "  w: " + size.width + ", h: " + size.height);
			}
		}

		// Adjust surface size with the closest aspect-ratio
		float reqRatio = ((float) reqPreviewWidth) / reqPreviewHeight;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		Camera.Size retSize = null;
		for (Camera.Size size : mPreviewSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}

		return retSize;
	}

	protected Camera.Size determinePictureSize(Camera.Size previewSize) {
		Camera.Size retSize = null;
		for (Camera.Size size : mPictureSizeList) {
			if (size.equals(previewSize)) {
				return size;
			}
		}

		if (DEBUGGING) {
			Log.v(LOG_TAG, "Same picture size not found.");
		}

		// if the preview size is not supported as a picture size
		float reqRatio = ((float) previewSize.width) / previewSize.height;
		float curRatio, deltaRatio;
		float deltaRatioMin = Float.MAX_VALUE;
		for (Camera.Size size : mPictureSizeList) {
			curRatio = ((float) size.width) / size.height;
			deltaRatio = Math.abs(reqRatio - curRatio);
			if (deltaRatio < deltaRatioMin) {
				deltaRatioMin = deltaRatio;
				retSize = size;
			}
		}

		return retSize;
	}

	protected boolean adjustSurfaceLayoutSize(Camera.Size previewSize,
			boolean portrait, int availableWidth, int availableHeight) {
		float tmpLayoutHeight, tmpLayoutWidth;
		if (portrait) {
			tmpLayoutHeight = previewSize.width;
			tmpLayoutWidth = previewSize.height;
		} else {
			tmpLayoutHeight = previewSize.height;
			tmpLayoutWidth = previewSize.width;
		}

		float factH, factW, fact;
		factH = availableHeight / tmpLayoutHeight;
		factW = availableWidth / tmpLayoutWidth;
		if (mLayoutMode == LayoutMode.FitToParent) {

			if (factH < factW) {
				fact = factH;

				Log.i("in if of fitparent", "asbahsj");
			} else {
				Log.i("in else of fitparent", "asbahsj");
				fact = factW;
			}
		} else {
			if (factH < factW) {
				Log.i("in else of if", "asbahsj");
				fact = factW;
			} else {
				Log.i("in else  of else", "asbahsj");
				fact = factH;
			}
		}

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this
				.getLayoutParams();

		int layoutHeight = (int) (availableHeight);
		int layoutWidth = (int) (availableWidth);
		if (DEBUGGING) {
			Log.v(LOG_TAG, "Preview Layout Size - w: " + layoutWidth + ", h: "
					+ layoutHeight);
			Log.v(LOG_TAG, "Scale factor: " + fact);
		}

		boolean layoutChanged;
		if ((layoutWidth != this.getWidth())
				|| (layoutHeight != this.getHeight())) {
			layoutParams.height = layoutHeight;
			layoutParams.width = layoutWidth;
			if (mCenterPosX >= 0) {
				layoutParams.topMargin = mCenterPosY - (layoutHeight / 2);
				layoutParams.leftMargin = mCenterPosX - (layoutWidth / 2);
			}
			this.setLayoutParams(layoutParams); // this will trigger another
												// surfaceChanged invocation.
			layoutChanged = true;
		} else {
			layoutChanged = false;
		}

		return layoutChanged;
	}

	public void setCenterPosition(int x, int y) {
		mCenterPosX = x;
		mCenterPosY = y;
	}

	protected void configureCameraParameters(Camera.Parameters cameraParams,
			boolean portrait) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) { // for 2.1 and
																	// before
			if (portrait) {
				cameraParams.set(CAMERA_PARAM_ORIENTATION,
						CAMERA_PARAM_PORTRAIT);
			} else {
				cameraParams.set(CAMERA_PARAM_ORIENTATION,
						CAMERA_PARAM_LANDSCAPE);
			}
		} else { // for 2.2 and later
			int angle;
			Display display = mActivity.getWindowManager().getDefaultDisplay();
			switch (display.getRotation()) {
			case Surface.ROTATION_0: // This is display orientation
				angle = 90; // This is camera orientation
				break;
			case Surface.ROTATION_90:
				angle = 0;
				break;
			case Surface.ROTATION_180:
				angle = 270;
				break;
			case Surface.ROTATION_270:
				angle = 180;
				break;
			default:
				angle = 90;
				break;
			}
			Log.v(LOG_TAG, "angle: " + angle);
			mCamera.setDisplayOrientation(angle);
		}

		cameraParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
		cameraParams.setPictureSize(mPictureSize.width, mPictureSize.height);
		if (DEBUGGING) {
			Log.v(LOG_TAG, "Preview Actual Size - w: " + mPreviewSize.width
					+ ", h: " + mPreviewSize.height);
			Log.v(LOG_TAG, "Picture Actual Size - w: " + mPictureSize.width
					+ ", h: " + mPictureSize.height);
		}

		mCamera.setParameters(cameraParams);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stop();
	}

	public void stop() {
		if (null == mCamera) {
			return;
		}
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	public boolean isPortrait() {
		return (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
	}

	public void setOneShotPreviewCallback(PreviewCallback callback) {
		if (null == mCamera) {
			return;
		}
		mCamera.setOneShotPreviewCallback(callback);
	}

	public void setPreviewCallback(PreviewCallback callback) {
		if (null == mCamera) {
			return;
		}
		mCamera.setPreviewCallback(callback);
	}

	public Camera.Size getPreviewSize() {
		return mPreviewSize;
	}

	public void setOnPreviewReady(PreviewReadyCallback cb) {
		mPreviewReadyCallback = cb;
	}
}
