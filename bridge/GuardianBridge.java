package com.visionaryguardian.bridge;

import android.content.Intent;
import android.os.Build;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.visionaryguardian.service.GuardianShieldService;

public class GuardianBridge extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public GuardianBridge(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    // 💡 必须返回这个名字，前端 NativeModules.GuardianBridge 才能精准找到它
    @Override
    public String getName() {
        return "GuardianBridge";
    }

    // 🚨 供前端调用的核心方法：开启天眼服务
    @ReactMethod
    public void startShieldService() {
        ReactApplicationContext context = getReactApplicationContext();
        Intent serviceIntent = new Intent(context, GuardianShieldService.class);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    // 🛑 供前端调用的核心方法：关闭天眼服务
    @ReactMethod
    public void stopShieldService() {
        ReactApplicationContext context = getReactApplicationContext();
        Intent serviceIntent = new Intent(context, GuardianShieldService.class);
        context.stopService(serviceIntent);
    }
}