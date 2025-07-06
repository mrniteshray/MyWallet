package xcom.appbrahma.xapps.MyWallet.ui.AddingScreens

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddBudgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBudgetBinding

    private var selectedDuration: String? = null
    private var selectedCategory: String? = null
    private var TotalAmount: String = ""
    private var spendAmount: Double = 0.0
    private val auth = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth?.uid.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupCategorySpinner()
        setupDurationSpinner()
        setupNumberButtons()

        binding.backspace.setOnClickListener {
            if(TotalAmount.isNotEmpty()){
                TotalAmount = TotalAmount.dropLast(1)
                binding.tvAmount.text = "₹$TotalAmount"
            }
        }

        binding.btnAdd.setOnClickListener {
            binding.progressBar7.visibility = View.VISIBLE
        if (TotalAmount.isNotEmpty() && selectedCategory != null && selectedDuration != null){
                saveBudget(selectedCategory!!, selectedDuration!!, TotalAmount.toDouble())
            }
        }
    }

    private fun saveBudget(selectedCategory: String, selectedDuration: String, toDouble: Double) {

        dbRef.collection("budget")
            .whereEqualTo("category", selectedCategory)
            .get()
            .addOnSuccessListener {
                if (it.isEmpty){
                    dbRef.collection("expenses")
                        .whereEqualTo("cateroryName", selectedCategory)
                        .get()
                        .addOnSuccessListener {
                            for (document in it) {
                                val expense = document.toObject(ExpenseData::class.java)
                                spendAmount += expense.amount
                            }
                            val budget = Budget(
                                category = selectedCategory,
                                totalAmont = toDouble,
                                spentAmount = spendAmount,
                                duration = selectedDuration,
                                startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            )

                            dbRef.collection("budget")
                                .add(budget).addOnSuccessListener {
                                    binding.progressBar7.visibility = View.GONE
                                    Toast.makeText(this, "Budget Added", Toast.LENGTH_SHORT).show()
                                    finish()
                                }.addOnFailureListener {
                                    Toast.makeText(this, "Failed to add budget", Toast.LENGTH_SHORT).show()
                                }
                        }
                }else{
                    binding.progressBar7.visibility = View.GONE
                    binding.tvAmount.text = "₹0"
                    Toast.makeText(this, "Budget already exists for $selectedCategory", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun onNumberButtonClick(number: String) {
        if (TotalAmount.length < 10) {
            TotalAmount += number
            binding.tvAmount.text = "₹$TotalAmount"
        }
    }

    private fun setupCategorySpinner() {
        val categories = listOf(
            "Food & Dining",
            "Groceries",
            "Transport",
            "Entertainment",
            "Health & Fitness",
            "Shopping",
            "Utilities",
            "Travel",
            "Education",
            "Miscellaneous"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.category.setAdapter(adapter)

        binding.category.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categories[position]
        }
    }

    private fun setupDurationSpinner() {
        val categories = listOf(
            "A Week",
            "A Month",
            "A Year"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        binding.duration.setAdapter(adapter)

        binding.duration.setOnItemClickListener { _, _, position, _ ->
            selectedDuration = categories[position]
        }
    }

    private fun setupNumberButtons() {
        val buttons = listOf(
            binding.btn0,
            binding.btn1,
            binding.btn2,
            binding.btn3,
            binding.btn4,
            binding.btn5,
            binding.btn6,
            binding.btn7,
            binding.btn8,
            binding.btn9,
            binding.dot
        )

        for (button in buttons) {
            button.setOnClickListener { onNumberButtonClick(button.text.toString()) }
        }
    }
}