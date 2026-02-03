package com.kate.expensetrackerclean

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.os.Looper
import android.content.Intent
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatDelegate


class LoginActivity: AppCompatActivity() {

    // ===== FIREBASE AUTHENTICATION INSTANCE =====
    private lateinit var auth: FirebaseAuth

    // ===== UI ELEMENTS =====
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var btnLogin: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         // ===== DARK MODE =====
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.activity_login)

        // ===== INITIALIZE FIREBASE AUTH =====
        auth = FirebaseAuth.getInstance()

        // ===== XML VIEWS =====
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)

        // ===== LOGIN BUTTON CLICK =====
        btnLogin.setOnClickListener {
            loginUser()
        }
        // ===== OPTIONAL: OPEN REGISTER SCREEN =====
        btnRegister.setOnClickListener {
            registerUser()
        }
    }
    // ===== LOGS IN USER USING FIREBASE EMAIL & PASSWORD =====
    private fun loginUser() {

        // ===== GET USER INPUT =====
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        // ===== VALIDATE EMAIL =====
        if (email.isEmpty()) {
            editEmail.error = "Email is required"
            editEmail.requestFocus()
            return
        }
        // ===== VALIDATE PASSWORD =====
        if (password.isEmpty()) {
            editPassword.error = "Password is required"
            editPassword.requestFocus()
            return
        }
        // ===== FIREBASE =====
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // ===== LOGIN SUCCESS =====
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // ===== NAVIGATE TO MAIN ACTIVITY =====
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // ===== LOGIN FAILED =====
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
    // =====  REGISTER USER USING FIREBASE EMAIL & PASSWORD =====
    private fun registerUser() {

        // ===== GET USER INPUT =====
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()

        // ===== VALIDATE EMAIL =====
        if (email.isEmpty()) {
            editEmail.error = "Email is required"
            editEmail.requestFocus()
            return
        }
        // ===== VALIDATE PASSWORD =====
        if (password.isEmpty()) {
            editPassword.error = "Password is required"
            editPassword.requestFocus()
            return
        }
        // ===== FIREBASE =====
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // ===== REGISTER SUCCESS =====
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                } else {
                    // ===== REGISTER FAILED =====
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Registration failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}


