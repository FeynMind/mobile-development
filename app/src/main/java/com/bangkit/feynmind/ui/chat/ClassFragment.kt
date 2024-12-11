package com.bangkit.feynmind.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.bangkit.feynmind.R

class ClassFragment : Fragment(), View.OnClickListener {

    private lateinit var btnKelas10: Button
    private lateinit var btnKelas11: Button
    private lateinit var btnKelas12: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnKelas10 = view.findViewById(R.id.btn_kelas10)
        btnKelas11 = view.findViewById(R.id.btn_kelas11)
        btnKelas12 = view.findViewById(R.id.btn_kelas12)

        btnKelas10.setOnClickListener(this)
        btnKelas11.setOnClickListener(this)
        btnKelas12.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val kelas: String = when (v?.id) {
            R.id.btn_kelas10 -> "Kelas 10"
            R.id.btn_kelas11 -> "Kelas 11"
            R.id.btn_kelas12 -> "Kelas 12"
            else -> return
        }

        val intent = Intent(requireContext(), TopicActivity::class.java)
        intent.putExtra("KELAS", kelas)
        startActivity(intent)
    }

}