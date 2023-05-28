package com.example.learningproject.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.learningproject.Message
import com.example.learningproject.R
import com.example.learningproject.databinding.ChatLayoutBinding

class ChatAdapter(private val messageList: ArrayList<Message>,private val context: Context,private val activity: Activity)
    :RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

        private lateinit var binding:ChatLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       binding=ChatLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       with(holder){
           with(messageList[position]){

                if (messageList[position].isSender){
                   binding.textMessage.layoutDirection=View.LAYOUT_DIRECTION_RTL
                    binding.messageTime.layoutDirection=View.LAYOUT_DIRECTION_RTL
                    binding.textMessage.background=ContextCompat.getDrawable(context, R.drawable.button_background)
                    binding.textMessage.setTextColor(ContextCompat.getColor(context, R.color.white))
                }else{
                 binding.textMessage.layoutDirection=View.LAYOUT_DIRECTION_LTR
                    binding.messageTime.layoutDirection=View.LAYOUT_DIRECTION_LTR
                    binding.textMessage.background=ContextCompat.getDrawable(context, R.drawable.chat_tile)
                    binding.textMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_text))
                }
               binding.textMessage.text=messageList[position].message
               binding.messageTime.text=messageList[position].date

           }
       }
    }

    override fun getItemCount(): Int {
      return  messageList.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(binding: ChatLayoutBinding):RecyclerView.ViewHolder(binding.root)

}