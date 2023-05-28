package com.example.learningproject

import com.example.learningproject.Message



import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.learningproject.adapter.ChatAdapter
import com.example.learningproject.adapter.MyAdapter
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.ActivityChatScreenBinding
import com.example.learningproject.util.Constant
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.type.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class ChatScreen() : AppCompatActivity() {


    private lateinit var binding:ActivityChatScreenBinding
    private var profileUrl=""
    private var phoneNumber=""
    private var name=""
    private var groupId=""
    private var receiverId=""
    private var appUserid=""
    private var  messageList:ArrayList<Message> =ArrayList()
    var db=FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatScreenBinding.inflate(layoutInflater)
        profileUrl=intent.getStringExtra("profile").toString()
        phoneNumber=intent.getStringExtra("phoneNumber").toString()
        name=intent.getStringExtra("name").toString()
        groupId=intent.getStringExtra("groupId").toString()
        receiverId=intent.getStringExtra("userId").toString()
        appUserid=PreferenceManager.getString(ConstantValue.userid).toString()

        Glide.with(this).load(profileUrl).centerCrop()
            .placeholder(R.drawable.userimg_removebg)
            .into(binding.profileView)

        binding.name.text=name


        CoroutineScope(Dispatchers.IO).launch{
            Log.d("idss", "onCreate: ${groupId}")
           if(groupId.isEmpty()){
               findGroupId {
                   createGroup()

               }

           }

        }
        binding.sendView.setOnClickListener {
           CoroutineScope(Dispatchers.IO).launch {
               sendMessage()
           }
        }
        setContentView(binding.root)

    }

        private fun createGroup(){
            val memberList= listOf(  appUserid,
                receiverId)


            val memberDetails:MutableMap<String,Any> = mutableMapOf()
            val mainMap:MutableMap<String,Any> = mutableMapOf()
            val senderId:MutableMap<String,Any> =mutableMapOf(
            "name" to PreferenceManager.getString(ConstantValue.name),
            "photoUrl" to PreferenceManager.getString(ConstantValue.profileImage),
            "unReadCount" to 0
            )

            val receiverIdMap:MutableMap<String,Any> = mutableMapOf(
                "name" to name,
                "photoUrl" to profileUrl,
                "unReadCount" to 0
            )
            memberDetails[appUserid] = senderId
            memberDetails[receiverId] = receiverIdMap

            mainMap["members"]=memberList
            mainMap["membersDetail"]=memberDetails

            db.collection(ConstantValue.GroupCollection).add(mainMap).addOnSuccessListener {
              groupId=it.id
            }
        }

        private fun findGroupId(Call:()->Unit){
           db.collection(ConstantValue.GroupCollection).whereArrayContainsAny("members", listOf(
               appUserid,
               receiverId
           )).let {
               it.get().addOnSuccessListener {
                  if(it.size()>0){
                      groupId= it.documents[0].id
                      getAllMessages()
                      Log.d("groupId", "findGroupId: ${groupId}")
                  }else{
                      Call.invoke()
                  }
               }.addOnFailureListener {
                   Call.invoke()
               }
           }
       }

        private fun sendMessage(){
             val message=binding.messageEdit.text.toString()
             val currentDate = System.currentTimeMillis()
             val senderId=appUserid
             val messageMap:MutableMap<String,String> = mutableMapOf(
                 "createdData" to currentDate.toString(),
                 "senderId" to senderId,
                 "message" to message

             )
             val random:MutableMap<String,String> = mutableMapOf(
                 "hello" to "hello"
             )
             db.collection(ConstantValue.userChats).document(groupId).set(random)
             db.collection(ConstantValue.userChats).document(groupId).collection(ConstantValue.messages).add(messageMap).addOnSuccessListener {
                 binding.messageEdit.text.clear()
             }
        }


       private fun getAllMessages(){
           messageList= ArrayList()
           db.collection(ConstantValue.userChats).document(groupId).collection(ConstantValue.messages).orderBy("createdData",
               ).addSnapshotListener { snapshot, error ->
               run {
                   snapshot.let {
                       messageList.clear()
                      if(it?.documents?.isNotEmpty() == true){

                          for(item in it.documents){
                              val timestamp=item?.get("createdData").toString().toLongOrNull()
                              val date= timestamp?.let { it1 -> Date(it1) }
                              val format = SimpleDateFormat("dd MMM h:mm a",Locale.getDefault())

                              val messageModel= Message(
                                  item?.get("message").toString(),
                                  appUserid==item?.get("senderId").toString(),
                                  format.format(date)

                              )
                              messageList.add(messageModel)

                          }
                          Log.d("messageList", "getAllMessages: ${messageList.size}")
                          val adapter= ChatAdapter(messageList,this,this)
                          binding.chatRecycleView.layoutManager= LinearLayoutManager(this)
                          binding.chatRecycleView.setHasFixedSize(true)

                          binding.chatRecycleView.adapter=adapter
                          val layoutManager = binding.chatRecycleView.layoutManager
                          val lastItemPosition=binding.chatRecycleView.adapter?.itemCount?.minus(1)?:0
                          layoutManager?.scrollToPosition(lastItemPosition)
                          adapter.notifyDataSetChanged()
                      }
                   }
               }
       }



}
    }