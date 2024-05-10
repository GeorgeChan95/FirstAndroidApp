package com.george.chapter14.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.george.chapter14.listener.OnStopListener;
import com.george.chapter14.utils.BitmapUtil;
import com.george.chapter14.utils.DateUtil;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("NewApi")
public class CameraXView extends RelativeLayout {
    private final static String TAG = "GeorgeTag";

    private Context mContext; // 声明一个上下文对象
    private PreviewView mCameraPreview; // 声明一个预览视图对象
    private CameraSelector mCameraSelector; // 摄像头选择器
    private Preview mPreview; // 预览对象
    private ProcessCameraProvider mCameraProvider; // 相机提供器
    private ImageCapture mImageCapture; // 图像捕捉器
    private VideoCapture mVideoCapture; // 视频捕捉器
    private ExecutorService mExecutorService; // 线程池对象
    private LifecycleOwner mOwner; // 声明一个声明周期拥有者

    private final Handler mHandler = new Handler(Looper.getMainLooper()); // 声明一个处理器对象
    private String mMediaDir; // 声明多媒体文件保存目录
    public final static int MODE_PHOTO = 0; // 拍照模式
    public final static int MODE_RECORD = 1; // 录像模式
    private int mCameraMode = MODE_PHOTO; // 相机打开默认为：拍照模式
    private int mCameraType = CameraSelector.LENS_FACING_BACK; // 摄像头类型，默认为后置摄像头
    private int mAspectRatio = AspectRatio.RATIO_16_9; // 宽高比例。RATIO_4_3表示宽高3比4；RATIO_16_9表示宽高9比16
    private int mFlashMode = ImageCapture.FLASH_MODE_AUTO; // 闪光灯模式
    // 自定义拍摄完成监听器
    private OnStopListener mStopListener;

    /**
     * 自定义视图需要重写构造方法
     * @param context
     */
    public CameraXView(Context context) {
        this(context, null);
    }

    /**
     * 自定义视图需要重写构造方法
     * @param context
     * @param attrs
     */
    public CameraXView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        // 创建预览视图
        mCameraPreview = new PreviewView(mContext);
        // 给预览视图设置宽高参数
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCameraPreview.setLayoutParams(params);
        // 给预览视图添加到界面上
        addView(mCameraPreview);

