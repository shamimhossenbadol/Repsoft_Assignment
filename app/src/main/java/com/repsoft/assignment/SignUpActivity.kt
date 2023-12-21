package com.repsoft.assignment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.repsoft.assignment.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.back.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        //Go to for Sign In
        binding.signIn.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            this@SignUpActivity.finish()
        }
        //Start Sign up process
        binding.signUp.setOnClickListener { startSignUpProcess() }

    }

    //Check data and then Sign Up a user
    private fun startSignUpProcess() {
        val name = binding.name.text.toString()
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val confirmPassword = binding.passwordConfirm.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        if (name.isEmpty()) {
            binding.name.error = "Required"
        } else if (email.isEmpty()) {
            binding.email.error = "Required"
        } else if (!email.matches(emailPattern.toRegex())) {
            binding.email.error = "Enter a valid email address"
        } else if (password.isEmpty() || password.length < 8) {
            binding.password.error = "Enter valid password"
        } else if (confirmPassword != password) {
            binding.passwordConfirm.error = "Password not matched"
        } else {
            //Data is valid now create an account
            firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        storeUserData(name, email)
                    } else {
                        //Something went wrong, unable to create account
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    //Store user data using user id
    private fun storeUserData(name: String, email: String) {
        val data = HashMap<String, Any>()
        data["name"] = name
        data["email"] = email
        firebaseAuth?.currentUser?.let { firebaseFirestore?.collection("users")?.document(it.uid) }
            ?.set(data)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    //All task task is successful
                    Toast.makeText(this@SignUpActivity, "Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                    firebaseAuth!!.signOut()
                    this@SignUpActivity.finish()
                } else {
                    //Something went wrong, so delete the account
                    Toast.makeText(this@SignUpActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
                    firebaseAuth!!.currentUser?.delete()?.addOnFailureListener {
                        firebaseAuth!!.currentUser?.delete()
                    }
                    firebaseAuth!!.signOut()
                }
            }
    }
}