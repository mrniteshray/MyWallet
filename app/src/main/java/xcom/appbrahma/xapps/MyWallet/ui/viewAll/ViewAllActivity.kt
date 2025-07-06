package xcom.appbrahma.xapps.MyWallet.ui.viewAll

import android.app.DatePickerDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        homeViewModel.filteredlist.observe(this, Observer { expenseList ->
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

        binding.btnFiltered.setOnClickListener {
            homeViewModel.clearFilter()
            binding.btnDate.text = "Select Date"
        }


        binding.btnDate.setOnClickListener {
            openDatePicker()
        }

    }

    private fun openDatePicker() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,{ _ , selectedyear , selectedmonth,selectedday ->
            val selectedDate =  String.format("%02d-%02d-%d", selectedday, selectedmonth + 1, selectedyear)
            binding.btnDate.text = selectedDate
            homeViewModel.filteredData(selectedDate)
        },year,month,day).show()
    }
}