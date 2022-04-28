package com.example.drill2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.drill2.databinding.FragmentGetLocBinding
import com.example.drill2.databinding.FragmentPermissionNotApprovedBinding

class PermissionNotApproved : Fragment() {

    private var _binding: FragmentPermissionNotApprovedBinding? = null

    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentPermissionNotApprovedBinding.inflate(inflater,container,false)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        binding.settingsBtn.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context?.packageName}")
            }
            activity?.startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}