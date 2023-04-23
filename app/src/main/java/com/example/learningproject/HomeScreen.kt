package com.example.learningproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.learningproject.constant.Constant
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.ActivityHomeScreenBinding

class HomeScreen : AppCompatActivity() {
    private lateinit var binding:ActivityHomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUserData()


    }

    private fun initUserData(){
        val name:String=PreferenceManager.getString(Constant.name)
        val profile:String=PreferenceManager.getString(Constant.profileImage)
        Glide.with(this).load(profile).centerCrop().into(binding.profileView)
        binding.userName.text=name
    }
}