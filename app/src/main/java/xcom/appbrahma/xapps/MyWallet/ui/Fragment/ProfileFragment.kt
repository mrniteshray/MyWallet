package xcom.appbrahma.xapps.MyWallet.ui.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xcom.niteshray.apps.mywallet.data.User
import xcom.niteshray.apps.mywallet.databinding.FragmentProfileBinding
import xcom.niteshray.apps.mywallet.ui.signin.SignInActivity

class ProfileFragment : Fragment() {

    private lateinit var _binding : FragmentProfileBinding
    private val binding get() = _binding!!
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val dbRef = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            fetchUserData()
        }

        binding.logout.setOnClickListener {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
        }
    }

    private fun fetchUserData() {
        dbRef.collection("Users")
            .document(currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                binding.progressBar4.visibility = View.GONE
                val user = it.toObject(User::class.java)

                binding.userImage.visibility = View.VISIBLE
                Glide.with(requireContext())
                    .load(user?.photoUrl.toString())
                    .into(binding.userImage)

                binding.name.visibility = View.VISIBLE
                binding.name.text = user?.displayName

                binding.linearlayout.visibility = View.VISIBLE
                binding.userEmail.text = user?.email
                binding.currentBalance.text = "$"+user?.avalableAmount.toString()
            }
    }
}
