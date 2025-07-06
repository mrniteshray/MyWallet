package xcom.appbrahma.xapps.MyWallet.ui.AddingScreens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.User
import xcom.niteshray.apps.mywallet.databinding.ActivityAddMoneyBinding

class AddMoneyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddMoneyBinding
    private var amountString = ""

    private var currentuser = FirebaseAuth.getInstance().currentUser?.uid
    private var dbref = FirebaseFirestore.getInstance().collection("Users").document(currentuser!!)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNumberButtons()

        binding.backspace.setOnClickListener {
            if (amountString.isNotEmpty()) {
                amountString = amountString.dropLast(1)
                binding.tvAmount.text = "₹$amountString"
            }
        }

        binding.btnAdd.setOnClickListener {
            val amount = amountString.toInt()
            if (amountString.isEmpty()){
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }else{
                dbref.get().addOnSuccessListener {
                    var user = it.toObject(User::class.java)
                    val totalamount = user?.avalableAmount?.plus(amount)
                    dbref.update("avalableAmount", totalamount).addOnSuccessListener{
                        finish()
                        Toast.makeText(this, "Money Added To Wallet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun onNumberButtonClick(number: String) {
        if (amountString.length < 10) {
            amountString += number
            binding.tvAmount.text = "₹$amountString"
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