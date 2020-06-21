package com.wings.okpermission.kotlin

import androidx.fragment.app.Fragment
import java.util.*

typealias PermissionCallBack = (Boolean, List<String>) -> Unit
private const val REQUEST_CODE : Int = 1

class PermissionFragment : Fragment() {

    private var callBack: PermissionCallBack? = null

    fun requestPermission(callBack: PermissionCallBack, vararg permissions: String) {
        this.callBack = callBack;
        requestPermissions(permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            val deniedList = ArrayList<String>()
            for ((index,result) in grantResults.withIndex()){
                deniedList.add(permissions[index]);
            }
            val allGranted = deniedList.isEmpty();
            callBack?.let { it(allGranted,deniedList) }
        }
    }

}