package com.example.learningproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.learningproject.RecentMessage
import com.example.learningproject.UserActivity
import com.example.learningproject.adapter.RecentMessageAdapter
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.FragmentChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatFragment : Fragment() {




    private var groupList:ArrayList<RecentMessage> =ArrayList()
    private var db=FirebaseFirestore.getInstance()
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
        binding.addButton.setOnClickListener {
            startActivity(Intent(activity,UserActivity::class.java))
        }

       CoroutineScope(Dispatchers.IO).launch {
           getRecentMessage()
       }
        return binding.root
    }



    private fun initUserData(){
        val name:String= PreferenceManager.getString(ConstantValue.name)
        val profile:String= PreferenceManager.getString(ConstantValue.profileImage)
        Log.d("userid", "initUserData: ${PreferenceManager.getString(ConstantValue.userid)}")
        Glide.with(this).load(profile).centerCrop().into(binding.profileView)
        binding.name.text=name
    }

    private fun getRecentMessage(){
        val userid=PreferenceManager.getString(ConstantValue.userid)
        db.collection(ConstantValue.GroupCollection).whereArrayContains("members",userid).addSnapshotListener { data, error ->
            data.let {
                groupList=ArrayList()
                if(data?.documents?.isNotEmpty() == true){
                    for(item in data.documents){
                        var messageMap=item.get("lastMessage") as Map<*, *>
                        var membersDetailMap=item.get("membersDetail") as Map<*,*>
                        var message=messageMap.get("message").toString()
                        var name=""
                        var profileUrl=""
                        var readCount=""
                        val messageTime=messageMap["date"]
                        var userId=""
                        val groupId=item.id
                        var receMap: Map<Any?, Any?>?
                        for(myData in membersDetailMap){
                             if(myData.key.toString()!=userid){
                                 userId=myData.key.toString()
                                 Log.d("userId", "getRecentMessage: $userId")
                                 receMap =  myData.value as Map<Any?, Any?>?
                                 name=receMap?.get("name").toString()
                                 profileUrl= receMap?.get("photoUrl").toString()
                                 readCount= receMap?.get("unReadCount").toString()


                           }
                        }
                        val timestamp=messageTime.toString().toLongOrNull()
                        val date= timestamp?.let { it1 -> Date(it1) }
                        val format = SimpleDateFormat("dd MMM h:mm a", Locale.getDefault())
                        val recentMessage=RecentMessage(
                            message,
                            name,
                            readCount,
                            profileUrl,
                            format.format(date),
                            userId,
                            groupId


                        )
                        groupList.add(recentMessage)



                    }

                    val adapter= context?.let { it1 -> RecentMessageAdapter(groupList, it1) }
                    binding.recentMsgRecycleView.layoutManager=LinearLayoutManager(context)
                    binding.recentMsgRecycleView.adapter=adapter
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }




}