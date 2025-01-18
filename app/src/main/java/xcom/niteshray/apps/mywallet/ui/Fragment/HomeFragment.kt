package xcom.niteshray.apps.mywallet.ui.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.ExpenseData
import xcom.niteshray.apps.mywallet.data.User
import xcom.niteshray.apps.mywallet.databinding.FragmentHomeBinding
import xcom.niteshray.apps.mywallet.ui.AddExpenseActivity
import xcom.niteshray.apps.mywallet.ui.AddMoneyActivity

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!

    private lateinit var recentAdapter: RecentAdapter

    private var selectedCurrent = ""
    private  val list = ArrayList<ExpenseData>()
    val pieEntries = ArrayList<PieEntry>()

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
        fetchAmount()
        fetchData()
    }

    private fun fetchAmount() {
        dbRef.get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            if (user?.avalableAmount!! < 0){
                binding.totalAmount.setTextColor(Color.RED)
            }
            selectedCurrent = user.selectedCurrency
            binding.totalAmount.text = user.selectedCurrency+ user?.avalableAmount.toString()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchData(){
        dbRef.collection("expenses").get().addOnSuccessListener { documents ->
            val categoryMap = mutableMapOf<String, Float>()
            list.clear()
            for (document in documents){
                val expense = document.toObject(ExpenseData::class.java)
                list.add(expense)
                recentAdapter.notifyDataSetChanged()
                categoryMap[expense.cateroryName] = categoryMap.getOrDefault(expense.cateroryName, 0f) + expense.amount.toFloat()
            }

            pieEntries.clear()
            for ((category, totalAmount) in categoryMap) {
                pieEntries.add(PieEntry(totalAmount, category))
            }

            pieChart()
            binding.progressBar3.visibility = View.GONE
            binding.scrollview.visibility = View.VISIBLE
        }

        binding.rec.layoutManager = LinearLayoutManager(requireContext())

        recentAdapter = RecentAdapter(list,"₹")
        binding.rec.adapter = recentAdapter


    }


    private fun pieChart(){

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