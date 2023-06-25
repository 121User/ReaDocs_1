package com.example.readocs_1;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {
    // Проверка на наличие разрешения на доступ ко всем файлам
    public static boolean hasPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Если версия Android 11 или выше
            return Environment.isExternalStorageManager();
        } else {
            // Если версия Android от 6 до 11
            return ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    // Запрашивает разрешение на доступ ко всем файлам
    public static void requestPermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Если версия Android 11 или выше
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", activity.getPackageName())));
                activity.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, requestCode);
            }
        } else {
            // Если версия Android от 6 до 11
            ActivityCompat.requestPermissions(activity,
                    new String[] { WRITE_EXTERNAL_STORAGE },
                    requestCode);
        }
    }
}