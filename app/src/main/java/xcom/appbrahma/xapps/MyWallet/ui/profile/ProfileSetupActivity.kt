package xcom.appbrahma.xapps.MyWallet.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.User
import xcom.niteshray.apps.mywallet.databinding.ActivityProfileSetupBinding

class ProfileSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSetupBinding

    private var firestore = FirebaseFirestore.getInstance()
    private var currentuser = FirebaseAuth.getInstance().currentUser
    private var selectedCurrency: String = ""
    private var selectedCurrencySymbol: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        Glide.with(this)
            .load(currentuser?.photoUrl)
            .error(R.drawable.google_icon)
            .into(binding.imageView)

        binding.BtnSave.setOnClickListener {
            binding.progressBar2.visibility = View.VISIBLE
            val availableAmount : Int = binding.amount.text.toString().toInt()
            if (selectedCurrency.isNotEmpty() && availableAmount > 0 ) {
                saveUserToFirebase(currentuser?.uid.toString(),currentuser?.displayName,currentuser?.email,currentuser?.photoUrl.toString(),selectedCurrency,availableAmount)
            }else{
                binding.progressBar2.visibility = View.GONE
                if (selectedCurrency.isEmpty()) {
                    Toast.makeText(this, "Please select a currency", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textView3.text = "Hello! \n ${currentuser?.displayName}"

        val currencyMap = mapOf(
            "USD - US Dollar" to "$",
            "EUR - Euro" to "€",
            "INR - Indian Rupee" to "₹",
            "JPY - Japanese Yen" to "¥",
            "GBP - British Pound" to "£",
            "AUD - Australian Dollar" to "A$",
            "CAD - Canadian Dollar" to "C$",
            "CHF - Swiss Franc" to "CHF",
            "CNY - Chinese Yuan" to "¥",
            "SEK - Swedish Krona" to "kr",
            "NZD - New Zealand Dollar" to "NZ$",
            "SGD - Singapore Dollar" to "S$",
            "HKD - Hong Kong Dollar" to "HK$",
            "NOK - Norwegian Krone" to "kr",
            "KRW - South Korean Won" to "₩",
            "TRY - Turkish Lira" to "₺",
            "RUB - Russian Ruble" to "₽",
            "BRL - Brazilian Real" to "R$",
            "ZAR - South African Rand" to "R",
            "MXN - Mexican Peso" to "$"
        )

        val currencies = currencyMap.keys.toList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        binding.currencyDropdown.setAdapter(adapter)

        binding.currencyDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedCurrency = currencies[position]
            selectedCurrencySymbol = currencyMap[selectedCurrency].orEmpty()
            Toast.makeText(this, "Selected currency: $selectedCurrency", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserToFirebase(uid: String?, displayName: String?, email: String?, photoUrl: String, selectedCurrency: String, availableAmount: Int) {

        val user = User(
            uid = uid!!,
            displayName = displayName!!,
            email = email!!,
            photoUrl = photoUrl,
            selectedCurrency = selectedCurrency,
            avalableAmount = availableAmount
        )

        firestore.collection("Users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                binding.progressBar2.visibility = View.GONE
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Welcome : $displayName", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error:${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}