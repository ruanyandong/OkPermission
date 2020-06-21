package com.wings.okpermission.javav2.request;

import com.wings.okpermission.javav2.PermissionX;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author AI
 */
public class RequestNormalPermissions extends BaseTask {

    RequestNormalPermissions(PermissionBuilder permissionBuilder) {
        super(permissionBuilder);
    }

    @Override
    public void request() {
        List<String> requestList = new ArrayList<>();
        for (String permission : pb.normalPermissions) {
            if (PermissionX.isGranted(pb.activity, permission)) {
                // already granted
                pb.grantedPermissions.add(permission);
            } else {
                // still need to request
                requestList.add(permission);
            }
        }
        // all permissions are granted
        if (requestList.isEmpty()) {
            finish();
            return;
        }
        if (pb.explainReasonBeforeRequest && (pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null)) {
            pb.explainReasonBeforeRequest = false;
            pb.deniedPermissions.addAll(requestList);
            if (pb.explainReasonCallbackWithBeforeParam != null) {
                // callback ExplainReasonCallbackWithBeforeParam prior to ExplainReasonCallback
                pb.explainReasonCallbackWithBeforeParam.onExplainReason(explainReasonScope, requestList, true);
            } else {
                pb.explainReasonCallback.onExplainReason(explainReasonScope, requestList);
            }
        } else {
            // Do the request at once. Always request all permissions no matter they are already granted or not, in case user turn them off in Settings.
            pb.requestNow(pb.normalPermissions, this);
        }
    }

    @Override
    public void requestAgain(List<String> permissions) {
        Set<String> permissionsToRequestAgain = new HashSet<>(pb.grantedPermissions);
        permissionsToRequestAgain.addAll(permissions);
        pb.requestNow(permissionsToRequestAgain, this);
    }

}
