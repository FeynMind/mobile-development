package com.bangkit.feynmind.ui.chat

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.feynmind.R

class TopicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val kelas = intent.getStringExtra("KELAS") ?: "Kelas tidak diketahui"

        val tvTopicTitile = findViewById<TextView>(R.id.tv_topic_title)
        tvTopicTitile.text = "Daftar Topik untuk $kelas"
    }
}