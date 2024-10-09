package com.example.proyecto1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class ChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var db: FirebaseFirestore
    private lateinit var chatId: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserId = currentUser.uid
        } else {
            Log.e("ChatActivity", "El usuario no está autenticado.")
            finish()
            return
        }

        chatId = intent.getStringExtra("chatId") ?: return

        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.setHasFixedSize(true)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(messageList)
        messageRecyclerView.adapter = messageAdapter

        db = FirebaseFirestore.getInstance()
        loadMessages()

        val sendButton: Button = findViewById(R.id.sendButton)
        val messageEditText: EditText = findViewById(R.id.messageEditText)

        sendButton.setOnClickListener {
            val messageText = messageEditText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageEditText.text.clear()
            }
        }
    }

    private fun loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ChatActivity", "Error al obtener mensajes.", e)
                    return@addSnapshotListener
                }
                messageList.clear()
                for (doc in snapshots!!) {
                    val message = doc.toObject(Message::class.java)
                    messageList.add(message)
                }
                messageAdapter.notifyDataSetChanged()
                messageRecyclerView.scrollToPosition(messageList.size - 1)
            }
    }

    private fun sendMessage(text: String) {
        val message = Message(
            text = text,
            senderId = currentUserId,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chats").document(chatId).collection("messages").add(message)
            .addOnSuccessListener {
                updateLastMessage(text)
            }
            .addOnFailureListener { e ->
                Log.w("ChatActivity", "Error al enviar el mensaje.", e)
            }
    }

    private fun updateLastMessage(lastMessage: String) {
        db.collection("chats").document(chatId)
            .update("lastMessage", lastMessage, "lastMessageTimestamp", System.currentTimeMillis(), "lastMessageSenderId", currentUserId)
            .addOnFailureListener { e ->
                Log.w("ChatActivity", "Error al actualizar el último mensaje.", e)
            }
    }
}


