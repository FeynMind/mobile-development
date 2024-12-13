import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.feynmind.R
import com.bangkit.feynmind.ui.chat.ChatMessage

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_SYSTEM = 2
        private const val VIEW_TYPE_USER_FILE = 3
        private const val VIEW_TYPE_SYSTEM_FILE = 4
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.isUserMessage && message.fileUri != null -> VIEW_TYPE_USER_FILE
            !message.isUserMessage && message.fileUri != null -> VIEW_TYPE_SYSTEM_FILE
            message.isUserMessage -> VIEW_TYPE_USER
            else -> VIEW_TYPE_SYSTEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_USER -> R.layout.item_chat_bubble_user
            VIEW_TYPE_SYSTEM -> R.layout.item_chat_bubble_system
            VIEW_TYPE_USER_FILE -> R.layout.item_chat_bubble_user_file
            else -> throw IllegalArgumentException("Invalid view type")
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ChatViewHolder) {
            holder.bind(messages[position])
        }
    }

    override fun getItemCount(): Int = messages.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView? = itemView.findViewById(R.id.chat_message)
        private val fileName: TextView? = itemView.findViewById(R.id.file_name)

        fun bind(chatMessage: ChatMessage) {
            if (chatMessage.fileUri != null) {
                // Tampilkan nama file
                fileName?.text = "File PDF: ${chatMessage.fileUri.split("/").last()}"
                fileName?.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(Uri.parse(chatMessage.fileUri), "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    itemView.context.startActivity(intent)
                }

            } else {
                // Tampilkan pesan teks
                messageText?.text = chatMessage.message
            }
        }
    }
}
