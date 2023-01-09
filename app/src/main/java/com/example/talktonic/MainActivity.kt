package com.example.talktonic


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.talktonic.authenticate.LoginActivity
import com.example.talktonic.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit  var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Firebase.auth.currentUser == null){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }
        binding.btnLogout.setOnClickListener{
            Firebase.auth.signOut()
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }
    }


}