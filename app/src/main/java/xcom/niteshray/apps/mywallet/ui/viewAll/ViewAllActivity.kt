package xcom.niteshray.apps.mywallet.ui.viewAll

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.databinding.ActivityViewAllBinding
import xcom.niteshray.apps.mywallet.domain.viewModels.HomeViewModel
import xcom.niteshray.apps.mywallet.ui.Fragment.RecentAdapter

class ViewAllActivity : AppCompatActivity() {
    private lateinit var binding : ActivityViewAllBinding

    private val homeViewModel = HomeViewModel()
    private lateinit var recentAdapter: RecentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewAllBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener {
            finish()
        }

        homeViewModel.fetchExpenseData()
        homeViewModel.expenseList.observe(this, Observer { expenseList ->
            binding.progressBar5.visibility = View.GONE
            if (expenseList.isEmpty()){
                binding.nodata.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }else{
                binding.nodata.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            recentAdapter = RecentAdapter(expenseList,"₹")
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = recentAdapter
        })

    }
}