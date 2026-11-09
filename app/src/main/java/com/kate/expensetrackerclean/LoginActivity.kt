package com.kate.expensetrackerclean

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // UI Elements
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable dark mode
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )

        setContentView(R.layout.activity_login)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()

        // Initialize XML views
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        tilPassword = findViewById(R.id.tilPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Password visibility - hidden by default
        editPassword.transformationMethod =
            PasswordTransformationMethod.getInstance()

        tilPassword.setEndIconDrawable(R.drawable.ic_eye_off)

        var isPasswordVisible = false

        tilPassword.setEndIconOnClickListener {

            val cursorPosition = editPassword.selectionStart

            if (isPasswordVisible) {

                // Hide password
                editPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()

                tilPassword.setEndIconDrawable(R.drawable.ic_eye_off)

                isPasswordVisible = false

            } else {

                // Show password
                editPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()

                tilPassword.setEndIconDrawable(R.drawable.ic_eye)

                isPasswordVisible = true
            }

            // Keep cursor in its previous position
            editPassword.setSelection(
                cursorPosition.coerceIn(0, editPassword.text?.length ?: 0)
            )
        }

        // Login button
        btnLogin.setOnClickListener {
            loginUser()
        }

        // Navigate to Sign Up screen
        tvSignUp.setOnClickListener {
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
        }

        // Forgot password
        tvForgotPassword.setOnClickListener {
            resetPassword()
        }
    }

    // =====================================
    // LOGIN USER
    // =====================================

    private fun loginUser() {

        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString()

        // Validate email
        if (email.isEmpty()) {
            editEmail.error = "Email is required"
            editEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Enter a valid email address"
            editEmail.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            editPassword.error = "Password is required"
            editPassword.requestFocus()
            return
        }

        // Show loading state
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                btnLogin.isEnabled = true
                btnLogin.text = getString(R.string.login)

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Open Main Activity
                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )

                    finish()

                } else {

                    val message = when (task.exception) {

                        is FirebaseNetworkException ->
                            "No internet connection. Please check your connection."

                        is FirebaseAuthInvalidUserException ->
                            "No account found with this email. Please sign up."

                        is FirebaseAuthInvalidCredentialsException ->
                            "Incorrect email or password. Please try again."

                        else ->
                            "Login failed. Please try again."
                    }

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // =====================================
    // FORGOT PASSWORD
    // =====================================

    private fun resetPassword() {

        val email = editEmail.text.toString().trim()

        if (email.isEmpty()) {
            editEmail.error = "Enter your email address first"
            editEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.error = "Enter a valid email address"
            editEmail.requestFocus()
            return
        }

        tvForgotPassword.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->

                tvForgotPassword.isEnabled = true

                if (task.isSuccessful) {

                    Toast.makeText(
                        this,
                        "Password reset instructions have been sent to your email.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    val message = when (task.exception) {

                        is FirebaseNetworkException ->
                            "No internet connection. Please try again."

                        else ->
                            "Unable to send password reset email. Please try again."
                    }

                    Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
