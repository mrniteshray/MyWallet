package xcom.niteshray.apps.mywallet.domain.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import xcom.niteshray.apps.mywallet.data.ExpenseData
import xcom.niteshray.apps.mywallet.data.User

class HomeViewModel : ViewModel() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users")

    private val _userData = MutableLiveData<User?>()
    val userData : LiveData<User?> get() = _userData

    private val _expenseList = MutableLiveData<List<ExpenseData>>()
    val expenseList : LiveData<List<ExpenseData>> get() = _expenseList

    private val _pieEntriesLiveData = MutableLiveData<List<PieEntry>>()
    val pieEntriesLiveData: LiveData<List<PieEntry>> get() = _pieEntriesLiveData


    fun fetchUserData(){
        dbRef
            .document(currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                _userData.value = user
            }
    }

    fun fetchExpenseData(){
        dbRef.document(currentUser?.uid.toString())
            .collection("expenses")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val categoryMap = mutableMapOf<String, Float>()
                val expenseList = mutableListOf<ExpenseData>()
                expenseList.clear()
                for (document in documents){
                    val expense = document.toObject(ExpenseData::class.java)
                    expenseList.add(expense)
                    categoryMap[expense.cateroryName] = categoryMap.getOrDefault(expense.cateroryName, 0f) + expense.amount.toFloat()
                }
                _expenseList.postValue(expenseList)

                val pieEntries = categoryMap.map { PieEntry(it.value, it.key) }
                _pieEntriesLiveData.postValue(pieEntries)
            }
    }

}