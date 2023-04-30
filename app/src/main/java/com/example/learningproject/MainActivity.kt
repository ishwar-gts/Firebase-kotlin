package com.example.learningproject

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.learningproject.constant.Constant
import com.example.learningproject.constant.PreferenceManager

import com.example.learningproject.databinding.ActivityMainBinding
import com.example.learningproject.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hbb20.CountryCodePicker
import kotlinx.coroutines.awaitAll
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private val loader:LoadingDialog= LoadingDialog(this,"");
private lateinit var loader:LoadingDialog
    private val auth=FirebaseAuth.getInstance()
    private val storage= FirebaseStorage.getInstance()
    private val dataBase= FirebaseFirestore.getInstance()
    private var profileUrl=""

    private val requestPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        it?.let {
           if (it){
               Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
           }
        }
    }

    private val fileAccess=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        it.data?.data?.let {
            val inputStream=this.contentResolver.openInputStream(it)
            inputStream?.readBytes()?.let {
                uploadFile(it)
            }

        }
    }


    override fun onStart() {
        PreferenceManager.initLocalDatabase(this)
        val name:String=PreferenceManager.getString(Constant.name)
        if(name.isNotEmpty() && name!="user"){
            startActivity(Intent(this,LoginActivity::class.java))
        }

        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.initLocalDatabase(this)
       loader= LoadingDialog(this,"")
        binding.signUpButton.setOnClickListener {
            registerUser()
        }

        binding.profileView.setOnClickListener {
            sdkIntOverO(this){
                val intent=Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type="*/*"
                fileAccess.launch(intent)

            }
        }
        binding.AlreadyAccountText.setOnClickListener {
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }



    }



    private fun registerUser(){
        val ccp:CountryCodePicker=binding.countryCodeLayout
        val email=binding.emailEditText.text.toString()
        val password=binding.passwordEditTest.text.toString()
        val name=binding.nameEditText.text.toString()
        val confirmPassword=binding.confirmPasswordEditTest.text.toString()
        ccp.registerCarrierNumberEditText(binding.phoneNumberEdit)


        when{
            name.isEmpty()->{
                binding.nameEditText.requestFocus()
                showMsg("Please enter your name")
            }

            email.isEmpty()->{
                binding.emailEditText.requestFocus()
                showMsg("Please enter email")}

            !ccp.isValidFullNumber->{
                binding.phoneNumberEdit.requestFocus()
                showMsg("Please enter correct phone number")
            }
            password.isEmpty() || password.length<=6->{
                binding.passwordEditTest.requestFocus()
                showMsg("Please enter correct password")

            }
            password!=confirmPassword->{
                binding.confirmPasswordEditTest.requestFocus()
                showMsg("Confirm Password Not Matched")
            }
            profileUrl.isEmpty()->{
            Toast.makeText(this, "profile image is required", Toast.LENGTH_SHORT).show()
        }




            else->{
                checkEmailExit {
                    checkPhoneNumberExit(ccp.fullNumber){
                        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                            loader.dismiss()

                        }.addOnCanceledListener {
                            loader.dismiss()
                            Toast.makeText(this, "Sign Up Canceled", Toast.LENGTH_SHORT).show()

                        }.addOnSuccessListener {

                            uploadUserInfoToFirestore(binding.nameEditText.text.toString(),binding.emailEditText.text.toString(),binding.passwordEditTest.text.toString(),it.user!!.uid,ccp.fullNumber)
                            Toast.makeText(this, it.user?.email, Toast.LENGTH_SHORT).show()

                        }.addOnFailureListener{
                            loader.dismiss()
                            Log.d("Failure","failred reason ${it.toString()}")
                        }
                    }
                    }
//            loader.setLoadingText("wait...")
//            loader.startLoadingDialog()

        }

        }
    }

    private fun uploadFile(byteArray: ByteArray){

        loader.setLoadingText("Uploading...")
        loader.startLoadingDialog()
        val storageRef=storage.reference
        storageRef.child("images/${Date().time}").putBytes(byteArray).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).centerCrop().into(binding.profileView)
                profileUrl=it.toString()
                runOnUiThread {
                    loader.dismiss()
                }

            }


            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            runOnUiThread {
                loader.dismiss()
            }
            Toast.makeText(this, "failure", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener{
            runOnUiThread {
                loader.dismiss()
            }
            Toast.makeText(this, "complete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun  sdkIntOverO(context: Context, call:()->Unit){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                call.invoke()
            }else{
                requestPermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun uploadUserInfoToFirestore(name:String,email:String,password:String,docId:String,phoneNumber: String){
        val map= mutableMapOf<String,String>()
        map.put("name",name)
        map.put("email",email)
        map.put("password",password)
        map.put("mobileNumber",phoneNumber)
        map.put("profileUrl",profileUrl)
        dataBase.collection(Constant.firebaseUserCollection).document(docId).set(map).addOnSuccessListener {
            Toast.makeText(this, "Data Added Successfully", Toast.LENGTH_SHORT).show()
            PreferenceManager.setString(Constant.name,name)
            PreferenceManager.setString(Constant.email,email)
            PreferenceManager.setString(Constant.profileImage,profileUrl)
            PreferenceManager.setString(Constant.mobileNumber,phoneNumber)
            Log.d("Preference Manager Data", "Local Database: ${PreferenceManager.getString(Constant.email)}")
            loader.dismiss()
//            val intent=Intent(this,LoginActivity::class.java)
//            startActivity(intent)
        }.addOnCompleteListener{
            loader.dismiss()
            Toast.makeText(this, "Task Complete", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener{
            loader.dismiss()
            Toast.makeText(this, "Task Upload Failed ${it.toString()}", Toast.LENGTH_SHORT).show()
            Log.d("Failure","failred reason ${it.toString()}")

            binding.signUpButton.visibility= View.VISIBLE
        }


    }

    private fun showMsg(msg:String){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

   private fun checkEmailExit(call:()->Unit){
       loader.setLoadingText("wait...")
       loader.startLoadingDialog()

       dataBase.collection(Constant.firebaseUserCollection).whereEqualTo(Constant.email,binding.emailEditText.text.toString()).get().let {
           it.addOnSuccessListener {
               if(it.documents.size>0){

                   loader.dismiss()
                   showMsg("email already exist")
               }else{

                   call.invoke()
               }
           }.addOnFailureListener {
               loader.dismiss()
               showMsg("Exception is $it")
           } .addOnCompleteListener{

           }

       }


    }

    private fun checkPhoneNumberExit(phoneNumber: String,call:()->Unit){

       dataBase.collection(Constant.firebaseUserCollection).whereEqualTo(Constant.mobileNumber,phoneNumber).get().let {
           it.addOnSuccessListener {
               if(it.documents.size>0){

                   loader.dismiss()
                   showMsg("Phone number already exist")
               }else{

                   call.invoke()
               }

           }.addOnFailureListener {
               showMsg("Exception is $it")

               loader.dismiss()
           } .addOnCompleteListener{

           }

       }

    }




}




