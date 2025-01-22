package xcom.niteshray.apps.mywallet.domain.viewModels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.data.Budget
import xcom.niteshray.apps.mywallet.ui.Fragment.BudgetAdapter

class BudgetViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth?.uid.toString())

    private var _budgetList = MutableLiveData<List<Budget>>()
    val budgetList : LiveData<List<Budget>> get() = _budgetList

    fun fetchBudget() {
        dbRef.collection("budget")
            .get()
            .addOnSuccessListener { documents ->
                val budgetList = mutableListOf<Budget>()
                for (document in documents) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                }
                _budgetList.value = budgetList
            }
    }
}