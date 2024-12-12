package com.bangkit.feynmind.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isWelcomeShown = sharedPreferences.getBoolean("isWelcomeShown", false)
        val currentUser = auth.currentUser
        currentUser?.let {
            // Set nama pengguna di TextView
            binding.userTv.text = it.displayName ?: "Pengguna"
        }
        // Tampilkan pesan hanya jika belum pernah ditampilkan sebelumnya
        if (!isWelcomeShown) {
            currentUser?.let {
                Toast.makeText(requireContext(), "Selamat Datang, ${it.displayName}", Toast.LENGTH_SHORT).show()
            }

            // Simpan status bahwa pesan sudah ditampilkan
            sharedPreferences.edit().putBoolean("isWelcomeShown", true).apply()
        }

        // Tambahkan aksi pada ImageButton
        binding.quizBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ini sedang dalam tahap pengembangan", Toast.LENGTH_SHORT).show()
        }

        binding.summaryBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ini sedang dalam tahap pengembangan", Toast.LENGTH_SHORT).show()
        }

        binding.tipsBtn.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ini sedang dalam tahap pengembangan", Toast.LENGTH_SHORT).show()
        }

//        binding.btnLogout.setOnClickListener {
//            logoutUser()
//        }
    }

    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isWelcomeShown", false).apply() // Reset status

        auth.signOut()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
