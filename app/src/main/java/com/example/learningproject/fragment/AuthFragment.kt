package com.example.learningproject.fragment


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.learningproject.databinding.FragmentAuthBinding
import com.example.learningproject.util.Constant
import com.hbb20.CountryCodePicker
import android.util.Log
import com.example.learningproject.HomeScreen
import com.example.learningproject.constant.ConstantValue
import com.example.learningproject.constant.PreferenceManager
import com.example.learningproject.util.LoadingDialog
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class AuthFragment : Fragment() {
    private lateinit var binding:FragmentAuthBinding
    private var auth:FirebaseAuth = FirebaseAuth.getInstance()
    private var database:FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var loader: LoadingDialog
    private  var verificationId:String=""
    private  var phoneNumber:String=""
    private var documentId:String=""
//    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAuthBinding.inflate(layoutInflater,container,false)
        loader= LoadingDialog(requireActivity(),"")
        addTextChangeListener()
        binding.getOtp.setOnClickListener {
            binding.emailEditText.visibility=View.GONE
            binding.otpLayout.visibility=View.VISIBLE
            binding.emailText.text="One Time Password"
            binding.getOtp.text="Verify otp"
        }

        binding.changePhoneNumber.setOnClickListener {
            binding.emailEditText.visibility=View.VISIBLE
            binding.otpLayout.visibility=View.GONE
            binding.emailText.text="Phone Number"
            binding.getOtp.text="Get otp"

        }
        binding.getOtp.setOnClickListener {
            if(phoneNumber.isEmpty()){
                verifyPhoneNumber()
            }else{
                        val typedOtp=binding.otpEditText1.text.toString()+
                       binding.otpEditText2.text.toString()+
                       binding.otpEditText3.text.toString()+
                       binding.otpEditText4.text.toString()+
                       binding.otpEditText5.text.toString()+
                       binding.otpEditText6.text.toString()

                if(typedOtp.isNotEmpty()){
                    if(typedOtp.length==6){
                        loader.setLoadingText("Wait...")
                        loader.startLoadingDialog()
                        val credential:PhoneAuthCredential=PhoneAuthProvider.getCredential(
                            verificationId,typedOtp
                        )
                        signInWithPhoneAuthCredential(credential)

                    }else{
                        Constant.showMsg("Please enter correct otp",requireContext())
                    }
                }else{
                    Constant.showMsg("Please enter otp",requireContext())
                }
            }

        }
        return binding.root


    }


    private fun addTextChangeListener(){
    binding.otpEditText1.addTextChangedListener(EditTextWatcher(binding.otpEditText1))
    binding.otpEditText2.addTextChangedListener(EditTextWatcher(binding.otpEditText2))
    binding.otpEditText3.addTextChangedListener(EditTextWatcher(binding.otpEditText3))
    binding.otpEditText4.addTextChangedListener(EditTextWatcher(binding.otpEditText4))
    binding.otpEditText5.addTextChangedListener(EditTextWatcher(binding.otpEditText5))
    binding.otpEditText6.addTextChangedListener(EditTextWatcher(binding.otpEditText6))
    }
    private fun verifyPhoneNumber(){
        val ccp: CountryCodePicker =binding.countryCodeLayout
        ccp.registerCarrierNumberEditText(binding.phoneNumberEdit)
        if(ccp.isValidFullNumber){
            checkNumberRegisterOrNot(ccp.fullNumberWithPlus.toString()){
                phoneNumber=ccp.fullNumberWithPlus.toString()
                loader.setLoadingText("Wait...")
                loader.startLoadingDialog()
                val options =  PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(ccp.fullNumberWithPlus)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(requireActivity())
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
            }else{

            context?.let {
                Constant.showMsg("This phone number is not valid. please enter correct number",
                    it
                )
            }
        }
    }

     private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            Log.d("this", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            loader.dismiss()
            Log.w("this", "onVerificationFailed", e)

//            if (e is FirebaseAuthInvalidCredentialsException) {
//                // Invalid request
//            } else if (e is FirebaseTooManyRequestsException) {
//                // The SMS quota for the project has been exceeded
//            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
//                // reCAPTCHA verification attempted with null Activity
//            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            Id: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            verificationId=Id
             loader.dismiss()
            binding.emailEditText.visibility=View.GONE
            binding.otpLayout.visibility=View.VISIBLE
            binding.emailText.text="One Time Password"
            binding.getOtp.text="Verify otp"

//            storedVerificationId = verificationId

//            resendToken = token
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    getDataFromFirestore()



                } else {
                    loader.dismiss()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        loader.dismiss()
                    }
                    binding.emailEditText.visibility=View.VISIBLE
                    binding.otpLayout.visibility=View.GONE
                    binding.emailText.text="Phone Number"
                    binding.getOtp.text="Get otp"

                }
            }
    }

    inner  class EditTextWatcher(private val view:View):TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text=p0.toString()
            when(view.id){
                binding.otpEditText1.id->if(text.isNotEmpty()) binding.otpEditText2.requestFocus()
                binding.otpEditText2.id->if(text.isNotEmpty()) binding.otpEditText3.requestFocus() else if (text.isEmpty()) binding.otpEditText1.requestFocus()
                binding.otpEditText3.id->if(text.isNotEmpty()) binding.otpEditText4.requestFocus() else if (text.isEmpty()) binding.otpEditText2.requestFocus()
                binding.otpEditText4.id->if(text.isNotEmpty()) binding.otpEditText5.requestFocus() else if (text.isEmpty()) binding.otpEditText3.requestFocus()
                binding.otpEditText5.id->if(text.isNotEmpty()) binding.otpEditText6.requestFocus() else if (text.isEmpty()) binding.otpEditText4.requestFocus()
                binding.otpEditText6.id-> if (text.isEmpty()) binding.otpEditText5.requestFocus()

            }
        }

    }

    private fun checkNumberRegisterOrNot(phoneNumber:String,call:()->Unit){
        loader.setLoadingText("Wait...")
        loader.startLoadingDialog()
        Log.d("Number is ", "checkNumberRegisterOrNot:$phoneNumber ")
        database.collection(ConstantValue.firebaseUserCollection)
            .whereEqualTo(ConstantValue.mobileNumber,phoneNumber)
            .get()
            .let {
                it.addOnSuccessListener {
                    if(it.documents.size>0){
                        call.invoke()
                        documentId=it.documents[0].id
                    }else{
                        Constant.showMsg("This number is not register in this app",requireContext())
                        loader.dismiss()
                    }
                }.addOnFailureListener {
                    Constant.showMsg("This number is not register in this app",requireContext())
                    loader.dismiss()
                }
        }
    }

    private fun getDataFromFirestore() {

        database.collection(ConstantValue.firebaseUserCollection).document(documentId).get()
            .addOnSuccessListener {

                val name: String = it.get("name").toString()
                val email: String = it.get("email").toString()
                val profileImage: String = it.get("profileUrl").toString()
                val mobileNumber: String = it.get(ConstantValue.mobileNumber).toString()
                val userId:String=it.getString(ConstantValue.userid).toString()
                PreferenceManager.setString(ConstantValue.name, name)
                PreferenceManager.setString(ConstantValue.email, email)
                PreferenceManager.setString(ConstantValue.profileImage, profileImage)
                PreferenceManager.setString(ConstantValue.mobileNumber, mobileNumber)
                PreferenceManager.setString(ConstantValue.userid,userId)
                loader.dismiss()
                val intent = Intent(requireActivity(), HomeScreen::class.java)
                startActivity(intent)

            }.addOnFailureListener {
                loader.dismiss()
//           binding.loginBtn.visibility= View.VISIBLE
//           binding.progressCircular.visibility=View.GONE
        }


    }






}