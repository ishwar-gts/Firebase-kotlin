package com.example.learningproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.learningproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val auth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser(){
        val email=binding.emailEditText.text.toString()
        val password=binding.passwordEditTest.text.toString()
        when{
            email.isEmpty()->{
                binding.emailEditText.requestFocus()}
            password.isEmpty() || password.length<=6->{
                binding.passwordEditTest.requestFocus()
            }else->{
            binding.progressCircular.visibility= View.VISIBLE
            binding.signUpButton.visibility= View.GONE

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                Toast.makeText(this, "Complete Task", Toast.LENGTH_SHORT).show()
                binding.progressCircular.visibility= View.GONE
                binding.signUpButton.visibility= View.VISIBLE
            }.addOnCanceledListener {
                Toast.makeText(this, "Sign Up Canceled", Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                binding.progressCircular.visibility= View.GONE
                binding.signUpButton.visibility= View.VISIBLE
                Toast.makeText(this, it.user?.email, Toast.LENGTH_SHORT).show()
            }
        }

        }
    }


}