package com.bangkit.feynmind.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        firebaseAuth = FirebaseAuth.getInstance()

        setupActions()
    }

    private fun setupActions() {
        binding.apply {
            // Tombol Login
            btnSignIn.setOnClickListener {
                val email = edLoginEmail.text.toString().trim()
                val password = edLoginPassword.text.toString().trim()
                login(email, password)
            }

            tvSignUp.setOnClickListener {
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
            btnSignIn.isEnabled = false

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    btnSignIn.isEnabled = true
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT).show()
                        moveToHomeScreen()
                    } else {
                        Toast.makeText(this@LoginActivity, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun moveToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}