package com.example.proyecto1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto1.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializa Firestore
        db = FirebaseFirestore.getInstance()

        // Muestra el email del usuario
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirigir a LoginActivity si no está autenticado
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            binding.emailTextView.text = currentUser.email
        }

        // Botón de cerrar sesión
        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Botón para añadir un nuevo chat
        binding.addChatButton.setOnClickListener {
            // Abre UserListActivity para seleccionar con quién chatear
            val intent = Intent(this, UserListActivity::class.java)
            startActivity(intent)
        }
    }
}
