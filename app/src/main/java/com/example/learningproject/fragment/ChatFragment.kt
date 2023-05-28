package com.example.learningproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.learningproject.R
import com.example.learningproject.UserActivity
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private lateinit var binding:FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentChatBinding.inflate(layoutInflater,container,false)
        initUserData()
        binding.startConversation.setOnClickListener {
            startActivity(Intent(activity,UserActivity::class.java))
        }
        return binding.root
    }



    private fun initUserData(){
        val name:String= PreferenceManager.getString(ConstantValue.name)
        val profile:String= PreferenceManager.getString(ConstantValue.profileImage)
        Log.d("userid", "initUserData: ${PreferenceManager.getString(ConstantValue.userid)}")
        Glide.with(this).load(profile).centerCrop().into(binding.profileView)
        binding.name.text=name
//        Ej6qSDsK1FYSNRpqRY0rgiiZyuv1
    }


}