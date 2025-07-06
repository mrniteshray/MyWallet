package xcom.appbrahma.xapps.MyWallet.ui.signin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import xcom.niteshray.apps.mywallet.R
import xcom.niteshray.apps.mywallet.databinding.ActivitySignInBinding
import xcom.niteshray.apps.mywallet.ui.profile.ProfileSetupActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignInBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var firestore = FirebaseFirestore.getInstance()
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.d("ErrorSignin", "Google sign in failed ${e.message.toString()}")
            Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.BtnSignin.visibility = View.GONE

        auth = FirebaseAuth.getInstance()

            if (auth.currentUser != null) {
                Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                },2000)
            }else{
                binding.BtnSignin.visibility = View.VISIBLE
            }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.BtnSignin.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signInWithGoogle()
        }

    }
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    userExistorNot()
                } else {
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun userExistorNot() {
        val currentuser = FirebaseAuth.getInstance().currentUser
        firestore.collection("Users")
            .document(currentuser?.uid.toString())
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    startActivity(Intent(this, ProfileSetupActivity::class.java))
                    finish()
                }
            }
    }
}