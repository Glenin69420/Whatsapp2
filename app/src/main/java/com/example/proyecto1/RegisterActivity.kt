package com.example.proyecto1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto1.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.appcheck.internal.util.Logger.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Lógica para registrar
        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val username = binding.UsernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Guardar el usuario en Firestore
                            saveUserToFirestore(email, username)
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserToFirestore(email: String, username: String) {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: return

        val user = hashMapOf(
            "Email" to email,
            "Username" to username
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }
}
