package com.example.talktonic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.talktonic.databinding.FragmentMainBinding



class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentMainBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment

        binding.miniProfileImg.setOnClickListener{
            findNavController().navigate(R.id.action_mainFragment_to_showUserDetailFragment2)
        }
        return binding.root
    }


}