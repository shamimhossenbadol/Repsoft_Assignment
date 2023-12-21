package com.repsoft.assignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.repsoft.assignment.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var remember = false
    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        remember = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("remember", false)
    }

    override fun onDestroy() {
        //Sign out user if does not needed to remember
        if (!remember) firebaseAuth!!.signOut()
        super.onDestroy()
    }
}