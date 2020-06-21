package com.wings.okpermission.javav2.callback;
import java.util.List;

/**
 * @author AI
 */
public interface RequestCallback {

    void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList);

}
