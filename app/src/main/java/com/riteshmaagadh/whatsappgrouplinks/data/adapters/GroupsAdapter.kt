package com.riteshmaagadh.whatsappgrouplinks.data.adapters

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshmaagadh.whatsappgrouplinks.R
import com.riteshmaagadh.whatsappgrouplinks.data.models.Group
import com.riteshmaagadh.whatsappgrouplinks.databinding.GroupItemBinding


class GroupsAdapter(private val list: List<Group>, val context: Context) :
    RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    // Change App name
    // Change App icon

    inner class GroupViewHolder(binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.groupNameTv
        private val joinBtn = binding.button
        val category = binding.textView2
        val imageView = binding.shapeableImageView

        init {
            joinBtn.setOnClickListener {
                val groupLink = list[adapterPosition].group_link
                try {
                    val list = context.packageManager.getLaunchIntentForPackage("com.whatsapp")
                    if (list != null) {
                        val intentWhatsAppGroup = Intent(Intent.ACTION_VIEW)
                        val uri: Uri = Uri.parse(groupLink)
                        intentWhatsAppGroup.data = uri
                        intentWhatsAppGroup.setPackage("com.whatsapp")
                        context.startActivity(intentWhatsAppGroup)
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Toast.makeText(context,"WhatsApp not installed!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder =
        GroupViewHolder(
            GroupItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.title.text = list[position].title
        holder.category.text = list[position].category
        Glide.with(context)
            .load(list[position].imageUrl)
            .placeholder(R.drawable.picture)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = list.size


}