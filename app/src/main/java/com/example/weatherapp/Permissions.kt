package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

val permissions = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
)

val MAX_NUMBER_REQUEST_PERMISSIONS = permissions.size

val REQUEST_CODE_PERMISSIONS = 101

fun isPermissionGranted(context: Context, permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun checkAllPermissions(context: Context): Boolean {
    var hasPermissions = true
    for (p in permissions) {
        /**
         * AND operator
         * true true => true
         * true false => false
         * false true => false
         * false false => false
         */
        hasPermissions = hasPermissions and isPermissionGranted(context, p)
    }
    return hasPermissions
}