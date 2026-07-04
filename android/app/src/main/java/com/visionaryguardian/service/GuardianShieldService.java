package com.visionaryguardian.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.visionaryguardian.ai.ContentClassifier; // 1. 引入我们的AI大脑包
import java.nio.ByteBuffer;

public class GuardianShieldService extends Service {

    private static final String CHANNEL_ID = "GuardianShieldChannel";
    private static final int NOTIFICATION_ID = 999;
    private static final String TAG = "GuardianShield";

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private ContentClassifier mClassifier; // 2. 声明本地AI检测器实例
    
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(metrics);
        mScreenWidth = metrics.widthPixels / 2; 
        mScreenHeight = metrics.heightPixels / 2;
        mScreenDensity = metrics.densityDpi;

        // 3. 在服务启动时，初始化AI加载机制（自动激活端侧NPU加速）
        mClassifier = new ContentClassifier(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Visionary Guardian Active")
                .setContentText("The visual shield is currently protecting your child.")
                .setSmallIcon(android.R.drawable.ic_secure)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }

        int resultCode = intent.getIntExtra("resultCode", 0);
        Intent data = intent.getParcelableExtra("data");
        if (resultCode != 0 && data != null) {
            MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mMediaProjection = projectionManager.getMediaProjection(resultCode, data);
            startScreenCapture();
        }

        return START_STICKY;
    }

    private void startScreenCapture() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                "GuardianScreen",
                mScreenWidth, mScreenHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null
        );

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mScreenWidth;

                        Bitmap bitmap = Bitmap.createBitmap(mScreenWidth + rowPadding / pixelStride, mScreenHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        // 4. 【核心联动】：把这一帧图像，直接送进AI大脑进行判定
                        if (mClassifier != null) {
                            boolean isViolation = mClassifier.isHarmfulContent(bitmap);
                            if (isViolation) {
                                // 🚨 抓到违规视觉内容！立刻记录日志并触发防御机制
                                Log.e(TAG, "🚨 ALERT: Harmful visual content detected locally! Triggering shield overlay.");
                                
                                // 💡【下一阶段预留】：WindowManager.addView() 毫秒级全屏动态黑屏/模糊遮罩将在这里执行
                            }
                        }
                        
                        // 判定完不管安全与否，立刻物理销毁，绝不在设备留存任何截图文件
                        bitmap.recycle(); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (image != null) {
                        image.close();
                    }
                }
            }
        }, null);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Guardian Shield Core Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVirtualDisplay != null) mVirtualDisplay.release();
        if (mImageReader != null) mImageReader.close();
        if (mMediaProjection != null) mMediaProjection.stop();
        if (mClassifier != null) {
            mClassifier.close(); // 释放AI模型占用的内存硬件资源
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}