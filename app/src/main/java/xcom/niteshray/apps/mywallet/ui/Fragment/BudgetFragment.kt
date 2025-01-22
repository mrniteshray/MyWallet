package xcom.niteshray.apps.mywallet.ui.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.data.Budget
import xcom.niteshray.apps.mywallet.databinding.FragmentBudgetBinding
import xcom.niteshray.apps.mywallet.domain.viewModels.BudgetViewModel
import xcom.niteshray.apps.mywallet.ui.AddingScreens.AddBudgetActivity


class BudgetFragment : Fragment() {

    private var _binding : FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetAdapter: BudgetAdapter
    private val auth = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance().collection("Users").document(auth?.uid.toString())

    private val budgetViewModel : BudgetViewModel by viewModels()

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

        budgetViewModel.fetchBudget()

        budgetViewModel.budgetList.observe(viewLifecycleOwner, Observer {
            budgetAdapter = BudgetAdapter(it){ itemDelete ->
                dbRef.collection("budget")
                    .whereEqualTo("category",itemDelete)
                    .get()
                    .addOnSuccessListener {
                        for (document in it) {
                            document.reference.delete()
                        }
                        budgetViewModel.fetchBudget()
                        Toast.makeText(requireContext(), "Budget Deleted", Toast.LENGTH_SHORT).show()
                    }

            }
            binding.recyclerView.adapter = budgetAdapter
            budgetAdapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        budgetViewModel.fetchBudget()
    }
}