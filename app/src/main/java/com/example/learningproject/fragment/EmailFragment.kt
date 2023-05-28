package com.example.learningproject.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.learningproject.HomeScreen
import com.example.learningproject.R
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.databinding.FragmentEmailBinding
import com.example.learningproject.util.Constant
import com.example.learningproject.util.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class EmailFragment : Fragment() {
    private lateinit var binding: FragmentEmailBinding
    private val database:FirebaseFirestore=FirebaseFirestore.getInstance()
    private val auth:FirebaseAuth=FirebaseAuth.getInstance()
    private lateinit var loader: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailBinding.inflate(layoutInflater, container, false)
        loader= LoadingDialog(requireActivity(),"")
        binding.loginBtn.setOnClickListener {
            if (binding.emailEditText.text.isEmpty()) {
                Constant.showMsg("Please enter email", requireContext())
            } else {
                binding.emailText.text = "Password"
                binding.emailEditText.visibility = View.GONE

                binding.passwordEditTest.visibility = View.VISIBLE
                val email=binding.emailEditText.text.toString()
                val password=binding.passwordEditTest.text.toString()

                if(password.isNotEmpty() && password.length>6){
                    loginToAccount(email,password)
                }else{
                    Constant.showMsg("Please Enter correct Password",requireContext())
                }
            }
        }
        return binding.root
    }



    private fun loginToAccount(email: String, password: String) {
        loader.setLoadingText("wait...")
        loader.startLoadingDialog()
       auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Constant.showMsg("Login Successfully",requireContext())
            getDataFromFirestore(it.user!!.uid)
        }.addOnCompleteListener {
            Constant.showMsg("Login Task Complete",requireContext())


        }.addOnCanceledListener {
           loader.dismiss()
            Constant.showMsg("Login Canceled",requireContext())


        }.addOnFailureListener {
           loader.dismiss()
            Constant.showMsg("Login Failure Listener\", \"loginToAccount: $it",requireContext())

        }
    }

    private fun getDataFromFirestore(docId: String) {
        database.collection(ConstantValue.firebaseUserCollection).document(docId).get()
            .addOnSuccessListener {

                val name: String = it.get("name").toString()
                val email: String = it.get("email").toString()
                val profileImage: String = it.get("profileUrl").toString()
                val mobileNumber: String = it.get(ConstantValue.mobileNumber).toString()
                val userId:String=it.get(ConstantValue.userid).toString()
                Log.d("Login uid ", "getDataFromFirestore: $userId")
                PreferenceManager.setString(ConstantValue.name, name)
                PreferenceManager.setString(ConstantValue.email, email)
                PreferenceManager.setString(ConstantValue.profileImage, profileImage)
                PreferenceManager.setString(ConstantValue.mobileNumber, mobileNumber)
                PreferenceManager.setString(ConstantValue.userid,userId)
                Log.d(
                    "Preference Manager Data",
                    "Local Database: ${PreferenceManager.getString(ConstantValue.email)}"
                )
                loader.dismiss()
                startActivity(Intent(requireActivity(), HomeScreen::class.java))

            }.addOnFailureListener {
                loader.dismiss()
        }
    }
}




