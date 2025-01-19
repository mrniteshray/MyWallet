package xcom.niteshray.apps.mywallet.ui.AddingScreens

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.ExpenseData
import xcom.niteshray.apps.mywallet.data.User
import xcom.niteshray.apps.mywallet.databinding.ActivityAddExpenseBinding
import xcom.niteshray.apps.mywallet.ui.main.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private var amountString = ""
    private lateinit var category: String
    private lateinit var note: String

    private val auth = FirebaseAuth.getInstance().currentUser?.uid
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupCategorySpinner()
        setupNumberButtons()

        binding.backspace.setOnClickListener {
            if (amountString.isNotEmpty()) {
                amountString = amountString.dropLast(1)
                binding.tvAmount.text = "₹$amountString"
            }
        }

        binding.btnAdd.setOnClickListener {
            note = binding.etNote.text.toString()
            if (note.isNotEmpty() && amountString.isNotEmpty() && category.isNotEmpty()) {
                saveInfoToDatabse(note, amountString.toInt(), category)
            }else{
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun saveInfoToDatabse(note: String, toInt: Int, category: String) {
        val date = getCurrentDate()
        val expense = ExpenseData(
            cateroryName = category,
            date = date,
            amount = toInt,
            note = note,
            id = 0
        )
        dbRef.collection("expenses").add(expense).addOnSuccessListener {
            dbRef.get().addOnSuccessListener {
                val user = it.toObject(User::class.java)
                val totalamount = user?.avalableAmount?.minus(toInt)
                dbRef.update("avalableAmount", totalamount).addOnSuccessListener {
                }
                Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onNumberButtonClick(number: String) {
        if (amountString.length < 10) {
            amountString += number
            binding.tvAmount.text = "₹$amountString"
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
            category = categories[position]
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}