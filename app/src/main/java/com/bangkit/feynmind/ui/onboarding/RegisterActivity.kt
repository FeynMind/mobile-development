package com.bangkit.feynmind.ui.onboarding

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val REQ_ONE_TAP_REGISTER = 1002
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
        setupGoogleSignIn()

    }

    private fun setupAction() {
        binding.apply {
            edRegisterPassword.apply {
                setOnEditorActionListener { _, actionId, _ -> clearFocusOnDoneAction(actionId) }
            }

            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                toggleLoginPasswordVisibility(isChecked)
            }

            tvLogin.setOnClickListener { moveToLoginActivity() }

            btnRegister.setOnClickListener {
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                register(name, email, password)
            }
        }
    }

    private fun clearFocusOnDoneAction(actionId: Int): Boolean {
        binding.apply {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edRegisterPassword.clearFocus()
                edRegisterPassword.error = null
                imm.hideSoftInputFromWindow(edRegisterPassword.windowToken, 0)
                return true
            }

            return false
        }
    }

    private fun toggleLoginPasswordVisibility(isChecked: Boolean) {
        binding.apply {
            val selection = edRegisterPassword.selectionEnd

            if (isChecked) {
                edRegisterPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                edRegisterPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            edRegisterPassword.setSelection(selection)
            edRegisterPassword.error = null
        }
    }

    private fun register(name: String, email: String, password: String) {
        binding.apply {
            when {
                name.isEmpty() -> {
                    edRegisterName.error = getString(R.string.isi_nama)
                }
                email.isEmpty() -> {
                    edRegisterEmail.error = getString(R.string.isi_email)
                }
                password.isEmpty() -> {
                    edRegisterPassword.error = getString(R.string.isi_kata_sandi)
                }
                password.length < 8 -> {
                    edRegisterPassword.error = getString(R.string.kata_sandi_minimal_8_karakter)
                }
                else -> {
                    executeRegister(name, email, password)
                }
            }
        }
    }

    private fun executeRegister(name: String, email: String, password: String) {
        binding.apply {
            // Menampilkan progress bar saat proses registrasi
            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterActivity) { task ->
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true

                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        Log.d("RegisterActivity", "User created successfully: ${user?.email}")
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }
                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Log.d("RegisterActivity", "Profile updated successfully with name: $name")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        getString(R.string.buat_akun_berhasil),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    moveToLoginActivity()
                                } else {
                                    Log.e("RegisterActivity", "Profile update failed: ${updateTask.exception?.message}")
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Failed to update profile: ${updateTask.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Log.e("RegisterActivity", "User creation failed: ${task.exception?.message}")
                        Toast.makeText(
                            this@RegisterActivity,
                            "Failed to register: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun setupGoogleSignIn() {
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

        binding.btnGoogleRegister.setOnClickListener {
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender,
                            REQ_ONE_TAP_REGISTER, null, 0, 0, 0, null
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
        if (requestCode == REQ_ONE_TAP_REGISTER) {
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
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        saveUserDataToDatabase(user.displayName, user.email)
                    }
                    Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT).show()
                    moveToHomeScreen()
                } else {
                    Toast.makeText(this, "Authentication Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserDataToDatabase(name: String?, email: String?) {
        Toast.makeText(this, "Hai $name, $email, Selamat Datang", Toast.LENGTH_SHORT).show()
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun moveToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}