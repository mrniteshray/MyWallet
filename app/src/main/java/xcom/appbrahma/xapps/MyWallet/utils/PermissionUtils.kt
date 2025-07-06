package xcom.appbrahma.xapps.MyWallet.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.security.Permissions

object PermissionUtils {

    fun isNotificationPermissionGranted(context : Context) : Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestNotificationPermission(activity : AppCompatActivity,launcher : ActivityResultLauncher<String>){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            Toast.makeText(
                activity,
                "Notification permission is required for alerts",
                Toast.LENGTH_SHORT
            ).show()
        }
        launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
    }
 }
