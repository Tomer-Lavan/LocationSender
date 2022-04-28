package com.example.drill2

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.VolumeShaper
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.drill2.databinding.FragmentGetLocBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

class GetLocFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()

    private var _binding: FragmentGetLocBinding? = null

    private val binding get() = _binding!!

    var btnLocState = 0

    var btnShareState = 0

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionLauncher  = requestLocPermission()

    @RequiresApi(Build.VERSION_CODES.N)
    val contactsPermissionLauncher  = requestContactPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentGetLocBinding.inflate(inflater,container,false)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        binding.linearLayout.setOnClickListener {
            checkLocationPermission(fusedLocationProviderClient)
        }

        viewModel.address.observe(viewLifecycleOwner) {
            createViews(it)
        }

        return binding.root
    }

    private fun changeButtonColor() {
        if (btnLocState == 0) {
            binding.linearLayout.background = resources.getDrawable(R.drawable.my_loc_pressed)
            binding.imageView.background = resources.getDrawable(R.drawable.ic_location_2)
            binding.textView.setTextColor(resources.getColor(R.color.pale_blue))
            btnLocState = 1
        } else {
            binding.linearLayout.background = resources.getDrawable(R.drawable.my_loc_not_pressed)
            binding.imageView.background = resources.getDrawable(R.drawable.ic_location)
            binding.textView.setTextColor(resources.getColor(R.color.white))
            btnLocState = 0
        }
    }

    private fun createViews(address : String) {
        binding.yourLoc.text = "${resources.getString(R.string.your_location_is)}\n${address}\n"
        if (btnShareState == 0) {
            val shareButton = Button(this.requireContext())
            shareButton.text = resources.getString(R.string.share)
            shareButton.background = resources.getDrawable(R.drawable.btn_back)
            val orientationConfigur = resources.configuration.orientation
            if (orientationConfigur == Configuration.ORIENTATION_LANDSCAPE) {
                binding.textAndBtn?.addView(shareButton)
            }
            else {
                binding.allViews.addView(shareButton)
            }
            btnShareState = 1
            shareButton.setOnClickListener {
                checkContactsPermission()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestLocPermission(): ActivityResultLauncher<Array<String>> {
        val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    getLoc(fusedLocationProviderClient)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    findNavController().navigate(R.id.action_getLocFragment_to_permissionNotApproved)
                } else -> {
                    findNavController().navigate(R.id.action_getLocFragment_to_permissionNotApproved)
                }
            }
        }
        return locationPermissionLauncher
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestContactPermission(): ActivityResultLauncher<Array<String>> {
        val contactsPermissionLauncher  = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.READ_CONTACTS, false) -> {
                    btnShareState = 0
                    findNavController().navigate(R.id.action_getLocFragment_to_contactsList)
                } else -> {
                  notApprovedContactsPermisDialog()
                }
            }
        }
        return contactsPermissionLauncher
    }

    private fun checkLocationPermission(fusedLocationProviderClient: FusedLocationProviderClient) {
        if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        } else  {
            getLoc(fusedLocationProviderClient)
        }
    }

    private fun checkContactsPermission() {
        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            contactsPermissionLauncher.launch(arrayOf(Manifest.permission.READ_CONTACTS))
        } else {
            btnShareState = 0
            findNavController().navigate(R.id.action_getLocFragment_to_contactsList)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLoc(fusedLocationProviderClient: FusedLocationProviderClient) {
        val geocoder : Geocoder = Geocoder(this.requireContext(), Locale.getDefault())
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if(it != null){
                var addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                var address = addresses.get(0).getAddressLine(0)
                viewModel.setAddress(address)
                createViews(address)
            }
        }
        changeButtonColor()
    }

    private fun notApprovedContactsPermisDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setMessage("Please grant access to contacts")
        builder.setPositiveButton(resources.getString(R.string.settings), DialogInterface.OnClickListener { dialog, id ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context?.packageName}")
            }
            activity?.startActivity(intent)
        })
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.red))
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}