package com.wings.okpermission.javav2.callback;
import com.wings.okpermission.javav2.request.ForwardScope;
import java.util.List;


/**
 * @author AI
 */
public interface ForwardToSettingsCallback {

    void onForwardToSettings(ForwardScope scope, List<String> deniedList);

}