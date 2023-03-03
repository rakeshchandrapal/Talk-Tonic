package com.example.talktonic.authenticate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.talktonic.MainActivity
import com.example.talktonic.userDetails.GetUserDetailActivity
import com.example.talktonic.databinding.ActivityOtpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OtpActivity : AppCompatActivity() {

    private  val TAG = "OtpActivity"
    private lateinit var binding: ActivityOtpBinding
    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // get VerificationId from the intent
        val verificationId = intent.getStringExtra("verificationId")

        // fill otp and call the on click on button
        binding.btnVerifyOtp.setOnClickListener{
            val otp =binding.otpNumber.text.toString()

            if(otp.isEmpty()){
                Log.d(TAG,"Empty OTP")
                Toast.makeText(this@OtpActivity,"Please Enter OTP",Toast.LENGTH_SHORT).show()
            }
            else{
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    verificationId.toString(),otp
                )
                signInWithPhoneAuthCredential(credential)
            }
        }

    }

    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

            auth.signInWithCredential(credential)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Log.d(TAG,"LogIn : $credential")

                        Log.d(TAG, "Log in SuccessFull and get userdetail..")
//                        val userName = FirebaseAuth.getInstance().currentUser!!.displayName
//                        val isDetailNotAvailable = userName == null || userName == "null" || userName == ""
//                        if(isDetailNotAvailable) {
//                            Log.d(TAG, "Details is not available: $userName")
//                            startActivity(Intent(this, GetUserDetailActivity::class.java))
//                            finish()
//                        }
//                        else{
//                            Log.d(TAG, userName.toString())
//                            Log.d(TAG , "Detail is available ")
//                            startActivity(Intent(this, MainActivity::class.java))
//                            finish()
//                        }
                        db.collection("users")
                            .document(auth.currentUser!!.phoneNumber.toString()).get().addOnCompleteListener{  task ->
                                if(task.isSuccessful){
                                    if(task.result.exists()){
//                                        Log.d(TAG, userName.toString())
                                        Log.d(TAG , "Detail is available ")
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    else
                                    {
                                        Log.d(TAG, "Details is not available: ")
                                        startActivity(Intent(this, GetUserDetailActivity::class.java))
                                        finish()
                                    }
                                }

                            }

                    }
                    else {
                        // Sign in failed, display a message and update the UI
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

    }
}