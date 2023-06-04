package com.example.learningproject.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learningproject.ChatScreen
import com.example.learningproject.R
import com.example.learningproject.RecentMessage
import com.example.learningproject.databinding.HomeChatTitleBinding

class RecentMessageAdapter(private val groupList: ArrayList<RecentMessage>,private val context: Context):RecyclerView.Adapter<RecentMessageAdapter.ViewHolder>() {
    private lateinit var   binding:HomeChatTitleBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding=HomeChatTitleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        with(holder){
            with((groupList[position])){
                binding.userName.text=groupList[position].name
                binding.messageText.text=groupList[position].message
                binding.time.text=groupList[position].time
                if(groupList[position].unreadCount=="0"){
                    binding.unreadCount.visibility= View.GONE
                }else{
                    binding.unreadCount.text=groupList[position].unreadCount
                }


                Glide.with(context).load(groupList[position].profileUrl).centerCrop()
                    .placeholder(R.drawable.userimg_removebg)
                    .into(binding.profileView)

                binding.mainCard.setOnClickListener {

                    val bundle = Bundle()
                    val intent= Intent(context, ChatScreen::class.java)
                    bundle.putString("name",groupList[position].name)
                    bundle.putString("profile",groupList[position].profileUrl)
                    bundle.putString("phoneNumber","")
                    bundle.putString("email","")
                    bundle.putString("userId",groupList[position].userId)
                    bundle.putString("groupId",groupList[position].groupId)

                    intent.putExtras(bundle)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    inner class ViewHolder(binding:HomeChatTitleBinding):RecyclerView.ViewHolder(binding.root)


}