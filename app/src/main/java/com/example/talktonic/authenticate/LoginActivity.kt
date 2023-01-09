package com.example.talktonic.authenticate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.talktonic.MainActivity
import com.example.talktonic.R
import com.example.talktonic.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var binding: ActivityLoginBinding
    private var mobileNumber = ""
    private lateinit var auth: FirebaseAuth

    lateinit var verificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSendOtp.setOnClickListener(){
            login()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(e: PhoneAuthCredential) {
                startActivity(Intent(applicationContext,MainActivity::class.java))
                finish()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // displaying error message with firebase exception.
                Log.d(TAG,"Verification is failed ",e)
                Toast.makeText(this@LoginActivity,"Verification is Failed ",Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(s: String, Token : PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(s, Token)

                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s
                resendToken = Token

                val intent = Intent(applicationContext,OtpActivity::class.java)
                intent.putExtra("verificationId",verificationId)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun login() {
        mobileNumber = binding.mobileNumber.text.toString()
        if(mobileNumber.isEmpty()){
            Log.d(TAG,"Mobile Number is Empty")
            Toast.makeText(this@LoginActivity,"Please Enter Valid Mobile Number",Toast.LENGTH_SHORT).show()
        }
        else{
            mobileNumber = "+91$mobileNumber"
            Log.d(TAG,mobileNumber)
            sendVerificationCode(mobileNumber)
        }
    }

    private fun sendVerificationCode(mobileNumber: String) {
        val options =
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(mobileNumber)       // this is to verify the phone-number
                    .setTimeout(60L,TimeUnit.SECONDS) // Time Out and unit
                    .setActivity(this)      // Activity (for callback binding)
                    .setCallbacks(callbacks)  // OnVerificationStateChangedCallbacks
                    .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d(TAG,"Verification is Started")

    }


}