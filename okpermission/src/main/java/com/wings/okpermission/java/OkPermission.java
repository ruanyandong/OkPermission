package com.wings.okpermission.java;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


/**
 * @author AI
 */
public class OkPermission {

    private static final String TAG = "com.wings.okpermission.java.PermissionFragment";

    public static OkPermission get() {
        return SingleTon.INSTANCE;
    }
    private static final class SingleTon{
        private static final OkPermission INSTANCE = new OkPermission();
    }
    private OkPermission() {
    }

    public static void request(FragmentActivity activity, PermissionCallBack callBack, String ... permissions){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        PermissionFragment permissionFragment = null;
        if (fragment != null){
            permissionFragment = (PermissionFragment) fragment;
        }else {
           permissionFragment = new PermissionFragment();
           fragmentManager.beginTransaction().add(permissionFragment,TAG).commitNow();
        }
        permissionFragment.requestPermission(callBack,permissions);

    }
}
