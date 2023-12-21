package com.repsoft.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.repsoft.assignment.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private var firebaseAuth: FirebaseAuth? = null
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check user already logged in or not
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth!!.currentUser != null) {
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
            this@SignInActivity.finish()
        }

        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            this@SignInActivity.finish()
        }

        //Password reset
        binding.forgot.setOnClickListener {
            val email = binding.email.text.toString()
            if (email.isEmpty()) {
                binding.email.error = "Required"
            } else if (!email.matches(emailPattern.toRegex())) {
                binding.email.error = "Enter a valid email address"
            } else {
                firebaseAuth!!.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) Toast.makeText(
                        this@SignInActivity,
                        "An email to reset your password has been sent to your email address.",
                        Toast.LENGTH_SHORT
                    ).show()
                    else Toast.makeText(
                        this@SignInActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        //Sign in to account using email and password
        binding.signIn.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isEmpty()) {
                binding.email.error = "Required"
            } else if (!email.matches(emailPattern.toRegex())) {
                binding.email.error = "Enter a valid email address"
            } else if (password.isEmpty() || password.length < 8) {
                binding.password.error = "Enter valid password"
            } else {
                firebaseAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val preference = getSharedPreferences("settings", MODE_PRIVATE)
                        preference.edit().putBoolean("remember", binding.remember.isChecked).apply()
                        Toast.makeText(this@SignInActivity, "Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                        this@SignInActivity.finish()
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            it.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}