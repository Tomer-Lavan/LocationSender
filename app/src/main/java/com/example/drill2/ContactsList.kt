package com.example.drill2

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drill2.databinding.FragmentContactsListBinding
import com.example.drill2.databinding.FragmentGetLocBinding
import com.example.drill2.databinding.FragmentYourLocationBinding

class ContactsList : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()

    private var _binding: FragmentContactsListBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = ContactAdapter(ContactsManager.contacts, object : ContactAdapter.ContactListener {
            override fun onContactClicked(index: Int) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, viewModel.addressPublic.value)
                }
                startActivity(intent)
            }

            override fun onContactLongClick(index: Int) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentContactsListBinding.inflate(inflater,container,false)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        getContacts()

        return binding.root
    }

    @SuppressLint("Range")
    fun getContacts(){
        val contentResolver : ContentResolver = requireActivity().contentResolver
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val cursor = contentResolver.query(uri,null,null,null,null)
        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()){
                    val contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val contact = Contact(contactName)
                    ContactsManager.add(contact)
                    //Log.i("CONTACTS_NAMES", contactName)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}