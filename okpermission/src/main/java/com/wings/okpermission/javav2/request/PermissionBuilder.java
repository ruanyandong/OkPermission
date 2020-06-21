package com.wings.okpermission.javav2.request;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.wings.okpermission.javav2.callback.*;
/**
 * @author AI
 */
public class PermissionBuilder {

    private static final String FRAGMENT_TAG = "InvisibleFragment";

    FragmentActivity activity;

    Set<String> normalPermissions;

    Set<String> permissionsWontRequest;

    boolean requireBackgroundLocationPermission;

    boolean explainReasonBeforeRequest = false;

    boolean showDialogCalled = false;

    Set<String> grantedPermissions = new HashSet<>();

    Set<String> deniedPermissions = new HashSet<>();

    Set<String> permanentDeniedPermissions = new HashSet<>();

    Set<String> forwardPermissions = new HashSet<>();


    RequestCallback requestCallback;


    ExplainReasonCallback explainReasonCallback;


    ExplainReasonCallbackWithBeforeParam explainReasonCallbackWithBeforeParam;


    ForwardToSettingsCallback forwardToSettingsCallback;

    public PermissionBuilder(FragmentActivity activity, Set<String> normalPermissions, boolean requireBackgroundLocationPermission, Set<String> permissionsWontRequest) {
        this.activity = activity;
        this.normalPermissions = normalPermissions;
        this.requireBackgroundLocationPermission = requireBackgroundLocationPermission;
        this.permissionsWontRequest = permissionsWontRequest;
    }

    public PermissionBuilder onExplainRequestReason(ExplainReasonCallback callback) {
        explainReasonCallback = callback;
        return this;
    }

    public PermissionBuilder onExplainRequestReason(ExplainReasonCallbackWithBeforeParam callback) {
        explainReasonCallbackWithBeforeParam = callback;
        return this;
    }

    public PermissionBuilder onForwardToSettings(ForwardToSettingsCallback callback) {
        forwardToSettingsCallback = callback;
        return this;
    }

    public PermissionBuilder explainReasonBeforeRequest() {
        explainReasonBeforeRequest = true;
        return this;
    }

    public void request(RequestCallback callback) {
        requestCallback = callback;
        // Build the request chain.
        // RequestNormalPermissions runs first.
        // Then RequestBackgroundLocationPermission runs.
        RequestChain requestChain = new RequestChain();
        requestChain.addTaskToChain(new RequestNormalPermissions(this));
        requestChain.addTaskToChain(new RequestBackgroundLocationPermission(this));
        requestChain.runTask();
    }

    void showHandlePermissionDialog(final ChainTask chainTask, final boolean showReasonOrGoSettings, final List<String> permissions, String message, String positiveText, String negativeText) {
        showDialogCalled = true;
        if (permissions == null || permissions.isEmpty()) {
            chainTask.finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setCancelable(!TextUtils.isEmpty(negativeText));
        builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (showReasonOrGoSettings) {
                    chainTask.requestAgain(permissions);
                } else {
                    forwardToSettings(permissions);
                }
            }
        });
        if (!TextUtils.isEmpty(negativeText)) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chainTask.finish();
                }
            });
        }
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void requestNow(Set<String> permissions, ChainTask chainTask) {
        getInvisibleFragment().requestNow(this, permissions, chainTask);
    }

    void requestAccessBackgroundLocationNow(ChainTask chainTask) {
        getInvisibleFragment().requestAccessBackgroundLocationNow(this, chainTask);
    }


    private InvisibleFragment getInvisibleFragment() {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment existedFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (existedFragment != null) {
            return (InvisibleFragment) existedFragment;
        } else {
            InvisibleFragment invisibleFragment = new InvisibleFragment();
            fragmentManager.beginTransaction().add(invisibleFragment, FRAGMENT_TAG).commitNow();
            return invisibleFragment;
        }
    }

    private void forwardToSettings(List<String> permissions) {
        forwardPermissions.clear();
        forwardPermissions.addAll(permissions);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        getInvisibleFragment().startActivityForResult(intent, InvisibleFragment.FORWARD_TO_SETTINGS);
    }

}