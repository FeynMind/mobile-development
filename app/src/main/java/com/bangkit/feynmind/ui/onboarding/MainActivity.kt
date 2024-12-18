package com.bangkit.feynmind.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bangkit.feynmind.R
import com.bangkit.feynmind.starter.StarterActivity
import com.bangkit.feynmind.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyThemePreference()
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("FeynMindPreferences", MODE_PRIVATE)
        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            val intent = Intent(this, StarterActivity::class.java)
            startActivity(intent)
            sharedPref.edit().putBoolean("isFirstLaunch", false).apply()
            finish()
            return
        }

        firebaseAuth = FirebaseAuth.getInstance()
        checkUserLoginStatus()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupBottomNavigation()
    }

    private fun applyThemePreference() {
        val sharedPreferences = getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_class,
                R.id.navigation_profile
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
    }

    private fun checkUserLoginStatus() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val fragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_home, fragment)
                .commit()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
