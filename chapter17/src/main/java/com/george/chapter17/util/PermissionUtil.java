package com.george.chapter17.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    /**
     * 校验活动页面是否具有指定的权限
     * @param activity 活动页面
     * @param permissions 权限编码数组集
     * @param requestCode 自定义功能权限标识码，用于标识在onRequestPermissionsResult回调函数中的权限类型
     * @return 检查多个权限。返回true表示已完全启用权限，返回false表示未完全启用权限
     */
    public static boolean checkPermission(Activity activity, String[] permissions, int requestCode) {
        // M标识安卓6.0,在此版本之后开始采用动态权限管理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = PackageManager.PERMISSION_GRANTED;
            for (String permission : permissions) {
                // 循环校验每一个权限
                check = ContextCompat.checkSelfPermission(activity, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            // 如果未开启权限，则调用系统弹窗，让用户主动开启权限
            if (check != PackageManager.PERMISSION_GRANTED) {
                // 检查所有权限是否都被授予。如果有任何一个权限未被授予，将调用 ActivityCompat.requestPermissions()
                // 方法请求权限，并返回 false，表示权限请求已经发出，需要等待用户的响应。
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查权限结果数组，返回true表示都已经获得授权。返回false表示至少有一个未获得授权
     * @param grantResults 权限结果数组集
     * @return
     */
    public static boolean checkGrant(int[] grantResults) {
        if (grantResults != null) {
            // 遍历权限结果数组中的每条选择结果
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
