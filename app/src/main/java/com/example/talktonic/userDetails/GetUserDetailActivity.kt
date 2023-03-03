package com.example.talktonic.userDetails

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.talktonic.databinding.ActivityGetUserDetailBinding
import com.google.firebase.storage.StorageReference
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import com.bumptech.glide.Glide
import com.example.talktonic.MainActivity
import com.example.talktonic.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class GetUserDetailActivity : AppCompatActivity() {

    private val TAG = "GetUserDetail"
    private lateinit var binding : ActivityGetUserDetailBinding
    private lateinit var storageRef: StorageReference
    private val db = Firebase.firestore
    private val currentUser = Firebase.auth.currentUser

    private var newUri: Uri = Uri.EMPTY

    private var pickImage = registerForActivityResult(PickVisualMedia()){
        if(it != null){
            newUri = it
            onImageSelected(newUri)
        }
    }

    private fun onImageSelected(newUri: Uri) {
        Glide.with(binding.profileImg.context).load(newUri).into(binding.profileImg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGetUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = Firebase.storage.reference

        binding.profileImg.setOnClickListener{
            pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.saveDetailBtn.setOnClickListener{
            Log.d(TAG ,binding.userName.toString())
            Log.d(TAG ,binding.userAbout.toString())
            addUserInFireStore()

        }

    }

    private fun addUserInFireStore() {
        Log.d(TAG,"adding details in firebase...")

        val addUser = User(
            currentUser?.uid,
            binding.userName.text.toString(),
            binding.userAbout.text.toString(),
            currentUser?.phoneNumber,
            ""
        )

        if(currentUser != null && !areDetailsIsInValid()){


            Log.d(TAG, "Are details invalid: ${areDetailsIsInValid()}")

            db.collection("users")
                .document(currentUser.phoneNumber.toString())
                .set(addUser)
                .addOnSuccessListener {
                    Log.d(TAG,"User detail is uploaded successfully ")

                    if (newUri != Uri.EMPTY) {
                        uploadProfilePhoto()
                    }
                    val userUpdatedProfile = userProfileChangeRequest {
                        displayName = binding.userName.text.toString()
                    }
                    currentUser.updateProfile(userUpdatedProfile)
                        .addOnSuccessListener {
                            Log.d(TAG ,"Updated user profile")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "user profile not updated")
                        }
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener{
                    Log.d(TAG, "user detail not add in fireStore $it")
                }
        }
        else {
            Toast.makeText(this, "Please enter a name and about", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadProfilePhoto() {
        storageRef.child("User Profiles/${currentUser?.uid}.png")
            .putFile(newUri)
            .addOnFailureListener{
                Log.d(TAG,"NOT SUCCESSFULL",it)
            }
            .addOnSuccessListener { takeSnapShot ->
                takeSnapShot.metadata?.reference?.downloadUrl
                    ?.addOnSuccessListener { uri ->
                        Firebase.firestore.collection("users")
                            .document(currentUser?.phoneNumber.toString())
                            .update("userPhotoUri",uri)
                            .addOnSuccessListener {
                                Log.d(TAG, "User photo upload")
                                val profileUpdate = userProfileChangeRequest { photoUri = uri }
                                currentUser?.updateProfile(profileUpdate)
                                    ?.addOnSuccessListener {
                                        Log.d(TAG,"User Profile Photo is updated SuccessFully")
                                    }

                            }
                            .addOnFailureListener{
                                Log.d(TAG,"User Profile Photo is not updated ")
                            }

                    }

            }.addOnFailureListener {
                Log.w(
                    TAG,
                    "Photo not store in firebase storage --> Task unsuccessful",
                    it
                )
            }
    }

    private fun areDetailsIsInValid(): Boolean {
        val newUserName = binding.userName.text.toString().trim()
        val newUserAbout = binding.userAbout.text.toString().trim()
        return newUserName == "" || newUserName == "null"
                || newUserAbout == "" || newUserAbout == "null"
    }
}