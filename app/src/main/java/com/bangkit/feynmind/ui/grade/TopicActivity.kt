package com.bangkit.feynmind.ui.grade

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bangkit.feynmind.R
import com.google.android.material.button.MaterialButton

class TopicActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        val toolbar = findViewById<Toolbar>(R.id.topic_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val kelas = intent.getStringExtra("KELAS") ?: "Kelas tidak diketahui"

        val tvTopicTitile = findViewById<TextView>(R.id.tv_topic_title)
        tvTopicTitile.text = "Daftar Topik untuk $kelas"

        val topicChoiceButton = findViewById<MaterialButton>(R.id.topic_choice)
        topicChoiceButton.setOnClickListener {
            val intent = Intent(this, com.bangkit.feynmind.ui.chat.VoiceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}