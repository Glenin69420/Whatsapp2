package com.example.proyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto1.databinding.ActivityUserListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserListActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            currentUserId = currentUser.uid
        } else {
            Log.e("UserListActivity", "El usuario no está autenticado.")
            finish()
            return
        }

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userList = ArrayList()
        userAdapter = UserAdapter(userList) { user ->
            checkOrCreateChat(user.id)
        }

        userRecyclerView.adapter = userAdapter

        db = FirebaseFirestore.getInstance()
        loadUsers()
    }

    private fun loadUsers() {
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                val user = document.toObject(User::class.java)
                userList.add(user)
            }
            userAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Log.w("UserListActivity", "Error al obtener los usuarios.", exception)
        }
    }

    private fun checkOrCreateChat(otherUserId: String) {
        db.collection("chats")
            .whereArrayContains("userIds", currentUserId)
            .whereArrayContains("userIds", otherUserId)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    createNewChat(otherUserId)
                } else {
                    val chatId = result.documents[0].id
                    openChat(chatId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("UserListActivity", "Error al verificar chat.", e)
            }
    }

    private fun createNewChat(otherUserId: String) {
        val chatroomId = db.collection("chats").document().id
        val chatroomModel = ChatroomModel(
            chatroomId = chatroomId,
            userIds = listOf(currentUserId, otherUserId),
            lastMessageTimestamp = System.currentTimeMillis(),
            lastMessageSenderId = currentUserId,
            lastMessage = ""
        )
        db.collection("chats").document(chatroomId).set(chatroomModel)
            .addOnSuccessListener {
                openChat(chatroomId)
            }
            .addOnFailureListener { e ->
                Log.w("UserListActivity", "Error al crear chat.", e)
            }
    }

    private fun openChat(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("chatId", chatId)
        startActivity(intent)
    }
}


