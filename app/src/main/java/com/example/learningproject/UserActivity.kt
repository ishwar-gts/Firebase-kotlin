package com.example.learningproject

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learningproject.adapter.MyAdapter
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.ActivityUserBinding
import com.example.learningproject.util.Constant
import com.example.learningproject.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserBinding

    private val db=FirebaseFirestore.getInstance()
    private lateinit var loader: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUserBinding.inflate(layoutInflater)
        loader= LoadingDialog(this,"")
        setContentView(binding.root)
        checkPermission()
        fetchUsers()

    }


    private fun fetchUsers(){
        loader.setLoadingText("Loading")
        loader.startLoadingDialog()
        val auth=FirebaseAuth.getInstance()
        Log.d("uid", "fetUsers: ${auth.uid}")
        val userList= ArrayList<User>()
        db.collection(ConstantValue.firebaseUserCollection).get().let {
            it.addOnSuccessListener {
                if (it.documents.size>0){
                   for(item in it.documents){
                       if(item.id!= PreferenceManager.getString(ConstantValue.userid)){
                           val name= item.get(ConstantValue.name).toString()
                           val profileImg= item.get("profileUrl").toString()
                           val phoneNumber= item.get("mobileNumber").toString()
                           val email=item.get("email").toString();
                           val userId=item.get(ConstantValue.userid).toString();
                           userList.add(
                               User(
                                   name,
                                   profileImg,
                                   false,
                                   phoneNumber,
                                   email,
                                   userId
                               ))
                       }


                   }

                }
                loader.dismiss()
                val adapter=MyAdapter(userList,this,this)
                binding.usersRecycleView.layoutManager=LinearLayoutManager(this)
                binding.usersRecycleView.setHasFixedSize(true)
                binding.usersRecycleView.adapter=adapter
               // adapter.notifyDataSetChanged()

            }

        }

    }

    private fun readDeviceContact(){

        val userList = ArrayList<User>()
        userList.clear()
        val contentResolver = this.contentResolver
        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        val nameIndex = contacts?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) ?: -1
        val photoIndex = contacts?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
        val mobileNumber = contacts?.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        if (nameIndex >= 0) {

            while (contacts?.moveToNext() == true) {
                val name = contacts.getString(nameIndex)
                val photoUriString = contacts.getString(photoIndex ?: -1)
                val photoUri = photoUriString?.let { Uri.parse(it) }
                val phoneNumber = contacts.getString(mobileNumber?:-1)


                val user = User(name, photoUri.toString(),true,phoneNumber,"","")
                if (!userList.contains(user)) {
                    userList.add(user)
                }


            }
            val adapter=MyAdapter(userList,this,this)
            binding.inviteRecycleView.layoutManager=LinearLayoutManager(this)
            binding.inviteRecycleView.setHasFixedSize(true)
            binding.inviteRecycleView.adapter=adapter
            adapter.notifyDataSetChanged()


        } else {
            Log.e("Error", "DISPLAY_NAME column not found")
        }
        contacts?.close()



    }

    private fun checkPermission(){
        val READ_CONTACTS_PERMISSION_CODE = 123

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            readDeviceContact()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSION_CODE
            )
        }



    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val READ_CONTACTS_PERMISSION_CODE = 123
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                readDeviceContact()
            } else {
               Constant.showMsg("Read Contacts permission denied",this)

            }
        }

    }



}