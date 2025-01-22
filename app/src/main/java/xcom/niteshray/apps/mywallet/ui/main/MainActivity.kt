package xcom.niteshray.apps.mywallet.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.databinding.ActivityMainBinding
import xcom.niteshray.apps.mywallet.ui.Fragment.BudgetFragment
import xcom.niteshray.apps.mywallet.ui.Fragment.HomeFragment
import xcom.niteshray.apps.mywallet.ui.Fragment.ProfileFragment
import xcom.niteshray.apps.mywallet.utils.NotificationUtils
import xcom.niteshray.apps.mywallet.utils.PermissionUtils

class MainActivity : AppCompatActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        NotificationUtils.createNotificationChannel(this)
        if (PermissionUtils.isNotificationPermissionGranted(this)){

        }else{
            PermissionUtils.requestNotificationPermission(this,notificationPermissionLauncher)
        }


        supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, HomeFragment()).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                        supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, HomeFragment()).commit()
                    true
                }
                R.id.budget -> {
                        supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, BudgetFragment()).commit()
                    true
                }
                R.id.profile -> {
                        supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, ProfileFragment()).commit()
                    true
            }
                else -> {
                    false
                }
            }
            }

    }
}