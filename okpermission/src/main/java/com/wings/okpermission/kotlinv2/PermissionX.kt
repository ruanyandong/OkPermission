package com.wings.okpermission.kotlinv2

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * An open source Android library that makes handling runtime permissions extremely easy.
 *
 * The following snippet shows the simple usage:
 * ```kotlin
 *   PermissionX.init(activity)
 *      .permissions(Manifest.permission.READ_CONTACTS, Manifest.permission.CAMERA)
 *      .request { allGranted, grantedList, deniedList ->
 *          // handling the logic
 *      }
 *```
 *
 * @author AI
 */
object PermissionX {

    fun init(activity: FragmentActivity) : PermissionCollection {
        return PermissionCollection(activity)
    }

    fun isGranted(context: Context, permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

}

