package com.wings.okpermission.javav2;

import android.os.Build;
import androidx.fragment.app.FragmentActivity;
import com.wings.okpermission.javav2.request.PermissionBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wings.okpermission.javav2.request.RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION;


/**
 * @author AI
 */
public class PermissionCollection {

    private FragmentActivity activity;

    public PermissionCollection(FragmentActivity activity) {
        this.activity = activity;
    }

    public PermissionBuilder permissions(String... permissions)  {
        return permissions(new ArrayList<>(Arrays.asList(permissions)));
    }

    public PermissionBuilder permissions(List<String> permissions)  {

        Set<String> permissionSet = new HashSet<>(permissions);
        boolean requireBackgroundLocationPermission = false;
        Set<String> permissionsWontRequest = new HashSet<>();

        if (permissionSet.contains(ACCESS_BACKGROUND_LOCATION)) {
            int osVersion = Build.VERSION.SDK_INT;
            int targetSdkVersion = activity.getApplicationInfo().targetSdkVersion;
            if (osVersion >= 30 && targetSdkVersion >= 30) {
                requireBackgroundLocationPermission = true;
                permissionSet.remove(ACCESS_BACKGROUND_LOCATION);
            } else if (osVersion < Build.VERSION_CODES.Q) {
                // If app runs under Android Q, there's no ACCESS_BACKGROUND_LOCATION permissions.
                // We remove it from request list, but will append it to the request callback as denied permission.
                permissionSet.remove(ACCESS_BACKGROUND_LOCATION);
                permissionsWontRequest.add(ACCESS_BACKGROUND_LOCATION);
            }
        }
        return new PermissionBuilder(activity, permissionSet, requireBackgroundLocationPermission, permissionsWontRequest);
    }

}
