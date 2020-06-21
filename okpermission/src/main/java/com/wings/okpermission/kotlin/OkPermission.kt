package com.wings.okpermission.kotlin

import androidx.fragment.app.FragmentActivity


object OkPermission {

    private const val TAG = "PermissionFragment"

    fun requestPermission(activity:FragmentActivity,vararg permissions:String,callBack: PermissionCallBack){
        val fragmentManager = activity.supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(TAG)
        val permissionFragment = if (fragment != null){
            fragment as PermissionFragment
        }else{
            val permissionFragment = PermissionFragment();
            fragmentManager.beginTransaction().add(permissionFragment,TAG).commitNow()
            permissionFragment
        }
        permissionFragment.requestPermission(callBack, *permissions)
    }
}