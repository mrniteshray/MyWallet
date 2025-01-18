package xcom.niteshray.apps.mywallet.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.databinding.ActivityMainBinding
import xcom.niteshray.apps.mywallet.ui.Fragment.BudgetFragment
import xcom.niteshray.apps.mywallet.ui.Fragment.HomeFragment
import xcom.niteshray.apps.mywallet.ui.Fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
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

        supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, HomeFragment()).commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.Fragcontainer, HomeFragment()).commit()
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.Fragcontainer, ProfileFragment()).commit()

                    true
            }
                else -> {
                    false
                }                }
            }

    }
}