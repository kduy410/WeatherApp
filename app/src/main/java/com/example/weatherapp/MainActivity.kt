package com.example.weatherapp

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.extension.*
import com.example.weatherapp.ui.home.DIALOG_FRAGMENT_TAG
import com.example.weatherapp.utils.Dialog
import com.google.android.material.navigation.NavigationView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels<MainActivityViewModel> { getViewModelFactory() }
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // Make sure the app has correct permissions to run
        // Potential bug if denied permissions
//        requestPermissionsIfNecessary()
        requestPermission()
    }

    /**
     * Request permissions twice - if the user denies twice then show a toast about how to update
     * the permission for storage
     */
    private fun requestPermissionsIfNecessary() {
        if (!checkAllPermissions(this)) {
            if (viewModel.getPermissionCount() < MAX_NUMBER_REQUEST_PERMISSIONS) {
                viewModel.setPermissionCount(1)
                // Request permissions
                ActivityCompat.requestPermissions(
                    this,
                    permissions.toTypedArray(),
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                Dialog(
                    R.string.permission_title,
                    R.string.permission_message_manually,
                    { dialog, _ -> dialog.dismiss() }, null
                ).show(supportFragmentManager, DIALOG_FRAGMENT_TAG)
            }
        } else {
            viewModel.setPermissionState(true)
        }
    }

    fun observePermissionState() = viewModel.observePermissionState()

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            requestPermissionsIfNecessary()
//        }
//    }

    /** Save the permission request count on a rotate  */
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Timber.e("IS RUNNING?")
        viewModel.savedInstanceState()
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun requestPermission() {
        val p = arrayOf<AppPermission>(
            AppPermission.ACCESS_FINE_LOCATION,
            AppPermission.ACCESS_COARSE_LOCATION
        )
        this.handlePermission(
            p,
            onGranted = {
                /** Permission is granted and we can use LOCATION */
                viewModel.setPermissionGranted(true)
            },
            onDenied = {
                /** Permission is not granted - we should request permission **/
                requestPermission(p)
            },
            onRationaleNeeded = {
                /** Additional explanation for permission usage needed **/
                Dialog(
                    R.string.permission_title,
                    R.string.permission_message,
                    { _, _ ->
                        requestPermission(p)
                    },
                    { dialog, _ -> dialog.dismiss() }).show(
                    supportFragmentManager,
                    DIALOG_FRAGMENT_TAG
                )
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionResultReceived(requestCode, permissions, grantResults,
            onPermissionGranted = {
                viewModel.setPermissionGranted(true)
            },
            onPermissionDenied = {
                val showRationale = shouldShowRequestPermissionRationale(permissions[0])
                if (!showRationale) {
                    // USER checked "NEVER ASK AGAIN"
                    viewModel.setPermissionGranted(true)
                    Dialog(
                        R.string.permission_title,
                        R.string.permission_message_manually,
                        { dialog, _ -> dialog.dismiss() }, null
                    ).show(supportFragmentManager, DIALOG_FRAGMENT_TAG)
                } else {
                    // USER DID NOT checked "NEVER ASK AGAIN"
                    viewModel.setPermissionGranted(true)
                    Dialog(
                        R.string.permission_title,
                        R.string.permission_message,
                        DialogInterface.OnClickListener { _, _ ->
                            AppPermission.getAppPermission(permissions[0])?.let { it ->
                                requestPermission(it)
                            }
                        }, { dialog, _ ->
                            dialog.dismiss()
                        }
                    ).show(supportFragmentManager, DIALOG_FRAGMENT_TAG)
                }
            })
    }
}

const val RESULT_OK = Activity.RESULT_OK
const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT"
