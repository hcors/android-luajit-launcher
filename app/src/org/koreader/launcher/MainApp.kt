package org.koreader.launcher

import java.io.File

import android.content.pm.ApplicationInfo
import android.os.Environment
import android.util.Log

class MainApp : android.app.Application() {

    companion object {
        lateinit var name: String
            private set
        lateinit var storage_path: String
            private set
        private lateinit var assets_path: String
        private lateinit var library_path: String
        private var debuggable: Boolean = false
        private var is_system_app: Boolean = false

        // change to false if you want to experiment with the sandbox
        // Switch to Environment.isExternalStorageLegacy() when bumping api to 29+
        val legacy_storage: Boolean = true

        private const val UNKNOWN_STRING = "Unknown"
    }

    override fun onCreate() {
        super.onCreate()
        getAppInfo()
        Log.i(name, "Application started")
        Log.v(name, formatAppInfo())
    }

    /* app info into a String */
    private fun formatAppInfo(): String {
        val sb = StringBuilder(400)
        sb.append("Application info {\n  Flags: ")
        sb.append(if (is_system_app) "system" else "user")
        sb.append(if (legacy_storage) ", legacy" else ", scoped").append(" storage")
        sb.append(if (debuggable) ", debuggable" else ".")
        sb.append("\n  Paths {")
                .append("\n    Assets: ").append(assets_path)
                .append("\n    Library: ").append(library_path)
                .append("\n    Storage: ").append(storage_path)
                .append("\n  }\n}")
        return sb.toString()
    }

    /* get read-only app info */
    private fun getAppInfo() {
        try {
            val pm = packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            val ai = pm.getApplicationInfo(packageName, 0)

            name = getString(pi.applicationInfo.labelRes)
            library_path = ai.nativeLibraryDir
            assets_path = filesDir.absolutePath

            if (legacy_storage) {
                storage_path = Environment.getExternalStorageDirectory().absolutePath
            } else {
                val writable_folder: File? = getApplicationContext().getExternalFilesDir(null)
                if (writable_folder != null) {
                    storage_path = writable_folder.absolutePath
                } else {
                    storage_path = UNKNOWN_STRING
                }
            }

            if (ai.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) debuggable = true
            if (ai.flags and ApplicationInfo.FLAG_SYSTEM == 1) is_system_app = true
        } catch (e: Exception) {
            name = UNKNOWN_STRING
            assets_path = UNKNOWN_STRING
            library_path = UNKNOWN_STRING
            storage_path = UNKNOWN_STRING
        }
    }
}
