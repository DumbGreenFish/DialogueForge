package io.github.dumbgreenfish.dialogueforge.notification

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import org.koin.core.annotation.Single

@Single
class AndroidNotificationPermission(private val application: Application) {
    private var activity: Activity? = null

    fun attach(activity: Activity) {
        this.activity = activity
    }

    fun detach(activity: Activity) {
        if (this.activity === activity) this.activity = null
    }

    fun requestOnce() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (application.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) return
        val preferences = application.getSharedPreferences(PREFERENCES_NAME, Application.MODE_PRIVATE)
        if (preferences.getBoolean(KEY_REQUESTED, false)) return
        val currentActivity = activity ?: return
        preferences.edit().putBoolean(KEY_REQUESTED, true).apply()
        currentActivity.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
    }

    fun requestOrOpenSettings() {
        if (canRequest()) {
            requestOnce()
        } else {
            openSettings()
        }
    }

    private fun canRequest(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        if (application.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            return false
        }
        val preferences = application.getSharedPreferences(PREFERENCES_NAME, Application.MODE_PRIVATE)
        return !preferences.getBoolean(KEY_REQUESTED, false) && activity != null
    }

    private fun openSettings() {
        val notificationSettings = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, application.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val applicationDetails = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", application.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val intent = if (notificationSettings.resolveActivity(application.packageManager) != null) {
            notificationSettings
        } else {
            applicationDetails
        }
        if (intent.resolveActivity(application.packageManager) != null) {
            application.startActivity(intent)
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "notification_permission"
        const val KEY_REQUESTED = "requested"
        const val REQUEST_CODE = 7_401
    }
}
