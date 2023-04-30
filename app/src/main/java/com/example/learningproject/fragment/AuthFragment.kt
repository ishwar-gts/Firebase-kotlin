package com.example.learningproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.learningproject.R
import com.example.learningproject.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth


class AuthFragment : Fragment() {
    private lateinit var binding:FragmentAuthBinding
    private var auth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAuthBinding.inflate(layoutInflater,container,false)

        binding.getOtp.setOnClickListener {
            binding.emailEditText.visibility=View.GONE
            binding.otpLayout.visibility=View.VISIBLE
            binding.emailText.text="One Time Password"
            binding.getOtp.text="Verify otp"
        }

        binding.changePhoneNumber.setOnClickListener {
            binding.emailEditText.visibility=View.VISIBLE
            binding.otpLayout.visibility=View.GONE
            binding.emailText.text="Phone Number"
            binding.getOtp.text="Get otp"

        }

        return binding.root


    }

    fun verifyPhoneNumber(){
        
    }


}