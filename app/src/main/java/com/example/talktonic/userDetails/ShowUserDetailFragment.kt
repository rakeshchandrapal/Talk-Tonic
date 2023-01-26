package com.example.talktonic.userDetails

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.talktonic.R
import com.example.talktonic.authenticate.LoginActivity
import com.example.talktonic.databinding.FragmentShowUserDetailBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream

class ShowUserDetailFragment : Fragment() {
    private val TAG = "ShowUserDetailFragment"
    private lateinit var  binding : FragmentShowUserDetailBinding

    private lateinit var firebaseRef : FirebaseStorage
    private lateinit var storage: StorageReference

   private val currentUser = Firebase.auth.currentUser

    private var userName: String = ""
    private var userAbout: String = ""
    private var userPhotoUrl: String = currentUser?.photoUrl.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentShowUserDetailBinding.inflate(layoutInflater,container,false)

        loadUserDetail()
        loadProfilePhoto()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRef = FirebaseStorage.getInstance()
        storage = firebaseRef.reference


        binding.signOutButton.setOnClickListener{
            signOut()
        }

    }


    private fun loadUserDetail() {
        Firebase.firestore.collection("users")
            .document(currentUser?.phoneNumber.toString())
            .addSnapshotListener { snapshot, e ->

                if (e != null) {
                    Log.d("TAG", "Error occurred in fetching user", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    userName = snapshot["userName"].toString()
                    binding.userNameProfile.text = userName

                    userAbout = snapshot["userAbout"].toString()
                    binding.aboutTextProfile.text = userAbout

                    binding.phoneNumberText.text = snapshot["userPhoneNumber"].toString()
                } else {
                    Log.d("TAG", "User details not available in firebase")
                }

            }
    }


    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        activity?.finish()
    }

    private fun  loadProfilePhoto(){
        if(currentUser != null) {
            Log.d(TAG,"Loading From FireBase ${currentUser.uid.toString() } hii ")

            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(
                "gs://talk-tonic-88858.appspot.com/User Profiles/${currentUser.uid}.png")


            val option = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

            storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Glide.with(requireContext())
                    .load(bitmap)
                    .apply(option)
                    .error(R.drawable.ic_user_default)
                    .into(binding.userPhotoProfile)

                    Log.d(TAG,"Load profile image SuccessFully ")
            }
                .addOnFailureListener{
                    Log.d(TAG,"Can not load profile image ")
                }
        }
    }
}