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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drill2.databinding.FragmentContactsListBinding
import com.example.drill2.databinding.FragmentGetLocBinding
import com.example.drill2.databinding.FragmentYourLocationBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContactsList.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactsList : Fragment() {

    private var _binding: FragmentContactsListBinding? = null

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter =
            ContactAdapter(ContactsManager.contacts, object : ContactAdapter.ContactListener {
                override fun onContactClicked(index: Int) {
                    //Toast.makeText(requireContext(), "${ContactsManager.contacts[index]}", Toast.LENGTH_SHORT).show()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "address")
                    }
                    startActivity(intent)
                }

                override fun onContactLongClick(index: Int) {
                    TODO("Not yet implemented")
                }
            })

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactsListBinding.inflate(inflater,container,false)
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
                    Log.i("CONTACTS_NAMES", contactName)
                }
            }
        }
    }

}