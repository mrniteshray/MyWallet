package xcom.appbrahma.xapps.MyWallet.ui.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.appbrahma.xapps.MyWallet.R
import xcom.appbrahma.xapps.MyWallet.data.ExpenseData
import xcom.appbrahma.xapps.MyWallet.databinding.FragmentHomeBinding
import xcom.appbrahma.xapps.MyWallet.domain.viewModels.HomeViewModel
import xcom.appbrahma.xapps.MyWallet.ui.AddingScreens.AddExpenseActivity
import xcom.appbrahma.xapps.MyWallet.ui.AddingScreens.AddMoneyActivity
import xcom.appbrahma.xapps.MyWallet.ui.viewAll.ViewAllActivity

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!
    private lateinit var recentAdapter: RecentAdapter
    private var selectedCurrent = ""

    private val homeViewModel = HomeViewModel()
    private lateinit var expenselist : List<ExpenseData?>

    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    private var dbRef = FirebaseFirestore.getInstance().collection("Users").document(currentUser.toString())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnClicks()
        val monthsMap = mapOf(
            "Overall" to "Overall",
            "January" to "01",
            "February" to "02",
            "March" to "03",
            "April" to "04",
            "May" to "05",
            "June" to "06",
            "July" to "07",
            "August" to "08",
            "September" to "09",
            "October" to "10",
            "November" to "11",
            "December" to "12"
        )

        val monthNames = monthsMap.keys.toList()
        binding.selectMonthPopup.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.selectMonthPopup)

            monthNames.forEachIndexed { index, month ->
                popupMenu.menu.add(0, index, 0, month)
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->
                binding.selectMonthPopup.text = menuItem.title.toString()
                val selectedMonthNumber = monthsMap[menuItem.title.toString()] ?: "Select Month"
                homeViewModel.filterExpensesByMonth(selectedMonthNumber)
                true
            }

            popupMenu.show()
        }

        homeViewModel.fetchUserData()
        homeViewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            binding.scrollview.visibility = View.VISIBLE
            binding.progressBar3.visibility = View.GONE
            if (user?.avalableAmount!! < 0){
                binding.totalAmount.setTextColor(Color.RED)
            }
            binding.totalAmount.text = user?.selectedCurrency + user?.avalableAmount.toString()
            selectedCurrent = user?.selectedCurrency.toString()
        })

       homeViewModel.fetchExpenseData()
        homeViewModel.expenseList.observe(viewLifecycleOwner, Observer { expenseList ->
            expenselist = expenseList
            val filteredList = if (expenseList.size > 4) expenseList.subList(0, 4) else expenseList
            if (expenseList.isEmpty()){
                binding.textView5.visibility = View.VISIBLE
                binding.rec.visibility = View.GONE
            }else{
                binding.textView5.visibility = View.GONE
                binding.rec.visibility = View.VISIBLE
            }
            recentAdapter = RecentAdapter(filteredList,"₹")
            binding.rec.layoutManager = LinearLayoutManager(requireContext())
            binding.rec.adapter = recentAdapter

        })



        homeViewModel.pieEntriesLiveData.observe(viewLifecycleOwner, Observer { pieEntries ->
            if (pieEntries.isEmpty()){
                binding.pieChart.visibility = View.GONE
                binding.textView6.visibility = View.VISIBLE
            }else{
                binding.pieChart.visibility = View.VISIBLE
                binding.textView6.visibility = View.GONE
            }
            pieChart(pieEntries)
        })
    }


    fun btnClicks(){
        binding.btnFab.setOnClickListener {
            val intent = Intent(requireContext(), AddExpenseActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnAddMoney.setOnClickListener {
            val intent = Intent(requireContext(), AddMoneyActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.viewAll.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.fetchExpenseData()
        homeViewModel.fetchUserData()
    }



    private fun pieChart(pieEntries: List<PieEntry>){

        val TotalExpense = pieEntries.sumOf { it.value.toInt() }
        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.color1),
            ContextCompat.getColor(requireContext(), R.color.color2),
            ContextCompat.getColor(requireContext(), R.color.color3),
            ContextCompat.getColor(requireContext(), R.color.color4),
            ContextCompat.getColor(requireContext(), R.color.color5),
            ContextCompat.getColor(requireContext(), R.color.color6),
            ContextCompat.getColor(requireContext(), R.color.color7),
            ContextCompat.getColor(requireContext(), R.color.color8)
        )

        binding.label.text = "All Expenses: $selectedCurrent$TotalExpense"

        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueTextSize = 16f
        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {

                return "${value.toInt()}%"
            }
        })
        binding.pieChart.data = pieData

        binding.pieChart.data = pieData
        binding.pieChart.description.isEnabled = false
        binding.pieChart.isDrawHoleEnabled = false
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.setEntryLabelTextSize(0f)
        binding.pieChart.setDrawEntryLabels(true)

        binding.pieChart.invalidate()

        binding.pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val pieEntry = e as PieEntry
                val label = pieEntry.label
                val value = pieEntry.value
                binding.label.text = "$label : $selectedCurrent${value.toInt()}"

            }
            override fun onNothingSelected() {
                binding.label.text = "All Expenses : $selectedCurrent$TotalExpense"
            }
        })
    }

}