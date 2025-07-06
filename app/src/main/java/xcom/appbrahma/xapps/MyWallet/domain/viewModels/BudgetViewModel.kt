package xcom.appbrahma.xapps.MyWallet.domain.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.appbrahma.xapps.MyWallet.data.Budget
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class BudgetViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth?.uid.toString())

    private var _budgetList = MutableLiveData<List<Budget>>()
    val budgetList : LiveData<List<Budget>> get() = _budgetList

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBudget() {
        dbRef.collection("budget")
            .get()
            .addOnSuccessListener { documents ->
                val budgetList = mutableListOf<Budget>()
                for (document in documents) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                    val startdate = budget.startDate
                    val duration = budget.duration

                    if (startdate!=null && duration!=null) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val startDate = LocalDate.parse(startdate, formatter)

                        val daysBetween = ChronoUnit.DAYS.between(startDate, currentDate)

                        val isOutdated = when (duration.lowercase()) {
                            "a week" -> daysBetween > 7
                            "a month" -> daysBetween > 30
                            else -> false
                        }

                        if (isOutdated) {
                            document.reference.delete()
                        }

                    }
                }
                _budgetList.value = budgetList
            }
    }
}