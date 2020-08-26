package es.sdos.customlogger.util

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.TargetApi
import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION_CODES.M
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import es.sdos.customlogger.R

/**
 * Sacada de LeakCanary: https://github.com/square/leakcanary
 */
@TargetApi(M)
internal class RequestStoragePermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            if (hasStoragePermission()) {
                finish()
                return
            }
            val permissions = arrayOf(WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissions,
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (!hasStoragePermission()) {
            Toast.makeText(application,
                R.string.customlog__permission_not_granted, LENGTH_LONG)
                .show()
        }
        finish()
    }

    override fun finish() {
        // Reset the animation to avoid flickering.
        overridePendingTransition(0, 0)
        super.finish()
    }

    private fun hasStoragePermission(): Boolean {
        return checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
    }

    companion object {
        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, RequestStoragePermissionActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
            return PendingIntent.getActivity(context, 1, intent, FLAG_UPDATE_CURRENT)
        }

        private const val REQUEST_CODE = 42
    }
}