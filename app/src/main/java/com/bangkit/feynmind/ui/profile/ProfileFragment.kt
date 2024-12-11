package com.bangkit.feynmind.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.feynmind.R
import com.bangkit.feynmind.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImageUri = it
                binding.imageView2.setImageURI(it)
                saveImageUriToPreferences(requireContext(), it)
                Log.d("ProfileFragment", "Image URI: $it")
            } ?: Log.e("ProfileFragment", "Failed to get image URI")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        loadUserProfile()
        loadImageFromPreferences()

        binding.iconEditPicture.setOnClickListener {
            openGallery()
        }

        binding.btnEditp.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadUserProfile() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            binding.textView2.text = user.displayName ?: "Nama tidak tersedia"
            binding.textViewEmail.text = user.email ?: "Email tidak tersedia"
        } else {
            Toast.makeText(requireContext(), "Pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "saved_image.png"
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageUriToPreferences(context: Context, imageUri: Uri) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val imagePath = saveImageToInternalStorage(context, imageUri)
        imagePath?.let {
            editor.putString("imagePath", it)
        }
        editor.apply()
    }

    private fun loadImageUriFromPreferences(context: Context): Uri? {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val uriString = sharedPreferences.getString("imageUri", null)
        return uriString?.let {
            try {
                Uri.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun loadImageFromPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("imagePath", null)

        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                binding.imageView2.setImageURI(uri)
            } else {
                binding.imageView2.setImageResource(R.drawable.ic_user)
            }
        } ?: run {
            binding.imageView2.setImageResource(R.drawable.ic_user)
        }
    }
}
