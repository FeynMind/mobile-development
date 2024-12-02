package com.bangkit.feynmind.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.feynmind.MainActivity
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityLoginBinding
import com.bangkit.feynmind.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        binding.apply {
            btnSignIn.setOnClickListener {
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()
                login(email, password)
            }

            tvSignUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }

    }

    private fun login(email: String, password: String) {
        if (email.isEmpty()) {
            binding.edLoginEmail.error = getString(R.string.isi_email)
            return
        }
        if (password.isEmpty()) {
            binding.edLoginPassword.error = getString(R.string.isi_kata_sandi)
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
                    moveToHomeScreen()
                } else {
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun moveToHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}