package com.kate.expensetrackerclean

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout

    private lateinit var editUsername: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var editConfirmPassword: TextInputEditText

    private lateinit var cbTermsAgreement: CheckBox
    private lateinit var btnCreateAccount: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        // Initialize views
        tilUsername = findViewById(R.id.tilUsername)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)

        editUsername = findViewById(R.id.editUsername)
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)

        cbTermsAgreement = findViewById(R.id.cbTermsAgreement)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)

        // Password visibility controls
        setupPasswordVisibilityToggle(
            tilPassword,
            editPassword
        )

        setupPasswordVisibilityToggle(
            tilConfirmPassword,
            editConfirmPassword
        )

        // Create Account button
        btnCreateAccount.setOnClickListener {
            registerUser()
        }

        // Navigate to the Sign-in screen
        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // =====================================
    // PASSWORD VISIBILITY TOGGLE
    // =====================================

    private fun setupPasswordVisibilityToggle(
        layout: TextInputLayout,
        passwordField: TextInputEditText
    ) {

        var isPasswordVisible = false

        // Password hidden by default
        passwordField.transformationMethod =
            PasswordTransformationMethod.getInstance()

        layout.setEndIconDrawable(R.drawable.ic_eye_off)

        layout.setEndIconOnClickListener {

            val cursorPosition = passwordField.selectionStart

            if (isPasswordVisible) {

                // Hide password
                passwordField.transformationMethod =
                    PasswordTransformationMethod.getInstance()

                layout.setEndIconDrawable(R.drawable.ic_eye_off)

                isPasswordVisible = false

            } else {

                // Show password
                passwordField.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()

                layout.setEndIconDrawable(R.drawable.ic_eye)

                isPasswordVisible = true
            }

            // Preserve cursor position
            passwordField.setSelection(
                cursorPosition.coerceIn(
                    0,
                    passwordField.text?.length ?: 0
                )
            )
        }
    }

    // =====================================
    // REGISTER USER
    // =====================================

    private fun registerUser() {

        val username = editUsername.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString()
        val confirmPassword = editConfirmPassword.text.toString()

        // Clear previous validation errors
        tilUsername.error = null
        tilEmail.error = null
        tilPassword.error = null
        tilConfirmPassword.error = null

        // Validate username
        if (username.isEmpty()) {
            tilUsername.error = "Please enter your username"
            editUsername.requestFocus()
            return
        }

        // Validate email
        if (email.isEmpty()) {
            tilEmail.error = "Please enter your email address"
            editEmail.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Please enter a valid email address"
            editEmail.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            tilPassword.error = "Please enter your password"
            editPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            tilPassword.error =
                "Password must contain at least 6 characters"
            editPassword.requestFocus()
            return
        }

        // Validate reconfirm password
        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error =
                "Please reconfirm your password"
            editConfirmPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            tilConfirmPassword.error = "Passwords do not match"
            editConfirmPassword.requestFocus()
            return
        }

        // Validate Terms & Privacy Policy
        if (!cbTermsAgreement.isChecked) {
            Toast.makeText(
                this,
                "Please agree to the Terms & Privacy Policy",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Disable button during registration
        btnCreateAccount.isEnabled = false
        btnCreateAccount.setText(R.string.creating_account)

        // Create Firebase account
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {

                        // Save username to Firebase profile
                        val profileUpdates =
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()

                        firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener { profileTask ->

                                btnCreateAccount.isEnabled = true
                                btnCreateAccount.setText(
                                    R.string.create_account
                                )

                                if (profileTask.isSuccessful) {

                                    Toast.makeText(
                                        this,
                                        "Account created successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Navigate to Sign-in screen
                                    val intent = Intent(
                                        this,
                                        LoginActivity::class.java
                                    )

                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                                Intent.FLAG_ACTIVITY_NEW_TASK

                                    startActivity(intent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this,
                                        "Account created, but username could not be saved.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    startActivity(
                                        Intent(
                                            this,
                                            LoginActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                            }

                    } else {

                        btnCreateAccount.isEnabled = true
                        btnCreateAccount.setText(
                            R.string.create_account
                        )

                        Toast.makeText(
                            this,
                            "Unable to retrieve account information.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {

                    btnCreateAccount.isEnabled = true
                    btnCreateAccount.setText(
                        R.string.create_account
                    )

                    // Handle registration errors
                    when (val exception = task.exception) {

                        is FirebaseAuthUserCollisionException -> {
                            tilEmail.error =
                                "This email is already registered"
                            editEmail.requestFocus()
                        }

                        is FirebaseAuthWeakPasswordException -> {
                            tilPassword.error =
                                "Please choose a stronger password"
                            editPassword.requestFocus()
                        }

                        else -> {

                            val errorMessage =
                                exception?.localizedMessage.orEmpty()

                            if (
                                exception is FirebaseAuthException &&
                                exception.errorCode ==
                                "ERROR_NETWORK_REQUEST_FAILED"
                            ) {

                                Toast.makeText(
                                    this,
                                    "No internet connection. Please check your internet and try again.",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else if (
                                errorMessage.contains(
                                    "network",
                                    ignoreCase = true
                                )
                            ) {

                                Toast.makeText(
                                    this,
                                    "Connection problem. Please check your internet and try again.",
                                    Toast.LENGTH_LONG
                                ).show()

                            } else {

                                Toast.makeText(
                                    this,
                                    errorMessage.ifEmpty {
                                        "Registration failed. Please try again."
                                    },
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
    }
}
