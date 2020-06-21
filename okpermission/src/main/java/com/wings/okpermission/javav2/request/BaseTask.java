package com.wings.okpermission.javav2.request;

import com.wings.okpermission.javav2.PermissionX;
import java.util.ArrayList;
import java.util.List;
import static com.wings.okpermission.javav2.request.RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION;

/**
 * @author AI
 * @since 2020/6/21
 */
public abstract class BaseTask implements ChainTask {

    protected ChainTask next;

    protected PermissionBuilder pb;

    ExplainScope explainReasonScope;

    ForwardScope forwardToSettingsScope;

    BaseTask(PermissionBuilder permissionBuilder) {
        pb = permissionBuilder;
        explainReasonScope = new ExplainScope(pb, this);
        forwardToSettingsScope = new ForwardScope(pb, this);
    }

    @Override
    public ExplainScope getExplainScope() {
        return explainReasonScope;
    }

    @Override
    public ForwardScope getForwardScope() {
        return forwardToSettingsScope;
    }

    @Override
    public void finish() {
        // If there's next task, then run it.
        if (next != null) {
            next.request();
        } else { // If there's no next task, finish the request process and notify the result
            List<String> deniedList = new ArrayList<>();
            deniedList.addAll(pb.deniedPermissions);
            deniedList.addAll(pb.permanentDeniedPermissions);
            deniedList.addAll(pb.permissionsWontRequest);
            if (pb.requireBackgroundLocationPermission) {
                if (PermissionX.isGranted(pb.activity, ACCESS_BACKGROUND_LOCATION)) {
                    pb.grantedPermissions.add(ACCESS_BACKGROUND_LOCATION);
                } else {
                    deniedList.add(ACCESS_BACKGROUND_LOCATION);
                }
            }
            if (pb.requestCallback != null) {
                pb.requestCallback.onResult(deniedList.isEmpty(), new ArrayList<>(pb.grantedPermissions), deniedList);
            }
        }
    }

}
