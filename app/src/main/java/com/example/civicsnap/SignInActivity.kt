package com.example.civicsnap

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.civicsnap.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var phoneNumberET: EditText
    private lateinit var number: String

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //Authentication Initialize
        auth = FirebaseAuth.getInstance()
        //Firebase Initialize
        database = FirebaseDatabase.getInstance().reference

        binding.requestBtnSI.setOnClickListener {
            Log.d("OTP", "Request OTP button clicked")
            number = binding.phoneETSI.text.trim().toString()
            if (number.isNotEmpty()) {
                if (number.length == 10) {
                    number = "+91$number"
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number) // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this) // Activity (for callback binding)
                        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(this, "Please Enter Correct Number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
            }
        }

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        //Google sign in initialise
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        binding.backBtnSI.setOnClickListener {
            val intent = Intent(this, SplashScreen::class.java)
            startActivity(intent)
        }

        binding.googleSignInBtn.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
    }

    //Launcher class for launching google function
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    //Task successfull then task result store in account
                    val account: GoogleSignInAccount ?= task.result
                    //When successfully google login all credential stored in credential variable
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { authTask -> //Lambda expression
                            if (authTask.isSuccessful) {
                                //Here Activity will open if successfully google logged in
                                Toast.makeText(
                                    this,
                                    "Successfully sign-in with Google",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateUI(authTask.result?.user)
                                finish()
                            } else {
                                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "Authenticate Successfully", Toast.LENGTH_SHORT).show()
                    sendToMain()
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.d("TAG", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun sendToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            Log.e("PhoneAuth", "Verification failed", e)
            Toast.makeText(this@SignInActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Log.d("TAG", "onVerificationFailed: ${e.toString()}")
            }
            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            // Save verification ID and resending token so we can use them later
            val intent = Intent(this@SignInActivity, OTPActivity::class.java)
            intent.putExtra("OTP", verificationId)
            intent.putExtra("phoneNumber", number)
            intent.putExtra("resendToken", token)
            startActivity(intent)
        }
    }


    //If user already logged in the application-directly go to main page
    override fun onStart() {
        super.onStart()
        val currentuser: FirebaseUser? = auth.currentUser
        if (currentuser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
