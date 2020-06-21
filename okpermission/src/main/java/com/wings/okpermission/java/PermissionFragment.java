package com.wings.okpermission.java;

import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;


/**
 * @author AI
 */
public class PermissionFragment extends Fragment {

    private PermissionCallBack callBack;
    private static final int REQUEST_CODE = 1;

    public void requestPermission(PermissionCallBack callBack, String ... permissions){
        this.callBack = callBack;
        requestPermissions(permissions,REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    deniedList.add(permissions[i]);
                }
            }
            boolean allGranted = deniedList.isEmpty();
            callBack.onResult(allGranted,deniedList);
        }
    }
}
