package com.bangkit.feynmind

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.feynmind.ui.home.HomeActivity
import com.bangkit.feynmind.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Periksa apakah pengguna sudah login atau belum
        checkUserLoginStatus()
    }

    private fun checkUserLoginStatus() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // Jika pengguna sudah login, arahkan ke halaman utama (misalnya, HomeActivity)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Menutup MainActivity agar tidak kembali ke halaman ini
        } else {
            // Jika pengguna belum login, arahkan ke halaman login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Menutup MainActivity agar tidak kembali ke halaman ini
        }
    }
}