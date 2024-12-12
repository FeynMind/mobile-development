package com.bangkit.feynmind.ui.chat

import ChatAdapter
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.feynmind.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class VoiceActivity : AppCompatActivity() {
    private lateinit var addButton: FloatingActionButton
    private lateinit var voiceButton: FloatingActionButton
    private lateinit var sendButton: ImageButton
    private lateinit var inputMessageEt: TextInputEditText
    private lateinit var chatRecyclerView: RecyclerView
    private val chatMessages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.voice_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Bind Views
        voiceButton = findViewById(R.id.voice_button)
        inputMessageEt = findViewById(R.id.input_message)
        sendButton = findViewById(R.id.send_button)
        chatRecyclerView = findViewById(R.id.chat_recycler_view)

        // Set Click Listener
        voiceButton.setOnClickListener {
            voiceInput()
        }

        // Setup RecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ChatAdapter(chatMessages)
        chatRecyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val message = inputMessageEt.text.toString()
            if (message.isNotEmpty()) {
                chatMessages.add(ChatMessage(message, true)) // Pesan pengguna
                chatMessages.add(ChatMessage("$message diterima", false)) // Respon sistem
                adapter.notifyItemInserted(chatMessages.size - 1)
                chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                inputMessageEt.text?.clear()
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun voiceInput() {
        val language = "id" // Mengatur bahasa (Indonesia)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        }

        try {
            voiceInputArl.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private val voiceInputArl = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            val result = activityResult.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = result?.get(0)

            if (!text.isNullOrEmpty()) {
                // Tambahkan teks ke EditText
                inputMessageEt.setText(text)
                inputMessageEt.setSelection(text.length)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
