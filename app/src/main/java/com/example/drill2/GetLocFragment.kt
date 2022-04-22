package com.example.drill2

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drill2.databinding.FragmentGetLocBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GetLocFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GetLocFragment : Fragment() {

    private var _binding: FragmentGetLocBinding? = null

    private val binding get() = _binding!!

    var btnLocState = 0

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.N)
    val locationPermissionLauncher  = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(this.requireContext(),"got permission",Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(this.requireContext(),"got aprox permission",Toast.LENGTH_SHORT).show()
            } else -> {
                Toast.makeText(this.requireContext(),"no permission",Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    val contactsPermissionLauncher  = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.READ_CONTACTS, false) -> {
                findNavController().navigate(R.id.action_getLocFragment_to_contactsList)
                Toast.makeText(this.requireContext(),"got permission",Toast.LENGTH_SHORT).show()
            } else -> {
                Toast.makeText(this.requireContext(),"no permission",Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        _binding = FragmentGetLocBinding.inflate(inflater,container,false)
        binding.linearLayout.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                locationPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
            } else {
                val geocoder : Geocoder = Geocoder(this.requireContext(), Locale.getDefault())
                val task: Task<Location> = fusedLocationProviderClient.lastLocation
                task.addOnSuccessListener {
                    if(it != null){
                        var addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                        var address = addresses.get(0).getAddressLine(0)
                        //Toast.makeText(this.requireContext(),"${address}",Toast.LENGTH_LONG).show()
                        createViews(address)
                    }
                }
                changeButtonColor()

                //findNavController().navigate(R.id.action_getLocFragment_to_yourLocation)
                Log.d("y","uuuu")
            }
        }
        return binding.root
    }

    fun getPermmissions(): Boolean {
        if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return false
        }
        return true
    }

    fun changeButtonColor() {
        if (btnLocState == 0) {
            binding.linearLayout.background = resources.getDrawable(R.drawable.my_loc_pressed)
            binding.imageView.background = resources.getDrawable(R.drawable.ic_location_2)
            binding.textView.setTextColor(resources.getColor(R.color.pale_blue))
            btnLocState = 1
        } else {
            binding.imageView.background = resources.getDrawable(R.drawable.ic_location)
            binding.textView.setTextColor(resources.getColor(R.color.white))
            btnLocState = 0
        }
    }

    fun createViews(address : String) {
        binding.yourLoc.text = "Your Location is:\n${address}\n"
        val shareButton = Button(this.requireContext())
        shareButton.text = "Share"
        binding.allViews.addView(shareButton)
        shareButton.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){
                contactsPermissionLauncher.launch(arrayOf(
                    Manifest.permission.READ_CONTACTS))
            } else {
                findNavController().navigate(R.id.action_getLocFragment_to_contactsList)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}