package com.example.drill2

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                    Log.i("CONTACTS_NAMES", contactName)
                }
            }

        }
    }

}