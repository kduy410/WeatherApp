package com.example.weatherapp.extension

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun isLollipopOrBelow(): Boolean =
    (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.LOLLIPOP)

/**************************************
 * HANDLE PERMISSIONS IN FRAGMENTS *
 *************************************/

fun android.app.Fragment.isPermissionGranted(permission: AppPermission) =
    (ContextCompat.checkSelfPermission(
        this.activity,
        permission.permissionName,
    ) == PackageManager.PERMISSION_GRANTED)


fun android.app.Fragment.isRationaleNeeded(permission: AppPermission) =
    this.shouldShowRequestPermissionRationale(permission.permissionName)

fun android.app.Fragment.requestPermission(permission: AppPermission) = this.requestPermissions(
    arrayOf(permission.permissionName), permission.requestCode
)

inline fun android.app.Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}

inline fun android.app.Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> requestPermission(permission)
    }
}

/**************************************
 * HANDLE PERMISSIONS IN V4 FRAGMENTS *
 *************************************/

fun Fragment.isPermissionGranted(permission: AppPermission) =
    (ContextCompat.checkSelfPermission(
        this.requireContext(),
        permission.permissionName,
    ) == PackageManager.PERMISSION_GRANTED)

fun Fragment.isRationaleNeeded(permission: AppPermission) =
    shouldShowRequestPermissionRationale(permission.permissionName)

fun Fragment.requestPermission(permission: AppPermission) = requestPermissions(
    arrayOf(permission.permissionName), permission.requestCode
)

inline fun Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}

inline fun Fragment.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> requestPermission(permission)
    }
}

/**
 * FOR ARRAY OF PERMISSION
 */

fun Fragment.isPermissionGranted(permissions: Array<AppPermission>): Boolean {
    for (p in permissions) {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                p.permissionName,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun Fragment.isRationaleNeeded(permissions: Array<AppPermission>): Boolean {
    for (p in permissions) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                requireContext() as Activity,
                p.permissionName
            )
        ) {
            return false
        }
    }
    return true
}

fun Fragment.requestPermission(permissions: Array<AppPermission>) {
    requestPermissions(
        permissions.map {
            it.permissionName
        }.toTypedArray(), permissions[0].requestCode
    )
}

inline fun Fragment.handlePermission(
    permission: Array<AppPermission>,
    onGranted: (Array<AppPermission>) -> Unit,
    onRationaleNeeded: (Array<AppPermission>) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> requestPermission(permission)
    }
}

inline fun Fragment.handlePermission(
    permission: Array<AppPermission>,
    onGranted: (Array<AppPermission>) -> Unit,
    onDenied: (Array<AppPermission>) -> Unit,
    onRationaleNeeded: (Array<AppPermission>) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}

/************************************
 * HANDLE PERMISSIONS IN ACTIVITIES *
 ***********************************/

fun Activity.isPermissionGranted(permission: AppPermission) = ContextCompat.checkSelfPermission(
    this,
    permission.permissionName
) == PackageManager.PERMISSION_GRANTED

fun Activity.isRationaleNeeded(permission: AppPermission) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission.permissionName)

fun Activity.requestPermission(permission: AppPermission) =
    requestPermissions(arrayOf(permission.permissionName), permission.requestCode)

/**
 * FOR INPUT MULTIPLE REQUEST
 */
fun Activity.isPermissionGranted(permissions: Array<AppPermission>): Boolean {
    for (p in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                p.permissionName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}

fun Activity.isRationaleNeeded(permissions: Array<AppPermission>): Boolean {
    for (p in permissions) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, p.permissionName)) {
            return false
        }
    }
    return true
}

fun Activity.requestPermission(permissions: Array<AppPermission>) {
//    val p = permissions.map { it.permissionName }
    requestPermissions(
        permissions.map { it.permissionName }.toTypedArray(),
        permissions[0].requestCode
    )
}

inline fun Activity.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onDenied: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> onDenied(permission)
    }
}

inline fun Activity.handlePermission(
    permission: AppPermission,
    onGranted: (AppPermission) -> Unit,
    onRationaleNeeded: (AppPermission) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permission) -> onGranted(permission)
        isRationaleNeeded(permission) -> onRationaleNeeded(permission)
        else -> requestPermission(permission)
    }
}

inline fun Activity.handlePermission(
    permissions: Array<AppPermission>,
    onGranted: (Array<AppPermission>) -> Unit,
    onDenied: (Array<AppPermission>) -> Unit,
    onRationaleNeeded: (Array<AppPermission>) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permissions) -> onGranted(permissions)
        isRationaleNeeded(permissions) -> onRationaleNeeded(permissions)
        else -> onDenied(permissions)
    }
}

inline fun Activity.handlePermission(
    permissions: Array<AppPermission>,
    onGranted: (Array<AppPermission>) -> Unit,
    onRationaleNeeded: (Array<AppPermission>) -> Unit
) {
    when {
        isLollipopOrBelow() || isPermissionGranted(permissions) -> onGranted(permissions)
        isRationaleNeeded(permissions) -> onRationaleNeeded(permissions)
        else -> requestPermission(permissions)
    }
}

/*********************************************
 * HANDLE onRequestPermissionResult CALLBACK *
 ********************************************/

fun onRequestPermissionResultReceived(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    onPermissionGranted: (AppPermission) -> Unit,
    onPermissionDenied: (AppPermission) -> Unit
) {
    AppPermission.permissions.find {
        it.requestCode == requestCode
    }?.let {
        val permissionGrantResult =
            mapPermissionsAndResults(permissions, grantResults)[it.permissionName]
        if (PackageManager.PERMISSION_GRANTED == permissionGrantResult) {
            onPermissionGranted(it)
        } else {
            onPermissionDenied(it)
        }
    }
}


private fun mapPermissionsAndResults(
    permissions: Array<out String>,
    grantResults: IntArray
): Map<String, Int> =
    permissions.mapIndexedTo(mutableListOf<Pair<String, Int>>()) { index: Int, permission: String ->
        permission to grantResults[index]
    }.toMap()

