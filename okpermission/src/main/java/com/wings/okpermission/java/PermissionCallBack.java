package com.wings.okpermission.java;

import java.util.List;


public interface PermissionCallBack {
    void onResult(boolean allGranted, List<String> deniedList);
}
