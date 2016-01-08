package lieying.com.avplayer.view;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lieying.com.avplayer.utils.U;


public class PreviewView extends SurfaceView implements Callback {

    private Object mLock = new Object();
    private MediaRecorder mMediaRecorder = null;
    private boolean mIsPlay;
    // 0表示后置，1表示前置
    private int mCameraPosition = 1;

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera.Parameters mParameter;
    private boolean mPreviewRunning;
    private Activity mActivity;
    private Map<Integer, Integer> mSupportSize = new HashMap<Integer, Integer>();
    boolean cameraConfigured = false;

    private int mWidth = 0, mHeight = 0;
    // 当前摄像头设备ID
    private int defaultCameraId;
    // 摄像头个数
    private int numberOfCameras;

    public PreviewView(Context context) {
        super(context);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        // 获得SurfaceHolder对象
        mSurfaceHolder = getHolder();
        // 指定用于捕捉拍照事件的SurfaceHolder.Callback对象
        mSurfaceHolder.addCallback(this);
        // 设置SurfaceHolder对象的类型
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mMediaRecorder = new MediaRecorder();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int version = getSDKVersionNumber();
        if (version > 8) {
            numberOfCameras = this.getNumberOfCameras();
            // Find the ID of the default camera
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    defaultCameraId = i;
                }
            }
        }
//        openCamera();
        // 开启相机
//        if (mCamera == null) {
//            numberOfCameras = this.getNumberOfCameras();
//            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//            for (int i = 0; i < numberOfCameras; i++) {
//                Camera.getCameraInfo(i, cameraInfo);
//                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                    defaultCameraId = i;
//                    mCameraPosition = i;//此时保存后置摄像头的状态位
//                }
//            }
            setStartPreview();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mSurfaceHolder = holder;
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mCamera.startPreview();
//        setStartPreview();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private void openCamera() {
        synchronized (mLock) {
            try {
                mCamera = Camera.open();
//                setStartPreview();
            } catch (Exception e) {
                U.ShowToast(getContext(), "连接摄像头失败！\r\n请检查是否在其他地方打开了摄像头。\r\n然后返回重新拍照。");
                mCamera = null;
            }
        }
    }

    private void releaseCamera() {
        synchronized (mLock) {
            try {
                if (null != mCamera) {
                    if (mPreviewRunning) {
                        mCamera.stopPreview();
                        mPreviewRunning = false;
                    }
                    mCamera.release();
                    mCamera = null;
                }
            } catch (Exception e) {
            }
        }
    }


    // 切换前后摄像头
    public void switchCamera() {
        mMediaRecorder.reset();
        if (mCamera != null) {
            mCamera.lock();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera = Camera.open((defaultCameraId + 1) % numberOfCameras);
        defaultCameraId = (defaultCameraId + 1) % numberOfCameras;
        cameraConfigured = false;
//        setCamera(mCamera);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestLayout();
        mPreviewRunning = true;
        mCamera.startPreview();
        mMediaRecorder.reset();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        // Set output file format
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 这两项需要放在setOutputFormat之后
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(640, 480);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
    }

    // 返回摄像头个数
    public int getNumberOfCameras() {
        int version = getSDKVersionNumber();
        if (version > 8)
            return Camera.getNumberOfCameras();
        else
            return 1;
    }

    // 获取当前手机端SDK版本号
    public int getSDKVersionNumber() {
        int sdkVersion;
        try {
            sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }


    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    private void setStartPreview() {
        try {
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.unlock();
            mMediaRecorder.reset();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // Set output file format
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 这两项需要放在setOutputFormat之后
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            mMediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setVideoSize(640, 480);
            mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        } catch (IOException e) {
            releaseCamera();
        }
    }


    //判断前置摄像头是否存在
    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    //判断后置摄像头是否存在
    private int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }


    public void setOutPutFile(String path) {
        mMediaRecorder.setOutputFile(path);
    }

    public void start() {
        if (mMediaRecorder != null && mIsPlay == false) {
            try {
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mIsPlay = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        if (mMediaRecorder != null && mIsPlay) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mIsPlay = true;
        }
    }

    public boolean isPlaying() {
        return mIsPlay;
    }

}
