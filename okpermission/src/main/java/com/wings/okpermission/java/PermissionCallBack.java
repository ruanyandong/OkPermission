package com.wings.okpermission.java;

import java.util.List;


/**
 * @author AI
 */
public interface PermissionCallBack {
    void onResult(boolean allGranted, List<String> deniedList);
}
