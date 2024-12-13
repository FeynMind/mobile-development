package com.bangkit.feynmind.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_edit_profile)

        // Additional setup for Edit Profile functionality can be added here
    }
}
