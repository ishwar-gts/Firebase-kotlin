package com.example.learningproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.learningproject.constant.Constant
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val auth=FirebaseAuth.getInstance()
    private lateinit var binding:ActivityLoginBinding
    private val database=FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.AlreadyAccountText.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.loginBtn.setOnClickListener {
            val email=binding.emailEditText.text.toString()
            val password=binding.passwordEditTest.text.toString()
            when{
               email.isEmpty()->{
                   binding.emailEditText.error="Email Required"
               } password.isEmpty()->{
                binding.passwordEditTest.error="Password Required"
               } else->{

                   loginToAccount(email,password)
               }
            }
        }



    }

    private  fun loginToAccount(email:String,password:String){
        binding.loginBtn.visibility= View.GONE
        binding.progressCircular.visibility=View.VISIBLE
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
            Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
            getDataFromFirestore(it.user!!.uid)
        }.addOnCompleteListener{
            Toast.makeText(this, "Login Task Complete", Toast.LENGTH_SHORT).show()
            binding.loginBtn.visibility= View.VISIBLE
            binding.progressCircular.visibility=View.GONE
        }.addOnCanceledListener {
            Toast.makeText(this, "Login Canceled ", Toast.LENGTH_SHORT).show()
            binding.loginBtn.visibility= View.VISIBLE
            binding.progressCircular.visibility=View.GONE

        }.addOnFailureListener {
            Log.d("Login Failure Listener", "loginToAccount: $it")
            Toast.makeText(this, "Login Failed ${it} ", Toast.LENGTH_SHORT).show()
            binding.loginBtn.visibility= View.VISIBLE
            binding.progressCircular.visibility=View.GONE
        }
    }

    private fun getDataFromFirestore(docId:String){
       database.collection(Constant.firebaseUserCollection).document(docId).get().addOnSuccessListener {

           val name:String=it.get("name").toString()
           val email:String=it.get("email").toString()
           val profileImage:String=it.get("profileUrl").toString()

           PreferenceManager.setString(Constant.name,name)
           PreferenceManager.setString(Constant.email,email)
           PreferenceManager.setString(Constant.profileImage,profileImage)
           Log.d("Preference Manager Data", "Local Database: ${PreferenceManager.getString(Constant.email)}")
           binding.loginBtn.visibility= View.VISIBLE
           binding.progressCircular.visibility=View.GONE
           startActivity(Intent(this,HomeScreen::class.java))

       }.addOnFailureListener {
           binding.loginBtn.visibility= View.VISIBLE
           binding.progressCircular.visibility=View.GONE
       }
    }


}