package com.wings.okpermission.javav2.callback;
import com.wings.okpermission.javav2.request.ExplainScope;
import java.util.List;

/**
 * @author AI
 */
public interface ExplainReasonCallbackWithBeforeParam {

    void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest);

}
