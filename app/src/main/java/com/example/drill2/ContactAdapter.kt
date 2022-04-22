package com.example.drill2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drill2.databinding.ContactLayoutBinding

class ContactAdapter(val contacts:List<Contact>, private val callback : ContactListener)
    : RecyclerView.Adapter<ContactAdapter.ContactViewHolder> () {

    interface ContactListener {
        fun onContactClicked(index: Int)
        fun onContactLongClick(index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ContactViewHolder(
            ContactLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) = holder.bind(contacts[position])

    override fun getItemCount() = contacts.size

    inner class ContactViewHolder(private val binding: ContactLayoutBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            callback.onContactClicked(adapterPosition)
        }

        override fun onLongClick(p0: View?): Boolean {
            callback.onContactLongClick(adapterPosition)
            return true
        }

        fun bind(contact: Contact) {
            binding.contactName.text = contact.name
        }
    }
}