        // 创建一个单线程的线程池
        mExecutorService = Executors.newSingleThreadExecutor();
        // 指定多媒体文件保存目录
        mMediaDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
    }

    /**
     * 打开相机，初始化参数设置
     * @param owner
     * @param camereMode
     * @param sl
     */
    public void openCamera(LifecycleOwner owner, int camereMode, OnStopListener sl) {
        this.mOwner = owner;
        this.mCameraMode = camereMode;
        this.mStopListener = sl;
        // 初始化相机
        mHandler.post(() -> initCamera());
    }

    /**
     * 初始化相机
     */
    private void initCamera() {
        // 获取一个 ListenableFuture 对象，用于异步获取相机提供器。
        ListenableFuture future = ProcessCameraProvider.getInstance(mContext);
        // 这是一个监听器，当 future 对象的状态发生改变时触发。
        // 在这里，使用了 lambda 表达式作为监听器的回调函数，表示异步任务的执行结果。
        // 通过ContextCompat.getMainExecutor(mContext)获取主线程的执行器，确保回调在主线程中执行。
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // 方法获取异步任务的结果，即相机提供器。
                    mCameraProvider = (ProcessCameraProvider) future.get();
                    // 重置相机
                    resetCamera();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(mContext));
    }

    // 照片保存路径
    private String mPhotoPath;

    /**
     * 获取照片的保存路径
     * @return
     */
    public String getPhotoPath() {
        return mPhotoPath;
    }

    /**
     * 开始拍照
     */
    public void takePicture() {
        // 拍摄图片保存的路径
        mPhotoPath = String.format("%s/%s.jpg", mMediaDir, DateUtil.getNowDateTime());
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        // 构建图像捕捉器的输出选项
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions
                                                        .Builder(new File(mPhotoPath))
                                                        .setMetadata(metadata).build();
        mImageCapture.takePicture(options, mExecutorService, new ImageCapture.OnImageSavedCallback() {
            // 拍摄成功时的回调
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // 通知相册，有了新的照片
                BitmapUtil.notifyPhotoAlbum(mContext, mPhotoPath);
                // 拍摄完成，调用回调监听
                mStopListener.onStop("已拍摄完成，照片路径为：" + mPhotoPath);
            }
            // 拍摄异常时的回调
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                mStopListener.onStop("拍摄失败，错误信息为：" + exception.getMessage());
            }
        });
    }

    /**
     * 切换相机的摄像头
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void switchCamera() {
        if (mCameraType == CameraSelector.LENS_FACING_BACK) {
            mCameraType = CameraSelector.LENS_FACING_FRONT;
        } else {
            mCameraType = CameraSelector.LENS_FACING_BACK;
        }
        // 重置相机
        resetCamera();
    }

    /**
     * 重置相机
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi") // 暂时忽略特定的 lint 错误或警告，允许代码中存在被标记的限制 API 使用。
    private void resetCamera() {
        // 获取当前屏幕的旋转角度
        int rotation = mCameraPreview.getDisplay().getRotation();
        // 构建一个摄像头选择器
        mCameraSelector = new CameraSelector.Builder().requireLensFacing(mCameraType).build();
        // 构建预览对象
        mPreview = new Preview.Builder()
                .setTargetAspectRatio(mAspectRatio)
                .build();
        // 构建一个图像捕捉器
        mImageCapture = new ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY) // 设置捕捉模式
                                    .setTargetRotation(rotation) // 设置旋转角度
                                    .setFlashMode(mFlashMode) // 设置闪关模式
                                    .build();
        // 如果是录像模式的话，还需要构建视频捕捉器
        if (mCameraMode == MODE_RECORD) {
            mVideoCapture = new VideoCapture.Builder()
                    .setTargetAspectRatio(mAspectRatio)
                    .setVideoFrameRate(60)
                    .setBitRate(3*1024*1024)
                    .setTargetRotation(rotation)
                    .setAudioRecordSource(MediaRecorder.AudioSource.MIC)
                    .build();
        }
        // 绑定摄像头
        bindCamera(MODE_PHOTO);
        // 设置预览视图的表面提供器, 从而将相机捕捉的图像显示在预览视图上
        mPreview.setSurfaceProvider(mCameraPreview.getSurfaceProvider());
    }

    /**
     * 绑定摄像头
     *
     * 关于 mOwner 对象的作用，它通常是一个 Activity 或 Fragment，用于指定生命周期所有者。
     * 在 Android 中，Activity 和 Fragment 都实现了 LifecycleOwner 接口，
     * 该接口定义了一组生命周期方法，如 onCreate()、onStart()、onResume() 等。
     * 通过将 mOwner 对象传递给相机提供器的 bindToLifecycle() 方法，
     * 相机提供器可以监听到 mOwner 对象的生命周期变化，并根据其生命周期状态来管理相机的开启、关闭等操作。
     *
     * @param cameraMode 相机模式：0拍照，1录像
     */
    private void bindCamera(int cameraMode) {
        // 绑定相机前，要先解绑
        mCameraProvider.unbindAll();
        try {
            if (cameraMode == MODE_PHOTO) { // 拍照
                // 把相机选择器、预览视图、图像捕捉器绑定到相机提供器的生命周期
                Camera camera = mCameraProvider.bindToLifecycle(mOwner, mCameraSelector, mPreview, mImageCapture);
            } else if (cameraMode == MODE_RECORD) { // 录像
                // 把相机选择器、预览视图、视频捕捉器绑定到相机提供器的生命周期
                Camera camera = mCameraProvider.bindToLifecycle(mOwner, mCameraSelector, mPreview, mVideoCapture);
            }
        } catch (Exception e) {
            Log.e(TAG, "绑定摄像头操作异常，相机模式：" + (cameraMode == MODE_PHOTO ? "拍照" : "录像"));
        }
    }

    // 拍摄的视频的路径
    private String mVideoPath;
    private int MAX_RECORD_TIME = 15; // 最大录制时长，默认15秒

    /**
     * 获取拍摄的视频路径
     * @return
     */

    public String getmVideoPath() {
        return mVideoPath;
    }

    /**
     * 开始录制
     * @param max_record_time 最大录制时长（毫秒）
     * @return
     */
    @SuppressLint("RestrictedApi")
    public void startRecord(int max_record_time) {
        MAX_RECORD_TIME = max_record_time;
        // 绑定摄像头
        bindCamera(MODE_RECORD);
        mVideoPath = String.format("%s/%s.mp4", mMediaDir, DateUtil.getNowDateTime());
        VideoCapture.Metadata metadata = new VideoCapture.Metadata();
        // 构建视频捕捉器的输出选项
        VideoCapture.OutputFileOptions options = new VideoCapture.OutputFileOptions.Builder(new File(mVideoPath))
                .setMetadata(metadata).build();
        // 开始录像
        mVideoCapture.startRecording(options, mExecutorService, new VideoCapture.OnVideoSavedCallback() {
            /**
             * 录制完成后的回调方法
             * @param outputFileResults
             */
            @Override
            public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                mHandler.post(() -> bindCamera(MODE_PHOTO));
                mStopListener.onStop("录制完成的视频路径为：" + mVideoPath);
            }

            @Override
            public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                mHandler.post(() -> bindCamera(MODE_PHOTO));
                mStopListener.onStop("录制失败，错误信息为："+cause.getMessage());
            }
        });

        // 到达限定时长后自动停止录像
        mHandler.postDelayed(() -> stopRecord(), MAX_RECORD_TIME*1000);
    }

    /**
     * 停止录像
     */
    @SuppressLint("RestrictedApi")
    public void stopRecord() {
        mVideoCapture.stopRecording();
    }

    /**
     * 关闭相机
     */
    public void closeCamera() {
        mCameraProvider.unbindAll(); // 解绑相机提供器
        mExecutorService.shutdown(); // 关闭线程池
    }
}
