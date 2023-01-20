package com.example.talktonic

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.talktonic.databinding.ActivityGetUserDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import com.bumptech.glide.Glide

class GetUserDetailActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGetUserDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference

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

        binding.profileImg.setOnClickListener{
            pickImage.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.saveDetailBtn.setOnClickListener{
            Log.d("GetUserDetail",binding.userName.toString())
            Log.d("GetUserDetail",binding.userAbout.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}