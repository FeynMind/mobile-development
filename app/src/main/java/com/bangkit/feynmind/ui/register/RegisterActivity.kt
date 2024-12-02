package com.bangkit.feynmind.ui.register

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityRegisterBinding
import com.bangkit.feynmind.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            edRegisterPassword.apply {
                setOnEditorActionListener { _, actionId, _ -> clearFocusOnDoneAction(actionId) }
            }

            cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
                toggleLoginPasswordVisibility(isChecked)
            }

            tvSignIn.setOnClickListener { moveToLoginActivity() }

            btnSignUp.setOnClickListener {
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
            btnSignUp.isEnabled = false

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@RegisterActivity) { task ->
                    progressBar.visibility = View.GONE
                    btnSignUp.isEnabled = true

                    if (task.isSuccessful) {
                        // Registrasi berhasil
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.buat_akun_berhasil),
                            Toast.LENGTH_SHORT
                        ).show()
                        moveToLoginActivity()
                    } else {
                        // Registrasi gagal
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.buat_akun_gagal),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun moveToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}