package com.wings.okpermission.kotlinv2

import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.Fragment

/**
 * @author AI
 */

typealias RequestCallback = (allGranted: Boolean, grantedList: List<String>, deniedList: List<String>) -> Unit

typealias ExplainReasonCallback = ExplainReasonScope.(deniedList: MutableList<String>) -> Unit

typealias ExplainReasonCallback2 = ExplainReasonScope.(deniedList: MutableList<String>, beforeRequest: Boolean) -> Unit

typealias ForwardToSettingsCallback = ForwardToSettingsScope.(deniedList: MutableList<String>) -> Unit

const val TAG = "InvisibleFragment"

const val PERMISSION_CODE = 1

const val SETTINGS_CODE = 2

class InvisibleFragment : Fragment() {

    private lateinit var permissionBuilder: PermissionBuilder


    private var explainReasonCallback: ExplainReasonCallback? = null


    private var explainReasonCallback2: ExplainReasonCallback2? = null


    private var forwardToSettingsCallback: ForwardToSettingsCallback? = null


    private lateinit var requestCallback: RequestCallback


    fun requestNow(builder: PermissionBuilder, cb1: ExplainReasonCallback?, cb2: ExplainReasonCallback2?, cb3: ForwardToSettingsCallback?, cb4: RequestCallback, vararg permissions: String) {
        permissionBuilder = builder
        explainReasonCallback = cb1
        explainReasonCallback2 = cb2
        forwardToSettingsCallback = cb3
        requestCallback = cb4
        requestPermissions(permissions, PERMISSION_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            val grantedList = ArrayList<String>() // holds granted permissions in the request permissions
            val showReasonList = ArrayList<String>() // holds denied permissions in the request permissions.
            val forwardList = ArrayList<String>() // hold permanently denied permissions in the request permissions.
            for ((index, result) in grantResults.withIndex()) { // iterate all granted results
                if (result == PackageManager.PERMISSION_GRANTED) {
                    grantedList.add(permissions[index])
                    // Remove granted permissions from deniedPermissions and permanentDeniedPermissions set in PermissionBuilder.
                    permissionBuilder.deniedPermissions.remove(permissions[index])
                    permissionBuilder.permanentDeniedPermissions.remove(permissions[index])
                } else {
                    // Denied permission can turn into permanent denied permissions, but permanent denied permission can not turn into denied permissions.
                    val shouldShowReason = shouldShowRequestPermissionRationale(permissions[index])
                    if (shouldShowReason) {
                        showReasonList.add(permissions[index])
                        permissionBuilder.deniedPermissions.add(permissions[index])
                        // So there's no need to remove the current permission from permanentDeniedPermissions because it won't be there.
                    } else {
                        forwardList.add(permissions[index])
                        permissionBuilder.permanentDeniedPermissions.add(permissions[index])
                        // We must remove the current permission from deniedPermissions because it is permanent denied permission now.
                        permissionBuilder.deniedPermissions.remove(permissions[index])
                    }
                }
            }
            // We can never holds granted permissions for safety, because user may turn some permissions off in Settings.
            // So every time request, must request the already granted permissions again and refresh the granted permission set.
            permissionBuilder.grantedPermissions.clear()
            permissionBuilder.grantedPermissions.addAll(grantedList)
            val allGranted = permissionBuilder.grantedPermissions.size == permissionBuilder.allPermissions.size
            if (allGranted) { // If all permissions are granted, call RequestCallback directly.
                requestCallback(true, permissionBuilder.allPermissions, listOf())
            } else {
                var goesToRequestCallback = true // If there's need goes to RequestCallback
                // If explainReasonCallback is not null and there're denied permissions. Try the ExplainReasonCallback.
                if ((explainReasonCallback != null || explainReasonCallback2 != null) && showReasonList.isNotEmpty()) {
                    goesToRequestCallback = false // No need cause ExplainReasonCallback handles it
                    explainReasonCallback2?.let { // callback ExplainReasonCallback2 prior to ExplainReasonCallback
                        permissionBuilder.explainReasonScope.it(showReasonList, false)
                    } ?:
                    explainReasonCallback?.let { permissionBuilder.explainReasonScope.it(showReasonList) }
                }
                // If forwardToSettingsCallback is not null and there're permanently denied permissions. Try the ForwardToSettingsCallback.
                else if (forwardToSettingsCallback != null && forwardList.isNotEmpty()) {
                    goesToRequestCallback = false // No need cause ForwardToSettingsCallback handles it
                    forwardToSettingsCallback?.let { permissionBuilder.forwardToSettingsScope.it(forwardList) }
                }
                // If showRequestReasonDialog or showForwardToSettingsDialog is not called. Try the RequestCallback.
                // There's case that ExplainReasonCallback or ForwardToSettingsCallback is called, but developer didn't invoke
                // showRequestReasonDialog or showForwardToSettingsDialog in the callback.
                // At this case and all other cases, RequestCallback will be called.
                if (goesToRequestCallback || !permissionBuilder.showDialogCalled) {
                    val deniedList = ArrayList<String>()
                    deniedList.addAll(permissionBuilder.deniedPermissions)
                    deniedList.addAll(permissionBuilder.permanentDeniedPermissions)
                    requestCallback(false, permissionBuilder.grantedPermissions.toList(), deniedList)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_CODE) {
            // When user switch back from settings, just request again.
            if (::permissionBuilder.isInitialized) { // On some phones, when switch back from settings, permissionBuilder may become uninitialized
                permissionBuilder.requestAgain(permissionBuilder.forwardPermissions)
            } else {
                Log.w("PermissionX", "permissionBuilder should not be uninitialized at this time, so we can do nothing in this case.")
            }
        }
    }

}