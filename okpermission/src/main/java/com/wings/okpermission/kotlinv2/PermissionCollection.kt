package com.wings.okpermission.kotlinv2

import androidx.fragment.app.FragmentActivity

/**
 * @author -> Wings
 * @date   -> 2020/6/21
 * @email  -> ruanyandongai@gmail.com
 *            729368173@qq.com
 * @phone  -> 18983790146
 * @blog   -> https://ruanyandong.github.io
 *         -> https://blog.csdn.net/qq_34681580
 */

class PermissionCollection (private val activity: FragmentActivity) {

    fun permissions(vararg permissions: String) : PermissionBuilder {
        return PermissionBuilder(activity, permissions.toList())
    }

}