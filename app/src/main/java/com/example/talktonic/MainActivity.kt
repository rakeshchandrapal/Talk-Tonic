package com.example.talktonic


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.talktonic.authenticate.LoginActivity
import com.example.talktonic.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit  var binding : ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Firebase.auth.currentUser == null){
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
            finish()
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
//        binding.btnLogout.setOnClickListener{
//            Firebase.auth.signOut()
//            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
//            finish()
//        }
    }


}