package com.example.learningproject.adapter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningproject.ChatScreen
import com.example.learningproject.R
import com.example.learningproject.User
import com.example.learningproject.databinding.UserLayoutBinding
import com.example.learningproject.util.Constant
import com.example.learningproject.util.LoadingDialog

class MyAdapter(private val userList:ArrayList<User>,private val context:Context,private val activity: Activity):RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private lateinit var binding: UserLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding=UserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("img", "onBindViewHolder: ${userList[position].profileImg}")
        with(holder){
            with(userList[position]){
                    binding.userName.text=userList[position].name
                    if(userList[position].showInviteText){
                        binding.inviteText.visibility=View.VISIBLE
                    }else{
                        binding.inviteText.visibility=View.GONE
                    }
                Glide.with(context).load(userList[position].profileImg).centerCrop()
                    .placeholder(R.drawable.userimg_removebg)
                    .into(binding.profileView)
                binding.inviteText.setOnClickListener {
                    checkPermission( userList[position].phoneNumber)
//                    sendInviteMessage(
//                        userList[position].phoneNumber
//                    )
                }
                binding.mainCard.setOnClickListener {
                    if(binding.inviteText.visibility==View.GONE){
                        val bundle = Bundle()
                        val intent=Intent(activity,ChatScreen::class.java)
                        bundle.putString("name",userList[position].name)
                        bundle.putString("profile",userList[position].profileImg)
                        bundle.putString("phoneNumber",userList[position].phoneNumber)
                        bundle.putString("email",userList[position].email)
                        bundle.putString("userId",userList[position].userId)
                        bundle.putString("groupId","")

                        intent.putExtras(bundle)
                        activity.startActivity(intent)
                    }
                }


            }
        }
    }

    override fun getItemCount(): Int {
      return  userList.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    private fun sendInviteMessage(phoneNumber: String){
        val loader: LoadingDialog = LoadingDialog(activity,"")
        loader.setLoadingText("Wait")
        loader.startLoadingDialog()
        val smsManager:SmsManager
        if(Build.VERSION.SDK_INT>=23){
            smsManager=context.getSystemService(SmsManager::class.java)
        }else{
            smsManager=SmsManager.getDefault()
        }

        smsManager.sendTextMessage(phoneNumber,null,"Download my app to chat this is advertisement text for testing sms service in app ",null,null)
        loader.dismiss()
        Constant.showMsg("Invitation Sent",context)



    }


   inner class ViewHolder(binding: UserLayoutBinding): RecyclerView.ViewHolder(binding.root)

    private fun checkPermission(phoneNumber: String){
         val MY_PERMISSIONS_REQUEST_SEND_SMS = 0

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.SEND_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS)
        } else {
           sendInviteMessage(phoneNumber)
        }
    }






}

