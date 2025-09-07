package com.example.civicsnap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.civicsnap.databinding.ActivitySignUpBinding
import com.example.civicsnap.databinding.ActivitySplashScreenBinding

class SignUpActivity : AppCompatActivity() {


    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var userName: String

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtnSU.setOnClickListener {
            val intent= Intent(this,SignInActivity::class.java)
            startActivity(intent)
        }

    }
}