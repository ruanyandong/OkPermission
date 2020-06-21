package com.wings.okpermission.javav2;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @author AI
 */
public class PermissionX {

    public static PermissionCollection init(FragmentActivity activity) {
        return new PermissionCollection(activity);
    }

    public static boolean isGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
