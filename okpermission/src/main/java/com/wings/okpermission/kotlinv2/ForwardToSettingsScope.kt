package com.wings.okpermission.kotlinv2

/**
 * @author -> Wings
 * @date   -> 2020/6/21
 * @email  -> ruanyandongai@gmail.com
729368173@qq.com
 * @phone  -> 18983790146
 * @blog   -> https://ruanyandong.github.io
-> https://blog.csdn.net/qq_34681580
 */
class ForwardToSettingsScope(private val permissionBuilder: PermissionBuilder) {

    fun showForwardToSettingsDialog(permissions: List<String>, message: String, positiveText: String, negativeText: String? = null) {
        permissionBuilder.showHandlePermissionDialog(false, permissions, message, positiveText, negativeText)
    }

}