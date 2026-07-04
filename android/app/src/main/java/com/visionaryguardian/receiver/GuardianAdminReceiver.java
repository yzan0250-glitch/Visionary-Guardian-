package com.visionaryguardian.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class GuardianAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, "Visionary Guardian: System core shield activated!", Toast.LENGTH_LONG).show();
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        // 当孩子试图去系统设置里关闭这个权限时，系统会弹窗显示这段警告语
        return "WARNING: Disabling this protection will expose the device to unrestricted content. Master password required.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, "Protection deactivated.", Toast.LENGTH_LONG).show();
    }
}
