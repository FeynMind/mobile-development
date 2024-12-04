package com.bangkit.feynmind.ui.onboarding

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val REQ_ONE_TAP = 1001 // Request code
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        // Konfigurasi Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getDefaultWebClientId())  // Token ID dari Firebase Console
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        setupActions()
        setupGoogleLogin()
    }

    private fun getDefaultWebClientId(): String {
        val jsonResId = applicationContext.resources.getIdentifier("default_web_client_id", "string", packageName)
        if (jsonResId != 0) {
            return applicationContext.getString(jsonResId)
        } else {
            throw IllegalStateException("default_web_client_id not found in google-services.json")
        }
    }


    private fun setupActions() {
        binding.apply {
            // Tombol Login Biasa
            btnLogin.setOnClickListener {
                val email = edLoginEmail.text.toString().trim()
                val password = edLoginPassword.text.toString().trim()
                login(email, password)
            }

            tvRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            // Tampilkan atau sembunyikan password
            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                togglePasswordVisibility(isChecked)
            }
        }
    }

    private fun togglePasswordVisibility(isChecked: Boolean) {
        binding.apply {
            val selection = edLoginPassword.selectionEnd
            edLoginPassword.inputType = if (isChecked) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            edLoginPassword.setSelection(selection)
        }
    }

    private fun login(email: String, password: String) {
        binding.apply {
            when {
                email.isEmpty() -> {
                    edLoginEmail.error = getString(R.string.isi_email)
                    return
                }
                password.isEmpty() -> {
                    edLoginPassword.error = getString(R.string.isi_kata_sandi)
                    return
                }
            }

            progressBar.visibility = View.VISIBLE
            btnLogin.isEnabled = false

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                        moveToHomeScreen()
                    } else {
                        Toast.makeText(this@LoginActivity, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun setupGoogleLogin() {
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        binding.btnGoogleLogin.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender,
                            REQ_ONE_TAP, null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_ONE_TAP) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "No ID Token!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    moveToHomeScreen()
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun moveToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}