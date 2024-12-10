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
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.feynmind.R
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var iconTopRight: ImageView
    private lateinit var imageView2: ShapeableImageView
    private var currentImageUri: Uri? = null

    // Gunakan ActivityResultContracts untuk galeri
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                currentImageUri = it
                imageView2.setImageURI(it) // Ganti gambar pada ImageView dengan gambar dari galeri
                saveImageUriToPreferences(requireContext(), it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi views
        iconTopRight = binding.findViewById(R.id.iconTopRight)
        imageView2 = binding.findViewById(R.id.imageView2)

        loadImageFromPreferences()

        // Menangani klik pada iconTopRight untuk membuka galeri
        iconTopRight.setOnClickListener {
            openGallery()
        }

        return binding
    }

    // Fungsi untuk membuka galeri
    private fun openGallery() {
        getContent.launch("image/*") // Membuka galeri untuk memilih gambar
    }

    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val fileName = "saved_image.png" // Nama file gambar
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return file.absolutePath // Return path ke file yang disimpan
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    // Menyimpan URI gambar di SharedPreferences
    private fun saveImageUriToPreferences(context: Context, imageUri: Uri) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Salin gambar ke internal storage
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
                null // Jika parsing gagal, kembalikan null
            }
        }
    }

    fun loadImageFromPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val imagePath = sharedPreferences.getString("imagePath", null)

        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                imageView2.setImageURI(uri) // Tampilkan gambar di ImageView
            } else {
                // Gambar tidak ditemukan, tampilkan default
                imageView2.setImageResource(R.drawable.ic_user)
            }
        } ?: run {
            // Tidak ada path disimpan, tampilkan default
            imageView2.setImageResource(R.drawable.ic_user)
        }
    }
}

