package com.bangkit.feynmind.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.ActivityHomeBinding
import com.bangkit.feynmind.databinding.FragmentHomeBinding
import com.bangkit.feynmind.ui.onboarding.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        currentUser?.let {
            Toast.makeText(requireContext(), "Welcome, ${it.email}", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        activity?.finish() // Close the hosting activity if necessary
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}