package com.wings.okpermission.javav2.request;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.wings.okpermission.javav2.PermissionX;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static com.wings.okpermission.javav2.request.RequestBackgroundLocationPermission.ACCESS_BACKGROUND_LOCATION;


/**
 * @author AI
 */
public class InvisibleFragment extends Fragment {

    public static final int REQUEST_NORMAL_PERMISSIONS = 1;

    public static final int REQUEST_BACKGROUND_LOCATION_PERMISSION = 2;

    public static final int FORWARD_TO_SETTINGS = 2;

    private PermissionBuilder pb;

    private ChainTask task;

    void requestNow(PermissionBuilder permissionBuilder, Set<String> permissions, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        requestPermissions(permissions.toArray(new String[0]), REQUEST_NORMAL_PERMISSIONS);
    }

    void requestAccessBackgroundLocationNow(PermissionBuilder permissionBuilder, ChainTask chainTask) {
        pb = permissionBuilder;
        task = chainTask;
        requestPermissions(new String[]{ ACCESS_BACKGROUND_LOCATION }, REQUEST_BACKGROUND_LOCATION_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_NORMAL_PERMISSIONS) {
            onRequestNormalPermissionsResult(permissions, grantResults);
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION_PERMISSION) {
            onRequestBackgroundLocationPermissionResult();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FORWARD_TO_SETTINGS) {
            // When user switch back from settings, just request again.
            // On some phones, when switch back from settings, permissionBuilder may become null
            if (task != null && pb != null) {
                task.requestAgain(new ArrayList<>(pb.forwardPermissions));
            } else {
                Log.w("PermissionX", "permissionBuilder should not be null at this time, so we can do nothing in this case.");
            }
        }
    }

    private void onRequestNormalPermissionsResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
        // We can never holds granted permissions for safety, because user may turn some permissions off in settings.
        // So every time request, must request the already granted permissions again and refresh the granted permission set.
        pb.grantedPermissions.clear();
        // holds denied permissions in the request permissions.
        List<String> showReasonList = new ArrayList<>();
        // hold permanently denied permissions in the request permissions.
        List<String> forwardList = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                pb.grantedPermissions.add(permission);
                // Remove granted permissions from deniedPermissions and permanentDeniedPermissions set in PermissionBuilder.
                pb.deniedPermissions.remove(permission);
                pb.permanentDeniedPermissions.remove(permission);
            } else {
                // Denied permission can turn into permanent denied permissions, but permanent denied permission can not turn into denied permissions.
                boolean shouldShowRationale = shouldShowRequestPermissionRationale(permission);
                if (shouldShowRationale) {
                    showReasonList.add(permissions[i]);
                    pb.deniedPermissions.add(permission);
                    // So there's no need to remove the current permission from permanentDeniedPermissions because it won't be there.
                } else {
                    forwardList.add(permissions[i]);
                    pb.permanentDeniedPermissions.add(permission);
                    // We must remove the current permission from deniedPermissions because it is permanent denied permission now.
                    pb.deniedPermissions.remove(permission);
                }
            }
        }
        List<String> deniedPermissions = new ArrayList<>();
        deniedPermissions.addAll(pb.deniedPermissions);
        deniedPermissions.addAll(pb.permanentDeniedPermissions);
        // maybe user can turn some permissions on in settings that we didn't request, so check the denied permissions again for safety.
        for (String permission : deniedPermissions) {
            if (PermissionX.isGranted(getContext(), permission)) {
                pb.deniedPermissions.remove(permission);
                pb.grantedPermissions.add(permission);
            }
        }
        boolean allGranted = pb.grantedPermissions.size() == pb.normalPermissions.size();
        if (allGranted) { // If all permissions are granted, finish current task directly.
            task.finish();
        } else {
            boolean shouldFinishTheTask = true; // Indicate if we should finish the task
            // If explainReasonCallback is not null and there're denied permissions. Try the ExplainReasonCallback.
            if ((pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) && !showReasonList.isEmpty()) {
                shouldFinishTheTask = false; // shouldn't because ExplainReasonCallback handles it
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    // callback ExplainReasonCallbackWithBeforeParam prior to ExplainReasonCallback
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(), new ArrayList<>(pb.deniedPermissions), false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(), new ArrayList<>(pb.deniedPermissions));
                }
            }
            // If forwardToSettingsCallback is not null and there're permanently denied permissions. Try the ForwardToSettingsCallback.
            else if (pb.forwardToSettingsCallback != null && !forwardList.isEmpty()) {
                shouldFinishTheTask = false; // shouldn't because ForwardToSettingsCallback handles it
                pb.forwardToSettingsCallback.onForwardToSettings(task.getForwardScope(), new ArrayList<>(pb.permanentDeniedPermissions));
            }
            // If showRequestReasonDialog or showForwardToSettingsDialog is not called. We should finish the task.
            // There's case that ExplainReasonCallback or ForwardToSettingsCallback is called, but developer didn't invoke
            // showRequestReasonDialog or showForwardToSettingsDialog in the callback.
            // At this case and all other cases, task should be finished.
            if (shouldFinishTheTask || !pb.showDialogCalled) {
                task.finish();
            }
        }
    }

    private void onRequestBackgroundLocationPermissionResult() {
        if (PermissionX.isGranted(getContext(), ACCESS_BACKGROUND_LOCATION)) {
            pb.grantedPermissions.add(ACCESS_BACKGROUND_LOCATION);
            // Remove granted permissions from deniedPermissions and permanentDeniedPermissions set in PermissionBuilder.
            pb.deniedPermissions.remove(ACCESS_BACKGROUND_LOCATION);
            pb.permanentDeniedPermissions.remove(ACCESS_BACKGROUND_LOCATION);
            task.finish();
        } else {
            boolean goesToRequestCallback = true; // Indicate if we should finish the task
            boolean shouldShowRationale = shouldShowRequestPermissionRationale(ACCESS_BACKGROUND_LOCATION);
            // If explainReasonCallback is not null and we should show rationale. Try the ExplainReasonCallback.
            if ((pb.explainReasonCallback != null || pb.explainReasonCallbackWithBeforeParam != null) && shouldShowRationale) {
                goesToRequestCallback = false; // shouldn't because ExplainReasonCallback handles it
                List<String> permissionsToExplain = new ArrayList<>();
                permissionsToExplain.add(ACCESS_BACKGROUND_LOCATION);
                if (pb.explainReasonCallbackWithBeforeParam != null) {
                    // callback ExplainReasonCallbackWithBeforeParam prior to ExplainReasonCallback
                    pb.explainReasonCallbackWithBeforeParam.onExplainReason(task.getExplainScope(), permissionsToExplain, false);
                } else {
                    pb.explainReasonCallback.onExplainReason(task.getExplainScope(), permissionsToExplain);
                }
            }
            // If forwardToSettingsCallback is not null and we shouldn't show rationale. Try the ForwardToSettingsCallback.
            else if (pb.forwardToSettingsCallback != null && !shouldShowRationale) {
                goesToRequestCallback = false; // shouldn't because ForwardToSettingsCallback handles it
                List<String> permissionsToForward = new ArrayList<>();
                permissionsToForward.add(ACCESS_BACKGROUND_LOCATION);
                pb.forwardToSettingsCallback.onForwardToSettings(task.getForwardScope(), permissionsToForward);
            }
            // If showRequestReasonDialog or showForwardToSettingsDialog is not called. We should finish the task.
            // There's case that ExplainReasonCallback or ForwardToSettingsCallback is called, but developer didn't invoke
            // showRequestReasonDialog or showForwardToSettingsDialog in the callback.
            // At this case and all other cases, task should be finished.
            if (goesToRequestCallback || !pb.showDialogCalled) {
                task.finish();
            }
        }
    }

}
