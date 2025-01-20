package xcom.niteshray.apps.mywallet.ui.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.Budget
import xcom.niteshray.apps.mywallet.databinding.FragmentBudgetBinding
import xcom.niteshray.apps.mywallet.ui.AddingScreens.AddBudgetActivity


class BudgetFragment : Fragment() {

    private var _binding : FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetAdapter: BudgetAdapter
    private val auth = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth?.uid.toString())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFab.setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchBudget()
    }

    private fun fetchBudget() {
        dbRef.collection("budget")
            .get()
            .addOnSuccessListener { documents ->
                val budgetList = mutableListOf<Budget>()
                for (document in documents) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                }
                budgetAdapter = BudgetAdapter(budgetList)
                binding.recyclerView.adapter = budgetAdapter
            }
    }
}